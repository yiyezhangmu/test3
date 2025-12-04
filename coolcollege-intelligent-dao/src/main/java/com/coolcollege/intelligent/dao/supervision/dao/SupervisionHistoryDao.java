package com.coolcollege.intelligent.dao.supervision.dao;

import com.coolcollege.intelligent.dao.supervision.SupervisionHistoryMapper;
import com.coolcollege.intelligent.model.supervision.SupervisionHistoryDO;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author byd
 * @date 2023-04-12 16:30
 */
@Repository
public class SupervisionHistoryDao {

    @Resource
    private SupervisionHistoryMapper supervisionHistoryMapper;

    
    
    public int insertSelective(SupervisionHistoryDO record,  String enterpriseId){
        return supervisionHistoryMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-04-10 03:56
     */
    public SupervisionHistoryDO selectByPrimaryKey(Long id,  String enterpriseId){
        return supervisionHistoryMapper.selectByPrimaryKey(id,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-10 03:56
     */
    public int updateByPrimaryKeySelective(SupervisionHistoryDO record,  String enterpriseId){
        return supervisionHistoryMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-04-10 03:56
     */
    public int deleteByPrimaryKey(Long id,  String enterpriseId){
        return supervisionHistoryMapper.deleteByPrimaryKey(id,enterpriseId);
    }


    public int batchInsert(String enterpriseId,  List<SupervisionHistoryDO> supervisionHistoryDOS){
        if (CollectionUtils.isEmpty(supervisionHistoryDOS)){
            return 0;
        }
        return supervisionHistoryMapper.batchInsert(enterpriseId,supervisionHistoryDOS);
    }


    public List<SupervisionHistoryDO> selectByTaskIdAndType(String enterpriseId, Long taskId, String type, Boolean onlyQueryReject){
        if ( StringUtils.isEmpty(type)&&taskId==null){
            return Collections.emptyList();
        }
        return supervisionHistoryMapper.selectByTaskIdAndType(enterpriseId,taskId,type,onlyQueryReject);
    }
}
