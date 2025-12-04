package com.coolcollege.intelligent.model.msg;

import com.coolcollege.intelligent.model.supervision.vo.SupervisionTaskVO;
import lombok.Data;

import java.util.List;

/**
 * @author wxp
 * @FileName: SupervisionTaskMessageDTO
 * @Description:
 * @date 2022-10-13 16:46
 */
@Data
public class SupervisionTaskMessageDTO {

    private String title;

    private String content;
    private Long supervisionTaskId;
    private List<String> handleUserIdList;
    private Integer businessType;
    private String taskName;
    private SupervisionTaskVO.HandleWay handleWay;
    private Integer taskState;
}
