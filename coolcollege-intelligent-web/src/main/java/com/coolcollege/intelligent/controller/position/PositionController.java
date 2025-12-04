package com.coolcollege.intelligent.controller.position;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.model.position.dto.PositionSearchRespDTO;
import com.coolcollege.intelligent.model.position.queryDto.PositionQueryDTO;
import com.coolcollege.intelligent.model.system.dto.RoleDTO;
import com.coolcollege.intelligent.service.position.PositionService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName PositionController
 * @Description 岗位
 */
@RestController
@RequestMapping("/v2/enterprises/{enterprise-id}/positions")
@BaseResponse
@Slf4j
public class PositionController {

    @Autowired
    private PositionService positionService;

    @GetMapping("/get")
    public Object getPositionList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                  @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                  @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                  @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword) {

        PositionQueryDTO positionQueryDTO = new PositionQueryDTO();
        positionQueryDTO.setKeyword(keyword);
        positionQueryDTO.setPage_num(pageNum);
        positionQueryDTO.setPage_size(pageSize);

        DataSourceHelper.changeToMy();

        return new PositionSearchRespDTO(positionService.getPositionList(enterpriseId, positionQueryDTO));
    }
    @GetMapping("/get/page")
    public ResponseResult getPositionPageList(@PathVariable(value = "enterprise-id", required = false) String enterpriseId,
                                  @RequestParam(value = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                  @RequestParam(value = "page_size", defaultValue = "20", required = false) Integer pageSize,
                                  @RequestParam(value = "keyword", defaultValue = "", required = false) String keyword) {

        PositionQueryDTO positionQueryDTO = new PositionQueryDTO();
        positionQueryDTO.setKeyword(keyword);
        positionQueryDTO.setPage_num(pageNum);
        positionQueryDTO.setPage_size(pageSize);

        DataSourceHelper.changeToMy();
//        DataSourceHelper.changeToSpecificDataSource("coolcollege_intelligent_2");

        List<RoleDTO> positionPageList = positionService.getPositionPageList(enterpriseId, positionQueryDTO);
        if(CollectionUtils.isNotEmpty(positionPageList)){
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>(positionPageList)));
        }else {
            return ResponseResult.success(PageHelperUtil.getPageInfo(new PageInfo<>()));

        }
    }


}
