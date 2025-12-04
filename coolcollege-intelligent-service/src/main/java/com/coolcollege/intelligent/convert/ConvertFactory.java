package com.coolcollege.intelligent.convert;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.dto.SysDepartmentDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.SysDepartmentDO;
import com.coolcollege.intelligent.model.patrolstore.AiPictureResultMappingDO;
import com.coolcollege.intelligent.model.patrolstore.dto.AiPictureResultMappingDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseDetailUserVO;
import com.coolcollege.intelligent.model.openApi.vo.*;
import com.coolcollege.intelligent.model.region.dto.RegionChildDTO;
import com.coolcollege.intelligent.model.region.dto.RegionNode;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolstore.base.enums.AppTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author chenyupeng
 * @since 2022/1/26
 */
@Component
public class ConvertFactory {

    public final static Integer NUM_MAX = Integer.MAX_VALUE;

    public SysDepartmentDO convertSysDepartmentDTO2SysDepartmentDO(SysDepartmentDTO dto, String appType) {
        if (Objects.isNull(dto) || Objects.isNull(dto.getId()) || Objects.isNull(dto.getName())) {
            return null;
        }
        SysDepartmentDO sysDepartmentDO = new SysDepartmentDO();
        sysDepartmentDO.setName(dto.getName());
        sysDepartmentDO.setId(dto.getId());
        sysDepartmentDO.setParentId(dto.getParentId());
        //统一排序 钉钉是升序，保持和钉钉统一，这里用值减去最大值，取绝对值
        if(appType.equals(AppTypeEnum.WX_APP.getValue()) || appType.equals(AppTypeEnum.WX_APP2.getValue())){
            if (Objects.nonNull(dto.getDepartOrder())) {
                sysDepartmentDO.setDepartOrder(Math.abs(dto.getDepartOrder() - NUM_MAX));
            }
        } else {
            sysDepartmentDO.setDepartOrder(dto.getDepartOrder());
        }
        sysDepartmentDO.setAutoAddUser(dto.getAutoAddUser());
        sysDepartmentDO.setDeptManagerUseridList(dto.getDeptManagerUseridList());
        return sysDepartmentDO;
    }

