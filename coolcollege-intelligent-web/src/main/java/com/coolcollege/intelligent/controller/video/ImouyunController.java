package com.coolcollege.intelligent.controller.video;

import com.coolcollege.intelligent.common.annotation.ErrorHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2022/05/09
 */
@RestController
@ErrorHelper
@RequestMapping("/v3/enterprises/{enterprise-id}/imou")
public class ImouyunController {
//    @Autowired
//    private ImouDeviceService imouDeviceService;
//    @GetMapping("/createUrl")
//    public ResponseResult createUrl(@PathVariable("enterprise-id") String eid,
//                                    @RequestParam(value = "storeId", required = false) String storeId,
//                                    @RequestParam(value = "userId", required = false) String userId) {
//        return ResponseResult.success(imouDeviceService.createYingshiAuthUrl(eid, storeId, userId));
//    }

}
