package com.coolcollege.intelligent.facade.inspection;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.inspection.InspectionDTO;
import com.coolcollege.intelligent.rpc.config.EnterpriseConfigApiService;
import com.coolcollege.intelligent.service.inspection.AiInspectionCapturePictureService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author byd
 * @date 2025-10-16 18:13
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.AI_INSPECTION_FACADE,
        interfaceType = AiInspectionFacade.class,
        bindings = {@SofaServiceBinding(bindingType = IntelligentFacadeConstants.SOFA_BINDING_TYPE)})
@Component
public class AiInspectionFacadeImpl implements AiInspectionFacade {

    @Autowired
    private AiInspectionCapturePictureService aiInspectionCapturePictureService;

    @Autowired
    private EnterpriseConfigApiService enterpriseConfigApiService;

    @Override
    public ResultDTO inspection(InspectionDTO inspectionDTO) {
        // 根据企业id切库
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(inspectionDTO.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        log.info("inspection#开始进行巡检,eid:{}", inspectionDTO.getEnterpriseId());
        aiInspectionCapturePictureService.capturePicture(inspectionDTO.getEnterpriseId(), inspectionDTO.getCaptureTime());
        return ResultDTO.successResult();
    }

    @Override
    public ResultDTO getInspectionResult(InspectionDTO inspectionDTO) {
        // 根据企业id切库
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(inspectionDTO.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        log.info("inspection#开始查询结果,eid:{}", inspectionDTO.getEnterpriseId());
        aiInspectionCapturePictureService.getInspectionResult(inspectionDTO.getEnterpriseId(), null);
        return ResultDTO.successResult();
    }

    @Override
    public ResultDTO queryDeviceCaptureResult(InspectionDTO inspectionDTO) {
        // 根据企业id切库
        DataSourceHelper.reset();
        String dbName = enterpriseConfigApiService.getEnterpriseDbName(inspectionDTO.getEnterpriseId());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        log.info("queryDeviceCaptureResult#开始查询结果,eid:{}", inspectionDTO.getEnterpriseId());
        aiInspectionCapturePictureService.queryDeviceCaptureResult(inspectionDTO.getEnterpriseId());
        return ResultDTO.successResult();
    }
}
