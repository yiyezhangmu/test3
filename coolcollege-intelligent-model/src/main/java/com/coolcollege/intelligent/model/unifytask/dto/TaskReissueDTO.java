package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * describe: 任务补发DTO
 *
 * @author wangff
 * @date 2025/1/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskReissueDTO {
    /**
     * 新增用户id列表
     */
    private List<String> newAddPersonList;
    /**
     * 移除用户id列表
     */
    private List<String> removePersonList;
    /**
     * 刷新策略
     */
    private String refreshStrategy;
    /**
     * 最新子任务
     */
    private TaskSubVO latestTaskSub;
    /**
     * 处理人id集合
     */
    private Set<String> handlerUserSet;
    /**
     * 新门店任务扩展信息
     */
    private String newExtendInfoStr;
    /**
     * 新抄送人id集合
     */
    private String newCcUserIdsStr;
    /**
     * 新增抄送人id集合
     */
    private List<String> addCcUserIds;
}
