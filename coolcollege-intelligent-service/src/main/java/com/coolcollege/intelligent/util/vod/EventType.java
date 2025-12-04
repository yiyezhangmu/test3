package com.coolcollege.intelligent.util.vod;

/**
 * Created by Joshua on 2017/9/28 13:41
 */
public class EventType {
    /**
     * 视频上传完成
     */
    public static final String FileUploadComplete = "FileUploadComplete";
    /**
     * 视频截图完成
     */
    public static final String SnapshotComplete = "SnapshotComplete";
    /**
     * 视频单个清晰度转码完成
     */
    public static final String StreamTranscodeComplete = "StreamTranscodeComplete";
    /**
     * 视频全部清晰度转码完成
     */
    public static final String TranscodeComplete = "TranscodeComplete";
    /**
     * 智能审核
     */
    public static final String AIMediaAuditComplete = "AIMediaAuditComplete";

    public static final String CreateAuditComplete = "CreateAuditComplete";
}
