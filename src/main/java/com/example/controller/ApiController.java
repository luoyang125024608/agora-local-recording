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
import java.util.concurrent.CompletableFuture;


@Slf4j
@RestController
@RequestMapping(value = "/record")
public class ApiController {

    private final RecordingRequest recordingRequest;

    //lzz add
    RecordingSDK RecordingSdk = new RecordingSDK();
    RecordingSample ars = new RecordingSample(RecordingSdk);

    @Autowired
    public ApiController(RecordingRequest recordingRequest) {
        this.recordingRequest = recordingRequest;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testController(){
        return  ResponseEntity.ok("test ok");
    }

    @GetMapping("/getProp")
    public  ResponseEntity<String> getPropController(){
//        System.out.println("default setting is = "+ recordingRequest.toArgs());
        return  ResponseEntity.ok("default setting is = "+ Arrays.toString(recordingRequest.toArgs()));
    }

    @PostMapping("/start")
    public ResponseEntity<String> startRecording(@RequestBody(required = false) RecordingRequest request) {
        try{
            String[] args = new String[]{""};
            // 请求体为空的逻辑，使用默认值
            if (request == null) {
                args = recordingRequest.toArgs();
                System.out.println("default setting: " + Arrays.toString(args));
            }else{

                String appId = request.getAppId();
                String channel = request.getChannel();
                String UID= request.getUid();
                // Create the arguments array
                args = new String[] {
                        "--appId", appId,
                        "--channel", channel,
                        "--uid", UID
                };
                System.out.println("Command line arguments: " + Arrays.toString(args));
            }

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

        }catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage());
        }

    }

    @PostMapping("/stop")
    public  ResponseEntity<String> stopController(){
        try {

            boolean isStop = RecordingSdk.leaveChannel();

            if (isStop) {
                return ResponseEntity.ok("Recording stop successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: Recording stop failed.");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + ex.getMessage());
        }
    }



    @GetMapping("/help")
    public String getHelp() {
        try {
            StringBuilder helpMessage = new StringBuilder();
            helpMessage.append("Type \"start\" to start recording! \n");
            helpMessage.append("Type \"stop\" to stop recording! \n");
            helpMessage.append("Type \"getProp\" to call get default Properties!\n");
            helpMessage.append("Type \"test\" to test the server is online or not!\n");
            return helpMessage.toString();
        } catch (Exception ex) {
            return "Error generating help message: " + ex.getMessage();
        }
    }
}
