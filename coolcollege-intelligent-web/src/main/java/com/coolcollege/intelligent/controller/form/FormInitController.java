package com.coolcollege.intelligent.controller.form;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.service.form.FormInitializeService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/13
 */
@RestController
@Slf4j
@BaseResponse
@RequestMapping("/v2/enterprises/form")
public class FormInitController {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private FormInitService formInitService;

    @Autowired
    private FormInitializeService initializeService;

    @Lazy
    @Autowired
    private UnifyTaskService unifyTaskService;

    @Resource
    private EnterpriseConfigMapper configMapper;



    @GetMapping(path = "/init/formData")
    public ResponseResult initData(@RequestParam(value = "type")String type) {
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOS = enterpriseConfigMapper.selectEnterpriseConfigAll();
        Map<String,List<EnterpriseConfigDO>> collect = enterpriseConfigDOS.stream()
                .collect(
                        Collectors.groupingBy(
                                EnterpriseConfigDO::getDbName/*, Collectors.counting()*/
                        )
                );
        AtomicInteger count = new AtomicInteger(0);
        for (Map.Entry<String, List<EnterpriseConfigDO>> entry : collect.entrySet()) {
             formInitService.initData(entry.getKey(), entry.getValue(),count, type);
            //formInitService.initScore("45f92210375346858b6b6694967f44de", "coolcollege_intelligent_2");

        }
        return ResponseResult.success(true);
    }

    @GetMapping("/init/defaulttemplate")
    public ResponseResult defaultCheckItem(@RequestParam(value = "eid") String eid) {
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = configMapper.selectByEnterpriseId(eid);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        return ResponseResult.success(initializeService.defaultDisplayTemplate(eid));
    }


}
