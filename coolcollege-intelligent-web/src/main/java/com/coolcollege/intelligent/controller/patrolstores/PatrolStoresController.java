package com.coolcollege.intelligent.controller.patrolstores;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.store.dto.StoreSignInMapDTO;
import com.coolcollege.intelligent.model.store.queryDto.NearbyStoreRequest;
import com.coolcollege.intelligent.model.store.vo.StoreSignInMapVO;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 巡店记录操作
 *
 * @author Aaron
 * @ClassName PatrolStoresController
 * @Description 巡店记录操作
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v2/enterprises/{enterprise-id}/patrol_stores")
@Deprecated
//FIXME 待清理
public class PatrolStoresController {


    @Autowired
    private StoreService storeService;


    /**
     * 获取签到门店列表
     *
     * @param enterpriseId
     * @param signInMap
     * @return
     */
    @GetMapping("/sing_in_store")
    public Object getSignInStoreList(@PathVariable("enterprise-id") String enterpriseId, @Valid StoreSignInMapVO signInMap) {
        return storeService.getSignInStoreMapList(enterpriseId, signInMap);
    }

    @GetMapping("/sing_in_store/new")
    public ResponseResult<List<StoreSignInMapDTO>> getSignInStoreListNew(@PathVariable("enterprise-id") String enterpriseId,
                                                                         @RequestParam("longitude") String longitude,
                                                                         @RequestParam("latitude") String latitude,
                                                                         @RequestParam(value = "storeStatusList", required = false) List<String> storeStatusList,
                                                                         @RequestParam(value = "queryDistance", defaultValue = "5") Double queryDistance) {

        NearbyStoreRequest request = new NearbyStoreRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setQueryDistance(queryDistance);
        request.setStoreStatusList(storeStatusList);
        DataSourceHelper.changeToMy();
        List<StoreSignInMapDTO> storeSignInMapDTOList = storeService.getSignInStoreMapListNew(enterpriseId, request, false);
        storeSignInMapDTOList = storeSignInMapDTOList == null ? new ArrayList<>() : storeSignInMapDTOList;
        List<String> storeIds = storeSignInMapDTOList.stream().map(StoreSignInMapDTO::getStoreId).collect(Collectors.toList());
        //  添加新店
        StoreSignInMapDTO defaultStore = storeService.getSignInStoreMapListById(enterpriseId, Constants.DEFAULT_STORE_ID);
        if (Constants.SENYU_ENTERPRISE_ID.equals(enterpriseId) && storeSignInMapDTOList != null && defaultStore != null && !storeIds.contains(defaultStore.getStoreId())) {
            defaultStore.setDistance(0d);
            defaultStore.setHasTask(false);
            storeSignInMapDTOList.add(0, defaultStore);
        }
        return ResponseResult.success(storeSignInMapDTOList);
    }

    /**
     * 获取签到门店列表
     *
     * @param enterpriseId
     * @param signInMap
     * @return
     */
    @GetMapping("/sing_in_store_page")
    public ResponseResult<PageVO<StoreSignInMapDTO>> getPageSignInStoreList(@PathVariable("enterprise-id") String enterpriseId,
                                                                            @RequestParam(value = "page_size", defaultValue = "20") Integer pageSize,
                                                                            @RequestParam(value = "page_num", defaultValue = "1") Integer pageNum,
                                                                            @Valid StoreSignInMapVO signInMap) {
        List<StoreSignInMapDTO> pageSignInStoreMapList = storeService.getPageSignInStoreMapList(enterpriseId, signInMap, pageSize, pageNum);
        if (CollectionUtils.isNotEmpty(pageSignInStoreMapList)) {
            return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>(pageSignInStoreMapList)));
        }
        return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo<>()));
    }


    /**
     * 获取门店自检门店列表 无权限 分页
     *
     * @param enterpriseId
     * @return
     */
    @GetMapping("/getNearStoreList")
    public ResponseResult<PageInfo<StoreSignInMapDTO>> getNearStoreList(@PathVariable("enterprise-id") String enterpriseId,
                                                                        @RequestParam(value = "longitude") String longitude,
                                                                        @RequestParam(value = "latitude") String latitude,
                                                                        @RequestParam(value = "storeName", defaultValue = "") String storeName,
                                                                        @RequestParam(value = "storeStatusList", required = false) List<String> storeStatusList,
                                                                        @RequestParam(value = "pageSize", defaultValue = "1") Integer pageSize,
                                                                        @RequestParam(value = "pageNum", defaultValue = "10") Integer pageNum) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getNearStoreList(enterpriseId, longitude, latitude, storeName, storeStatusList, pageNum, pageSize));
    }

    /**
     * 获取除自己管辖区域外的门店自检门店列表 无权限 分页
     *
     * @param enterpriseId
     * @param longitude
     * @param latitude
     * @param storeName
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/getNotMyNearStoreList")
    public ResponseResult<PageInfo<StoreSignInMapDTO>> getNotMyNearStoreList(@PathVariable("enterprise-id") String enterpriseId,
                                                                             @RequestParam(value = "longitude") String longitude,
                                                                             @RequestParam(value = "latitude") String latitude,
                                                                             @RequestParam(value = "storeName", defaultValue = "") String storeName,
                                                                             @RequestParam(value = "storeStatusList", required = false) List<String> storeStatusList,
                                                                             @RequestParam(value = "pageSize", defaultValue = "1") Integer pageSize,
                                                                             @RequestParam(value = "pageNum", defaultValue = "10") Integer pageNum) {
        DataSourceHelper.changeToMy();
        return ResponseResult.success(storeService.getNotMyNearStoreList(
                enterpriseId,
                longitude,
                latitude,
                storeName,
                storeStatusList,
                pageNum,
                pageSize));
    }


}
