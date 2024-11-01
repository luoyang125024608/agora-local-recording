package com.example.controller;

import com.example.bean.RecordingRequest;
import io.agora.recording.RecordingSDK;
import io.agora.recording.test.RecordingSample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Slf4j
@RestController
@RequestMapping(value = "/record")
public class ApiController {

    private final RecordingRequest recordingRequest;

    private final Map<String, RecordingSample> records = new HashMap<String, RecordingSample>();

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
            String[] args = new String[]{""};
            //lzz add
            RecordingSDK RecordingSdk = new RecordingSDK();
            RecordingSample ars = new RecordingSample(RecordingSdk);

            String appId = request.getAppId();
            String channel = request.getChannel();
            String UID = request.getUid();
            String channelKey = request.getChannelKey();

            if(records.containsKey(channel)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("recording already started");
            }
            // Create the arguments array
            args = new String[]{
                    "--appId", appId,
                    "--channel", channel,
                    "--channelKey", channelKey,
                    "--uid", UID,
                    "--appliteDir", this.recordingRequest.getAppliteDir(),
                    "--recordFileRootDir", this.recordingRequest.getRecordFileRootDir(),
                    "--isMixingEnabled", "1",
                    "--mixedVideoAudio", "6",
            };
            System.out.println("Command line arguments: " + Arrays.toString(args));
            records.put(channel, ars);

            // 异步执行录制
            String[] finalArgs = args;
            CompletableFuture.runAsync(() -> {
                // 调用相应的录制服务或启动录制任务
                ars.createChannel(finalArgs);
            });

            //判断录制是否成功
            boolean isStartError = ars.isRecordingError;

            if (!isStartError) {
                return ResponseEntity.ok("Recording started successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Recording start failed.");
            }

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage());
        }

    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopController(@RequestBody RecordingRequest request) {
        try {
            String channel = request.getChannel();
            if (records.containsKey(channel)) {
                RecordingSample ars = records.get(channel);
                boolean isStop = ars.leaveChannel();

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
}
