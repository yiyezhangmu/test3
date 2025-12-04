package com.coolcollege.intelligent.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/05/17
 */
@RestController
@Slf4j
public class HealthController {

    @RequestMapping(value={"/check/ok","/web/check/ok"})
    public JSONObject health(){
        DataSourceHelper.reset();
        JSONObject jsonData = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject db = new JSONObject();
        try {
            db.put("db", "ok");
        } catch (Exception e) {
            db.put("db", "error");
        }
        try {
            String ip = NetUtil.getLocalhostStr();
            db.put("ip", ip);
        } catch (Exception e) {
            db.put("ip", "error");
        }
        array.add(db);
        jsonData.put("success", true);
        jsonData.put("data", array);
        jsonData.put("appname", "intelligent-web");
        return jsonData;
    }

}
