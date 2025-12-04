package com.coolcollege.intelligent.controller.homepage;

import com.cool.store.enums.BusinessCycleEnum;
import com.cool.store.enums.SortFieldEnum;
import com.cool.store.enums.SortTypeEnum;
import com.cool.store.response.ResponseResult;
import com.cool.store.rpc.model.AuthDataStatisticRpcRequestDTO;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.model.homepage.dto.DataStatisticRequestDTO;
import com.coolcollege.intelligent.model.homepage.vo.DisplayRegionDataVO;
import com.coolcollege.intelligent.model.homepage.vo.PatrolRegionDataVO;
import com.coolcollege.intelligent.model.homepage.vo.QuestionRegionDataVO;
import com.coolcollege.intelligent.model.homepage.vo.TableAverageScoreVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.homepage.HomePageDataService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author zhangchenbiao
 * @FileName: HomePageDataStatisticController
 * @Description: 首页数据统计
 * @date 2022-06-23 9:58
 */
@Api(tags = "首页数据")
@RestController
@RequestMapping("/home/page/{enterprise-id}")
public class HomePageDataStatisticController {

    @Resource
    private HomePageDataService homePageDataService;
    @Resource
    private RegionService regionService;


    @ApiOperation("获取巡店数据")
    @PostMapping("/getPatrolDataStatistic")
    public ResponseResult<PatrolRegionDataVO> getPatrolDataStatistic(@PathVariable("enterprise-id") String enterpriseId, @RequestBody DataStatisticRequestDTO param){
        AuthDataStatisticRpcRequestDTO rpcRequestDTO = getRpcRequestDTO(enterpriseId, param);
        if(CollectionUtils.isEmpty(rpcRequestDTO.getRegionLists())){
            return ResponseResult.success(new PatrolRegionDataVO(null, null));
        }
        return ResponseResult.success(homePageDataService.getPatrolDataStatistic(rpcRequestDTO));
    }

    @ApiOperation("获取工单数据")
    @PostMapping("/getQuestionDataStatistic")
    public ResponseResult<QuestionRegionDataVO> getQuestionDataStatistic(@PathVariable("enterprise-id") String enterpriseId, @RequestBody DataStatisticRequestDTO param){
        AuthDataStatisticRpcRequestDTO rpcRequestDTO = getRpcRequestDTO(enterpriseId, param);
        if(CollectionUtils.isEmpty(rpcRequestDTO.getRegionLists())){
            return ResponseResult.success(new QuestionRegionDataVO(null, null));
        }
        return ResponseResult.success(homePageDataService.getQuestionDataStatistic(rpcRequestDTO));
    }

    @ApiOperation("获取检查表平均分数据")
    @PostMapping("/getTableAvgScoreStatistic")
    public ResponseResult<TableAverageScoreVO> getTableAverageScoreStatistic(@PathVariable("enterprise-id") String enterpriseId, @RequestBody DataStatisticRequestDTO param){
        AuthDataStatisticRpcRequestDTO rpcRequestDTO = getRpcRequestDTO(enterpriseId, param);
        if(CollectionUtils.isEmpty(rpcRequestDTO.getRegionLists())){
            return ResponseResult.success(new TableAverageScoreVO(null, null));
        }
        return ResponseResult.success(homePageDataService.getTableAverageScoreStatistic(rpcRequestDTO));
    }

    @ApiOperation("获取陈列数据")
    @PostMapping("/getDisplayDataStatistic")
    public ResponseResult<DisplayRegionDataVO> getDisplayDataStatistic(@PathVariable("enterprise-id") String enterpriseId, @RequestBody DataStatisticRequestDTO param){
        AuthDataStatisticRpcRequestDTO rpcRequestDTO = getRpcRequestDTO(enterpriseId, param);
        if(CollectionUtils.isEmpty(rpcRequestDTO.getRegionLists())){
            return ResponseResult.success(new DisplayRegionDataVO(null, null));
        }
        return ResponseResult.success(homePageDataService.getDisplayDataStatistic(rpcRequestDTO));
    }

    /**
     * 获取远程调研dto
     * @param enterpriseId
     * @param queryParam
     * @return
     */
    private AuthDataStatisticRpcRequestDTO getRpcRequestDTO(String enterpriseId, DataStatisticRequestDTO queryParam){
        DataSourceHelper.changeToMy();
        AuthDataStatisticRpcRequestDTO rpcRequestDTO = new AuthDataStatisticRpcRequestDTO();
        rpcRequestDTO.setEnterpriseId(enterpriseId);
        CurrentUser user = UserHolder.getUser();
        if(Objects.isNull(user)){
            throw new ServiceException(ErrorCodeEnum.LOGIN_ERROR);
        }
        rpcRequestDTO.setUserId(user.getUserId());
        SysRoleDO sysRoleDO = user.getSysRoleDO();
        String roleEnum = Optional.ofNullable(sysRoleDO).map(o -> o.getRoleEnum()).orElse(null);
        rpcRequestDTO.setIsAdmin(Boolean.FALSE);
        if(Role.MASTER.getRoleEnum().equals(roleEnum)){
            rpcRequestDTO.setIsAdmin(Boolean.TRUE);
        }
        BusinessCycleEnum businessCycleEnum = BusinessCycleEnum.getBusinessCycleEnumByCode(queryParam.getBusinessCycle());
        if(Objects.isNull(businessCycleEnum)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        rpcRequestDTO.setBusinessCycle(businessCycleEnum);
        rpcRequestDTO.setTimeUnion(queryParam.getTimeUnion());
        if(StringUtils.isNotBlank(queryParam.getSortType())){
            SortTypeEnum sortTypeEnum = SortTypeEnum.getSortTypeEnum(queryParam.getSortType());
            if(Objects.isNull(sortTypeEnum)){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
            }
            rpcRequestDTO.setSortType(sortTypeEnum);
        }
        if(StringUtils.isNotBlank(queryParam.getSortField())){
            SortFieldEnum sortFieldEnum = SortFieldEnum.getSortFieldEnum(queryParam.getSortField());
            if(Objects.isNull(sortFieldEnum)){
                throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
            }
            rpcRequestDTO.setSortField(sortFieldEnum);
        }
        rpcRequestDTO.setLimitNum(queryParam.getLimitNum());
        rpcRequestDTO.setIsNeedCompare(queryParam.getIsNeedCompare());
        if(StringUtils.isNotBlank(queryParam.getStoreId())){
            RegionDO regionDO = regionService.getByStoreId(enterpriseId, queryParam.getStoreId());
            if(Objects.isNull(regionDO)){
                throw new ServiceException(ErrorCodeEnum.ACH_TARGET_STORE_NOT_EXIST);
            }
            AuthDataStatisticRpcRequestDTO.RegionType storeRegion = new AuthDataStatisticRpcRequestDTO.RegionType();
            storeRegion.setRegionId(regionDO.getId());
            storeRegion.setRegionName(regionDO.getName());
            storeRegion.setRegionType(regionDO.getRegionType());
            storeRegion.setStoreNum(Long.valueOf(regionDO.getStoreNum()));
            rpcRequestDTO.setRegionLists(Arrays.asList(storeRegion));
        }else{
            //处理权限数据
            homePageDataService.dealAuthRegion(rpcRequestDTO);
        }
        return rpcRequestDTO;
    }
}
