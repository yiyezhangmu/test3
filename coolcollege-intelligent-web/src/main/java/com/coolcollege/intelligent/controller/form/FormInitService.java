package com.coolcollege.intelligent.controller.form;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.SysDepartmentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDepartmentDO;
import com.coolcollege.intelligent.model.unifytask.vo.TaskSubVO;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * describe: 请勿随意使用该service 全量库同步 表单到业务库
 *
 * @author zhouyiping
 * @date 2020/08/14
 */
@Service
@Slf4j
public class FormInitService {
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;
    @Resource
    private SysDepartmentMapper sysDepartmentMapper;
    @Resource
    private TaskSubMapper taskSubMapper;

    /**
     * EnterpriseConfigDO
     * @param enterpriseConfigDOList
     */
    public void initData(String db, List<EnterpriseConfigDO> enterpriseConfigDOList, AtomicInteger count, String type) {
        DataSourceHelper.changeToSpecificDataSource(db);
        try{
            for (EnterpriseConfigDO enterConfig : enterpriseConfigDOList) {
                switch (type){
                    case "taskTime":
                        batchUpdateTaskTime(enterConfig);
                        break;
                    default:
                        break;
                }
            }
        }catch (Exception e){
            log.info("#########initData error", e);
            log.info("#########initData fail db={}",db);
            throw new ServiceException(ErrorCodeEnum.FAIL);
        }
        count.getAndIncrement();
        log.info("#########initData success db={},AtomicInteger count={}",db,count);
    }

    public void batchUpdateTaskTime(EnterpriseConfigDO enterConfig) {
        String enterpriseId = enterConfig.getEnterpriseId();
        //查询所有任务
        List<TaskSubVO> taskList = taskSubMapper.selectAllInfo(enterpriseId);
        if (CollectionUtils.isNotEmpty(taskList)) {
            taskSubMapper.batchUpdateTaskTime(enterpriseId, taskList);
        }
    }

}
