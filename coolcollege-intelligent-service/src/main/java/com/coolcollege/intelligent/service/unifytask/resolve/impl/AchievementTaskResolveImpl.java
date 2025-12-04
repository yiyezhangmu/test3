package com.coolcollege.intelligent.service.unifytask.resolve.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.achievement.AchievementTaskRecordMapper;
import com.coolcollege.intelligent.dao.achievement.PanasonicMapper;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTaskRecordDO;
import com.coolcollege.intelligent.model.achievement.entity.ManageStoreCategoryCodeDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enums.TaskTypeEnum;
import com.coolcollege.intelligent.model.unifytask.TaskParentDO;
import com.coolcollege.intelligent.model.unifytask.TaskStoreDO;
import com.coolcollege.intelligent.model.unifytask.TaskSubDO;
import com.coolcollege.intelligent.model.unifytask.dto.ProductInfoDTO;
import com.coolcollege.intelligent.service.achievement.AchievementTaskRecordService;
import com.coolcollege.intelligent.service.unifytask.resolve.TaskResolveAbstractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchenbiao
 * @FileName: AchievementTaskResovleImpl
 * @Description:
 * @date 2025-01-07 14:19
 */
@Slf4j
@Service
public class AchievementTaskResolveImpl extends TaskResolveAbstractService<AchievementTaskRecordDO> {

    @Resource
    private PanasonicMapper panasonicMapper;
    @Resource
    private AchievementTaskRecordService achievementTaskRecordService;
    @Resource
    private AchievementTaskRecordMapper achievementTaskRecordMapper;

    @Override
    public AchievementTaskRecordDO getBusinessData(String enterpriseId, Long unifyTaskId, String storeId, long loopCount) {
        return achievementTaskRecordMapper.getIdByUnifyTaskIdAndStoreIdAndLoopCount(enterpriseId, unifyTaskId, storeId, loopCount);
    }

    @Override
    public boolean addBusinessRecord(String enterpriseId, TaskParentDO taskParent, TaskStoreDO taskStore, List<TaskSubDO> subTaskList, EnterpriseStoreCheckSettingDO enterpriseStoreCheckSetting) {
        TaskSubDO taskSub = subTaskList.get(0);
        return achievementTaskRecordService.addRecord(enterpriseId, taskParent, taskSub);
    }

    @Override
    protected boolean filterSongXiaStoreTask(String enterpriseId, String storeId, TaskParentDO parentDO){
        //松下  出样撤样 任务选择型号跟门店品类不对应不生成任务
        Long unifyTaskId = parentDO.getId();
        if (TaskTypeEnum.ACHIEVEMENT_NEW_RELEASE.getCode().equals(parentDO.getTaskType())|| TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(parentDO.getTaskType())) {
            JSONObject jsonObject = JSON.parseObject(parentDO.getTaskInfo());
            String taskInfoStr = jsonObject.getString(Constants.PRODUCT);
            //任务选择型号列表
            List<ProductInfoDTO> productInfoDTOS = jsonObject.parseArray(taskInfoStr, ProductInfoDTO.class);
            //查当前门店关联品类
            List<ManageStoreCategoryCodeDO> storeCategoryCodeDOS = panasonicMapper.selectManageStoreCategoryCode(storeId, null);
            if (CollectionUtils.isEmpty(storeCategoryCodeDOS)){
                log.info("松下出样和撤样任务，门店未关联品类，不生成任务，enterpriseId = {},taskId = {}, storeId = {}, productInfoDTO = {}", enterpriseId, unifyTaskId, storeId, JSONObject.toJSONString(productInfoDTOS));
                return false;
            }
            List<String> storeCategoryCodes = storeCategoryCodeDOS.stream().map(c -> c.getCategoryCode()).collect(Collectors.toList());
            //取交集
            List<ProductInfoDTO> storeProducts = productInfoDTOS.stream().filter(o -> storeCategoryCodes.contains(o.getCategoryCode())).collect(Collectors.toList());
            log.info("松下出样和撤样任务，门店任务型号数量过滤后，storeProducts = {}", JSONObject.toJSONString(storeProducts));
            if (CollectionUtils.isEmpty(storeProducts)){
                log.info("松下出样和撤样任务，门店任务型号数量过滤后为空，不生成任务,enterpriseId = {},taskId = {}, storeId = {}, productInfoDTO = {}", enterpriseId, unifyTaskId, storeId, JSONObject.toJSONString(productInfoDTOS));
                return false;
            }
            //该门店未出样该型号或者库存为0  不生成撤样任务
            if (TaskTypeEnum.ACHIEVEMENT_OLD_PRODUCTS_OFF.getCode().equals(parentDO.getTaskType())){
                //查询已出样且库存大于0 的
                List<String> models = panasonicMapper.selectStoreSampleExtraction(storeId, storeProducts.stream().map(c -> c.getType()).collect(Collectors.toList()));
                //留下可撤样型号
                storeProducts = storeProducts.stream().filter(c->models.contains(c.getType())).collect(Collectors.toList());
                log.info("松下出样和撤样任务，过滤库存和未出样的，storeProducts = {}", JSONObject.toJSONString(storeProducts));
                if (CollectionUtils.isEmpty(storeProducts)){
                    log.info("松下撤样任务，该门店未出样该型号或者库存为0，不生成任务,enterpriseId = {},taskId = {}, storeId = {}, productInfoDTO = {}", enterpriseId, unifyTaskId, storeId, JSONObject.toJSONString(productInfoDTOS));
                    return false;
                }
            }
            JSONObject storeProductsJsonObject = new JSONObject();
            storeProductsJsonObject.put(Constants.PRODUCT, storeProducts);
            parentDO.setTaskInfo(storeProductsJsonObject.toJSONString());
        }
        return true;
    }
}
