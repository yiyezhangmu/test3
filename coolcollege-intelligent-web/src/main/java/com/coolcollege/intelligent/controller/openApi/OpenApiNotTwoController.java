package com.coolcollege.intelligent.controller.openApi;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.EncryptUtil;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.convert.ConvertFactory;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.facade.UnifyTaskFcade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseUserQueryRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDetailUserVO;
import com.coolcollege.intelligent.model.openApi.dto.OpenApiSignDTO;
import com.coolcollege.intelligent.model.openApi.request.OpenApiBasePageRequest;
import com.coolcollege.intelligent.model.openApi.request.OpenApiRegionRequest;
import com.coolcollege.intelligent.model.openApi.request.OpenApiRequest;
import com.coolcollege.intelligent.model.openApi.request.OpenApiStoreRequest;
import com.coolcollege.intelligent.model.openApi.vo.OpenApiRegionChildVO;
import com.coolcollege.intelligent.model.openApi.vo.OpenApiRoleVO;
import com.coolcollege.intelligent.model.openApi.vo.OpenApiStoreVO;
import com.coolcollege.intelligent.model.openApi.vo.OpenApiUserListVO;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.question.QuestionRecordService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenyupeng
 * @since 2022/3/29
 */
@Slf4j
@RestController
@RequestMapping("/openApi/share/b2zg/")
public class OpenApiNotTwoController {

    @Autowired
    @Lazy
    private UnifyTaskFcade unifyTaskFcade;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private QuestionRecordService questionRecordService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private StoreService storeService;

