package com.coolcollege.intelligent.controller.video;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.service.video.vod.VodService;
import com.coolcollege.intelligent.util.vod.AliResponseUtil;
import com.coolcollege.intelligent.util.vod.CallbackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/vod")
public class VodController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private VodService vodService;

    /**
     * vod回调
     * @author chenyupeng
     * @date 2021/10/11
     * @param request
     * @return java.lang.Object
     */
    @RequestMapping("callback")
    public Object callback(HttpServletRequest request) throws IOException {
        logger.info("=================================");
        logger.info("VOD视频回调");
        String cl = request.getHeader("content-length");
        logger.info("content-length:" + cl);
        String vodCallbackBody = AliResponseUtil.GetPostBody(request.getInputStream(), Integer.parseInt(cl));
        logger.info("vodCallbackBody:" + vodCallbackBody);
        CallbackRequest callback = JSON.parseObject(vodCallbackBody, CallbackRequest.class);
        logger.info("callback" + JSON.toJSONString(callback));
        vodService.callback(callback);
        logger.info("=================================");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Status", "OK");
        return jsonObject;
    }

    /**
     * 刷新凭证
     * @author chenyupeng
     * @date 2021/10/11
     * @param videoId
     * @return java.lang.Object
     */
    @RequestMapping(value = "refreshTicket", method = RequestMethod.GET)
    public Object refreshTicket(String videoId) {
        return vodService.refreshTicket(videoId);
    }


    /**
     * * 1. 客户端获取上传地址和凭证
     *      * 2. 用户选择文件
     *      * 3. 添加待上传文件到列表
     *      * 4. 开始上传
     *      * 5. 设置上传凭证和地址
     *      * 6. 上传完成事件
     * @author chenyupeng
     * @date 2021/10/11
     * @param fileName
     * @return java.lang.Object
     */
    @RequestMapping(value = "/ticket", method = RequestMethod.GET)
    public Object getTicket(String fileName) {
        logger.info("getTicket length:{}，fileName:{}", fileName.length(), fileName);
        if (fileName.length() > Constants.LENGTH_EXCEEDED_MAX) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "title length is " + fileName.length() + ", has exceeded 128");
        }
        return vodService.getTicket(fileName);
    }

    /**
     * 从redis获取上传的视频信息，一周过期
     * @author chenyupeng
     * @date 2021/10/14
     * @param videoId
     * @return java.lang.Object
     */
    @RequestMapping(value = "getVideoInfoByRedis", method = RequestMethod.GET)
    public Object getVideoInfoByRedis(String videoId) {
        return vodService.getVideoInfoByRedis(videoId);
    }


    /**
     * sts token
     * @return
     */
    @RequestMapping(value = "/getSTSTicket", method = RequestMethod.GET)
    public ResponseResult getSTSTicket() {
        return ResponseResult.success(vodService.getSTSTicket());
    }

    /**
     * sts token
     * @return
     */
    @RequestMapping(value = "/getXfsgSTSTicket", method = RequestMethod.GET)
    public ResponseResult getSxfgSTSTicket(String fileName) {
        return ResponseResult.success(vodService.getXfsgSTSTicket(fileName));
    }

    @RequestMapping(value = "/getVideoTicket", method = RequestMethod.GET)
    public ResponseResult getVideoTicket(@RequestParam("fileName") String fileName, @RequestParam("callbackUrlSuffix") String callbackUrlSuffix) {
        return ResponseResult.success(vodService.getVideoTicket(fileName, callbackUrlSuffix));
    }
}
