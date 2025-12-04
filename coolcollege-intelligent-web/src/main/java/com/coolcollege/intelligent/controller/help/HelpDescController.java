package com.coolcollege.intelligent.controller.help;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.help.HelpDescDO;
import com.coolcollege.intelligent.service.help.HelpDescService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/8 14:09
 */
@RestController
@RequestMapping("/v3/system/help")
@BaseResponse
@Slf4j
public class HelpDescController {

    @Autowired
    private HelpDescService helpDescService;

    @PostMapping(path = "/add")
    public ResponseResult addHelpDesc(@RequestBody HelpDescDO help) {
        DataSourceHelper.reset();
        helpDescService.insertHelpDesc(help);
        return ResponseResult.success(true);
    }

    @PostMapping(path = "/change")
    public ResponseResult changeHelpDesc(@RequestBody HelpDescDO help) {
        DataSourceHelper.reset();
        helpDescService.updateHelpDescById(help);
        return ResponseResult.success(true);
    }

    @PostMapping(path = "/changecontent")
    public ResponseResult changeHelpContent(@RequestBody HelpDescDO help) {
        DataSourceHelper.reset();
        helpDescService.updateHelpDescByPath(help);
        return ResponseResult.success(true);
    }
    @PostMapping(path = "/delete")
    public ResponseResult deleteHelpDesc(@RequestParam (value = "id") Long id) {
        DataSourceHelper.reset();
        helpDescService.deleteHelpDescById(id);
        return ResponseResult.success(true);
    }

    @GetMapping(path = "/info")
    public ResponseResult getDisplaySubDetail(@RequestParam (value = "id", required = false) Long id,
                                              @RequestParam (value = "path", required=false) String path) {
        DataSourceHelper.reset();
        return ResponseResult.success(helpDescService.getHelpDesc(id, path));
    }
}
