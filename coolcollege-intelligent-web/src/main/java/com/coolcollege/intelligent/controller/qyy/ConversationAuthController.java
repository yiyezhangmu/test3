package com.coolcollege.intelligent.controller.qyy;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.achievement.qyy.dto.UpdateConversationAuthDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.ConversationAuthVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.UserConversationAuthVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.achievement.qyy.ConversationAuthService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: ConversationAuthController
 * @Description:
 * @date 2023-04-07 10:42
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/conversation/auth")
@Api(tags = "群入口权限")
@Slf4j
public class ConversationAuthController {

    @Resource
    private ConversationAuthService conversationAuthService;

    @ApiOperation("群入口权限编辑")
    @PostMapping("/updateConversationAuth")
    public ResponseResult updateConversationAuth(@PathVariable("enterprise-id") String enterpriseId, @RequestBody UpdateConversationAuthDTO param){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(conversationAuthService.updateConversationAuth(enterpriseId, UserHolder.getUser().getUserId(), param));
    }

    @ApiOperation("获取群场景权限")
    @GetMapping("/getConversationAuth")
    public ResponseResult<ConversationAuthVO> getConversationAuth(@PathVariable("enterprise-id") String enterpriseId, @RequestParam("sceneCode")String sceneCode){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(conversationAuthService.getConversationAuth(enterpriseId, sceneCode));
    }

    @ApiOperation("用户获取所有群场景权限")
    @GetMapping("/getUserConversationAuth")
    public ResponseResult<List<UserConversationAuthVO>> getUserConversationAuth(@PathVariable("enterprise-id") String enterpriseId){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(conversationAuthService.getUserConversationAuth(enterpriseId, UserHolder.getUser().getUserId()));
    }

}
