package com.coolcollege.intelligent.service.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.sofa.common.utils.StringUtil;
import com.coolcollege.intelligent.common.config.PlaformApiConfig;
import com.coolcollege.intelligent.common.enums.EnterpriseApiErrorEnum;
import com.coolcollege.intelligent.common.enums.PlatFormApiErrorEnum;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import com.coolcollege.intelligent.common.http.HttpRestTemplateService;
import com.coolcollege.intelligent.model.platform.BizCourse;
import com.coolcollege.intelligent.model.platform.BizCourseQuery;
import com.coolcollege.intelligent.model.platform.PlatformApiResponse;
import com.coolcollege.intelligent.model.platform.SysCourseClassify;
import com.coolcollege.intelligent.model.userholder.UserToken;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对于token服务的相关包装
 *
 * @author 柳敏 min.liu@coolcollege.cn
 * @since 2021-05-08 11:26
 */
@Slf4j
@Service
public class PlatformApiProxy {

    @Resource
    private PlaformApiConfig plaformApiConfig;

    @Resource
    private HttpRestTemplateService httpRestTemplateService;

    @Value("${coolcollege.third.oa.login}")
    private String oaLoginUrl;


    /**
     * 生成调用酷学院接口生成token
     *
     * @param unionId    钉钉unionId
     * @param dingCropId 钉钉企业id
     * @return left---》token right-> 异常码
     */
    public Pair<UserToken, PlatFormApiErrorEnum> createToken(String unionId, String dingCropId) {
        try {
            String url = plaformApiConfig.getDigitalStoreLoginApi();
            Map<String, Object> map = new HashMap<>();

            map.put("unionid", unionId);
            map.put("corp_id", dingCropId);
            map.put("login_type", "digital_store_login");
            String json = httpRestTemplateService.postForString(url, map);
            log.info("createToken返回参数, json{}", json);
            if (StringUtil.isEmpty(json)) {
                return null;
            }
            JSONObject jb = JSON.parseObject(json);
            String code = jb.getString("code");
            if (StringUtil.isNotEmpty(code) && !String.valueOf(ResponseCodeEnum.SUCCESS.getCode()).equals(code)) {
                return Pair.of(null, PlatFormApiErrorEnum.getByCode(jb.getString("code")));
            }
            String data = jb.getString("data");
            UserToken ut = JSON.parseObject(data, UserToken.class);
            return Pair.of(ut, null);
        } catch (Exception e) {
            log.error("PlatformApiProxy.createToken_has_exception:{}", unionId, e);
        }
        return null;
    }

