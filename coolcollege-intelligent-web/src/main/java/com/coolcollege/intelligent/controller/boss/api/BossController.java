package com.coolcollege.intelligent.controller.boss.api;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserQueryDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolBiosVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolCheckResultVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterprisePatrolLevelVO;
import com.coolcollege.intelligent.service.boss.BossUserService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseStoreCheckSettingService;
import com.coolcollege.intelligent.service.passengerflow.PassengerFlowService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 邵凌志
 * @date 2021/1/28 9:38
 */
@RestController
@RequestMapping({"/boss/api/bossController"})
@BaseResponse
@Slf4j
public class BossController {

    @Autowired
    EnterpriseStoreCheckSettingService enterpriseStoreCheckSettingService;

    @Autowired
    private BossUserService bossUserService;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Autowired
    private PassengerFlowService passengerFlowService;

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    /**
     * 获取巡店bios配置
     * @return
     */
    @GetMapping("/getCheckResultAndLevelInfo")
    public EnterprisePatrolBiosVO getCheckResultAndLevelInfo(@RequestParam("enterpriseId") String eid) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.getPatrolBiosInfo(eid);
    }

    /**
     * 获取巡店等级信息
     * @return
     */
    @GetMapping("/getLevelInfo")
    public EnterprisePatrolLevelVO getLevelInfo(@RequestParam("enterpriseId") String eid) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.getStoreCheckLevel(eid);
    }

    /**
     * 获取巡店检查结果信息
     * @return
     */
    @GetMapping("getCheckResultInfo")
    public EnterprisePatrolCheckResultVO getCheckResultInfo(@RequestParam("enterpriseId") String eid) {
        DataSourceHelper.reset();
        return enterpriseStoreCheckSettingService.getStoreCheckResult(eid);
    }

    /**
     * 创建自动关流调度器
     * @return
     */
    @GetMapping("/update/passenger")
    public ResponseResult updatePassenger(@RequestParam("eid") String eid,
                                          @RequestParam("id") Long id,
                                          @RequestParam("flowIn") Integer flowIn,
                                          @RequestParam("flowOut") Integer flowOut,
                                          @RequestParam("flowInOut") Integer flowInOut){

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        passengerFlowService.updatePassenger(eid,id,flowIn,flowOut,flowInOut);
        return ResponseResult.success(true);
    }


    @GetMapping("getTokenByEidAndUserID")
    public ResponseResult getTokenByEidAndUserID(@RequestParam("enterpriseId") String eid,
                                             @RequestParam("userId") String userId){
        return bossUserService.bossGetTokenByEidAndUserID(eid,userId);
    }

    @GetMapping(path = "/userList")
    public ResponseResult getUserList(@RequestParam(value = "enterpriseId", required = true) String eid,
                                      @RequestParam(name = "userName", required = false, defaultValue = "") String userName,
                                      @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        EnterpriseUserQueryDTO enterpriseUserQueryDTO = new EnterpriseUserQueryDTO();
        enterpriseUserQueryDTO.setUserName(userName);
        enterpriseUserQueryDTO.setPage_num(pageNum);
        enterpriseUserQueryDTO.setPage_size(pageSize);
        return ResponseResult.success(PageHelperUtil.getPageInfo(enterpriseUserService.getUserListNew(eid, enterpriseUserQueryDTO)));
    }

}
