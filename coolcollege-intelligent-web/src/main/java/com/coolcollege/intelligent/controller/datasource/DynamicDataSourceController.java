package com.coolcollege.intelligent.controller.datasource;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.service.datasource.DynamicDataSourceService;
import com.coolcollege.intelligent.model.datasource.CorpDataSourceNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName DynamicDataSourceController
 * @Description 用一句话描述什么
 * @author 首亮
 */
@RestController
@BaseResponse
public class DynamicDataSourceController {

    @Autowired
    private DynamicDataSourceService dynamicDataSourceService;

    @PostMapping(path = "/datasources/nodes")
    public void addNode() {
        dynamicDataSourceService.updateDataSourceNode();

    }
}
