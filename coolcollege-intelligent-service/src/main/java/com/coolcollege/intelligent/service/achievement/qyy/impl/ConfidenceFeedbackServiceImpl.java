package com.coolcollege.intelligent.service.achievement.qyy.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.MsgUniteDataTypeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ExportTemplateEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskConstant;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.mapper.achieve.qyy.ConfidenceFeedbackDAO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ConfidenceFeedbackPageDTO;
import com.coolcollege.intelligent.model.achievement.qyy.dto.ExportConfidenceFeedbackRequest;
import com.coolcollege.intelligent.model.achievement.qyy.dto.SubmitConfidenceFeedbackDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConfidenceFeedbackDetailVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.msg.MsgUniteData;
import com.coolcollege.intelligent.model.question.request.ExportStoreWorkRecordRequest;
import com.coolcollege.intelligent.model.qyy.QyyConfidenceFeedbackDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.achievement.qyy.ConfidenceFeedbackService;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.service.importexcel.ImportTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: ConfidenceFeedbackServiceImpl
 * @Description: 信心反馈
 * @date 2023-04-12 19:32
 */
@Service
@Slf4j
public class ConfidenceFeedbackServiceImpl implements ConfidenceFeedbackService {

    @Resource
    private RegionDao regionDao;
    @Resource
    private ConfidenceFeedbackDAO confidenceFeedbackDAO;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private ConfidenceFeedbackService confidenceFeedbackService;

