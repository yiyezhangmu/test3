package com.coolcollege.intelligent.controller.login;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.login.LoginRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * describe:登录统计接口
 *
 * @author zhouyiping
 * @date 2020/10/27
 */
@RestController
@BaseResponse
@Slf4j
public class LoginRecordController {

    @Autowired
    private LoginRecordService loginRecordService;

    @GetMapping(value = "/v2/enterprise/record/statistics")
    public ResponseResult loginRecord() {
        return ResponseResult.success(loginRecordService.statisticsLoginRecord());

    }
}
