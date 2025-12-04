package com.coolcollege.intelligent.controller.coolrelation;

import com.coolcollege.intelligent.common.enums.EnterpriseApiErrorEnum;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.PlatFormApiErrorEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.coolrelation.dto.AdvertDTO;
import com.coolcollege.intelligent.model.coolrelation.dto.CoolInfoDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.platform.BizCourse;
import com.coolcollege.intelligent.model.platform.BizCourseQuery;
import com.coolcollege.intelligent.model.platform.SysCourseClassify;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.coolrelation.CoolCollegeRelationService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.proxy.PlatformApiProxy;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2021/5/8 17:28
 */
@RestController
@RequestMapping("/coolRelation")
@Slf4j
public class CoolCollegeRelationController {

    @Autowired
    private CoolCollegeRelationService coolCollegeRelationService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private PlatformApiProxy platformApiProxy;

    @Resource
    private RedisUtilPool redis;

    /**
     * 判断该企业是否需要展示广告页
     * @param enterpriseId
     * @param platFormType 平台类型：app, pc
     * @return
     */
    @GetMapping("/{enterpriseId}/getAdvertSetting")
    public ResponseResult getAdvertSetting(@PathVariable(value = "enterpriseId") String enterpriseId,
                                           @RequestParam(value = "platFormType") String platFormType,
                                           @RequestParam(value = "type", required = false) String type) {
        return ResponseResult.success(coolCollegeRelationService.getAdvertSetting(enterpriseId, platFormType, type));
    }

    /**
     * 设置企业不展示广告页
     * @param enterpriseId
     * @param advertDTO
     * @return
     */
    @PostMapping("/{enterpriseId}/setAdvertSetting")
    public ResponseResult setAdvertSetting(@PathVariable(value = "enterpriseId") String enterpriseId,
                                           @RequestBody @Valid AdvertDTO advertDTO) {
        coolCollegeRelationService.setAdvertSetting(enterpriseId, advertDTO);
        return ResponseResult.success(true);
    }

    /**
     * 用户鉴权，校验该用户对应的企业是否开通酷学院，以及该用户是否存在在酷学院组织架构中
     * @param enterpriseId
     * @param userId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/13 16:26
     */
    @GetMapping("/{enterpriseId}/userAuthentication")
    public ResponseResult userAuthentication(@PathVariable(value = "enterpriseId") String enterpriseId,
                                             @RequestParam(value = "userId") String userId) {
        DataSourceHelper.reset();
//        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
//        String cropId = enterpriseConfigDO.getDingCorpId();
        return ResponseResult.success(true);
    }

