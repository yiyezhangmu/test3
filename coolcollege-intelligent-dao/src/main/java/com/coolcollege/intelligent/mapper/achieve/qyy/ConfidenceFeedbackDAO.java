package com.coolcollege.intelligent.mapper.achieve.qyy;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.qyy.QyyConfidenceFeedbackMapper;
import com.coolcollege.intelligent.model.qyy.QyyConfidenceFeedbackDO;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchenbiao
 * @FileName: ConfidenceFeedbackDAO
 * @Description:
 * @date 2023-04-12 19:46
 */
@Service
@Slf4j
public class ConfidenceFeedbackDAO {

    @Resource
    private QyyConfidenceFeedbackMapper qyyConfidenceFeedbackMapper;

    /**
     * 新增信心反馈
     * @param enterpriseId
     * @param param
     * @return
     */
    public Long addConfidenceFeedback(String enterpriseId, QyyConfidenceFeedbackDO param){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(param)){
            log.error("enterpriseId：{}或param：{}为空",enterpriseId,param);
        }
        qyyConfidenceFeedbackMapper.insertSelective(param, enterpriseId);

        return param.getId();
    }

    /**
     * 获取信心反馈详情
     * @param enterpriseId
     * @param id
     * @return
     */
    public QyyConfidenceFeedbackDO getConfidenceFeedback(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return null;
        }
        return qyyConfidenceFeedbackMapper.getConfidenceFeedback(enterpriseId, id);
    }

    public Boolean deleteConfidenceFeedback(String enterpriseId, Long id){
        if(StringUtils.isBlank(enterpriseId) || Objects.isNull(id)){
            return null;
        }
        QyyConfidenceFeedbackDO update = new QyyConfidenceFeedbackDO();
        update.setId(id);
        update.setDeleted(true);
        return qyyConfidenceFeedbackMapper.updateByPrimaryKeySelective(update, enterpriseId) > Constants.ZERO;
    }

    public Page<QyyConfidenceFeedbackDO> getConfidenceFeedbackPage(String enterpriseId, List<String> userIds, Date beginTime, Date endTime){
        if(StringUtils.isBlank(enterpriseId)){
            return new Page<>();
        }
        return qyyConfidenceFeedbackMapper.getConfidenceFeedbackPage(enterpriseId, userIds, beginTime, endTime);
    }

    public Long getConfidenceFeedbackPageCount(String enterpriseId, List<String> userIds, Date beginTime, Date endTime){
        if(StringUtils.isBlank(enterpriseId)){
            return 0L;
        }
        return qyyConfidenceFeedbackMapper.getConfidenceFeedbackPageCount(enterpriseId, userIds, beginTime, endTime);
    }


    public Long getConfidenceFeedbackId(String enterpriseId,QyyConfidenceFeedbackDO insert) {
        return qyyConfidenceFeedbackMapper.getConfidenceFeedbackId(enterpriseId,insert);
    }
}
