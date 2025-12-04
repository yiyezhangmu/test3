package com.coolcollege.intelligent.facade.qyy;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.dao.region.RegionDao;
import com.coolcollege.intelligent.facade.constants.IntelligentFacadeConstants;
import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.BigOrderBoardDTO;
import com.coolcollege.intelligent.facade.enums.CardDeptTypeEnum;
import com.coolcollege.intelligent.facade.open.api.achieve.qyy.AKAchieveApi;
import com.coolcollege.intelligent.model.department.dto.DeptUserTreeDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.service.achievement.qyy.SendCardService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.Current;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@SofaService(uniqueId = IntelligentFacadeConstants.DEMO_CARD_FACADE_UNIQUE_ID, interfaceType = CardDemoFacade.class, bindings = {@SofaServiceBinding(bindingType = "bolt")})
@Component
public class CardDemoFacadeImpl implements CardDemoFacade {

    @Resource
    private SendCardService sendCardService;

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;

    @Resource
    private RegionDao regionDao;

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Override
    public ResultDTO sendUserOrderTop(String eid){
        DataSourceHelper.reset();
        EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(eid);
        DataSourceHelper.changeToMy();
        List<RegionDO> regionDOList = new ArrayList<>();
        RegionDO HQRegion = regionDao.getRootRegionDo(eid);
        List<RegionDO> allRegion = regionDao.getAllRegion(eid);
        regionDOList.add(HQRegion);
        regionDOList.addAll(allRegion);
        List<EnterpriseUserDO> allUser = enterpriseUserMapper.getAllUser(eid);
        for (RegionDO regionDO : regionDOList) {
            List<RegionDO> allStoreRegionIdsByRegionId = regionDao.getAllStoreRegionIdsByRegionId(eid, regionDO.getId());
            EnterpriseUserDO enterpriseUserDO = allUser.get(new Random(allUser.size()).nextInt());
            RegionDO randomRegion = Objects.isNull(allStoreRegionIdsByRegionId.get(new Random(allStoreRegionIdsByRegionId.size()).nextInt())) ? regionDO : allStoreRegionIdsByRegionId.get(new Random(allStoreRegionIdsByRegionId.size()).nextInt());
            BigOrderBoardDTO param = new BigOrderBoardDTO();
            BigOrderBoardDTO.BigOrderBoard bigOrderBoard = new BigOrderBoardDTO.BigOrderBoard();
            List<BigOrderBoardDTO.BigOrderBoard> bigOrderBoards = new ArrayList<>();
            param.setDingDeptId(regionDO.getThirdDeptId());
            //组织类型 STORE、COMP、HQ
            param.setDeptType(CardDeptTypeEnum.getByRegionType(regionDO.getRegionType()));
            param.setEtlTm(new Date().getTime());
            param.setDeptName(regionDO.getName());
            bigOrderBoard.setCompId(null);
            bigOrderBoard.setCompName(regionDO.getName());
            bigOrderBoard.setSalesAmt(new BigDecimal(new Random().nextInt(201) + 100));
            bigOrderBoard.setSalesTm(new Date());
            bigOrderBoard.setStoreId(Long.valueOf(randomRegion.getStoreId()));
            bigOrderBoard.setStoreName(randomRegion.getName());
            bigOrderBoard.setUserId(enterpriseUserDO.getUserId());
            bigOrderBoard.setUserImage(enterpriseUserDO.getAvatar());
            bigOrderBoard.setUserName(enterpriseUserDO.getName());
            bigOrderBoards.add(bigOrderBoard);
            param.setTopUserList(bigOrderBoards);
            sendCardService.sendUserOrderTop(enterpriseConfig,regionDO,param);
        }
        return ResultDTO.SuccessResult();
    }
}
