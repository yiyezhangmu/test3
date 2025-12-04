package com.coolcollege.intelligent.controller.enterpriseMenu;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterpriseMenu.dao.EnterpriseMenuInfoDAO;
import com.coolcollege.intelligent.model.enterpriseMenu.EnterpriseMenuInfoDO;
import com.coolcollege.intelligent.model.enterpriseMenu.EnterpriseMenuInfoDTO;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping({"/enterpriseMenu/{enterprise-id}"})
@BaseResponse
public class EnterpriseMenuInfoController {

    @Resource
    private EnterpriseMenuInfoDAO enterpriseMenuInfoDAO;


    @GetMapping("/getByEnterpriseId")
    public ResponseResult<EnterpriseMenuInfoDO> getByEnterpriseId(@PathVariable("enterprise-id") String eid) {
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseMenuInfoDAO.getByEnterpriseId(eid));
    }


    @PostMapping("/createOrUpdate")
    public ResponseResult<Void> updateUserMenuApp(@PathVariable("enterprise-id") String eid,
                                                  @RequestBody EnterpriseMenuInfoDTO dto) {
        DataSourceHelper.reset();
        dto.setEnterpriseId(eid);
        enterpriseMenuInfoDAO.createOrUpdateByEnterpriseId(dto);
        return ResponseResult.success();
    }
}
