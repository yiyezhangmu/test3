package com.coolcollege.intelligent.controller.mapstrack;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.service.mapstrack.MapStrackService;

import lombok.extern.slf4j.Slf4j;

/**
 * 地图轨迹基本操作
 * @ClassName  MapsTrackController
 * @Description 地图轨迹基本操作
 * @author Aaron
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v2/enterprises/{enterprise-id}/map")
public class MapStrackController {


    @Autowired
    private MapStrackService mapStrackService;



    /**
     * 门店地图
     * @Description 门店地图
     * @param map
     * @return map
     * @throws Exception
     */
    @PostMapping("/store_map")
    @Deprecated
    //FIXME 待清理
    public Object queryStoreMap(@PathVariable("enterprise-id") String enterpriseId, @RequestBody(required = false) Map<String, Object> map,
                                            @RequestParam(value = "page_number", defaultValue = "1", required = false) Integer pageNumber,
                                            @RequestParam(value = "page_size", defaultValue = "10", required = false) Integer pageSize) {
        return mapStrackService.queryStoreMap(enterpriseId, map, pageNumber, pageSize);
    }


}
