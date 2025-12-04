package com.coolcollege.intelligent.controller.common;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2020/1/21.
 */

@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v2/enterprises/{enterprise-id}/common")
public class CommonController {

    /*@RequestMapping(path = "upload", method = RequestMethod.POST)
    public Object upload(MultipartFile file) {
        FileUploadParam fileUploadParam = null;
        try {
            fileUploadParam = fileUploadService.uploadFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件上传失败", e);
            baseOut.setCode(1);
            baseOut.setMsg(e.getMessage());
        }
        baseOut.setData(fileUploadParam);
        return baseOut;
    }*/
}
