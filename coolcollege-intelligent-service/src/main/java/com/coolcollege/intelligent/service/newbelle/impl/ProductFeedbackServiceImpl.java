package com.coolcollege.intelligent.service.newbelle.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.unifytask.TaskParentMapper;
import com.coolcollege.intelligent.dao.unifytask.TaskSubMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.newbelle.dto.BaseGoodsDetailDTO;
import com.coolcollege.intelligent.model.newbelle.dto.InventoryStoreDataDTO;
import com.coolcollege.intelligent.model.newbelle.request.BaseGoodsDetailRequest;
import com.coolcollege.intelligent.model.newbelle.request.InventoryStoreDataRequest;
import com.coolcollege.intelligent.model.newbelle.request.ProductNoBySubTaskIdRequest;
import com.coolcollege.intelligent.model.newbelle.request.RegionAndStoreRequest;
import com.coolcollege.intelligent.model.selectcomponent.SelectComponentStoreVO;
import com.coolcollege.intelligent.model.selectcomponent.SelectComptRegionStoreVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.newbelle.ProductFeedbackService;
import com.coolcollege.intelligent.service.selectcomponent.SelectionComponentService;
import com.coolcollege.intelligent.common.http.CoolHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductFeedbackServiceImpl implements ProductFeedbackService {


    @Resource
    SelectionComponentService selectionComponentService;

    @Resource
    StoreDao storeDao;

    @Resource
    TaskSubMapper taskSubMapper;

    @Resource
    TaskParentMapper taskParentMapper;

    @Override
    public List<InventoryStoreDataDTO> getInventoryStoreData(String enterpriseId, InventoryStoreDataRequest request) {
        String post = CoolHttpClient.sendPostJsonRequest(Constants.NEW_BELLE_url_1, JSONObject.toJSONString(request), putHeader());
        JSONArray jsonArray = JSONArray.parseArray(String.valueOf(JSONObject.parseObject(String.valueOf(JSONObject.parseObject(post).get("payload"))).get("data")));
        List<InventoryStoreDataDTO> inventoryStoreDataDTOS = JSONObject.parseArray(jsonArray.toJSONString(), InventoryStoreDataDTO.class);
        if (CollectionUtils.isNotEmpty(inventoryStoreDataDTOS) && inventoryStoreDataDTOS.size() > 0) {
            return inventoryStoreDataDTOS;
        }
        return new ArrayList<>();
    }

    @Override
    public BaseGoodsDetailDTO getBaseGoodsDetail(String enterpriseId, BaseGoodsDetailRequest request) {
        String post = CoolHttpClient.sendPostJsonRequest(Constants.NEW_BELLE_url_2, JSONObject.toJSONString(request), putHeader());
        Object obj = JSONArray.parseArray(String.valueOf(JSONObject.parseObject(String.valueOf(JSONObject.parseObject(post).get("payload"))).get("data"))).get(0);
        if (Objects.nonNull(obj)) {
            BaseGoodsDetailDTO response = JSONObject.parseObject(String.valueOf(obj), BaseGoodsDetailDTO.class);
            return response;
        }
        return new BaseGoodsDetailDTO();
    }

    private Map<String, String> putHeader() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(Constants.NEW_BELLE_HEADER_PARAM, Constants.NEW_BELLE_APPCODE);
        return headerMap;
    }


    @Override
    public SelectComptRegionStoreVO getRegionAndStore(String eid, Long parentId, String userId, List<String> storeNewNo) {
        log.info("getRegionAndStore parentId：{},userId:{},storeNewNo,{}",parentId,userId,JSONObject.toJSONString(storeNewNo));
        //复用旧接口
        SelectComptRegionStoreVO regionAndStore = selectionComponentService.getRegionAndStore(eid, parentId, userId, null);
        log.info("regionAndStore：{}",JSONObject.toJSONString(regionAndStore));
        //根据百丽的新门店编码查询铺货门店
        List<StoreDO> storeByNewNos = storeDao.getStoreByStoreNewNos(eid, storeNewNo);
        List<String> storeIdList = storeByNewNos.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        log.info("storeByNewNos：{}",JSONObject.toJSONString(storeIdList));
        //过滤铺过货的门店
        if (CollectionUtils.isNotEmpty(regionAndStore.getStoreList()) && regionAndStore.getStoreList().size() > 0){
            List<SelectComponentStoreVO> newStoreList = regionAndStore.getStoreList().stream().filter(o -> storeIdList.contains(o.getStoreId())).collect(Collectors.toList());
            log.info("newStoreList：{}",JSONObject.toJSONString(newStoreList));
            regionAndStore.setStoreList(newStoreList);
            return regionAndStore;
        }
        return regionAndStore;
    }

    @Override
    public PageVO<SelectComponentStoreVO> getStoresByKeyword(String enterpriseId,
                                                             String keyword,
                                                             Integer pageNum,
                                                             Integer pageSize,
                                                             String userId,
                                                             List<String> storeNewNo) {
        PageVO<SelectComponentStoreVO> storesByKeyword = selectionComponentService.getStoresByKeyword(enterpriseId, keyword, pageNum, pageSize, userId, null);
        //根据百丽的新门店编码查询铺货门店
        List<StoreDO> storeByNewNos = storeDao.getStoreByStoreNewNos(enterpriseId, storeNewNo);
        List<String> storeIdList = storeByNewNos.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
        List<SelectComponentStoreVO> list = storesByKeyword.getList();
        if (CollectionUtils.isNotEmpty(list) && list.size() > 0){
            List<SelectComponentStoreVO> collect = list.stream().filter(o -> storeIdList.contains(o.getStoreId())).collect(Collectors.toList());
            storesByKeyword.setList(collect);
            return storesByKeyword;
        }
        return new PageVO<SelectComponentStoreVO>();
    }

    @Override
    public String getProductNoBySubTaskId(String enterpriseId, ProductNoBySubTaskIdRequest request) {
        String parentTaskId = taskSubMapper.getTaskBySubTaskId(enterpriseId,request.getSubTaskId());
        String productNo = taskParentMapper.getProductNoBySubTaskId(enterpriseId,parentTaskId);
        return productNo;
    }
}
