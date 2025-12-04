package com.coolcollege.intelligent.model.safetycheck.dto;

import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author wxp
 * @Date 2023/9/11 15:05
 * @Version 1.0
 */
@Data
public class SafetyCheckCcUserDTO {

    List<GeneralDTO> afterHandleCcInfo;

    List<GeneralDTO> afterApproveCcInfo;

    List<GeneralDTO> appealResultCcInfo;

}
