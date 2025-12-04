package com.coolcollege.intelligent.service.unifytask.resolve;

import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.service.unifytask.resolve.impl.*;
import com.coolstore.base.utils.CommonContextUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: TaskResolveFactory
 * @Description:任务分解工厂类
 * @date 2025-01-08 17:43
 */
@Configuration
@DependsOn("commonContextUtil")
public class TaskResolveFactory {

    private final Map<TaskTypeEnum, ITaskResolve> taskResolveMap = new HashMap<>();

    @PostConstruct
    public void initTaskResolveFactory() {
        taskResolveMap.put(TaskTypeEnum.TB_DISPLAY_TASK, CommonContextUtil.getBean(DisplayTaskResolveImpl.class));
        taskResolveMap.put(TaskTypeEnum.QUESTION_ORDER, CommonContextUtil.getBean(QuestionOrderTaskResolveImpl.class));
        taskResolveMap.put(TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE, CommonContextUtil.getBean(AchievementTaskResolveImpl.class));
        taskResolveMap.put(TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF, CommonContextUtil.getBean(AchievementTaskResolveImpl.class));
        taskResolveMap.put(TaskTypeEnum.PATROL_STORE_INFORMATION, CommonContextUtil.getBean(PatrolStoreInformationTaskResolveImpl.class));
    }

    public ITaskResolve getTaskResolve(TaskTypeEnum taskType) {
        return taskResolveMap.getOrDefault(taskType, CommonContextUtil.getBean(PatrolStoreTaskResolveImpl.class));
    }

}
