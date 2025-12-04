package com.coolcollege.intelligent.dao.question.dao;

import com.coolcollege.intelligent.dao.question.TbQuestionParentInfoMapper;
import com.coolcollege.intelligent.model.question.TbQuestionParentInfoDO;
import com.coolcollege.intelligent.model.question.request.QuestionParentRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author byd
 * @date 2022-08-04 14:12
 */
@Repository
public class QuestionParentInfoDao {

    @Resource
    private TbQuestionParentInfoMapper questionParentInfoMapper;


    public List<TbQuestionParentInfoDO> list(String enterpriseId, QuestionParentRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return Lists.newArrayList();
        }
        return questionParentInfoMapper.list(enterpriseId, request);
    }

    public TbQuestionParentInfoDO selectById(String enterpriseId, Long id) {
        return questionParentInfoMapper.selectByPrimaryKey(enterpriseId, id);
    }

    public TbQuestionParentInfoDO selectByUnifyTaskId(String enterpriseId, Long unifyTaskId) {
        return questionParentInfoMapper.selectByUnifyTaskId(enterpriseId, unifyTaskId);
    }

    public void deleteById(String enterpriseId, Long id) {
        questionParentInfoMapper.deleteByPrimaryKey(enterpriseId, id);
    }

    public void insertSelective(String enterpriseId, TbQuestionParentInfoDO record) {
        questionParentInfoMapper.insertSelective(enterpriseId, record);
    }

    public List<TbQuestionParentInfoDO> selectByIdList(String enterpriseId, List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Lists.newArrayList();
        }
        return questionParentInfoMapper.selectByIdList(enterpriseId, idList);
    }

    public void addFinishNum(String enterpriseId, Long id) {
        questionParentInfoMapper.addFinishNum(enterpriseId, id);
    }


    public void updateByPrimaryKeySelective(String enterpriseId, TbQuestionParentInfoDO record) {
        questionParentInfoMapper.updateByPrimaryKeySelective(enterpriseId, record);
    }

    public Long questionListCount(String enterpriseId, QuestionParentRequest request) {
        if (StringUtils.isBlank(enterpriseId)) {
            return 0L;
        }
        return questionParentInfoMapper.questionListCount(enterpriseId, request);
    }

    public List<TbQuestionParentInfoDO> selectByUnifyTaskIds(String enterpriseId, List<Long> unifyTaskIds) {
        if (CollectionUtils.isEmpty(unifyTaskIds)){
            return Lists.newArrayList();
        }
        return questionParentInfoMapper.selectByUnifyTaskIds(enterpriseId, unifyTaskIds);
    }

    public Long selectCount(String enterpriseId, String userId, Boolean questionExpireHandle, Boolean questionExpireApprove, Integer status, Boolean isHandleUser) {
        return questionParentInfoMapper.selectCount(enterpriseId, userId, questionExpireHandle, questionExpireApprove, status, isHandleUser);
    }
}