    /**
     * 用户鉴权，校验该用户对应的企业是否开通酷学院，以及该用户是否存在在酷学院组织架构中
     * @param enterpriseId
     * @param userId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/13 16:26
     */
    @GetMapping("/{enterpriseId}/getCoolToken")
    public ResponseResult getCoolToken(@PathVariable(value = "enterpriseId") String enterpriseId,
                                             @RequestParam(value = "userId") String userId) {
        //从平台库获取企业corpId
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        String corpId = enterpriseConfigDO.getDingCorpId();
        Pair<CoolInfoDTO, PlatFormApiErrorEnum> pair = coolCollegeRelationService.getCoolToken(enterpriseId, corpId, userId, enterpriseConfigDO.getAppType());
        if (pair == null || (pair.getKey() == null && pair.getValue() == null)) {
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_ERROR.getCode(), "鉴权失败");
        }
        if (pair.getKey() == null) {
            //如果是调用thirdOa失败，直接返回
            if (PlatFormApiErrorEnum.THIRD_OA_LOGIN_ERROR.equals(pair.getValue())) {
                return ResponseResult.fail(Integer.getInteger(PlatFormApiErrorEnum.THIRD_OA_LOGIN_ERROR.getCode()),
                        PlatFormApiErrorEnum.THIRD_OA_LOGIN_ERROR.getMsg(), pair.getValue().getMsg());
            }
            boolean isEnterpriseError = PlatFormApiErrorEnum.isEnterpriseError(pair.getValue());
            int code = isEnterpriseError ? ErrorCodeEnum.ENTERPRISE_ERROR.getCode() : ErrorCodeEnum.USER_ERROR.getCode();
            String msg = isEnterpriseError ? "" : platformApiProxy.getGetEnterpriseAdmin(corpId);
            return ResponseResult.fail(code, msg, pair.getValue().getMsg());
        } else {
            return ResponseResult.success(pair.getKey());
        }
    }

    /**
     * 获取pc端酷学院知识库
     * @param courseQuery
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/13 16:26
     */
    @PostMapping("/queryCourseByPage")
    public ResponseResult queryCourseByPage(@RequestBody BizCourseQuery courseQuery) {
        Pair<PageInfo<BizCourse>, EnterpriseApiErrorEnum> pair = platformApiProxy.queryCourseByPage(courseQuery);

        EnterpriseApiErrorEnum enterpriseApiErrorEnum = pair == null ? null : pair.getValue();
        String token = coolCollegeRelationService.checkAndRefreshToken(enterpriseApiErrorEnum,UserHolder.getUser());
        if(token != null){
            courseQuery.setCoolToken(token);
            pair = platformApiProxy.queryCourseByPage(courseQuery);
        }

        if (pair == null) {
            return ResponseResult.success(pair);
        }
        if (pair.getKey() != null) {
            return ResponseResult.success(pair.getKey());
        }
        if (pair.getValue() != null) {
            return ResponseResult.fail(ErrorCodeEnum.GET_COURSE_ERROR.getCode(), pair.getValue().getMsg());
        }
        return ResponseResult.success(null);
    }

    /**
     * 获取pc端酷学院知识库分类
     * @param coolToken
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/13 16:26
     */
    @GetMapping("/getCourseClassify")
    public ResponseResult getCourseClassify(@RequestParam(value = "coolToken") String coolToken) {

        Pair<List<SysCourseClassify>, EnterpriseApiErrorEnum> pair = platformApiProxy.getCourseClassify(coolToken);

        EnterpriseApiErrorEnum enterpriseApiErrorEnum = pair == null ? null : pair.getValue();
        String token = coolCollegeRelationService.checkAndRefreshToken(enterpriseApiErrorEnum,UserHolder.getUser());
        if(token != null){
            pair = platformApiProxy.getCourseClassify(token);
        }
        if (pair == null) {
            return ResponseResult.success(pair);
        }
        if (pair.getKey() != null) {
            return ResponseResult.success(pair.getKey());
        }
        if (pair.getValue() != null) {
            return ResponseResult.fail(ErrorCodeEnum.GET_COURSE_ERROR.getCode(), pair.getValue().getMsg());
        }
        return ResponseResult.success(null);
    }

    /**
     * 获取APP端酷学院知识库
     * @param courseQuery
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/13 16:26
     */
    @PostMapping("/queryCourseByPageForApp")
    public ResponseResult queryCourseByPageForApp(@RequestBody BizCourseQuery courseQuery) {
        Pair<PageInfo<BizCourse>, EnterpriseApiErrorEnum> pair = platformApiProxy.queryCourseByPageForApp(courseQuery);

        EnterpriseApiErrorEnum enterpriseApiErrorEnum = pair == null ? null : pair.getValue();
        String token = coolCollegeRelationService.checkAndRefreshToken(enterpriseApiErrorEnum,UserHolder.getUser());
        if(token != null){
            courseQuery.setCoolToken(token);
            pair = platformApiProxy.queryCourseByPageForApp(courseQuery);
        }
        if (pair == null) {
            return ResponseResult.success(pair);
        }
        if (pair.getKey() != null) {
            return ResponseResult.success(pair.getKey());
        }
        if (pair.getValue() != null) {
            return ResponseResult.fail(ErrorCodeEnum.GET_COURSE_ERROR.getCode(), pair.getValue().getMsg());
        }
        return ResponseResult.success(null);
    }

    /**
     * 获取APP端酷学院知识库分类
     * @param enterpriseId 酷学院企业id
     * @param coolToken
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/5/13 16:26
     */
    @GetMapping("/getCourseClassifyForApp")
    public ResponseResult getCourseClassifyForApp(@RequestParam(value = "enterpriseId") String enterpriseId,
                                                  @RequestParam(value = "coolToken") String coolToken) {
        Pair<List<SysCourseClassify>, EnterpriseApiErrorEnum> pair = platformApiProxy.getCourseClassifyForApp(enterpriseId, coolToken);

        EnterpriseApiErrorEnum enterpriseApiErrorEnum = pair == null ? null : pair.getValue();
        String token = coolCollegeRelationService.checkAndRefreshToken(enterpriseApiErrorEnum,UserHolder.getUser());
        if(token != null){
            pair = platformApiProxy.getCourseClassifyForApp(enterpriseId, token);
        }

        if (pair == null) {
            return ResponseResult.success(pair);
        }
        if (pair.getKey() != null) {
            return ResponseResult.success(pair.getKey());
        }
        if (pair.getValue() != null) {
            return ResponseResult.fail(ErrorCodeEnum.GET_COURSE_ERROR.getCode(), pair.getValue().getMsg());
        }
        return ResponseResult.success(null);
    }


}
