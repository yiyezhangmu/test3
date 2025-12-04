package com.coolcollege.intelligent.model.workHandover.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author   zhangchenbiao
 * @date   2022-11-16 11:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkHandoverRequest implements Serializable {


    @NotBlank(message = "转交人id不能为空")
    @ApiModelProperty("移交人id")
    private String transferUserId;

    @NotBlank(message = "交接人id不能为空")
    @ApiModelProperty("交接人id")
    private String handoverUserId;

    @NotEmpty(message = "交接内容不能为空")
    @ApiModelProperty("交接内容    PATROL_STORE_ONLINE(\"PATROL_STORE_ONLINE\", \"线上巡店任务\"),\n" +
            "    PATROL_STORE_OFFLINE(\"PATROL_STORE_OFFLINE\", \"线下巡店任务\"),\n" +
            "    PATROL_STORE_PLAN(\"PATROL_STORE_PLAN\", \"巡店计划\"),\n" +
            "    QUESTION_ORDER(\"QUESTION_ORDER\", \"工单\"),\n" +
            "    DISPLAY_TASK(\"TB_DISPLAY_TASK\", \"陈列任务\"),\n" +
            "    STORE_WORK(\"STORE_WORK\", \"店务\"),")
    private List<String> handoverContentList;
}