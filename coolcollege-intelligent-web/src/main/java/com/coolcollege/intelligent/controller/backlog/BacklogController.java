package com.coolcollege.intelligent.controller.backlog;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.service.backlog.BacklogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 邵凌志
 * @date 2021/3/1 17:24
 */
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/backlog"})
@BaseResponse
@Slf4j
public class BacklogController {

    @Autowired
    private BacklogService backlogService;

    /**
     * 更新待办消息状态
     *
     * @param eid
     * @param backlogId
     * @return
     */
    @GetMapping("/update")
    public Boolean updateBacklogStatus(@PathVariable(value = "enterprise-id") String eid,
                                       String backlogId) {
        return backlogService.updateBacklogStatus(eid, backlogId);
    }
}
