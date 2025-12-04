package com.coolcollege.intelligent.dao.question.dao;

import com.coolcollege.intelligent.dao.question.TbQuestionParentUserMappingMapper;
import com.coolcollege.intelligent.model.question.TbQuestionParentUserMappingDO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author byd
 * @date 2022-08-16 15:52
 */
@Repository
public class QuestionParentUserMappingDao {

    @Resource
    private TbQuestionParentUserMappingMapper questionParentUserMappingMapper;

    public void insert(String enterpriseId,
                               TbQuestionParentUserMappingDO record) {
        questionParentUserMappingMapper.insert(enterpriseId, record);
    }

    public void update(String enterpriseId,
                       TbQuestionParentUserMappingDO record) {
        questionParentUserMappingMapper.update(enterpriseId, record);
    }


    public List<TbQuestionParentUserMappingDO> list(String enterpriseId, String handleUserId, String questionParentName,
                                                    Boolean isHandleUser, Boolean isCcUser, Integer status, Boolean questionExpireHandle, Boolean questionExpireApprove) {
        return questionParentUserMappingMapper.list(enterpriseId, handleUserId, questionParentName, isHandleUser, isCcUser, status,
                questionExpireHandle, questionExpireApprove);
    }

    public void deleteByUnifyTaskId(String enterpriseId,
                               Long unifyTaskId) {
        questionParentUserMappingMapper.deleteByUnifyTaskId(enterpriseId, unifyTaskId);
    }

    public TbQuestionParentUserMappingDO selectByUnifyTaskIdAndUerId(String enterpriseId,
                       Long unifyTaskId, String userId) {
        return questionParentUserMappingMapper.selectByUnifyTaskIdAndUerId(enterpriseId, unifyTaskId, userId);
    }

    public TbQuestionParentUserMappingDO selectByQuestionParentIdAndUerId(String enterpriseId,
                                                                     Long questionParentId, String userId) {
        return questionParentUserMappingMapper.selectByQuestionParentIdAndUerId(enterpriseId, questionParentId, userId);
    }

    public List<TbQuestionParentUserMappingDO> selectByQuestionParentByUnifyTaskId(String enterpriseId, Long unifyTaskId) {
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(unifyTaskId)){
            return Lists.newArrayList();
        }
        return questionParentUserMappingMapper.selectByQuestionParentByUnifyTaskId(enterpriseId, unifyTaskId);
    }
}
