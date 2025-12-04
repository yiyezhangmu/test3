package com.coolcollege.intelligent.controller.aliyun;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.device.DeviceTypeEnum;
import com.coolcollege.intelligent.common.enums.role.AuthRoleEnum;
import com.coolcollege.intelligent.model.aliyun.vo.AliyunVdsStatisticsDateVO;
import com.coolcollege.intelligent.model.store.vo.StoreDeviceVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.aliyun.AliyunVdsStatisticsService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 邵凌志
 * @date 2021/1/13 19:46
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v3/{enterprise-id}/aliyun/vds/statistics")
public class AliyunVdsStatisticsController {

    @Autowired
    private AliyunVdsStatisticsService vdsStatisticsService;
    @Lazy
    @Autowired
    private StoreService storeService;

    /**
     * 获取本周数据统计
     * @param enterpriseId
     * @param statisticsWeek
     * @return
     */
    @GetMapping("thisWeekByCorp")
    public Object getNowWeekByCorp(@PathVariable("enterprise-id") String enterpriseId,
                                   AliyunVdsStatisticsDateVO statisticsWeek) {

        return vdsStatisticsService.getNowWeekByCorp(enterpriseId, statisticsWeek);
    }

    /**
     * 获取两周周数据统计
     * @param enterpriseId
     * @param statisticsWeek
     * @return
     */
    @GetMapping("twoWeekByCorp")
    public Object getTwoWeekByCorp(@PathVariable("enterprise-id") String enterpriseId,
                             @Valid AliyunVdsStatisticsDateVO statisticsWeek) {

        return vdsStatisticsService.getTwoWeekByCorp(enterpriseId, statisticsWeek);
    }

    /**
     * 获取本周数据统计
     * @param enterpriseId
     * @param statisticsWeek
     * @return
     */
    @GetMapping("thisWeek")
    public Object getNowWeek(@PathVariable("enterprise-id") String enterpriseId,
                             AliyunVdsStatisticsDateVO statisticsWeek) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        Boolean all = Boolean.TRUE;
        String storeId = null;
        String storeName = null;
        String corpId = null;
        if(StringUtils.isBlank(statisticsWeek.getCorpId())&&AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())){
            statisticsWeek.setAll(Boolean.TRUE);
        }
        else if(StringUtils.isBlank(statisticsWeek.getCorpId())){
            statisticsWeek.setAll(Boolean.FALSE);
            List<StoreDeviceVO> deviceStore = storeService.getDeviceStore(enterpriseId, null, 1, 1, DeviceTypeEnum.DEVICE_VIDEO.getCode(),Boolean.TRUE,null);
            if(CollectionUtils.isEmpty(deviceStore)){
                return new HashMap<>(8);
            }
            statisticsWeek.setCorpId(deviceStore.get(0).getVdsCorpId());
            all = false;
            storeId = deviceStore.get(0).getStoreId();
            storeName = deviceStore.get(0).getStoreName();
            corpId = deviceStore.get(0).getVdsCorpId();
        }
        Map  result = (Map) vdsStatisticsService.getNowWeekByCorp(enterpriseId, statisticsWeek);
        result.put("all",all);
        result.put("storeId",storeId);
        result.put("storeName",storeName);
        result.put("corpId",corpId);
        return result;
    }

    /**
     * 获取两周周数据统计
     * @param enterpriseId
     * @param statisticsWeek
     * @return
     */
    @GetMapping("twoWeek")
    public Object getTwoWeek(@PathVariable("enterprise-id") String enterpriseId,
                             @Valid AliyunVdsStatisticsDateVO statisticsWeek) {
        DataSourceHelper.changeToMy();
        CurrentUser user = UserHolder.getUser();
        Boolean all = Boolean.TRUE;
        String storeId = null;
        String storeName = null;
        String corpId = null;
        if(StringUtils.isBlank(statisticsWeek.getCorpId())&&AuthRoleEnum.ALL.getCode().equals(user.getRoleAuth())){
            statisticsWeek.setAll(Boolean.TRUE);
        }
        else if(StringUtils.isBlank(statisticsWeek.getCorpId())){
            statisticsWeek.setAll(Boolean.FALSE);
            List<StoreDeviceVO> deviceStore = storeService.getDeviceStore(enterpriseId, null, 1, 1, DeviceTypeEnum.DEVICE_VIDEO.getCode(),Boolean.TRUE,null);
            if(CollectionUtils.isEmpty(deviceStore)){
                return new HashMap<>(8);
            }
            statisticsWeek.setCorpId(deviceStore.get(0).getVdsCorpId());
            all = false;
            storeId = deviceStore.get(0).getStoreId();
            storeName = deviceStore.get(0).getStoreName();
            corpId = deviceStore.get(0).getVdsCorpId();
        }
        Map result = (Map)vdsStatisticsService.getTwoWeekByCorp(enterpriseId, statisticsWeek);
        result.put("all",all);
        result.put("storeId",storeId);
        result.put("storeName",storeName);
        result.put("corpId",corpId);
        return result;
    }

    /**
     * 获取性别比例
     * @param enterpriseId
     * @param statisticsWeek
     * @return
     */
    @GetMapping("sex")
    public Object getSexData(@PathVariable("enterprise-id") String enterpriseId,
                             @Valid AliyunVdsStatisticsDateVO statisticsWeek) {

        return vdsStatisticsService.getSexData(enterpriseId, statisticsWeek);
    }

    /**
     * 获取年龄段数据
     * @param enterpriseId
     * @param statisticsWeek
     * @return
     */
    @GetMapping("age")
    public Object getAgeData(@PathVariable("enterprise-id") String enterpriseId,
                             @Valid AliyunVdsStatisticsDateVO statisticsWeek) {

        return vdsStatisticsService.getAgeData(enterpriseId, statisticsWeek);
    }

    /**
     * 获取人员轨迹数据
     * @param enterpriseId
     * @param statisticsWeek
     * @return
     */
    @GetMapping("trackDetail")
    public Object getTrackDetail(@PathVariable("enterprise-id") String enterpriseId,
                             @Valid AliyunVdsStatisticsDateVO statisticsWeek) {

        return vdsStatisticsService.getTrackDetail(enterpriseId, statisticsWeek);
    }
}
