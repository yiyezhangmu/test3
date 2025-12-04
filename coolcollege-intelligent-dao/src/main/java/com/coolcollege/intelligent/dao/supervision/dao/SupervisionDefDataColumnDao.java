package com.coolcollege.intelligent.dao.supervision.dao;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.supervision.SupervisionDefDataColumnMapper;
import com.coolcollege.intelligent.model.supervision.SupervisionDefDataColumnDO;
import com.coolcollege.intelligent.model.supervision.SupervisionTaskDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 17:15
 * @Version 1.0
 */
@Repository
public class SupervisionDefDataColumnDao {

    @Resource
    SupervisionDefDataColumnMapper supervisionDefDataColumnMapper;


    public int insertSelective(SupervisionDefDataColumnDO record, String enterpriseId){
        return supervisionDefDataColumnMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2023-02-27 03:03
     */
    public SupervisionDefDataColumnDO selectByPrimaryKey(Long id, String enterpriseId){
        return supervisionDefDataColumnMapper.selectByPrimaryKey(id,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-02-27 03:03
     */
    public int updateByPrimaryKeySelective(SupervisionDefDataColumnDO record,  String enterpriseId){
        return supervisionDefDataColumnMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2023-02-27 03:03
     */
    public int deleteByPrimaryKey(Long id,  String enterpriseId){
        return supervisionDefDataColumnMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public List<SupervisionDefDataColumnDTO> getDataColumnListByTaskIdAndType(String enterpriseId,  List<Long> taskIds, String type){
        return supervisionDefDataColumnMapper.getDataColumnListByTaskIdAndType(enterpriseId,taskIds,type);
    }

    public int batchInsert( String enterpriseId, List<SupervisionDefDataColumnDO> records){
        if (CollectionUtils.isEmpty(records)){
            return Constants.INDEX_ZERO;
        }
        return supervisionDefDataColumnMapper.batchInsert(enterpriseId,records);
    }

    public int batchUpdate( String enterpriseId, List<SupervisionDefDataColumnDO> records){
        if (CollectionUtils.isEmpty(records)){
            return Constants.INDEX_ZERO;
        }
        return supervisionDefDataColumnMapper.batchUpdate(enterpriseId,records);
    }


}
