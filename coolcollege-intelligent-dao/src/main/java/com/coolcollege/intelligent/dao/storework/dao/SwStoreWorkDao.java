package com.coolcollege.intelligent.dao.storework.dao;

import com.coolcollege.intelligent.dao.storework.SwStoreWorkMapper;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkSearchRequest;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class SwStoreWorkDao {
    @Resource
    SwStoreWorkMapper swStoreWorkMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(SwStoreWorkDO record, String enterpriseId){
        return swStoreWorkMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public SwStoreWorkDO selectByPrimaryKey(Long id, String enterpriseId){
        return swStoreWorkMapper.selectByPrimaryKey(enterpriseId, id);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(SwStoreWorkDO record, String enterpriseId){
        return swStoreWorkMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return swStoreWorkMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    /**
     * 查询当前时间范围内的任务
     * @param enterpriseId
     * @param currentDate
     * @param workCycle
     * @return
     */
    public List<SwStoreWorkDO> selectByTime(String enterpriseId, Date currentDate,Long storeWorkId, String workCycle){
        return swStoreWorkMapper.selectByTime(enterpriseId,currentDate,storeWorkId,workCycle);
    }

    public List<SwStoreWorkDO> list(String enterpriseId, StoreWorkSearchRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return swStoreWorkMapper.list(enterpriseId, request);
    }

    public void updateStatusByStoreWorkId(String enterpriseId, String workStatus, Long storeWorkId) {
        if (StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(workStatus) || storeWorkId == null) {
            return;
        }
        swStoreWorkMapper.updateStatusByStoreWorkId(enterpriseId, workStatus, storeWorkId);
    }

    public List<SwStoreWorkDO> listBystoreWorkIds(String enterpriseId, List<Long>  storeWorkIdList){
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(storeWorkIdList)) {
            return Collections.emptyList();
        }
        return  swStoreWorkMapper.listBystoreWorkIds(enterpriseId, storeWorkIdList);
    }


    public List<SwStoreWorkDO> selectAllPersonInfo(String enterpriseId) {
        if(StringUtils.isBlank(enterpriseId)) {
            return Collections.emptyList();
        }
        return  swStoreWorkMapper.selectAllPersonInfo(enterpriseId);
    }

    public List<SwStoreWorkDO> selectListByWorkCycle(String enterpriseId, String workCycle,String swWorkId) {
        return swStoreWorkMapper.selectListByWorkCycle(enterpriseId,workCycle,swWorkId);
    }
}