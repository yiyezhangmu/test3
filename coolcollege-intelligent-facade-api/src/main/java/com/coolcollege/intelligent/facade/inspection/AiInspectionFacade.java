package com.coolcollege.intelligent.facade.inspection;

import com.coolcollege.intelligent.facade.dto.inspection.InspectionDTO;
import com.coolstore.base.dto.ResultDTO;

/**
 * @author byd
 * @date 2025-10-16 18:13
 */
public interface AiInspectionFacade {

    ResultDTO inspection(InspectionDTO inspectionDTO);

    ResultDTO getInspectionResult(InspectionDTO inspectionDTO);

    ResultDTO queryDeviceCaptureResult(InspectionDTO inspectionDTO);
}
