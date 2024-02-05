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
    private String appliteDir;
    private String recordFileRootDir;
    private String triggerMode;
    private String autoSubscribe;
    private String streamType;

    public String[] toArgs() {
        return new String[]{
                "--appId", this.appId,
                "--channel", this.channel,
                "--uid", String.valueOf(this.uid),
                "--channelProfile", String.valueOf(this.channelProfile),
                "--appliteDir", this.appliteDir,
                "--recordFileRootDir", this.recordFileRootDir,
                "--triggerMode", this.triggerMode,
                "--streamType", this.streamType,
                "--autoSubscribe", this.autoSubscribe,
                // 添加其他属性的映射

        };
    }

}

