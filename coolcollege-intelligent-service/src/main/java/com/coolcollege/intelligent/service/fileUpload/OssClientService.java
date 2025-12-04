package com.coolcollege.intelligent.service.fileUpload;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.*;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.util.FileUtils;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Created by gavin on 15/8/5.
 * 阿里云OSS客户端
 */
@Slf4j
@Service
public class OssClientService {

    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.upload.endpoint}")
    private String uploadEndpoint;
    @Value("${oss.access.key.id}")
    private String accessKeyId;
    @Value("${oss.access.key.secret}")
    private String accessKeySecret;
    @Value("${oss.bucket.name}")
    private String bucketName;

    @Value("${oss.host}")
    private String ossHost;
    @Value("${oss.preview.host}")
    private String ossPreviewHost;


    private OSSClient client = null;

    private OSSClient oldCoolStoreClient = null;

    private OSSClient externalClient = null;

    /**
     * 阿里云水印Base64编码后的字符串，最大长度为64个字符
     * （str.byte[].length/3）*4 = len（Base64（str））
     */
    public static final int WATER_MARK_LENGTH = 64;


    private OSSClient getClient() {
        if(client==null) {
            synchronized(this){
                client= new OSSClient(endpoint, accessKeyId, accessKeySecret);
            }
        }
        return client;
    }

    private OSSClient getOldCoolStoreClient() {
        if (oldCoolStoreClient == null) {
            synchronized (this) {
                oldCoolStoreClient = new OSSClient(endpoint, "", "");
            }
        }
        return oldCoolStoreClient;
    }

    /*
    oss 外网地址
     */
    private OSSClient getExternalClient() {
        if(externalClient==null) {
            synchronized(this){
                externalClient= new OSSClient(uploadEndpoint, accessKeyId, accessKeySecret);
            }
        }
        return externalClient;
    }

    /**OSSClient
//     * 获取上传进度回调
//     */
//    class PutObjectProgressListener implements ProgressListener {
//
//        private long bytesWritten = 0;
//        private long totalBytes = -1;
//        private boolean succeed = false;
//
//        @Override
//        public void progressChanged(ProgressEvent progressEvent) {
//            long bytes = progressEvent.getBytes();
//            ProgressEventType eventType = progressEvent.getEventType();
//            switch (eventType) {
//                case TRANSFER_STARTED_EVENT:
//                    log.info("Start to upload......");
//                    break;
//
//                case REQUEST_CONTENT_LENGTH_EVENT:
//                    this.totalBytes = bytes;
//                    log.info(this.totalBytes + " bytes in total will be uploaded to OSS");
//                    break;
//
//                case REQUEST_BYTE_TRANSFER_EVENT:
//                    this.bytesWritten += bytes;
//                    if (this.totalBytes != -1) {
//                        int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
//                        log.info(bytes + " bytes have been written at this time, upload progress: " +
//                                percent + "%(" + this.bytesWritten + "/" + this.totalBytes + ")");
//                    } else {
//                        log.info(bytes + " bytes have been written at this time, upload ratio: unknown" +
//                                "(" + this.bytesWritten + "/...)");
//                    }
//                    break;
//
//                case TRANSFER_COMPLETED_EVENT:
//                    this.succeed = true;
//                    log.info("Succeed to upload, " + this.bytesWritten + " bytes have been transferred in total");
//                    break;
//
//                case TRANSFER_FAILED_EVENT:
//                    log.info("Failed to upload, " + this.bytesWritten + " bytes have been transferred");
//                    break;
//
//                default:
//                    break;
//            }
//        }
//
//        public boolean isSucceed() {
//            return succeed;
//        }
//    }

    /**
     * 上传文件到阿里云OSS
     *
     * @param fileName
     * @param inputStream
     * @param contentLength
     * @param contentType
     * @throws Exception
     */
    @Async("taskExecutor")
    public void putObjectAsync(String fileName, InputStream inputStream, Long contentLength, String contentType) throws Exception {
        putObject(fileName, inputStream, contentLength, contentType);
    }

    /**
     * 上传文件到阿里云OSS
     *
     * @param fileName
     * @param inputStream
     * @param contentLength
     * @param contentType
     * @throws Exception
     */
    public String putObject(String fileName, InputStream inputStream, Long contentLength, String contentType) throws Exception {

        OSSClient ossClient = getClient();

        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(contentLength);

            meta.setContentType(contentType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, meta);
           // PutObjectResult p = ossClient.putObject(putObjectRequest.<PutObjectRequest>withProgressListener(new PutObjectProgressListener()));
            PutObjectResult p = ossClient.putObject(putObjectRequest);
            String url = ossHost + fileName;
            return url;

            // return  p.getETag();

        } catch (OSSException oe) {
            log.warn("oss出现问题", oe);
            log.error("Caught an OSSException, which means your request made it to OSS, " + "but was rejected with an error response for some reason.");
            log.error("Error Message: " + oe.getErrorCode());
            log.error("Error Code:       " + oe.getErrorCode());
            log.error("Request ID:      " + oe.getRequestId());
            log.error("Host ID:           " + oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            log.warn("oss客户端出现问题", ce);
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: " + ce.getMessage());
            throw ce;
        }catch (Exception e){
            log.warn("uploadBaseImage, error");
            throw e;
        }
    }

    /**
     * 上传文件到阿里云OSS
     *
     * @param fileName
     * @param inputStream
     */
    public String putObject(String fileName, InputStream inputStream) {

        OSSClient ossClient = getClient();

        try {
            log.info("uploadBaseImage, send...");
            PutObjectResult p = ossClient.putObject(bucketName, fileName, inputStream);
            log.info("uploadBaseImage, send over");
            String url = ossHost + fileName;
            return url;
            // return  p.getETag();
        } catch (OSSException oe) {
            log.warn("oss出现问题", oe);
            log.error("Caught an OSSException, which means your request made it to OSS, " + "but was rejected with an error response for some reason.");
            log.error("Error Message: " + oe.getErrorCode());
            log.error("Error Code:       " + oe.getErrorCode());
            log.error("Request ID:      " + oe.getRequestId());
            log.error("Host ID:           " + oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            log.warn("oss客户端出现问题", ce);
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: " + ce.getMessage());
            throw ce;
        }catch (Exception e){
            log.warn("uploadBaseImage, error");
            throw e;
        }
    }


    public String putSOPObject(String fileName, InputStream inputStream, Long contentLength, String contentType) throws Exception {

        OSSClient ossClient = this.getClient();
        try {

            ObjectMetadata meta = new ObjectMetadata();

            meta.setContentLength(contentLength);

           //  meta.setContentType(contentType);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, meta);
           // PutObjectResult p = ossClient.putObject(putObjectRequest.<PutObjectRequest>withProgressListener(new PutObjectProgressListener()));
            PutObjectResult p = ossClient.putObject(putObjectRequest);

            String url = ossHost + fileName;
            return url;

            // return  p.getETag();

        } catch (OSSException oe) {
            log.warn("oss出现问题", oe);
            log.error("Caught an OSSException, which means your request made it to OSS, " + "but was rejected with an error response for some reason.");
            log.error("Error Message: " + oe.getErrorCode());
            log.error("Error Code:       " + oe.getErrorCode());
            log.error("Request ID:      " + oe.getRequestId());
            log.error("Host ID:           " + oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            log.warn("oss客户端出现问题", ce);
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: " + ce.getMessage());
            throw ce;
        }catch (Exception e){
            log.warn("uploadSOP, error");
            throw e;
        }
    }

    public boolean copyObject(String aKey,String bKey) {
        OSSClient ossClient = getClient();
        try {
            //复制a到b
            ossClient.copyObject(bucketName, aKey, bucketName, bKey);
        } catch (OSSException oe) {
            log.warn("oss出现问题", oe);
            log.error("Caught an OSSException, which means your request made it to OSS, " + "but was rejected with an error response for some reason.");
            log.error("Error Message: " + oe.getErrorCode());
            log.error("Error Code:       " + oe.getErrorCode());
            log.error("Request ID:      " + oe.getRequestId());
            log.error("Host ID:           " + oe.getHostId());
            throw oe;
        } catch (ClientException ce) {
            log.warn("oss客户端出现问题", ce);
            log.error("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: " + ce.getMessage());
            throw ce;
        }
        return true;
    }

    // 文档预览
    public String getPreviewUrl(String objectKey) {
        String host="";
        String bucket="";
        OSSClient ossClient = null;
        if(objectKey.startsWith("https://ossfile1.coolstore.cn")){
            ossClient = this.getClient();
            host=ossPreviewHost;
            bucket=bucketName;
        }else if(objectKey.startsWith("https://oss-cool.coolstore.cn")){
            ossClient = this.getClient();
            host= "https://preview-cool.coolstore.cn";
            bucket= "store-ossfile";
        }else{
            ossClient = getOldCoolStoreClient();
            if(objectKey.startsWith("https://oss-store.coolcollege.cn")){
                host="https://oss-store.coolcollege.cn";
                bucket="coolstore-storage";
            }else {
                host="https://oss-processor.coolcollege.cn";
                bucket="coolcollege-storage-hz";
            }
        }
        objectKey = FileUtils.getFilePathWithOutHost(objectKey);
        String process = "imm/previewdoc";
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, objectKey);
        getObjectRequest.setProcess(process);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectKey);
        request.setProcess(process);
        request.setExpiration(new Date(new Date().getTime() +   1000*60*60*24*7));// 过期时间设置为1天
        URL url = ossClient.generatePresignedUrl(request);
        return (host + url.getFile()).replace("//eid", "/eid");
    }

    /**
     * 生成签名图片地址（证照桶）
     * @param urlString 逗号分隔的oss文件key
     * @param watermark 水印
     * @param resize 缩放
     * @return 逗号分隔签名url
     */
    public String generatePresignedUrls(String urlString, String watermark, String resize) {
        if(StringUtils.isBlank(urlString)) {
            return null;
        }
        StringBuilder process = new StringBuilder();
        // 水印不为空，设置水印样式
        if(StringUtils.isNotBlank(watermark)) {
            try {
                // 水印base64处理,要用safe的方式转码，会将结果中的加号（+）替换成短划线（-）,将结果中的正斜线（/）替换成下划线（_）,将结果中尾部的等号（=）省略。
                process.append(String.format(Constants.OSS_IMAGE_WATER_MARK,
                        Base64.getUrlEncoder().encodeToString(this.getBytesForWaterMark(watermark))));
                // 缩放处理
                if(StringUtils.isNotBlank(resize)) {
                    process.append(Constants.COMMA).append(resize);
                }
            } catch (UnsupportedEncodingException e) {
                log.error("oss client service water mark get bytes error", e);
            }
        }else if(StringUtils.isNotBlank(resize)){
            // 缩放处理
            process.append(resize);
        }
        OSSClient ossClient = getClient();
        List<String> imageNameList = Arrays.asList(urlString.split(Constants.COMMA));
        StringBuilder sb = new StringBuilder();
        log.info("process:{}", process);
        for(String imageName : imageNameList) {
            // 根据“_”分隔，获取bucket名称和文件名
            String[] imageInfo = imageName.split(Constants.UNDERLINE);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(imageInfo[0], imageInfo[1]);
            request.setProcess(process.toString());
            // 有效期 9 小时
            request.setExpiration(new Date(System.currentTimeMillis() + 32400 * 1000));
            sb.append(Constants.COMMA + ossClient.generatePresignedUrl(request));
        }
        return sb.substring(Constants.INDEX_ONE).replace("oss-cn-hangzhou-internal.aliyuncs.com", "oss-cn-hangzhou.aliyuncs.com");
    }

    /**
     * 生成签名url（用作前端直传oss）
     * @param key ossObjectKey
     * @return URL
     */
    public URL generatePresignedUrl(String key) {
        OSSClient ossClient = getExternalClient();
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName,key);
        long time = System.currentTimeMillis() + Constants.UPLOAD_URL_EXPIRE;
        log.info("generatePresignedUrl setExpiration：{}", time);
        request.setExpiration(new Date(time));
        request.setMethod(HttpMethod.PUT);
        request.setContentType(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
        return ossClient.generatePresignedUrl(request);
    }

    /**
     * 获取水印
     * 截取阿里云水印长度的水印内容
     * @param waterMark 水印内容
     * @return byte[]
     * @throws UnsupportedEncodingException
     */
    private byte[] getBytesForWaterMark(String waterMark) throws UnsupportedEncodingException {
        byte[] bytes = waterMark.getBytes("UTF-8");
        if(bytes.length < WATER_MARK_LENGTH) {
            return bytes;
        }
        byte[] newBytes = Arrays.copyOf(bytes, WATER_MARK_LENGTH);
        if(waterMark.contains(new String(newBytes))) {
            return newBytes;
        }
        newBytes = Arrays.copyOf(bytes, WATER_MARK_LENGTH - 1);
        if(waterMark.contains(new String(newBytes))) {
            return newBytes;
        }
        newBytes = Arrays.copyOf(bytes, WATER_MARK_LENGTH - 2);
        if(waterMark.contains(new String(newBytes))) {
            return newBytes;
        }
        newBytes = Arrays.copyOf(bytes, WATER_MARK_LENGTH - 3);
        return newBytes;
    }

}
