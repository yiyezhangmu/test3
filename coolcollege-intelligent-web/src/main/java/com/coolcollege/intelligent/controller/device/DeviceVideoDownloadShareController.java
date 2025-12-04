package com.coolcollege.intelligent.controller.device;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * describe: 录像下载分享
 *
 * @author wangff
 * @date 2025/1/9
 */
@RestController
@RequestMapping({"/video/download/share/{enterprise-id}"})
@Slf4j
public class DeviceVideoDownloadShareController {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private DeviceService deviceService;

    @GetMapping("/{id}")
    public ResponseEntity<?> videoDownloadShare(@PathVariable(name = "enterprise-id") String enterpriseId,
                                             @PathVariable(name = "id") Long id) {
        DataSourceHelper.reset();
        EnterpriseConfigDO configDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(configDO.getDbName());
        String downloadUrl = deviceService.download(enterpriseId, id);
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", downloadUrl).build();
    }
}
