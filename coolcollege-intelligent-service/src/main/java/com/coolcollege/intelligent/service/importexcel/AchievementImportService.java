package com.coolcollege.intelligent.service.importexcel;

import cn.hutool.core.collection.CollUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.achievement.AchievementTargetMonthEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.achievement.dto.AchievementTargetImportDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTargetDetailDO;
import com.coolcollege.intelligent.model.enums.AchievementKeyPrefixEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportConstants;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.achievement.AchievementTargetService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * 业绩导入
 *
 * @author chenyupeng
 * @since 2021/12/7
 */
@Slf4j
@Service
public class AchievementImportService extends ImportBaseService{

    @Resource
    ImportTaskMapper importTaskMapper;

    @Resource
    GenerateOssFileService generateOssFileService;

    @Resource
    StoreMapper storeMapper;

    @Resource
    AchievementTargetService achievementTargetService;

    private static final String STORE_ID_ABNORMAL = "门店ID无法识别";

    private static final String TARGET_ABNORMAL = "请正确输入业绩目标（0-999999999.99）";

    private static final String MAX_IMPORT_ABNORMAL = "仅支持导入前2万条数据";

    private static final String TITLE = "注意事项：\n" +
            "1、请勿擅自修改导入模板的表头字段，否则会导致导入失败！\n" +
            "2、模板默认会填入关联门店，下载模板后填写门店每月业绩目标即可。\n" +
            "3、填写业绩目标时，请填写大于0的数字；若有小数，请保留小数点后两位；若未填写，门店当月目标不变（不会识别）。\n" +
            "4、业绩目标导入按门店ID查重，检测到门店ID重复数据会覆盖，若未填写内容，将不录入。\n" +
            "5、每次最多导入2万条数据。";

    @Async("importExportThreadPool")
    public void importAchievementTarget(String eid, String dbName, Future<List<AchievementTargetImportDTO>> importTask, String contentType, ImportTaskDO task, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        try {
            boolean lock = lock(eid, ImportConstants.ACHIEVEMENT_TARGET_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<AchievementTargetImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }
            importDeal(eid,dbName,importList,contentType,task,user);

        } catch (BaseException e) {
            log.error("业绩目标文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            log.error("业绩目标文件上传失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("业绩目标文件上传失败");
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.ACHIEVEMENT_TARGET_KEY);
        }
    }

    public void importDeal(String eid, String dbName, List<AchievementTargetImportDTO> importList, String contentType, ImportTaskDO task, CurrentUser user){


        log.info("{},导入业绩目标总条数：{}", eid, importList.size());
        List<AchievementTargetImportDTO> errorList = new ArrayList<>();
        int importSum = 0;
        for (List<AchievementTargetImportDTO> achievementTargetImportDTOS : Lists.partition(importList, Constants.MAX_INSERT_SIZE)) {
            if(importSum >= Constants.MAX_IMPORT_SIZE){
                for (AchievementTargetImportDTO achievementTargetImportDTO : achievementTargetImportDTOS) {
                    achievementTargetImportDTO.setDec(MAX_IMPORT_ABNORMAL);
                    errorList.add(achievementTargetImportDTO);
                }
            }else {
                partImportDeal(eid,achievementTargetImportDTOS, user,errorList);
                importSum += Constants.MAX_INSERT_SIZE;
            }
        }

        DataSourceHelper.changeToSpecificDataSource(dbName);
        if (errorList.size() != 0) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            if(importList.size() - errorList.size() > 0){
                task.setRemark("部分数据导入失败");
            }
            String url = generateOssFileService.generateOssExcel(errorList, eid, TITLE, "出错门店列表", contentType, AchievementTargetImportDTO.class);
            task.setFileUrl(url);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setTotalNum(importList.size());
        task.setSuccessNum(task.getTotalNum() - errorList.size());
        importTaskMapper.update(eid, task);

        //年目标
        achievementTargetService.updateYearAchievementTarget(eid);
    }

