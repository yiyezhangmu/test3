package com.coolcollege.intelligent.service.qywx.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.QyNameReplaceEnum;
import com.coolcollege.intelligent.model.qywx.vo.QyWechatDepartmentListVo;
import com.coolcollege.intelligent.model.qywx.vo.QyWechatUserGetVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Slf4j
public class FetchIdAndNameService {


    public static final  String userGet = "https://qyapi.weixin.qq.com/cgi-bin/user/get";

    public static final String departmentList = "https://qyapi.weixin.qq.com/cgi-bin/department/list";

    @Autowired
    private RestTemplate restTemplate;


    public JSONArray fetchIdAndNameByCorpIdAndCorpSecret(String corpId, String dataType, JSONArray idList, String dkfAccessToken) {
        log.info("获取id和name的参数corpId：{},dataType:{},idList:{}", corpId, dataType, idList);
        JSONArray result = new JSONArray();
        //2.如果dataType等于用户，获取用户信息
        if (dataType.equals(QyNameReplaceEnum.USER.getValue())) {
            for (int i = 0; i < idList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                String userId = idList.getString(i);
                if (Constants.AI_USER_ID.equals(userId)) {
                    continue;
                }
                userId = userId.replace(corpId+"_", "");
                String url = userGet + "?access_token=" + dkfAccessToken + "&userid=" + userId;
                QyWechatUserGetVo qyWechatUserGetVo = restTemplate.getForObject(url, QyWechatUserGetVo.class);
                log.info("获取企微待开发用户详情：{}", JSON.toJSONString(qyWechatUserGetVo));
                if (qyWechatUserGetVo != null) {
                    jsonObject.put("id", corpId + "_" + userId);
                    jsonObject.put("name", qyWechatUserGetVo.getName());
                    result.add(jsonObject);
                }
            }
            log.info("获取企微待开发用户列表：{}", JSONArray.toJSONString(result));
            return result;

        }
        //3.如果dataType等于企业，获取部门信息
        if (dataType.equals(QyNameReplaceEnum.DEPARTMENT.getValue())) {
            List<String> departmentIdList = Lists.newArrayList();
            for (int i = 0; i < idList.size(); i++) {
                String departmentId = idList.getString(i);
                departmentIdList.add(departmentId);
            }
            String url = departmentList + "?access_token=" + dkfAccessToken;
            QyWechatDepartmentListVo qyWechatDepartmentListVo = restTemplate.getForObject(url, QyWechatDepartmentListVo.class);
            log.info("获取企微待开发部门列表：{}", JSON.toJSONString(qyWechatDepartmentListVo));
            if (qyWechatDepartmentListVo != null) {
                for (QyWechatDepartmentListVo.DepartmentDTO departmentDTO : qyWechatDepartmentListVo.getDepartment()) {
                    if (departmentIdList.contains(departmentDTO.getId())) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", departmentDTO.getId());
                        jsonObject.put("name", departmentDTO.getName());
                        result.add(jsonObject);
                    }
                }

            }
            log.info("获取部门信息列表如下：{}", JSONArray.toJSONString(result));
            return result;
        }
        return null;
    }


}
