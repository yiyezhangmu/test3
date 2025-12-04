package com.coolcollege.intelligent.dao.safetycheck.dao;

import com.coolcollege.intelligent.dao.safetycheck.TbDataColumnCommentMapper;
import com.coolcollege.intelligent.model.safetycheck.TbDataColumnCommentDO;
import com.coolcollege.intelligent.model.safetycheck.vo.DataColumnHasHistoryVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wxp
 * @date 2022-09-08 02:01
 */
@Repository
public class TbDataColumnCommentDao {

    @Resource
    TbDataColumnCommentMapper tbDataColumnCommentMapper;
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2022-09-08 02:01
     */
    public int insertSelective(TbDataColumnCommentDO record, String enterpriseId){
        return tbDataColumnCommentMapper.insertSelective(record,enterpriseId);
    }

    /**
     *
     * 默认查询方法，通过主键获取所有字段的值
     * dateTime:2022-09-08 02:01
     */
    public TbDataColumnCommentDO selectByPrimaryKey(Long id, String enterpriseId){
        return tbDataColumnCommentMapper.selectByPrimaryKey(id, enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2022-09-08 02:01
     */
    public int updateByPrimaryKeySelective(TbDataColumnCommentDO record, String enterpriseId){
        return tbDataColumnCommentMapper.updateByPrimaryKeySelective(record,enterpriseId);
    }

    /**
     *
     * 默认更新方法，根据主键物理删除
     * dateTime:2022-09-08 02:01
     */
    public int deleteByPrimaryKey(Long id, String enterpriseId){
        return tbDataColumnCommentMapper.deleteByPrimaryKey(id,enterpriseId);
    }

    public List<TbDataColumnCommentDO> listDataColumnCommentHistory(String enterpriseId, Long businessId, Long dataColumnId){
        if (businessId == null || dataColumnId == null){
            return Lists.newArrayList();
        }
        return tbDataColumnCommentMapper.listDataColumnCommentHistory(enterpriseId, businessId, dataColumnId);
    }

    public List<TbDataColumnCommentDO> getLatestComment(String enterpriseId, Long businessId){
        if (businessId == null){
            return null;
        }
        return tbDataColumnCommentMapper.getLatestComment(enterpriseId, businessId);
    }

    public int batchInsert(String enterpriseId, List<TbDataColumnCommentDO> list){
        if (CollectionUtils.isEmpty(list)){
            return 0;
        }
        return tbDataColumnCommentMapper.batchInsert(enterpriseId,list);
    }

    /**
     * 获取每项的点评数量
     * @param enterpriseId
     * @param businessId
     * @return
     */
    public List<DataColumnHasHistoryVO> getCommentCount(String enterpriseId, Long businessId){
        return tbDataColumnCommentMapper.getCommentCount(enterpriseId,businessId);
    }

    public int updateDelByBusinessIds(String enterpriseId, List<Long> businessIds){
        if (CollectionUtils.isEmpty(businessIds)){
            return 0;
        }
        return tbDataColumnCommentMapper.updateDelByBusinessIds(enterpriseId, businessIds);
    }


}