    @Resource
    private SendCardService sendCardService;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Override
    public Boolean submitConfidenceFeedback(String enterpriseId, String userId, String username, SubmitConfidenceFeedbackDTO param) {
        if(StringUtils.isBlank(param.getSynDingDeptId())){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        DataSourceHelper.reset();
        //企业配置
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
        DataSourceHelper.changeToMy();
        RegionDO region = regionDao.selectBySynDingDeptId(enterpriseId, param.getSynDingDeptId());
        Long regionId = Optional.ofNullable(region).map(RegionDO::getId).orElse(null);
        QyyConfidenceFeedbackDO insert = SubmitConfidenceFeedbackDTO.convert(param, username, userId, regionId);
//        Boolean aBoolean = confidenceFeedbackDAO.addConfidenceFeedback(enterpriseId, insert);
        Long insertId = confidenceFeedbackDAO.addConfidenceFeedback(enterpriseId, insert);

        //-----信心反馈卡片------
        if (Objects.isNull(insert) || Objects.isNull(enterpriseConfig) || Objects.isNull(region)){
            return true;
        }
        try {
            //信心反馈详情
            ConfidenceFeedbackDetailVO convert = new ConfidenceFeedbackDetailVO();
            if (!Objects.isNull(insert)){
                insert.setId(insertId);
                convert = ConfidenceFeedbackDetailVO.convert(insert, region.getName());
            }
            log.info("sendConfidenceFeedbackCard -> enterpriseId:{},enterpriseConfig:{},convert:{},region:{}",
                    enterpriseId,enterpriseConfig,convert,region);
            sendCardService.sendConfidenceFeedbackCard(enterpriseId,enterpriseConfig,convert,region);
        } catch (UnsupportedEncodingException e) {
            log.error("发送信心反馈卡片异常",e);
            e.printStackTrace();
        }
        //-----信心反馈卡片------

        return true;
    }

    @Override
    public ConfidenceFeedbackDetailVO getConfidenceFeedback(String enterpriseId, Long id) {
        QyyConfidenceFeedbackDO confidenceFeedback = confidenceFeedbackDAO.getConfidenceFeedback(enterpriseId, id);
        String regionName = null;
        if(Objects.nonNull(confidenceFeedback)){
            RegionDO region = regionDao.getRegionById(enterpriseId, confidenceFeedback.getRegionId());
            regionName = Optional.ofNullable(region).map(RegionDO::getName).orElse(null);
        }
        return ConfidenceFeedbackDetailVO.convert(confidenceFeedback, regionName);
    }

    @Override
    public PageInfo<ConfidenceFeedbackDetailVO> getConfidenceFeedbackPage(String enterpriseId, ConfidenceFeedbackPageDTO param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        Page<QyyConfidenceFeedbackDO> page = confidenceFeedbackDAO.getConfidenceFeedbackPage(enterpriseId, param.getUserIds(), param.getBeginTime(), param.getEndTime());
        List<ConfidenceFeedbackDetailVO> resultList = null;
        if(Objects.nonNull(page) && CollectionUtils.isNotEmpty(page)){
            List<Long> regionIds = page.stream().map(QyyConfidenceFeedbackDO::getRegionId).distinct().collect(Collectors.toList());
            Map<Long, String> regionNameMap = regionDao.getRegionNameMap(enterpriseId, regionIds);
            resultList = new ArrayList<>();
            for (QyyConfidenceFeedbackDO confidenceFeedback : page) {
                resultList.add(ConfidenceFeedbackDetailVO.convert(confidenceFeedback, regionNameMap.get(confidenceFeedback.getRegionId())));
            }
        }
        PageInfo result = new PageInfo<>(page);
        result.setList(resultList);
        result.setTotal(page.getTotal());
        return result;
    }

    @Override
    public List<ConfidenceFeedbackDetailVO> exportConfidenceFeedbackPage(String enterpriseId, ConfidenceFeedbackPageDTO param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        Page<QyyConfidenceFeedbackDO> page = confidenceFeedbackDAO.getConfidenceFeedbackPage(enterpriseId, param.getUserIds(), param.getBeginTime(), param.getEndTime());
        List<ConfidenceFeedbackDetailVO> resultList = null;
        if(Objects.nonNull(page) && CollectionUtils.isNotEmpty(page)){
            List<Long> regionIds = page.stream().map(QyyConfidenceFeedbackDO::getRegionId).distinct().collect(Collectors.toList());
            Map<Long, String> regionNameMap = regionDao.getRegionNameMap(enterpriseId, regionIds);
            resultList = new ArrayList<>();
            for (QyyConfidenceFeedbackDO confidenceFeedback : page) {
                resultList.add(ConfidenceFeedbackDetailVO.convert(confidenceFeedback, regionNameMap.get(confidenceFeedback.getRegionId())));
            }
        }
        return resultList;
    }

    @Override
    public ImportTaskDO exportConfidenceFeedbackPage(String enterpriseId, ConfidenceFeedbackPageDTO param, CurrentUser user) {
        // 查询导出数量，限流
        Long count = confidenceFeedbackDAO.getConfidenceFeedbackPageCount(enterpriseId, param.getUserIds(), param.getBeginTime(), param.getEndTime());
        if (count == null || count == 0L) {
            throw new ServiceException("当前无记录可导出");
        }
        if (count > Constants.MAX_EXPORT_SIZE) {
            throw new ServiceException("导出数据不能超过" + Constants.MAX_EXPORT_SIZE + "条，请缩小导出范围");
        }
        String fileName = ExportTemplateEnum.getByCode(ImportTaskConstant.CONFIDENCE_FEEDBACK);
        // 保存导出任务
        ImportTaskDO importTaskDO = importTaskService.insertExportTask(enterpriseId, fileName, ImportTaskConstant.CONFIDENCE_FEEDBACK);
        // 构造异步导出参数
        ExportConfidenceFeedbackRequest msg = new ExportConfidenceFeedbackRequest();
        msg.setEnterpriseId(enterpriseId);
        msg.setRequest(param);
        msg.setTotalNum(count);
        msg.setImportTaskDO(importTaskDO);
        msg.setDbName(user.getDbName());
        msg.setUser(user);
        MsgUniteData msgUniteData = new MsgUniteData();
        msgUniteData.setData(JSONObject.toJSONString(msg));
        msgUniteData.setMsgType(MsgUniteDataTypeEnum.CONFIDENCE_FEEDBACK_EXPORT.getCode());
        //分开异步导出
        simpleMessageService.send(JSONObject.toJSONString(msgUniteData), RocketMqTagEnum.EXPORT_IMPORT_MESSAGE);
        return importTaskDO;
    }

    @Override
    public Boolean deleteConfidenceFeedback(String enterpriseId, Long id) {
        return confidenceFeedbackDAO.deleteConfidenceFeedback(enterpriseId, id);
    }
}