    /**
     * 使用ticket换accessToken
     * @param tempCode
     * @return left---》token right-> 异常码
     */
    public Pair<UserToken, PlatFormApiErrorEnum> createThirdOaToken(String tempCode) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("temp_code", tempCode);
            map.put("login_type", "third_oa_encrypt_login");
            String json = httpRestTemplateService.postForString(oaLoginUrl, map);
            log.info("createThirdOaToken返回参数, json{}", json);
            if (StringUtil.isEmpty(json)) {
                return null;
            }
            JSONObject jb = JSON.parseObject(json);
            String code = jb.getString("code");
            if (StringUtil.isNotEmpty(code) && !String.valueOf(ResponseCodeEnum.SUCCESS.getCode()).equals(code)) {
                return Pair.of(null, PlatFormApiErrorEnum.THIRD_OA_LOGIN_ERROR);
            }
            String data = jb.getString("data");
            UserToken ut = JSON.parseObject(data, UserToken.class);
            ut.setExpire(3600 * 3);
            return Pair.of(ut, null);
        } catch (Exception e) {
            log.error("PlatformApiProxy.createThirdOaToken.tempCode:{}", tempCode, e);
        }
        return null;
    }

    /**
     * 获取酷学院企业管理员名称列表
     *
     * @param dingCropId 钉钉企业id
     * @return 企业管理员名称列表
     */
    public String getGetEnterpriseAdmin(String dingCropId) {
        try {
            String url = plaformApiConfig.getGetEnterpriseAdminApi();
            url = String.format(url, dingCropId);
            String jsonStr = httpRestTemplateService.getForString(url);
            if (StringUtil.isEmpty(jsonStr)) {
                return null;
            }
            PlatformApiResponse<String> response = JSON.parseObject(jsonStr, new TypeReference<PlatformApiResponse<String>>() {
            });
            return response.getData();
        } catch (Exception e) {
            log.error("PlatformApiProxy.getGetEnterpriseAdmin_has_exception:{}", dingCropId, e);
        }
        return null;
    }


    /**
     * 查询课程企业分类
     *
     * @param token
     * @return
     */
    public Pair<List<SysCourseClassify>, EnterpriseApiErrorEnum> getCourseClassify(String token) {
        try {
            String url = plaformApiConfig.getAdminClassifyGetTree();
            url = String.format(url, token);
            String json = httpRestTemplateService.getForString(url);
            JSONObject jb;
            List<SysCourseClassify> response = null;
            try {
                jb = JSON.parseObject(json);
                if (StringUtil.isNotEmpty(jb.getString("code"))) {
                    return Pair.of(null, EnterpriseApiErrorEnum.getByCode(jb.getString("code")));
                }
            }catch (Exception e){
                response = JSON.parseObject(json, new TypeReference<List<SysCourseClassify>>() {});
            }
            if (null != response && !response.isEmpty()) {
                return Pair.of(response, null);
            }
        } catch (Exception e) {
            log.error("PlatformApiProxy.getCourseClassify_has_exception:{} ", token, e);
            return null;
        }
        return null;
    }


    /**
     * 分页查询课程列表
     *
     * @param courseQuery
     * @return
     */
    public Pair<PageInfo<BizCourse>, EnterpriseApiErrorEnum> queryCourseByPage(BizCourseQuery courseQuery) {
        try {
            String url = plaformApiConfig.getQueryCourseByPage();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            builder.queryParam("access_token", courseQuery.getCoolToken());
            builder.queryParam("pageNumber", courseQuery.getPageNumber());
            builder.queryParam("pageSize", courseQuery.getPageSize());
            builder.queryParam("image_text", "all");
            builder.queryParam("type", "normal");
            builder.queryParam("classifyType", "all");
            if (null != courseQuery.getClassifyId()) {
                builder.queryParam("classifyId", courseQuery.getClassifyId());
            }
            url = builder.build().encode().toString();
            if (StringUtil.isNotEmpty(courseQuery.getTitle())) {
                url = url + "&title=" + courseQuery.getTitle();
            }
            log.info("postForObject start:url={}", url);
            String json = httpRestTemplateService.getForString(url);
            log.info("postForObject end:json={}", json);
            JSONObject jb = JSON.parseObject(json);
            if (StringUtil.isNotEmpty(jb.getString("code"))) {
                return Pair.of(null, EnterpriseApiErrorEnum.getByCode(jb.getString("code")));
            }
            PageInfo<BizCourse> response = JSON.parseObject(json, new TypeReference<PageInfo<BizCourse>>() {
            });
            return Pair.of(response, null);
        } catch (Exception e) {
            log.error("PlatformApiProxy.queryCourseByPage_has_exception:{} ", e);
            return null;
        }
    }

    /**
     * 分页查询课程列表（移动端专用）
     *
     * @param courseQuery
     * @return
     */
    public Pair<PageInfo<BizCourse>, EnterpriseApiErrorEnum> queryCourseByPageForApp(BizCourseQuery courseQuery) {
        try {
            String url = plaformApiConfig.getQueryCourseByPageForApp();
            // 酷学院自己的用户id和企业id，从登陆接口获取
            url = String.format(url, courseQuery.getEnterpriseId(), courseQuery.getUserId());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
            builder.queryParam("userId", courseQuery.getUserId());
            builder.queryParam("status", "all");
            builder.queryParam("type", "normal");
            builder.queryParam("access_token", courseQuery.getCoolToken());
            builder.queryParam("page_number", courseQuery.getPageNumber());
            builder.queryParam("page_size", courseQuery.getPageSize());
            if (null != courseQuery.getClassifyId()) {
                builder.queryParam("classify_id", courseQuery.getClassifyId());
            }
            url = builder.build().encode().toString();
            if (StringUtil.isNotEmpty(courseQuery.getTitle())) {
                url = url + "&keyword=" + courseQuery.getTitle();
            }
            log.info("postForObject start:url={}", url);
            String json = httpRestTemplateService.getForString(url);
            log.info("postForObject end:json={}", json);
            JSONObject jb = JSON.parseObject(json);
            if (StringUtil.isNotEmpty(jb.getString("code"))) {
                return Pair.of(null, EnterpriseApiErrorEnum.getByCode(jb.getString("code")));
            }
            PageInfo<BizCourse> response = JSON.parseObject(json, new TypeReference<PageInfo<BizCourse>>() {
            });
            return Pair.of(response, null);
        } catch (Exception e) {
            log.error("PlatformApiProxy.queryCourseByPageForApp_has_exception:{} ", e);
            return null;
        }
    }


    /**
     * 查询课程企业分类（移动端专用）
     *
     * @param token
     * @return
     */
    public Pair<List<SysCourseClassify>, EnterpriseApiErrorEnum> getCourseClassifyForApp(String enterpriseId, String token) {
        try {
            String url = plaformApiConfig.getAdminClassifyGetTreeForApp();
            url = String.format(url, enterpriseId, token);
            String json = httpRestTemplateService.getForString(url);
            log.info("postForObject end:json={}", json);
            JSONObject jb = JSON.parseObject(json);
            if (StringUtil.isNotEmpty(jb.getString("code"))) {
                return Pair.of(null, EnterpriseApiErrorEnum.getByCode(jb.getString("code")));
            }
            Map<String, List<SysCourseClassify>> response = JSON.parseObject(json, new TypeReference<Map<String, List<SysCourseClassify>>>() {
            });
            if (null != response && !response.isEmpty()) {
                return Pair.of(response.get("classify"), null);
            }
        } catch (Exception e) {
            log.error("PlatformApiProxy.getCourseClassifyForApp_has_exception:{} ", token, e);
        }
        return null;
    }

}
