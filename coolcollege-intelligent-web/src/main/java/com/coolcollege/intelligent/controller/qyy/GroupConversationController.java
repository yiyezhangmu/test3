package com.coolcollege.intelligent.controller.qyy;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dto.OpGroupConversationScopeDTO;
import com.coolcollege.intelligent.dto.OpenApiPushCardMessageDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.GroupConversationVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.scopeandScene.VO.OpGroupConversationScopeVO;
import com.coolcollege.intelligent.service.achievement.qyy.GroupConversationService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author wxp
 * @FileName: GroupConversationController
 * @Description:群列表
 * @date 2023-04-07 10:35
 */
@RestController
@RequestMapping("/v3/enterprises/{enterprise-id}/qyy/groupConversation")
@Api(tags = "群会话")
@Slf4j
public class GroupConversationController {

    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;

    @Autowired
    private GroupConversationService groupConversationService;


    @ApiOperation("群列表")
    @GetMapping("/listGroupConversation")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "conversationTitle", value = "群名称"),
            @ApiImplicitParam(name = "conversationType", value = "其他群: other, 区域群: region, 门店群: store")
    })
    public ResponseResult<List<GroupConversationVO>> listGroupConversation(@PathVariable("enterprise-id") String enterpriseId,
                                                                           @RequestParam("conversationType") String conversationType,
                                                                           @RequestParam(value = "conversationTitle", required = false) String conversationTitle){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        List<GroupConversationVO> groupConversationVOList = groupConversationService.listGroupConversation(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), conversationType, conversationTitle);
        return ResponseResult.success(groupConversationVOList);
    }

    @ApiOperation("通过群id和场景code查找业务范围")
    @GetMapping("/getScopeByOpenCidAndSceneCode")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openConversationId", value = "开放群id"),
            @ApiImplicitParam(name = "sceneCode", value = "场景code")
    })
    public ResponseResult<OpGroupConversationScopeVO> getScopeByOpenCidAndSceneCode(@PathVariable("enterprise-id") String enterpriseId,
                                                                                     @RequestParam("openConversationId") String openConversationId,
                                                                                     @RequestParam("sceneCode") String sceneCode){

        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        OpGroupConversationScopeVO opGroupConversationScopeDTO = groupConversationService.getScopeByOpenCidAndSceneCode(enterpriseConfigDO, enterpriseConfigDO.getAppType(), openConversationId, sceneCode);
        return ResponseResult.success(opGroupConversationScopeDTO);
    }

    @ApiOperation("推送消息")
    @PostMapping("/pushCardMessage")
    public ResponseResult<Boolean> pushCardMessage(@PathVariable("enterprise-id") String enterpriseId,
                                                   @RequestBody OpenApiPushCardMessageDTO.MessageData param){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfigDO = enterpriseConfigMapper.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(enterpriseConfigDO.getDbName());
        groupConversationService.pushCardMessage(enterpriseConfigDO.getDingCorpId(), enterpriseConfigDO.getAppType(), param);
        return ResponseResult.success(true);
    }



    /*@ApiOperation("卡片消息回调")
    @RequestMapping("/cardMsgCallBack")
    public ResponseResult cardMsgCallBack(@RequestParam("param") String param) {
        log.info("卡片消息回调：{}", param);
        return ResponseResult.success();
    }*/

}