    public List<SysDepartmentDO> convertDeptList(List<SysDepartmentDTO> dtoList, String appType, Long syncId) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return null;
        }
        int deptOrder = 0;
        List<SysDepartmentDO> resultList = new ArrayList<>();
        for (SysDepartmentDTO dto : dtoList) {
            if(Objects.isNull(dto) || Objects.isNull(dto.getId()) || Objects.isNull(dto.getName())){
                continue;
            }
            SysDepartmentDO sysDepartmentDO = new SysDepartmentDO();
            sysDepartmentDO.setName(dto.getName());
            sysDepartmentDO.setId(dto.getId());
            sysDepartmentDO.setParentId(dto.getParentId());
            //统一排序 钉钉是升序，保持和钉钉统一，这里用值减去最大值，取绝对值
            if(appType.equals(AppTypeEnum.WX_APP.getValue()) || appType.equals(AppTypeEnum.WX_APP2.getValue())){
                if (Objects.nonNull(dto.getDepartOrder())) {
                    sysDepartmentDO.setDepartOrder(Math.abs(dto.getDepartOrder() - NUM_MAX));
                }
            } else {
                sysDepartmentDO.setDepartOrder(dto.getDepartOrder());
            }
            sysDepartmentDO.setDepartOrder(deptOrder++);
            sysDepartmentDO.setAutoAddUser(dto.getAutoAddUser());
            sysDepartmentDO.setDeptManagerUseridList(dto.getDeptManagerUseridList());
            sysDepartmentDO.setSyncId(syncId);
            resultList.add(sysDepartmentDO);
        }
        return resultList;
    }

    public EnterpriseUserDO convertEnterpriseUserDTO2EnterpriseUserDO(EnterpriseUserDTO dto){
        if(dto == null){
            return null;
        }
        EnterpriseUserDO enterpriseUserDO = new EnterpriseUserDO();
        enterpriseUserDO.setName(dto.getName());
        enterpriseUserDO.setId(dto.getId());
        enterpriseUserDO.setUserId(dto.getUserId());
        enterpriseUserDO.setTel(dto.getTel());
        enterpriseUserDO.setRemark(dto.getRemark());
        enterpriseUserDO.setMobile(dto.getMobile());
        enterpriseUserDO.setEmail(dto.getEmail());
        enterpriseUserDO.setActive(dto.getActive());
        enterpriseUserDO.setIsAdmin(dto.getIsAdmin());
        enterpriseUserDO.setUnionid(dto.getUnionid());
        enterpriseUserDO.setIsHide(dto.getIsHide());
        enterpriseUserDO.setPosition(dto.getPosition());
        enterpriseUserDO.setAvatar(dto.getAvatar());
        enterpriseUserDO.setIsEnterprise(dto.getIsEnterprise());
        enterpriseUserDO.setRoles(dto.getRoles());
        enterpriseUserDO.setIsLeader(dto.getIsLeader());
        enterpriseUserDO.setCreateTime(dto.getCreateTime());
        enterpriseUserDO.setLanguage(dto.getLanguage());
        enterpriseUserDO.setIsLeaderInDepts(JSONObject.toJSONString(dto.getIsLeaderInDepts()));
        enterpriseUserDO.setJobnumber(dto.getJobnumber());
        enterpriseUserDO.setAppType(dto.getAppType());
        enterpriseUserDO.setPassword(dto.getPassword());
        enterpriseUserDO.setThirdOaUniqueFlag(dto.getThirdOaUniqueFlag());
        return enterpriseUserDO;

    }

    public static OpenApiRoleVO convertSysRoleDO2OpenApiRoleVO(SysRoleDO dto){
        if(dto == null){
            return null;
        }
        OpenApiRoleVO openApiRoleVO = new OpenApiRoleVO();
        openApiRoleVO.setRoleId(dto.getId());
        openApiRoleVO.setRoleName(dto.getRoleName());
        openApiRoleVO.setSource(dto.getSource());
        openApiRoleVO.setPositionType(dto.getPositionType());
        return openApiRoleVO;
    }

    public static OpenApiEnterpriseVO convertEnterpriseDO2OpenApiEnterpriseVO(EnterpriseDO dto){
        if(dto == null){
            return null;
        }
        OpenApiEnterpriseVO openApiEnterpriseVO = new OpenApiEnterpriseVO();
        openApiEnterpriseVO.setId(dto.getId());
        openApiEnterpriseVO.setName(dto.getName());
        openApiEnterpriseVO.setProvince(dto.getProvince());
        openApiEnterpriseVO.setCity(dto.getCity());
        return openApiEnterpriseVO;
    }

    public static OpenApiUserVO convertEnterpriseDetailUserVO2OpenApiUserVO(EnterpriseDetailUserVO dto){
        if(dto == null){
            return null;
        }
        OpenApiUserVO openApiUserVO = new OpenApiUserVO();
        openApiUserVO.setUserId(dto.getUserId());
        openApiUserVO.setUserName(dto.getUserName());
        openApiUserVO.setJobnumber(dto.getJobnumber());
        openApiUserVO.setUserRoles(dto.getUserRoles());
        return openApiUserVO;
    }
    public static OpenApiUserListVO convertEnterpriseUserDO2OpenApiUserListVO(EnterpriseUserDO dto){
        if(dto == null){
            return null;
        }
        OpenApiUserListVO openApiUserListVO = new OpenApiUserListVO();
        openApiUserListVO.setUserId(dto.getUserId());
        openApiUserListVO.setUserName(dto.getName());
        openApiUserListVO.setJobnumber(dto.getJobnumber());
        return openApiUserListVO;
    }

    public static OpenApiRegionVO convertRegionNode2OpenApiRegionVO(RegionNode dto){
        if(dto == null){
            return null;
        }
        OpenApiRegionVO openApiRegionVO = new OpenApiRegionVO();
        openApiRegionVO.setId(dto.getId());
        openApiRegionVO.setName(dto.getName());
        openApiRegionVO.setParentId(dto.getParentId());
        openApiRegionVO.setRegionType(dto.getRegionType());
        openApiRegionVO.setRegionPath(dto.getRegionPath());
        return openApiRegionVO;
    }

    public static OpenApiRegionChildVO convertRegionChildDTO2OpenApiRegionVO(RegionChildDTO dto){
        if(dto == null){
            return null;
        }
        OpenApiRegionChildVO openApiRegionChildVO = new OpenApiRegionChildVO();
        openApiRegionChildVO.setId(Long.valueOf(dto.getId()));
        openApiRegionChildVO.setName(dto.getName());
        openApiRegionChildVO.setParentId(dto.getPid());
        openApiRegionChildVO.setRegionType(dto.getRegionType());
        openApiRegionChildVO.setRegionPath(dto.getRegionPath());
        openApiRegionChildVO.setStoreId(dto.getStoreId());
        return openApiRegionChildVO;
    }
    public static OpenApiStoreVO convertStoreDTO2OpenApiStoreVO(StoreDTO dto){
        if(dto == null){
            return null;
        }
        OpenApiStoreVO openApiStoreVO = new OpenApiStoreVO();
        openApiStoreVO.setStoreId(dto.getStoreId());
        openApiStoreVO.setStoreName(dto.getStoreName());
        openApiStoreVO.setStoreNum(dto.getStoreNum());
        openApiStoreVO.setRegionId(dto.getRegionId());
        return openApiStoreVO;
    }

    public static OpenApiStoreVO convertStoreDO2OpenApiStoreVO(StoreDO dto){
        if(dto == null){
            return null;
        }
        OpenApiStoreVO openApiStoreVO = new OpenApiStoreVO();
        openApiStoreVO.setStoreId(dto.getStoreId());
        openApiStoreVO.setStoreName(dto.getStoreName());
        openApiStoreVO.setStoreNum(dto.getStoreNum());
        openApiStoreVO.setRegionId(dto.getRegionId());
        return openApiStoreVO;
    }

    public static AiPictureResultMappingDO convertAiPictureResultMappingDTO2AiPictureResultMappingDO(AiPictureResultMappingDTO dto){
        if(dto == null){
            return null;
        }
        AiPictureResultMappingDO aiPictureResultMappingDO = new AiPictureResultMappingDO();
        aiPictureResultMappingDO.setId(dto.getId());
        aiPictureResultMappingDO.setCreateTime(dto.getCreateTime());
        aiPictureResultMappingDO.setUpdateTime(dto.getUpdateTime());
        aiPictureResultMappingDO.setPictureId(dto.getPictureId());
        aiPictureResultMappingDO.setMetaColumnId(dto.getMetaColumnId());
        aiPictureResultMappingDO.setAiResult(dto.getAiResult());
        aiPictureResultMappingDO.setAiContent(JSONObject.toJSONString(dto.getAiContent()));
        return aiPictureResultMappingDO;
    }
}