    public void partImportDeal(String eid, List<AchievementTargetImportDTO> importList,
                               CurrentUser user, List<AchievementTargetImportDTO> errorList){
        List<String> storeIds = importList.stream().map(AchievementTargetImportDTO::getStoreId).collect(Collectors.toList());
        List<StoreDO> storeDOS = storeMapper.getByStoreIdList(eid, storeIds);
        Map<String, StoreDO> storeDOMap = storeDOS.stream().collect(Collectors.toMap(StoreDO::getStoreId, data -> data, (a, b) -> a));

        List<AchievementTargetDO> achievementTargetDOS = new ArrayList<>();
        Map<String, List<AchievementTargetDetailDO>> detaiListMap = new HashMap<>();
        AchievementTargetDO tempDo;
        List<AchievementTargetDetailDO> tempDetailList;
        Date now = new Date();
        StoreDO tempStore;
        BigDecimal yearAchievementTarget = new BigDecimal(0);
        Integer achievementYear;
        for (AchievementTargetImportDTO importDTO : importList) {
            yearAchievementTarget = new BigDecimal(0);
            tempDo = new AchievementTargetDO();
            tempStore = storeDOMap.get(importDTO.getStoreId());
            tempDetailList = new ArrayList<>();
            achievementYear = importDTO.getYear();
            if(tempStore == null){
                importDTO.setDec(STORE_ID_ABNORMAL);
                errorList.add(importDTO);
                continue;
            }
            tempDo.setAchievementYear(achievementYear);
            tempDo.setCreateTime(now);
            tempDo.setCreateUserId(user.getUserId());
            tempDo.setCreateUserName(user.getName());
            tempDo.setEditTime(now);
            tempDo.setUpdateUserId(user.getUserId());
            tempDo.setUpdateUserName(user.getName());

            tempDo.setRegionPath(tempStore.getRegionPath());
            tempDo.setRegionId(tempStore.getRegionId());
            tempDo.setStoreId(tempStore.getStoreId());
            tempDo.setStoreName(tempStore.getStoreName());
            tempDo.setStoreNum(tempStore.getStoreNum());
            Map<AchievementTargetMonthEnum, String> map = importDTO.getMap();
            for (AchievementTargetMonthEnum achievementTargetMonthEnum : map.keySet()) {
                String value = map.get(achievementTargetMonthEnum);
                if(StringUtils.isNotBlank(value)){
                    try{
                        BigDecimal bigDecimal = new BigDecimal(value);
                        yearAchievementTarget = yearAchievementTarget.add(bigDecimal);
                        tempDetailList.add(transAchievementTargetDetailDO(null,bigDecimal,achievementYear,tempStore,
                                getMonth(achievementYear, achievementTargetMonthEnum.getMsg()),getMonthLastDay(achievementYear,achievementTargetMonthEnum.getMsg()),user));
                    }catch (Exception e){
                        addErrorInfo(errorList,importDTO);
                    }
                }
            }
            //为0不插入
            if(tempDetailList.size() == 0){
                continue;
            }
            tempDo.setYearAchievementTarget(yearAchievementTarget);
            achievementTargetDOS.add(tempDo);
            detaiListMap.put(importDTO.getStoreId() + "-" + achievementYear,tempDetailList);
        }

        achievementTargetService.importTarget(eid, achievementTargetDOS, detaiListMap);

        storeDOS.clear();
        storeDOMap.clear();
        achievementTargetDOS.clear();
        detaiListMap.clear();
    }
    public void addErrorInfo(List<AchievementTargetImportDTO> errorList,AchievementTargetImportDTO importDTO){
        importDTO.setDec(TARGET_ABNORMAL);
        errorList.add(importDTO);
    }

    public BigDecimal transTarget(String target) {
        BigDecimal bigDecimal;
        try {
            bigDecimal = new BigDecimal(target);
        } catch (Exception e) {
            return null;
        }
        return bigDecimal;
    }

    public Date getMonth(Integer achievementYear,String month){
        return DateUtil.parse(achievementYear + month,"yyyy-MM-dd");
    }

    public Date getMonthLastDay(Integer achievementYear,String month){
        return DateUtil.getLastOfDayMonth(DateUtil.parse(achievementYear + month,"yyyy-MM-dd"));
    }

    public AchievementTargetDetailDO transAchievementTargetDetailDO(Long targetId,BigDecimal target,Integer achievementYear,StoreDO storeDO,Date beginDate,Date endDate,CurrentUser user){
        Date now = new Date();
        AchievementTargetDetailDO detailDO = new AchievementTargetDetailDO();
        detailDO.setCreateTime(now);
        detailDO.setCreateUserId(user.getUserId());
        detailDO.setCreateUserName(user.getName());
        detailDO.setEditTime(now);
        detailDO.setUpdateUserId(user.getUserId());
        detailDO.setUpdateUserName(user.getName());

        detailDO.setRegionPath(storeDO.getRegionPath());
        detailDO.setRegionId(storeDO.getRegionId());
        detailDO.setStoreId(storeDO.getStoreId());
        detailDO.setStoreName(storeDO.getStoreName());
        detailDO.setStoreNum(storeDO.getStoreNum());

        detailDO.setTargetId(targetId);
        detailDO.setTimeType(AchievementKeyPrefixEnum.ACHIEVEMENT_TARGET_MONTH.type);
        detailDO.setBeginDate(beginDate);
        detailDO.setEndDate(endDate);
        detailDO.setAchievementTarget(target);
        detailDO.setAchievementYear(achievementYear);
        return detailDO;
    }
}
