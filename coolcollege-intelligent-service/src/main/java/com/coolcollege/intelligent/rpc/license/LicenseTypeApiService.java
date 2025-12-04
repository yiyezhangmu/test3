package com.coolcollege.intelligent.rpc.license;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolstore.base.dto.ResultDTO;
import com.coolstore.license.client.api.LicenseTypeApi;
import com.coolstore.license.client.constants.CoolStoreLicenseConstants;
import com.coolstore.license.client.dto.LcLicenseTypeExtendFieldDTO;
import com.coolstore.license.client.dto.LicenseTypeDTO;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangnan
 * @date 2021-12-08 18:56
 */
@Service
public class LicenseTypeApiService {

    @SofaReference(uniqueId = CoolStoreLicenseConstants.LICENSE_TYPE_FACADE_UNIQUE_ID, interfaceType = LicenseTypeApi.class,
            binding = @SofaReferenceBinding(bindingType = "bolt"))
    private LicenseTypeApi licenseTypeApi;

    /**
     * 获取门店证照水印map
     * @param configDO 企业
     * @param typeIds 证照类型id列表
     * @return Map<Long, String>
     */
    public Map<Long, String> getStoreLicenseWaterMarkMap(EnterpriseConfigDO configDO, List<Long> typeIds) {
        if(Objects.isNull(configDO)) {
            return Maps.newHashMap();
        }
        ResultDTO<List<LicenseTypeDTO>> result = licenseTypeApi.getStoreLicenseTypesByTypeIds(configDO.getEnterpriseId(), configDO.getDbName() , typeIds);
        if(!result.isSuccess()) {
            return Maps.newHashMap();
        }
        return result.getData().stream().collect(Collectors.toMap(LicenseTypeDTO::getLicenseTypeId, LicenseTypeDTO::getWaterMark));
    }

    /**
     * 通过类型获取证照类型列表()
     * @param configDO 企业
     * @param source  store||user
     */
    public List<LicenseTypeDTO> getStoreLicenseTypesBySourceOrId(EnterpriseConfigDO configDO, String source,String id) {
        if (Objects.isNull(configDO)) {
            return Lists.newArrayList();
        }
        ResultDTO<List<LicenseTypeDTO>> result = licenseTypeApi.getStoreLicenseTypesBySourceOrId(configDO.getEnterpriseId(), configDO.getDbName(), source,id);
        if (!result.isSuccess()) {
            return Lists.newArrayList();
        }
        return result.getData();
    }

    /**
     * 获取证照类型详情
     * @param configDO 企业
     * @param licenseTypeId 证照类型id
     * @return List<LcLicenseTypeExtendFieldDTO>
     */
    public List<LcLicenseTypeExtendFieldDTO> getLicenseTypeDetail(EnterpriseConfigDO configDO, Long licenseTypeId) {
        if (Objects.isNull(configDO)) {
            return Lists.newArrayList();
        }
        ResultDTO<List<LcLicenseTypeExtendFieldDTO>> result = licenseTypeApi.getLicenseTypeDetail(configDO.getEnterpriseId(), configDO.getDbName(), licenseTypeId);
        if (!result.isSuccess()) {
            return Lists.newArrayList();
        }
        return result.getData();
    }
}
