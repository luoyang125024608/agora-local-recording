package com.example.bean;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "agora")
public class RecordingRequest {

    private String appId;
    private String channel;
    private String uid;
    private String channelProfile;
    private String channelKey;
    private String appliteDir;
    private String recordFileRootDir;
    private String triggerMode;
    private String autoSubscribe;
    private String streamType;

}

