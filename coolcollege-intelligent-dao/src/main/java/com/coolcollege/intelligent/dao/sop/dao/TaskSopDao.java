package com.coolcollege.intelligent.dao.sop.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.sop.TaskSopMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author zhangnan
 * @date 2022-06-28 10:49
 */
@Repository
public class TaskSopDao {

    @Resource
    private TaskSopMapper taskSopMapper;


    /**
     * 查询sop数量
     * @param enterpriseId
     * @return
     */
    public Integer count(String enterpriseId){
        if(StringUtils.isBlank(enterpriseId)) {
            return Constants.ZERO;
        }
        return taskSopMapper.count(enterpriseId);
    }

}
