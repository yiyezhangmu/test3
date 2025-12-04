package com.coolcollege.intelligent.controller.wecom;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;
import com.coolcollege.intelligent.model.qywx.dto.TemplateMsgDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.platform.PlatformExpandInfoService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.coolstore.base.enums.RocketMqTagEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 企业微信回调controller
 * @author ：xugangkun
 * @date ：2021/6/3 11:47
 */
@RestController
@Slf4j
public class WeComController {

    @Resource
    private EnterpriseConfigService enterpriseConfigService;

    @Resource
    private PlatformExpandInfoService platformExpandInfoService;

    @Resource
    private SimpleMessageService simpleMessageService;

    /**
     * 酷渲科技企微应用校验
     * @param
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/6/3 9:47
     */
    @GetMapping("/WW_verify_gbRti27HDz8hyIb0.txt")
    public String WW_verify() {
        log.info("企业微信回调校验接口：WW_verify_gbRti27HDz8hyIb0.txt。Response：gbRti27HDz8hyIb0");
        return "gbRti27HDz8hyIb0";
    }

    /**
     * 酷店掌企微应用校验
     * @param
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/8/18 9:47
     */
    @GetMapping("/WW_verify_LNFOo6kZmb7MWy1p.txt")
    public String WW_verify2() {
        log.info("企业微信回调校验接口：WW_verify_LNFOo6kZmb7MWy1p.txt。Response：LNFOo6kZmb7MWy1p");
        return "LNFOo6kZmb7MWy1p";
    }

    /**
     * 酷店掌企微应用校验
     * @param
     * @author: xugangkun
     * @return java.lang.String
     * @date: 2021/8/18 9:47
     */
    @PostMapping("/weCom/sentMemberMsg")
    public ResponseResult sentMemberMsg(@RequestBody @Valid TemplateMsgDTO msgDto) {
        DataSourceHelper.reset();
        log.info("sentMemberMsg msgDto:{}", JSONObject.toJSONString(msgDto));
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(msgDto.getEid());
        if (!Constants.INDEX_ONE.equals(config.getLicenseType())) {
            return ResponseResult.fail(ErrorCodeEnum.AUTHORIZATION_TYPE_EXCEPTION);
        }
        if (msgDto.getSelectedTicketList().size() > Constants.INDEX_TEN) {
            return ResponseResult.fail(ErrorCodeEnum.TICKET_TOO_LONG);
        }
        msgDto.setAppType(config.getAppType());
        msgDto.setCorpId(config.getDingCorpId());
        simpleMessageService.send(JSONObject.toJSONString(msgDto), RocketMqTagEnum.QW_MEMBER_TEMPLE_MSG_QUEUE);
        return ResponseResult.success();
    }

    /**
     * 判断企业是否再导出白名单内
     * @param enterpriseId
     * @author: xugangkun
     * @return com.coolcollege.intelligent.common.response.ResponseResult
     * @date: 2021/12/20 10:38
     */
    @GetMapping("judgeExportWhitelist")
    public ResponseResult judgeExportWhitelist(@RequestParam(value = "enterpriseId") String enterpriseId){
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        //非企微企业，不需要屏蔽导出
        if (!AppTypeEnum.isQwType(config.getAppType())) {
            return ResponseResult.success(true);
        }
        //未设置，默认为不需要屏蔽导出
        PlatformExpandInfoDO platformExpandInfo = platformExpandInfoService.selectByCode(Constants.USER_EXPORT_WHITELIST);
        if (platformExpandInfo == null || !platformExpandInfo.getValid() || StringUtils.isBlank(platformExpandInfo.getContent())) {
            return ResponseResult.success(true);
        }
        int start = platformExpandInfo.getContent().indexOf(Constants.COMMA + enterpriseId + Constants.COMMA);
        if (start < Constants.ZERO) {
            return ResponseResult.success(false);
        }
        return ResponseResult.success(true);
    }

}
