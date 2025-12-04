package com.coolcollege.intelligent.util.vod;

/**
 * Created by Joshua on 2017/9/28 13:09
 */
public class CallbackStreamInfo {
    private String Status;// 视频流转码状态，取值：success(成功)，fail(失败)
    private Float Bitrate;//     视频流码率，单位Kbps
    private String Definition;//  视频流清晰度定义, 取值：FD(流畅)，LD(标清)，SD(高清)，HD(超清)，OD(原画)，2K(2K)，4K(4K)
    private Float Duration;// 视频流长度，单位秒
    private Boolean Encrypt;//     视频流是否加密流
    private String ErrorCode;// 视频流转码出错的时候，会有该字段表示出错代码
    private String ErrorMessage;//	视频流转码出错的时候，会有该字段表示出错信息
    private String FileUrl;//     视频流的播放地址，不带鉴权的auth_key，如果开启了播放鉴权，此地址会无法访问
    private String Format;// 视频流格式，取值：mp4, m3u8
    private Float Fps;//     视频流帧率，每秒多少帧
    private Long Height;// 视频流高度，单位px
    private Long Size;// 视频流大小，单位Byte
    private Long Width;// 视频流宽度，单位px

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Float getBitrate() {
        return Bitrate;
    }

    public void setBitrate(Float bitrate) {
        Bitrate = bitrate;
    }

    public String getDefinition() {
        return Definition;
    }

    public void setDefinition(String definition) {
        Definition = definition;
    }

    public Float getDuration() {
        return Duration;
    }

    public void setDuration(Float duration) {
        Duration = duration;
    }

    public Boolean getEncrypt() {
        return Encrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        Encrypt = encrypt;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public Float getFps() {
        return Fps;
    }

    public void setFps(Float fps) {
        Fps = fps;
    }

    public Long getHeight() {
        return Height;
    }

    public void setHeight(Long height) {
        Height = height;
    }

    public Long getSize() {
        return Size;
    }

    public void setSize(Long size) {
        Size = size;
    }

    public Long getWidth() {
        return Width;
    }

    public void setWidth(Long width) {
        Width = width;
    }
}
