package com.coolcollege.intelligent.controller.messageboard;

import com.cool.store.response.ResponseResult;
import com.coolcollege.intelligent.model.messageboard.dto.MessageBoardDTO;
import com.coolcollege.intelligent.model.messageboard.entity.MessageBoardDO;
import com.coolcollege.intelligent.model.messageboard.vo.MessageBoardVO;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.messageboard.MessageBoardService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author wxp
 * @FileName: MessageBoardController
 * @Description:
 * @date 2024-07-29 16:24
 */
@Api(tags = "留言板")
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/messageboard")
@Slf4j
public class MessageBoardController {

    @Resource
    private MessageBoardService messageBoardService;

    @ApiOperation("获取留言列表")
    @GetMapping("/list")
    public ResponseResult<PageInfo<MessageBoardVO>> getMessagePage(@PathVariable("enterprise-id") String enterpriseId,
                                                                   @RequestParam("businessId")String businessId, @RequestParam("businessType")String businessType,
                                                                   @RequestParam("pageNum")Integer pageNum, @RequestParam("pageSize") Integer pageSize){
        DataSourceHelper.changeToMy();
        return ResponseResult.success(messageBoardService.getMessagePage(enterpriseId, businessId, businessType, pageNum, pageSize));
    }

    @ApiOperation("留言/点赞")
    @PostMapping("/leaveMessageOrLike")
    public ResponseResult<MessageBoardVO> leaveMessageOrLike(@PathVariable("enterprise-id") String enterpriseId, @RequestBody @Valid MessageBoardDTO param){
        String userId = UserHolder.getUser().getUserId();
        DataSourceHelper.changeToMy();
        return ResponseResult.success(messageBoardService.leaveMessageOrLike(enterpriseId, userId, param));
    }

}