    @PostMapping("/getUserList")
    public ResponseResult getUserList(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        EnterpriseUserQueryRequest enterpriseUserQueryRequest = JSONObject.parseObject(request.getBizContent(), EnterpriseUserQueryRequest.class);
        PageHelper.startPage(enterpriseUserQueryRequest.getPageNum(),enterpriseUserQueryRequest.getPageSize());
        List<EnterpriseUserDO> enterpriseUserDOList = enterpriseUserService.selectAllList(enterpriseConfigDO.getEnterpriseId());
        PageInfo pageInfo = new PageInfo(enterpriseUserDOList);
        List<OpenApiUserListVO> openApiUserListVOS = ListUtils.emptyIfNull(enterpriseUserDOList).stream().map(ConvertFactory::convertEnterpriseUserDO2OpenApiUserListVO).collect(Collectors.toList());
        pageInfo.setList(openApiUserListVOS);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    @PostMapping("/getUserByUserId")
    public ResponseResult getUserByUserId(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        EnterpriseUserQueryRequest enterpriseUserQueryRequest = JSONObject.parseObject(request.getBizContent(), EnterpriseUserQueryRequest.class);
        EnterpriseDetailUserVO fullDetail = enterpriseUserService.getFullDetail(enterpriseConfigDO.getEnterpriseId(), enterpriseUserQueryRequest.getUserId());
        return ResponseResult.success(ConvertFactory.convertEnterpriseDetailUserVO2OpenApiUserVO(fullDetail));
    }

    @PostMapping("/getCorpInfo")
    public ResponseResult getCorpInfo(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        EnterpriseDO enterpriseDO = enterpriseService.selectById(enterpriseConfigDO.getEnterpriseId());
        return ResponseResult.success(ConvertFactory.convertEnterpriseDO2OpenApiEnterpriseVO(enterpriseDO));
    }

    @PostMapping("/getRole")
    public ResponseResult getRole(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        OpenApiBasePageRequest openApiBasePageRequest = JSONObject.parseObject(request.getBizContent(), OpenApiBasePageRequest.class);
        PageHelper.startPage(openApiBasePageRequest.getPageNum(),openApiBasePageRequest.getPageSize());
        List<SysRoleDO> roleListPage = sysRoleMapper.getRoleListPage(enterpriseConfigDO.getEnterpriseId());
        PageInfo pageInfo = new PageInfo(roleListPage);
        List<OpenApiRoleVO> openApiRoleVOS = ListUtils.emptyIfNull(roleListPage).stream().map(ConvertFactory::convertSysRoleDO2OpenApiRoleVO).collect(Collectors.toList());
        pageInfo.setList(openApiRoleVOS);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    @PostMapping("/getRegionById")
    public ResponseResult getRegionById(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        OpenApiRegionRequest openApiRegionRequest = JSONObject.parseObject(request.getBizContent(), OpenApiRegionRequest.class);
        RegionNode regionNode = regionService.getRegionById(enterpriseConfigDO.getEnterpriseId(), openApiRegionRequest.getRegionId());
        return ResponseResult.success(ConvertFactory.convertRegionNode2OpenApiRegionVO(regionNode));
    }

    @PostMapping("/getRegionChildrenById")
    public ResponseResult getRegionChildrenById(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        OpenApiRegionRequest openApiRegionRequest = JSONObject.parseObject(request.getBizContent(), OpenApiRegionRequest.class);
        List<RegionChildDTO> regionChildDTOList = regionService.getRegionByParentId(enterpriseConfigDO.getEnterpriseId(),
                openApiSignDTO.getUserId(), openApiRegionRequest.getRegionId(), true, false, false, null, enterpriseConfigDO.getAppType(), null, null);
        List<OpenApiRegionChildVO> regionChildVOS = ListUtils.emptyIfNull(regionChildDTOList).stream().map(ConvertFactory::convertRegionChildDTO2OpenApiRegionVO).collect(Collectors.toList());
        return ResponseResult.success(regionChildVOS);
    }

    @PostMapping("/getStoreById")
    public ResponseResult getStoreById(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        OpenApiStoreRequest openApiStoreRequest = JSONObject.parseObject(request.getBizContent(), OpenApiStoreRequest.class);
        StoreDTO store = storeService.getStoreByStoreId(enterpriseConfigDO.getEnterpriseId(), openApiStoreRequest.getStoreId());
        return ResponseResult.success(ConvertFactory.convertStoreDTO2OpenApiStoreVO(store));
    }

    @PostMapping("/getStoreList")
    public ResponseResult getStoreList(@RequestBody OpenApiRequest request) {
        String b2gnData = EncryptUtil.getB2gnData(request.getSign());
        if(StringUtils.isEmpty(b2gnData)){
            return ResponseResult.fail(ErrorCodeEnum.AES_DECRYPT_FAIL);
        }
        OpenApiSignDTO openApiSignDTO = signDataDeal(b2gnData);
        if(checkCreateTime(openApiSignDTO.getCreateTime())){
            return ResponseResult.fail(ErrorCodeEnum.THIRD_TOKEN_EXPIRE);
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigService.selectByCorpId(openApiSignDTO.getCropId(),openApiSignDTO.getAppType());
        if(enterpriseConfigDO == null){
            return ResponseResult.fail(ErrorCodeEnum.ENTERPRISE_NOT_EXIST);
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        OpenApiBasePageRequest openApiBasePageRequest = JSONObject.parseObject(request.getBizContent(), OpenApiBasePageRequest.class);
        PageHelper.startPage(openApiBasePageRequest.getPageNum(),openApiBasePageRequest.getPageSize());
        List<StoreDO> aLlStoreList = storeService.getALlStoreList(enterpriseConfigDO.getEnterpriseId());
        PageInfo pageInfo = new PageInfo(aLlStoreList);
        List<OpenApiStoreVO> openApiStoreVOS = ListUtils.emptyIfNull(aLlStoreList).stream().map(ConvertFactory::convertStoreDO2OpenApiStoreVO).collect(Collectors.toList());
        pageInfo.setList(openApiStoreVOS);
        return ResponseResult.success(PageHelperUtil.getPageInfo(pageInfo));
    }

    public OpenApiSignDTO signDataDeal(String signData) {
        OpenApiSignDTO openApiSignDTO = new OpenApiSignDTO();
        if(StringUtils.isEmpty(signData)){
            return openApiSignDTO;
        }
        try {
            String[] split = signData.split("&");
            Field[] declaredFields = openApiSignDTO.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                for (int i = 0; i < split.length; i++) {
                    String[] property = split[i].split("=");
                    if(declaredField.getName().equals(property[0]) && StringUtils.isNotEmpty(property[1])){
                        declaredField.setAccessible(true);
                        declaredField.set(openApiSignDTO,property[1]);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return openApiSignDTO;
    }

    public boolean checkCreateTime(String createTime){
        if(StringUtils.isEmpty(createTime)){
            return true;
        }
        //10天
        long expireTime = 10 * 24 * 60 * 60 * 1000;
        long nowTime = new Date().getTime();
        return nowTime - Long.parseLong(createTime) > expireTime;
    }
}
