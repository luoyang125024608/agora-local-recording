package com.example.controller;

import com.example.bean.RecordingRequest;
import io.agora.recording.RecordingSDK;
import io.agora.recording.test.RecordingSample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Slf4j
@RestController
@RequestMapping(value = "/record")
public class ApiController {

    private final RecordingRequest recordingRequest;

    private final Map<String, RecordingSample> records = new HashMap<String, RecordingSample>();

    private final String saveVideoUrl = "http://tgx-sg:4321/nest-api/live/host/live/video/save";

    @Resource
    private RestTemplate restTemplate;

    @Autowired
    public ApiController(RecordingRequest recordingRequest) {
        this.recordingRequest = recordingRequest;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testController() {
        return ResponseEntity.ok("test ok");
    }

    @PostMapping("/start")
    public ResponseEntity<String> startRecording(@RequestBody RecordingRequest request) {
        try {
            //lzz add
            RecordingSDK recordingSdk = new RecordingSDK();
            RecordingSample record = new RecordingSample(recordingSdk);

            if (StringUtils.isBlank(request.getChannel())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("channel is empty");
            }

            if (records.containsKey(request.getChannel())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("recording already started");
            }

            request.setAppliteDir(this.recordingRequest.getAppliteDir());
            request.setRecordFileRootDir(this.recordingRequest.getRecordFileRootDir());
            records.put(request.getChannel(), record);
            // 异步执行录制
            CompletableFuture.runAsync(() -> {
                record.createChannel(request);
            });
            return ResponseEntity.ok("Recording started successfully.");

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage());
        }

    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopController(@RequestBody RecordingRequest request) {
        try {
            String channel = request.getChannel();
            String hostImage = request.getHostImage();
            if (records.containsKey(channel)) {
                RecordingSample record = records.get(channel);
                boolean isStop = record.leaveChannel();
                String storageDir = record.getStorageDir();
                String rootDir = recordingRequest.getRootDir();
                String videoPath = "/host-video/" + channel + "/";
                String hostVideoDir = rootDir + videoPath;
                if (storageDir != null && !storageDir.isEmpty()) {
                    CompletableFuture.runAsync(() -> {
                        String videoFileName = moveMp4Files(storageDir, hostVideoDir);
                        if (videoFileName != null) {
                            String poster = moveHostImage(rootDir, hostImage, channel, videoFileName);
                            saveVideo(channel, request.getHistoryId(), videoPath + videoFileName, poster);
                        }
                    });
                }
                if (isStop) {
                    records.remove(channel);
                    return ResponseEntity.ok("Recording stop successfully.");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Recording stop failed.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Recording not exists.");
            }

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage());
        }
    }

    private String moveHostImage(String rootDir, String hostImage, String channel, String videoFileName) {
        File hostImageFile = new File(rootDir + hostImage);
        String path = "/images/host-video-image/" + channel + "/";
        // 提取 mp4 文件名的前缀（去掉扩展名）
        String baseName = videoFileName.substring(0, videoFileName.lastIndexOf('.'));
        // 获取图像文件的扩展名（例如 jpg、png）
        String imageExtension = hostImageFile.getName().substring(hostImageFile.getName().lastIndexOf('.'));
        // 使用相同的前缀生成新的图像文件名
        String newImageFileName = baseName + imageExtension;
        File destinationDir = new File(rootDir + path);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        File destinationFile = new File(destinationDir, newImageFileName);
        if (hostImageFile.renameTo(destinationFile)) {
            System.out.println("Moved file: " + hostImageFile.getAbsolutePath() + " to " + destinationFile.getAbsolutePath());
        } else {
            System.out.println("Failed to move file: " + hostImageFile.getAbsolutePath());
        }
        return path + newImageFileName;
    }

    private String moveMp4Files(String storageDir, String hostVideoDir) {
        File storageDirectory = new File(storageDir);
        File[] mp4Files = storageDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));
        if (mp4Files != null) {
            File destinationDir = new File(hostVideoDir);
            if (!destinationDir.exists()) {
                destinationDir.mkdirs();
            }
            for (File mp4File : mp4Files) {
                File destinationFile = new File(destinationDir, mp4File.getName());
                if (mp4File.renameTo(destinationFile)) {
                    System.out.println("Moved file: " + mp4File.getAbsolutePath() + " to " + destinationFile.getAbsolutePath());
                } else {
                    System.out.println("Failed to move file: " + mp4File.getAbsolutePath());
                }
            }
            return mp4Files.length > 0 ? mp4Files[0].getName() : null;
        }
        return null;
    }

    private void saveVideo(String channel, String historyId, String videoFile, String poster) {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        // 设置请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("hostId", channel);
        requestBody.put("historyId", historyId);
        requestBody.put("videoFile", videoFile);
        requestBody.put("poster", poster);

        // 创建请求实体
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // 发送 POST 请求
        restTemplate.exchange(saveVideoUrl, HttpMethod.POST, request, String.class);
    }
}
