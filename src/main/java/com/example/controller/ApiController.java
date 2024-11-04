package com.example.controller;

import com.example.bean.RecordingRequest;
import io.agora.recording.RecordingSDK;
import io.agora.recording.test.RecordingSample;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
            RecordingSDK recordingSdk = new RecordingSDK();
            RecordingSample record = new RecordingSample(recordingSdk);

            if(StringUtils.isBlank(request.getChannel())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("channel is empty");
            }

            if(records.containsKey(request.getChannel())){
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
