package com.coolcollege.intelligent.controller.metatable;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.data.correction.DataCorrectionService;
import com.coolcollege.intelligent.mapper.metatable.TbMetaColumnCategoryDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @FileName: HistoryDataDealController
 * @Description: 历史数据处理
 * @date 2022-04-14 19:22
 */
@RestController
@Slf4j
@RequestMapping("/history")
public class HistoryDataDealController {

    @Resource
    private EnterpriseConfigService enterpriseConfigService;
    @Resource
    private TbMetaColumnCategoryDAO tbMetaColumnCategoryDAO;
    @Resource
    private TbMetaTableMapper metaTableMapper;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private DataCorrectionService dataCorrectionService;

    /**
     * 处理其他分类
     * @param enterpriseIds
     * @return
     */
    @GetMapping("/deal/meta/column/category")
    public ResponseResult dealMetaColumnCategory(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds){
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
                try {
                    tbMetaColumnCategoryDAO.getOtherCategoryId(enterpriseId);
                } catch (Exception e) {
                    log.info("dealMetaColumnCategory", e);
                }
            }
        }
        return ResponseResult.success();
    }

    @GetMapping("/deal/meta/column/result")
    public ResponseResult dealMetaColumnResult(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds,
                                               @RequestParam(value = "beginTime", required = false) String beginTime){
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                try {
                    dataCorrectionService.initColumnResult(enterpriseId,enterpriseConfig.getDbName(), beginTime);
                } catch (Exception e) {
                    log.info("dealMetaColumnResult ---", e);
                }
            }
        }
        return ResponseResult.success();
    }

    @Deprecated
    @GetMapping("/deal/meta/column/checkResultLevel")
    public ResponseResult dealMetaColumnCheckResultLevel(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds){
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                try {
                    dataCorrectionService.initCheckResultLevel(enterpriseId,enterpriseConfig.getDbName());
                } catch (Exception e) {
                    log.info("dealMetaColumnCheckResultLevel---", e);
                }
            }
        }
        return ResponseResult.success();
    }

    /**
     * 处理高级项的结果项分数
     * @param enterpriseIds
     * @return
     */
    @GetMapping("/deal/meta/column/result/score")
    public ResponseResult dealHighMetaColumnResultScore(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds,
                                                        @RequestParam(value = "beginTime", required = false) String beginTime){
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                try {
                    dataCorrectionService.dealHighResultScore(enterpriseId,enterpriseConfig.getDbName(), beginTime);
                } catch (Exception e) {
                    log.info("dealHighMetaColumnResultScore----", e);
                }
            }
        }
        return ResponseResult.success();
    }

    @GetMapping("/deal/ai/meta/column/result")
    public ResponseResult dealAiMetaColumnResult(@RequestParam(value = "enterpriseIds", required = false) List<String> enterpriseIds,
                                               @RequestParam(value = "beginTime", required = false) String beginTime){
        boolean hasNext = true;
        int pageSize = 100;
        int pageNum = 1;
        while(hasNext){
            PageHelper.startPage(pageNum, pageSize);
            DataSourceHelper.reset();
            List<EnterpriseConfigDO> enterpriseConfigList = enterpriseConfigService.selectAllEnterpriseConfig(enterpriseIds);
            PageHelper.clearPage();
            hasNext = enterpriseConfigList.size() >= pageSize;
            if(CollectionUtils.isEmpty(enterpriseConfigList)){
                break;
            }
            pageNum++;
            for (EnterpriseConfigDO enterpriseConfig : enterpriseConfigList) {
                String enterpriseId = enterpriseConfig.getEnterpriseId();
                try {
                    dataCorrectionService.initAiColumnResult(enterpriseId,enterpriseConfig.getDbName(), beginTime);
                } catch (Exception e) {
                    log.info("dealMetaColumnResult ---", e);
                }
            }
        }
        return ResponseResult.success();
    }
}
