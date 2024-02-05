# Agora-LocalRecording-JavaWeb-Demo

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Version](https://img.shields.io/badge/version-v1.0-blue)
![License](https://img.shields.io/badge/license-MIT-orange)

[English](#english) | [中文](#中文)

## English

## Installation
1. Clone the project
   ```bash
   git clone https://github.com/your-username/your-project.git
   cd your-project
   ```
2. Edit Configure
   Replace the configuration file src/main/resources/application.yml  with your project values

   ```yml
    agora:
        domain: https://api.agora.io
        appid: your-app-id
        key: your-key
        secret: your-secret
        channel: your-channel
        uid: your-uid
        channelProfile: your-channel-profile
        appliteDir: /your/custom/path/to/AgoraRecord/bin
        recordFileRootDir: /your/custom/path/to/tmp
        lowUdpPort: your-low-udp-port
        highUdpPort: your-high-udp-port
        triggerMode: your-trigger-mode
        isAudioOnly: your-is-audio-only
        isVideoOnly: your-is-video-only
        streamType: your-stream-type
        autoSubscribe: your-auto-subscribe
   ```

3. Compile
   ```bash
   mvn clean package
   ```
4. Deploy 
   ```bash
   java -jar target/your-project.jar &
   ```



### Interface Description


| **Endpoint**               | **HTTP Method** | **Request Type** | **Request Body**                               | **Response Status Codes**                  | **Response Body**                               |
|-----------------------------|------------------|------------------|-------------------------------------------------|---------------------------------------------|-------------------------------------------------|
| `/record/getProp`           | `GET`            | -                | None                                            | 200 - 5XX                                    | Default recording configuration information   |
| `/record/test`              | `GET`            | -                | None                                            | 200 - 5XX                                       | "test ok"                                       |
| `/record/start`             | `POST`           | JSON             | `{ "appId": "string", "channel": "string", "uid": "string" }` | 200 - 5XX           | "Recording started successfully" or Error message |
| `/record/stop`              | `POST`           | Query Parameter  | None                             | 200 - 5XX            | "Recording stop successfully" or Error message    |
| `/record/help`              | `GET`            | -                | None                                            | -                                           | Help message with available commands             |


## 中文

## 安装与运行
1. 克隆项目到本地
   ```bash
   git clone https://github.com/your-username/your-project.git
   cd your-project
   ```
2. 修改配置
   将 src/main/resources/application.yml 文件中的以下配置替换为你的项目值
   ```yml
    agora:
        domain: https://api.agora.io
        appid: your-app-id
        key: your-key
        secret: your-secret
        channel: your-channel
        uid: your-uid
        channelProfile: your-channel-profile
        appliteDir: /your/custom/path/to/AgoraRecord/bin
        recordFileRootDir: /your/custom/path/to/tmp
        lowUdpPort: your-low-udp-port
        highUdpPort: your-high-udp-port
        triggerMode: your-trigger-mode
        isAudioOnly: your-is-audio-only
        isVideoOnly: your-is-video-only
        streamType: your-stream-type
        autoSubscribe: your-auto-subscribe
   ```

3. 编译与打包
   ```bash
   mvn clean package
   ```
4. 部署运行
   ```bash
   java -jar target/your-project.jar
   ```


## 接口文档

| **Endpoint**               | **HTTP Method** | **Request Type** | **Request Body**                               | **Response Status Codes**                  | **Response Body**                               |
|-----------------------------|------------------|------------------|-------------------------------------------------|---------------------------------------------|-------------------------------------------------|
| `/record/getProp`           | `GET`            | -                | None                                            | 200 - 5XX                                    | Default recording configuration information   |
| `/record/test`              | `GET`            | -                | None                                            | 200 - 5XX                                       | "test ok"                                       |
| `/record/start`             | `POST`           | JSON             | `{ "appId": "string", "channel": "string", "uid": "string" }` | 200 - 5XX           | "Recording started successfully" or Error message |
| `/record/stop`              | `POST`           | Query Parameter  | None                             | 200 - 5XX            | "Recording stop successfully" or Error message    |
| `/record/help`              | `GET`            | -                | None                                            | -                                           | Help message with available commands             |

## 参考链接

- [本地录制](https://doc.shengwang.cn/doc/recording/java/get-started/enable-service)：本地录制官方文档
- [API 文档](https://doc.shengwang.cn/api-ref/recording/java/overview)：API文档说明。
- [本地录制 错误码](https://docs-legacy.agora.io/cn/Recording/API%20Reference/recording_java/interfaceio_1_1agora_1_1recording_1_1_recording_event_handler.html?platform=Linux)：本地录制相关错误码说明。
-  [SDK 错误码](https://docportal.shengwang.cn/cn/live-streaming-premium-legacy/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler_1_1_error_code.html?platform=All%20Platforms)：SDK 相关错误码说明。

