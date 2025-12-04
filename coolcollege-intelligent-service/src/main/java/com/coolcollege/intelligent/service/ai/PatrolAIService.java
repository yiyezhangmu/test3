package com.coolcollege.intelligent.service.ai;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.objectdet20191230.Client;
import com.aliyun.objectdet20191230.models.DetectWorkwearRequest;
import com.aliyun.objectdet20191230.models.DetectWorkwearResponse;
import com.aliyun.objectdet20191230.models.DetectWorkwearResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.common.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
@Slf4j
@Service
public class PatrolAIService {

    @Autowired
    private HttpRestTemplateService httpRestTemplateService;
    @Resource
    private RedisUtilPool redisUtilPool;

    /**
     * 周大福AI对比
     * @param videoUrl
     * @return
     */
    public String zdfAI(String videoUrl){
        if(StringUtils.isAnyBlank(videoUrl)){
            return null;
        }
        String url = "https://videomodel.coolstore.cn/videldeal?videourl="+videoUrl;
        log.info("zdfAI,url:{}", url);
        String response = httpRestTemplateService.getForString(url);
        if(Objects.nonNull(response)){
            JSONObject jsonObject = JSONObject.parseObject(response);
            jsonObject.remove("img_list");
            StringBuilder result = new StringBuilder();
            jsonObject.keySet().forEach(key->{
                result.append(key).append(":").append(jsonObject.getString(key)).append("<br/>");
            });
            log.info("zdfAI:{}", response);
            return result.toString();
        }
        return null;
    }

}
