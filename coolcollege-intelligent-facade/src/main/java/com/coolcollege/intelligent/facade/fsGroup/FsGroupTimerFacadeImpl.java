package com.coolcollege.intelligent.facade.fsGroup;


import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.service.fsGroup.FsGroupService;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;


@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.FS_GROUP_FACADE_UNIQUE_ID, interfaceType = FsGroupTimerFacade.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class FsGroupTimerFacadeImpl implements FsGroupTimerFacade {

    @Resource
    private FsGroupService fsGroupService;

    @Override
    public ResultDTO<Integer> sendFsGroupNotice(String eid) throws ApiException {
        try {
            log.info("定时扫描发送群公告：eid：{}", eid);
            fsGroupService.searchChatNotice(eid);
        }catch (Exception e){
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }

    @Override
    public ResultDTO<Integer> queryFsGroupNoticeReadNum(String enterpriseId) throws ApiException {
        try {
            log.info("查询群公告已读人数：enterpriseId：{}", enterpriseId);
            fsGroupService.queryChatNoticeReadNum(enterpriseId);
        }catch (Exception e){
            throw new ApiException(e);
        }
        return ResultDTO.SuccessResult();
    }


}
