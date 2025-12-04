package com.coolcollege.intelligent.service.mapstrack.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.dao.mapstrack.MapStrackMapper;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.mapstrack.MapStrackService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 地图轨迹基本操作
 *
 * @author Aaron
 * @ClassName MapsTrackController
 * @Description 地图轨迹基本操作
 */
@Service
@Slf4j
public class MapStrackServiceImpl implements MapStrackService {


    @Resource
    private MapStrackMapper mapStrackMapper;


    @Autowired
    private AuthVisualService authVisualService;


    /**
     * 门店地图
     *
     * @param map
     * @param enterpriseId
     * @return map
     * @throws Exception
     * @Description 门店地图
     */
    @Override
    public Object queryStoreMap(String enterpriseId, Map<String, Object> map, Integer pageNum, Integer pageSize) {
        DataSourceHelper.changeToMy();
//        DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");
//        PageHelper.startPage(pageNum, pageSize);
        CurrentUser user = UserHolder.getUser();
        AuthVisualDTO authVisual = authVisualService.authRegionStoreByRole(enterpriseId, user.getUserId());
        if (!authVisual.getIsAllStore()) {
            if (CollUtil.isEmpty(authVisual.getStoreIdList())) {
                return new ArrayList<>();
            } else {
                map.put("storeIds", authVisual.getStoreIdList());
            }
        }
        map.put("eid", enterpriseId);
        map.put("isDelete", StoreIsDeleteEnum.EFFECTIVE.getValue());

        List<Map<String, Object>> storeList = mapStrackMapper.getStoreList(map);
        storeList = ListUtils.emptyIfNull(storeList).stream().filter(m -> StrUtil.isNotBlank(MapUtil.getStr(m, "longitude_latitude"))).collect(Collectors.toList());

        return storeList;
    }


}
