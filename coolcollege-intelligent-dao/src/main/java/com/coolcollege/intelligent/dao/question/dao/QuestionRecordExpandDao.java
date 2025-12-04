package com.coolcollege.intelligent.dao.question.dao;

import com.coolcollege.intelligent.dao.question.TbQuestionRecordExpandMapper;
import com.coolcollege.intelligent.model.question.TbQuestionRecordExpandDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangchenbiao
 * @date 2021-12-20 07:18
 */
@Repository
public class QuestionRecordExpandDao {

    @Resource
    private TbQuestionRecordExpandMapper tbQuestionRecordExpandMapper;

    /**
     * 根据问题工单id列表查询
     * @param enterpriseId 企业id
     * @param questionRecordIds 问题工单id列表
     * @return List<TbQuestionRecordExpandDO>
     */
    public List<TbQuestionRecordExpandDO> selectByQuestionRecordIds(String enterpriseId, List<Long> questionRecordIds) {
        if(StringUtils.isBlank(enterpriseId) || CollectionUtils.isEmpty(questionRecordIds)) {
            return Lists.newArrayList();
        }
        return tbQuestionRecordExpandMapper.selectByQuestionRecordIds(enterpriseId, questionRecordIds);
    }

    public int insertSelective(String enterpriseId, TbQuestionRecordExpandDO recordExpandDO) {
        return tbQuestionRecordExpandMapper.insertSelective(recordExpandDO, enterpriseId);
    }

    public int updateByPrimaryKeySelective(String enterpriseId, TbQuestionRecordExpandDO recordExpandDO) {
        return tbQuestionRecordExpandMapper.updateByPrimaryKeySelective(recordExpandDO, enterpriseId);
    }

    public TbQuestionRecordExpandDO selectByRecordId(String enterpriseId, Long recordId) {
        return tbQuestionRecordExpandMapper.selectByRecordId(enterpriseId, recordId);
    }
}