package com.coolcollege.intelligent.util;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author wxp
 * @date 2021/6/10
 */
@Slf4j
public class MediaUploadUtil {

    /**
     * https://work.weixin.qq.com/api/doc/90001/90143/90390
     * 获取媒体文件
     * @param accessToken 接口访问凭证
     * @param mediaId 媒体文件id
     * */
    public static HttpURLConnection downloadMedia(String accessToken, String mediaId, String baseUrl) {
        // 拼接请求地址
        String requestUrl = baseUrl + "/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", mediaId);
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            return  conn;
        } catch (Exception e) {
            log.error("下载媒体文件失败：", e);
        }
        return null;
    }

}
