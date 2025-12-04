package com.coolcollege.intelligent.controller.operationboard;

import javax.annotation.Resource;
import javax.validation.Valid;

import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardStatisticDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.operationboard.query.TableBoardQuery;
import com.coolcollege.intelligent.service.operationboard.TableBoardService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yezhe
 * @date 2021-01-08 14:52
 */
@RestController
@RequestMapping({"/v3/enterprises/{enterprise-id}/operationboard/tableboard"})
@BaseResponse
@Slf4j
public class TableBoardController {

    @Resource
    private TableBoardService tableBoardService;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;

    /**
     * 汇总信息
     */
    @PostMapping("/statistics")
    public ResponseResult statistics(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody @Valid TableBoardQuery query) {
        DataSourceHelper.changeToMy();
        List<TbMetaTableDO> metaTableDOList = getDefaultTable(enterpriseId,query);
        TableBoardStatisticDTO result = tableBoardService.statistics(enterpriseId, query);
        result.setDefaultTableList(metaTableDOList);
        return ResponseResult.success(result);
    }

    /**
     * 方案排名
     */
    @PostMapping("/rank")
    public ResponseResult rank(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
        @RequestBody @Valid TableBoardQuery query) {
        DataSourceHelper.changeToMy();
        getDefaultTable(enterpriseId, query);
        return ResponseResult.success(tableBoardService.rank(enterpriseId, query));
    }

    private List<TbMetaTableDO> getDefaultTable(@PathVariable(value = "enterprise-id", required = false) String enterpriseId, @RequestBody @Valid TableBoardQuery query) {
        List<TbMetaTableDO> metaTableDOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(query.getMetaTableIds())) {
            metaTableDOList = tbMetaTableMapper.getDefaultMetaTable(enterpriseId, 5);
            List<Long> metaTableIds = metaTableDOList.stream().map(data -> data.getId()).collect(Collectors.toList());
            query.setMetaTableIds(metaTableIds);
            query.setDefaultTables(metaTableDOList);
        }
        return metaTableDOList;
    }

    /**
     * 方案趋势表
     */
    @PostMapping("/trend")
    public ResponseResult trend(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                               @RequestBody @Valid TableBoardQuery query) {
        DataSourceHelper.changeToMy();
        getDefaultTable(enterpriseId, query);
        return ResponseResult.success(tableBoardService.trend(enterpriseId, query));
    }

}
