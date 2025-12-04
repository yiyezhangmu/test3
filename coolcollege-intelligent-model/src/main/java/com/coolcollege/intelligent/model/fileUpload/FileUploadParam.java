package com.coolcollege.intelligent.model.fileUpload;

import lombok.Data;

/**
 * Created by gavin on 15/8/25.
 * 为了给上传文件返回构成json使用
 * @author 王春辉
 */

@Data
public class FileUploadParam {

    private String fileName;
    private String fileNewName;
    private String fileThumbName;
    private Long fileSize;
    private String fileType;
    private String server;
    private String extension;
    private String fileUrl;
}
