package com.coolcollege.intelligent.util.vod;

import java.util.List;

/**
 * Created by Joshua on 2017/9/28 13:06
 */
public class CallbackRequest {
    private String EventType;
    private String VideoId;
    private String MediaId;
    private String Status;
    private String Bitrate;//视频流码率，单位Kbps
    private String Definition;//视频流清晰度定义, 取值：FD(流畅)，LD(标清)，SD(高清)，HD(超清)，OD(原画)，2K(2K)，4K(4K)
    private Float Duration;//视频流长度，单位秒
    private Boolean Encrypt;//视频流是否加密流
    private String ErrorCode;//视频流转码出错的时候，会有该字段表示出错代码
    private String ErrorMessage;//	视频流转码出错的时候，会有该字段表示出错信息
    private String FileUrl;//视频流的播放地址，不带鉴权的auth_key，如果开启了URL鉴权，则需要自己生成auth_key才能访问
    private String Format;//视频流格式，取值：mp4, m3u8
    private String Fps;//视频流帧率，每秒多少帧
    private Long Height;//    视频流高度，单位px
    private Long Size;//    视频流大小，单位Byte
    private Long Width;//    视频流宽度，单位px
    private List<CallbackStreamInfo> StreamInfos;
    private String CoverUrl;//封面图片地址，若未设置封面，则取第一张截图为封面
    private String[] Snapshots;//	截图地址列表

    private String Data;

    private String CreationTime;  //人工审核时间
    private String AuditStatus;  //人工审核的结果

    public String getEventType() {
        return EventType;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getBitrate() {
        return Bitrate;
    }

    public void setBitrate(String bitrate) {
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

    public String getFps() {
        return Fps;
    }

    public void setFps(String fps) {
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

    public List<CallbackStreamInfo> getStreamInfos() {
        return StreamInfos;
    }

    public void setStreamInfos(List<CallbackStreamInfo> streamInfos) {
        StreamInfos = streamInfos;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        CoverUrl = coverUrl;
    }

    public String[] getSnapshots() {
        return Snapshots;
    }

    public void setSnapshots(String[] snapshots) {
        Snapshots = snapshots;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getCreationTime() {
        return CreationTime;
    }

    public void setCreationTime(String creationTime) {
        CreationTime = creationTime;
    }

    public String getAuditStatus() {
        return AuditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        AuditStatus = auditStatus;
    }

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }
}
