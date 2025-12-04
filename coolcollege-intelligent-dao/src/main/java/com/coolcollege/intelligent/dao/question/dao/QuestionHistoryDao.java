package com.coolcollege.intelligent.dao.question.dao;

import com.coolcollege.intelligent.dao.question.TbQuestionHistoryMapper;
import com.coolcollege.intelligent.model.question.TbQuestionHistoryDO;
import com.coolcollege.intelligent.model.question.vo.TbQuestionHistoryVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 问题工单历史记录
 * @author byd
 */
@Repository
public class QuestionHistoryDao {

    @Resource
    private TbQuestionHistoryMapper questionHistoryMapper;

    public void insert(String enterpriseId, TbQuestionHistoryDO historyDO){
        questionHistoryMapper.insertSelective(historyDO, enterpriseId);
    }

    public TbQuestionHistoryDO selectByPrimaryKey(String enterpriseId, Long historyId){
        return questionHistoryMapper.selectByPrimaryKey(historyId, enterpriseId);
    }

    public int updateByPrimaryKeySelective(String enterpriseId, TbQuestionHistoryDO record){
        return questionHistoryMapper.updateByPrimaryKeySelective(record, enterpriseId);
    }

    public List<TbQuestionHistoryVO> selectHistoryList(Long recordId, String enterpriseId){
        return questionHistoryMapper.selectHistoryList(recordId, enterpriseId);
    }

    public List<TbQuestionHistoryVO> selectHistoryListByRecordIdList(String enterpriseId, List<Long> recordIdList,Integer nodeNo){
        if(CollectionUtils.isEmpty(recordIdList)){
            return new ArrayList<>();
        }
        return questionHistoryMapper.selectHistoryListByRecordIdList(recordIdList, enterpriseId,nodeNo);
    }

    public List<TbQuestionHistoryVO> selectLatestHistoryListByRecordIdList(String enterpriseId, List<Long> recordIdList, String nodeNo){
        if(CollectionUtils.isEmpty(recordIdList)){
            return new ArrayList<>();
        }
        List<Long> idList = questionHistoryMapper.selectMaxIdByRecordIdList(recordIdList, enterpriseId, nodeNo);
        if(CollectionUtils.isEmpty(idList)){
            return new ArrayList<>();
        }
        return questionHistoryMapper.selectLatestHistoryListByIdList(idList, enterpriseId);
    }

    public TbQuestionHistoryDO selectLatestHistoryListByRecordId(String enterpriseId, Long recordId, String nodeNo){
        if(Objects.isNull(recordId) || StringUtils.isAnyBlank(enterpriseId, nodeNo)){
            return new TbQuestionHistoryDO();
        }
        return questionHistoryMapper.selectLatestHistoryListByRecordId(enterpriseId, recordId, nodeNo);
    }
}
