package com.coolcollege.intelligent.rpc.license;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.facade.dto.openApi.OpenApiStoreLicenseRequestDTO;
import com.coolstore.base.dto.ResultDTO;
import com.coolstore.license.client.api.LicenseApi;
import com.coolstore.license.client.constants.CoolStoreLicenseConstants;
import com.coolstore.license.client.dto.LicenseDTO;
import com.coolstore.license.client.dto.LicenseQueryDTO;
import com.coolstore.license.client.dto.LicenseRequestDTO;
import com.coolstore.license.client.request.StoreLicenseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 证照rpc接口
 * @author byd
 */
@Slf4j
@Service
public class LicenseApiService {

    @SofaReference(uniqueId = CoolStoreLicenseConstants.LICENSE_FACADE_UNIQUE_ID, interfaceType = LicenseApi.class,
            binding = @SofaReferenceBinding(bindingType = CoolStoreLicenseConstants.SOFA_BINDING_TYPE))
    private LicenseApi licenseApi;

    @Resource
    private StoreMapper storeMapper;

    /**
     * 初始化企业证照信息
     * @param eid
     * @param userId
     */
    public void initLicense(String eid, String userId, String dbName) {
        LicenseDTO licenseDTO = new LicenseDTO();
        licenseDTO.setUserId(userId);
        licenseDTO.setEnterpriseId(eid);
        licenseDTO.setDbName(dbName);
        licenseApi.initLicense(licenseDTO);
    }

    public ResultDTO batchSaveStoreLicenseInstance(List<OpenApiStoreLicenseRequestDTO> requests,String dbName,String enterpriseId){
        LicenseRequestDTO licenseRequestDTO = new LicenseRequestDTO();
        licenseRequestDTO.setDbName(dbName);
        licenseRequestDTO.setEnterpriseId(enterpriseId);

        List<OpenApiStoreLicenseRequestDTO> requests1 = requests;
        String jsonString = JSON.toJSONString(requests1);
        List<OpenApiStoreLicenseRequestDTO> openApiStoreLicenseRequestDTOS = JSON.parseArray(jsonString, OpenApiStoreLicenseRequestDTO.class);

        List<StoreLicenseRequest> data = this.conver(openApiStoreLicenseRequestDTOS, enterpriseId);
        licenseRequestDTO.setRequests(data);
        licenseRequestDTO.setUserId(Constants.SYSTEM_USER_ID);
        try {
            licenseApi.batchSaveStoreLicenseInstance(licenseRequestDTO);
        }catch (Exception e){
            log.error("证照保存失败:{}",e.getMessage());
            return ResultDTO.failResult(e.getMessage());
        }
        return ResultDTO.successResult();
    }

    //OpenApiStoreLicenseRequestDTO转StoreLicenseRequest
    private List<StoreLicenseRequest> conver(List<OpenApiStoreLicenseRequestDTO> openApiStoreLicenseRequestDTOs,String enterpriseId){
        List<StoreLicenseRequest> list = Lists.newArrayList();
        openApiStoreLicenseRequestDTOs.stream().forEach(openApiStoreLicenseRequestDTO -> {
            StoreLicenseRequest storeLicenseRequest = new StoreLicenseRequest();
            storeLicenseRequest.setId(openApiStoreLicenseRequestDTO.getId());
            storeLicenseRequest.setStoreId(openApiStoreLicenseRequestDTO.getStoreId());
            storeLicenseRequest.setLicenseTypeId(openApiStoreLicenseRequestDTO.getLicenseTypeId());
            storeLicenseRequest.setPicture(openApiStoreLicenseRequestDTO.getPicture());
            storeLicenseRequest.setExtendFieldInfo(openApiStoreLicenseRequestDTO.getExtendFieldInfo());
            storeLicenseRequest.setExpiryType(openApiStoreLicenseRequestDTO.getExpiryType());
            storeLicenseRequest.setExpiryBeginDate(openApiStoreLicenseRequestDTO.getExpiryBeginDate());
            storeLicenseRequest.setExpiryEndDate(openApiStoreLicenseRequestDTO.getExpiryEndDate());
            String storeCode = openApiStoreLicenseRequestDTO.getStoreCode();
            //如果StoreCode不为空，根据storeCode查询storeId
            if (StringUtils.isNotBlank(storeCode)){
                //根据storeCode查询storeId(沪上)
                String storeId = storeMapper.getIdByHuShangCode(openApiStoreLicenseRequestDTO.getStoreCode(), enterpriseId);
                //如果为空返回错误信息
                if (storeId==null){
                    log.info("根据storeCode查询storeId为空:{}",openApiStoreLicenseRequestDTO.getStoreCode());
                    throw new ServiceException(ErrorCodeEnum.STORE_CODE_ERROR.getCode(),ErrorCodeEnum.STORE_CODE_ERROR.getMessage());
                }
                storeLicenseRequest.setStoreId(storeId);
            }
            list.add(storeLicenseRequest);
        });
        return list;
    }


    public List<LicenseDTO>  queryLicenseByQuery(LicenseQueryDTO query){
        log.info("queryLicenseByStoreId:{}",query);
        ResultDTO<List<LicenseDTO>> resultDTO = licenseApi.queryLicenseByQuery(query);
        if (resultDTO.isSuccess()){
            return resultDTO.getData();
        }
        return null;
    }

}
