package com.coolcollege.intelligent.dao.supervision.dao;

import com.coolcollege.intelligent.dao.supervision.SupervisionApproveMapper;
import com.coolcollege.intelligent.model.supervision.SupervisionApproveDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionApproveCountDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author byd
 * @date 2023-04-12 16:30
 */
@Repository
public class SupervisionApproveDao {

    @Resource
    private SupervisionApproveMapper supervisionApproveMapper;


    public int insertSelective(SupervisionApproveDO record,  String enterpriseId){
        return supervisionApproveMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-04-10 03:56
     */
    public SupervisionApproveDO selectByPrimaryKey(Long id,  String enterpriseId){
        return supervisionApproveMapper.selectByPrimaryKey(id,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-10 03:56
     */
    public int updateByPrimaryKeySelective(SupervisionApproveDO record,  String enterpriseId){
        return supervisionApproveMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-04-10 03:56
     */
    public int deleteByPrimaryKey(Long id,  String enterpriseId){
        return supervisionApproveMapper.deleteByPrimaryKey(id,enterpriseId);
    }



    public List<SupervisionApproveDO> selectByTaskIdList(String enterpriseId,
                                                         List<Long> taskIdList,
                                                         String type) {
        if (CollectionUtils.isEmpty(taskIdList)) {
            return new ArrayList<>();
        }
        return supervisionApproveMapper.selectByTaskIdList(enterpriseId, taskIdList, type);

    }

    /**
     * 批量新增数据
     * @param records
     * @param enterpriseId
     * @return
     */
    public int batchInsert(List<SupervisionApproveDO> records,  String enterpriseId){
        if (CollectionUtils.isEmpty(records)){
            return -1;
        }
        return supervisionApproveMapper.batchInsert(enterpriseId,records);
    }


    /**
     * 批量删除数据
     * @param enterpriseId
     * @param taskIdList
     * @param type
     * @return
     */
    public int batchDelete(String enterpriseId, List<Long> taskIdList, String type){
        if (CollectionUtils.isEmpty(taskIdList)|| StringUtils.isEmpty(type)){
            return -1;
        }
        return supervisionApproveMapper.batchDelete(enterpriseId,taskIdList,type);
    }

    public int selectApproveDataByUserId(String enterpriseId, String userId){
        if (StringUtils.isEmpty(userId)){
            return 0;
        }
        return supervisionApproveMapper.selectApproveDataByUserId(enterpriseId,userId);
    }

    public SupervisionApproveCountDTO getApproveCountByUserId(String enterpriseId, String userId,String taskName){
        if (StringUtils.isEmpty(userId)){
            return new SupervisionApproveCountDTO();
        }
        return supervisionApproveMapper.getApproveCountByUserId(enterpriseId,userId,taskName);
    }


    public List<SupervisionApproveDO> getSupervisionApproveData(String enterpriseId, String userId,String type,String taskName){
        if (StringUtils.isEmpty(userId)){
            return new ArrayList<>();
        }
        return supervisionApproveMapper.getSupervisionApproveData(enterpriseId,userId,type, taskName);
    }

    public SupervisionApproveDO getSupervisionApproveDataByTaskId(String enterpriseId, String userId, String type, Long taskId) {
        return supervisionApproveMapper.getSupervisionApproveDataByTaskId(enterpriseId, userId, type, taskId);
    }

    public int batchDeleteByTaskParentId(String enterpriseId,  List<Long> taskIds,String type,Long taskParentId){
        if (CollectionUtils.isEmpty(taskIds)&&taskParentId==null){
            return 0;
        }
        return supervisionApproveMapper.batchDeleteByTaskParentId(enterpriseId,taskIds,type,taskParentId);
    }
}
