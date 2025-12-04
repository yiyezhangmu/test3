package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.FileUtil;
import com.coolcollege.intelligent.model.enterprise.dto.DeviceAuthReportPageDTO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseActivationPageDTO;
import com.coolcollege.intelligent.model.enterprise.vo.DeviceAuthReportVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseActivationVO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseActivationService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
public class EnterpriseActivationController {

    @Resource
    private EnterpriseActivationService enterpriseActivationService;

    @PostMapping("/boss/manage/getEnterpriseActivationPage")
    public ResponseResult<PageInfo<EnterpriseActivationVO>> getEnterpriseActivationPage(@RequestBody EnterpriseActivationPageDTO param) {
        DataSourceHelper.reset();
        PageInfo<EnterpriseActivationVO> pageInfo = enterpriseActivationService.getEnterpriseActivationPage(param);
        return ResponseResult.success(pageInfo);
    }

    @PostMapping("/boss/manage/exportEnterpriseActivation")
    public void exportEnterpriseActivation(@RequestBody EnterpriseActivationPageDTO param, HttpServletResponse response) {
        DataSourceHelper.reset();
        param.setPageNum(1);
        param.setPageSize(Constants.ONE_THOUSAND);
        if(param.isNoCondition()){
            param.setTag("数仓");
        }
        PageInfo<EnterpriseActivationVO> pageInfo = enterpriseActivationService.getEnterpriseActivationPage(param);
        List<EnterpriseActivationVO> exportList = pageInfo.getList();
        FileUtil.exportBigDataExcel(exportList, "企业活跃度", "企业活跃度", EnterpriseActivationVO.class, "企业活跃度.xlsx", response);
    }


    @PostMapping("/boss/manage/getDeviceAuthReport")
    public ResponseResult<PageInfo<DeviceAuthReportVO>> getDeviceAuthReport(@RequestBody DeviceAuthReportPageDTO param) {
        DataSourceHelper.reset();
        PageInfo<DeviceAuthReportVO> pageInfo = enterpriseActivationService.getDeviceAuthReport(param);
        return ResponseResult.success(pageInfo);
    }

    @PostMapping("/boss/manage/exportDeviceAuthReport")
    public void exportDeviceAuthReport(@RequestBody DeviceAuthReportPageDTO param, HttpServletResponse response) {
        DataSourceHelper.reset();
        param.setPageNum(1);
        param.setPageSize(Constants.ONE_THOUSAND);
        PageInfo<DeviceAuthReportVO> pageInfo = enterpriseActivationService.getDeviceAuthReport(param);
        List<DeviceAuthReportVO> exportList = pageInfo.getList();
        FileUtil.exportBigDataExcel(exportList, "明厨亮灶", "明厨亮灶", DeviceAuthReportVO.class, "明厨亮灶报表.xlsx", response);
    }

}
