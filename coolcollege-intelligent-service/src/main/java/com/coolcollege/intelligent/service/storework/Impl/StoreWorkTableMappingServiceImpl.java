package com.coolcollege.intelligent.service.storework.Impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.dao.storework.dao.SwStoreWorkTableMappingDao;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import com.coolcollege.intelligent.model.storework.request.StoreWorkDutyInfoRequest;
import com.coolcollege.intelligent.service.storework.StoreWorkTableMappingService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author wxp
 * @Date 2022/9/8 15:22
 * @Version 1.0
 */
@Service
public class StoreWorkTableMappingServiceImpl implements StoreWorkTableMappingService {

    @Resource
    SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;

    @Override
    public Pair<List<SwStoreWorkTableMappingDO>, List<TbMetaStaTableColumnDO>> insertDutyInfoList(String enterpriseId, List<StoreWorkDutyInfoRequest> dutyInfoList, SwStoreWorkDO storeWorkDO, String createUserId) {
        List<Long>  metaTableIds = Lists.newArrayList();
        // 已有检查表
        List<SwStoreWorkTableMappingDO> oldTableMappingList = swStoreWorkTableMappingDao.selectListByStoreWorkIds(enterpriseId, Collections.singletonList(storeWorkDO.getId()));
        // 要更新的检查表id
        List<Long> updateIdList = Lists.newArrayList();
        List<SwStoreWorkTableMappingDO>  tableMappingDOList = Lists.newArrayList();
        List<TbMetaStaTableColumnDO> columnDOList = Lists.newArrayList();
        Date createTime = new Date();
        dutyInfoList.forEach(e -> {
            List<SwStoreWorkTableMappingDO> groupTableMappingList = e.getTableInfoList().stream().map(x ->{
                SwStoreWorkTableMappingDO tableMappingDO = new SwStoreWorkTableMappingDO();
                if(x.getTableMappingId() != null){
                    tableMappingDO.setId(x.getTableMappingId());
                    updateIdList.add(x.getTableMappingId());
                }
                metaTableIds.add(x.getMetaTableId());
                tableMappingDO.setStoreWorkId(storeWorkDO.getId());
                tableMappingDO.setWorkCycle(storeWorkDO.getWorkCycle());
                tableMappingDO.setBeginDate(x.getBeginDate());
                tableMappingDO.setBeginTime(x.getBeginTime());
                tableMappingDO.setLimitHour(x.getLimitHour());
                tableMappingDO.setMetaTableId(x.getMetaTableId());
                tableMappingDO.setDutyName(x.getDutyName());
                tableMappingDO.setGroupNum(e.getGroupNum());
                tableMappingDO.setCreateTime(createTime);
                tableMappingDO.setCreateUserId(createUserId);
                tableMappingDO.setUpdateUserId(createUserId);
                tableMappingDO.setHandlePersonInfo(JSONObject.toJSONString(e.getHandlePersonInfo()));
                tableMappingDO.setTableInfo(x.getTableInfo());
                tableMappingDO.setDeleted(false);
                //点评人
                tableMappingDO.setCommentPersonInfo(e.getCommentPersonInfo()==null?JSONObject.toJSONString(Arrays.asList(new StoreWorkCommonDTO())):JSONObject.toJSONString(e.getCommentPersonInfo()));

                // 只更新 标准检查项执行要求
                if (!MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty())){
                    List<TbMetaStaTableColumnDO> columnDOListTmp = x.getColumnInfoList().stream().map(m -> {
                        TbMetaStaTableColumnDO columnDO = new TbMetaStaTableColumnDO();
                        columnDO.setExecuteDemand(JSONObject.toJSONString(m.getExecuteDemand()));
                        columnDO.setId(m.getMetaColumnId());
                        columnDO.setMetaTableId(x.getMetaTableId());
                        return columnDO;
                    }).collect(Collectors.toList());
                    columnDOList.addAll(columnDOListTmp);
                }
                return tableMappingDO;
            }).collect(Collectors.toList());
            tableMappingDOList.addAll(groupTableMappingList);
        });
        if (CollectionUtils.isNotEmpty(oldTableMappingList)) {
            List<SwStoreWorkTableMappingDO> delTableMappings = oldTableMappingList.stream().filter(a -> !updateIdList.contains(a.getId())).collect(Collectors.toList());
            CollectionUtils.emptyIfNull(delTableMappings).stream().forEach(delTableMapping -> {
                delTableMapping.setUpdateUserId(createUserId);
                delTableMapping.setDeleted(true);
                tableMappingDOList.add(delTableMapping);
            });
        }
        return Pair.of(tableMappingDOList, columnDOList);
    }

    @Override
    public List<SwStoreWorkTableMappingDO> listByStoreWorkId(String enterpriseId, Long storeWorkId) {
        return swStoreWorkTableMappingDao.listByStoreWorkId(enterpriseId, storeWorkId);
    }

}
