package com.coolcollege.intelligent.facade.qyy;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.facade.user.EnterpriseUserFacade;
import com.coolcollege.intelligent.mapper.achieve.qyy.QyyRecommendStyleDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.qyy.QyyRecommendStyleDO;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: QyyCardTimerServiceImpl
 * @Description:
 * @date 2023-04-27 10:15
 */
@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.QYY_SEND_CARD_FACADE_UNIQUE_ID ,interfaceType = QyyCardTimerFacade.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class QyyCardTimerFacadeImpl implements QyyCardTimerFacade {

    @Resource
    private SendCardService sendCardService;
    @Resource
    private QyyRecommendStyleDAO qyyRecommendStyleDAO;
    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;


    @Override
    public ResultDTO<Integer> batchSendRecommendStyle(String enterpriseId)  throws ApiException {
        log.info("主推款推送：{}", enterpriseId);
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        if(Objects.isNull(enterpriseConfig)){
            return null;
        }
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
        try {
            //当前时间前10分钟 和 后10分钟
            String beginTime = LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String endTime = LocalDateTime.now().minusMinutes(-10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            int pageNum = Constants.INDEX_ONE, pageSize = Constants.MSG_SIZE;
            boolean hasNext = true;
            while (hasNext){
                PageHelper.startPage(pageNum, pageSize);
                List<QyyRecommendStyleDO> page = qyyRecommendStyleDAO.getTimerRecommendStylePage(enterpriseId, beginTime, endTime);
                if (CollectionUtils.isEmpty(page) || page.size() < pageSize) {
                    hasNext = false;
                }
                List<Long> recommendStyleIds = page.stream().map(QyyRecommendStyleDO::getId).collect(Collectors.toList());
                sendCardService.batchSendRecommendStyle(enterpriseId, page);
                qyyRecommendStyleDAO.updateRecommendStyleSendStatus(enterpriseId, recommendStyleIds);
            }
        } catch (Exception e) {
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }

    @Override
    public ResultDTO<Integer> sendTodayUserGoal(String enterpriseId) throws ApiException{
        try {
            log.info("发送每日业绩目标：{}", enterpriseId);
            sendCardService.sendTodayUserGoal(enterpriseId);
        } catch (Exception e) {
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }

    @Override
    public ResultDTO<Integer> sendUserGoal(String enterpriseId) throws ApiException {
        try {
            log.info("卓诗尼发送用户目标：{}", enterpriseId);
            sendCardService.pushUserGoalByTime(enterpriseId);
        } catch (Exception e) {
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }

    @Override
    public ResultDTO<Integer> sendWeeklyNewsPaperCount(String enterpriseId) throws ApiException {
        try {
            log.info("卓诗尼周报统计：{}", enterpriseId);
            sendCardService.weeklyStatisticsCard(enterpriseId);
        } catch (Exception e) {
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }

    @Override
    public ResultDTO<Integer> sendWeeklyNewsPaperDing(String enterpriseId) throws ApiException {
        try {
            log.info("卓诗尼周报提醒：{}", enterpriseId);
            sendCardService.sendDingWeeklyNewspaper(enterpriseId);
        } catch (Exception e) {
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }
}
