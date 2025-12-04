package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.enterprise.SubordinateSourceEnum;
import com.coolcollege.intelligent.common.enums.enterprise.UserSelectRangeEnum;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.SubordinateUserRangeDTO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.dto.GroupUserInfoExportDTO;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreDTO;
import com.coolcollege.intelligent.model.region.dto.AuthRegionStoreUserDTO;
import com.coolcollege.intelligent.model.region.dto.AuthStoreCountDTO;
import com.coolcollege.intelligent.model.region.dto.MySubordinatesDTO;
import com.coolcollege.intelligent.model.usergroup.dto.UserGroupDTO;
import com.coolcollege.intelligent.model.usergroup.request.UserGroupExportRequest;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.enterpriseUserGroup.EnterpriseUserGroupService;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户分组导出service
 * @author ：wxp
 * @date ：2023/1/4 10:22
 */
@Service
@Slf4j
public class UserGroupExportService implements BaseExportService {

    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private AuthVisualService authVisualService;

    @Resource
    private EnterpriseUserGroupService enterpriseUserGroupService;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        UserGroupExportRequest userGroupExportRequest = (UserGroupExportRequest)request;
        Integer total = enterpriseUserMapper.countUserByGroupId(enterpriseId, userGroupExportRequest.getGroupId());
        return Long.valueOf(total);
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_GROUP_USER_INFO;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        List<GroupUserInfoExportDTO> result = new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        //填充用户角色
        UserGroupExportRequest userGroupExportRequest = JSONObject.toJavaObject(request, UserGroupExportRequest.class);
        List<EnterpriseUserDTO> enterpriseUserList = enterpriseUserMapper.listUserByGroupId(enterpriseId, userGroupExportRequest.getGroupId(), null, null);
        List<String> userIdList = enterpriseUserService.initUserRole(enterpriseId, enterpriseUserList);
        //转换对象
        enterpriseUserList.forEach(user -> {
            GroupUserInfoExportDTO exportDTO = new GroupUserInfoExportDTO();
            BeanUtils.copyProperties(user, exportDTO);
            result.add(exportDTO);
        });

        Map<String, String> userRegionMap = enterpriseUserService.getUserRegion(enterpriseId, userIdList);
        //获取用户所有门店和区域权限
        List<AuthRegionStoreDTO> authRegionStoreDTOList = authVisualService.authRegionStoreByUserList(enterpriseId, userIdList);
        Map<String, List<AuthRegionStoreUserDTO>> userAuthMap = authRegionStoreDTOList.stream()
                .collect(Collectors.toMap(AuthRegionStoreDTO::getUserId, AuthRegionStoreDTO::getAuthRegionStoreUserList, (a, b) -> a));

        List<AuthStoreCountDTO> authStoreCountDTOS = authVisualService.authStoreCount(enterpriseId, userIdList, false);
        Map<String, AuthStoreCountDTO> storeCountMap = ListUtils.emptyIfNull(authStoreCountDTOS)
                .stream()
                .collect(Collectors.toMap(AuthStoreCountDTO::getUserId, data -> data, (a, b) -> a));

        Map<String, List<UserGroupDTO>> userGroupMap = enterpriseUserGroupService.getUserGroupMap(enterpriseId, userIdList);
        Map<String, SubordinateUserRangeDTO> subordinateUserRangeMap = enterpriseUserService.fillUserSubordinateNames(enterpriseId, userIdList);

        result.forEach(user -> {
            //填充用户的门店和区域信息
            List<AuthRegionStoreUserDTO> userAuthList = userAuthMap.get(user.getUserId());

            if(CollectionUtils.isNotEmpty(userAuthList)){
                //捞出门店列表
                List<AuthRegionStoreUserDTO> userStores = userAuthList.stream().filter(e -> e.getStoreId() != null).collect(Collectors.toList());
                //移除门店后,剩下的是区域列表
                userAuthList.removeAll(userStores);
                String storeName = userStores.stream().map(AuthRegionStoreUserDTO::getName).collect(Collectors.joining(","));
                String regionName = userAuthList.stream().map(AuthRegionStoreUserDTO::getName).collect(Collectors.joining(","));
                user.setStoreName(storeName);
                user.setRegionName(regionName);
            }
            user.setDepartment(userRegionMap.get(user.getUserId()));
            // 门店数
            if(MapUtils.isNotEmpty(storeCountMap)&&storeCountMap.get(user.getUserId())!=null){
                AuthStoreCountDTO authStoreCountDTO = storeCountMap.get(user.getUserId());
                if(authStoreCountDTO.getStoreCount()!=null){
                    user.setStoreCount(authStoreCountDTO.getStoreCount());
                }else {
                    user.setStoreCount(0);
                }
            }
            //填充用户分组
            List<UserGroupDTO> userGroupDTOList = userGroupMap.get(user.getUserId());
            String groupName = userGroupDTOList.stream().map(UserGroupDTO::getGroupName).collect(Collectors.joining(","));
            user.setGroupName(groupName);
            // 填充下属用户
            SubordinateUserRangeDTO subordinateUserRangeDTO = subordinateUserRangeMap.get(user.getUserId());
            if (subordinateUserRangeDTO != null){
                if(UserSelectRangeEnum.SELF.getCode().equals(subordinateUserRangeDTO.getSubordinateUserRange())){
                    user.setAuthUserName(UserSelectRangeEnum.SELF.getMsg());
                }else if(UserSelectRangeEnum.ALL.getCode().equals(subordinateUserRangeDTO.getSubordinateUserRange())){
                    user.setAuthUserName(UserSelectRangeEnum.ALL.getMsg());
                }else if(UserSelectRangeEnum.DEFINE.getCode().equals(subordinateUserRangeDTO.getSubordinateUserRange())){
                    List<String> nameList = new ArrayList<>();
                    List<String> sourceList = subordinateUserRangeDTO.getSourceList();
                    if(CollectionUtils.isNotEmpty(sourceList) && sourceList.contains(SubordinateSourceEnum.AUTO.getCode())){
                        nameList.add(SubordinateSourceEnum.AUTO.getMsg());
                    }
                    if(CollectionUtils.isNotEmpty(subordinateUserRangeDTO.getMySubordinates())){
                        for (MySubordinatesDTO mySubordinatesDTO: subordinateUserRangeDTO.getMySubordinates()) {
                            if (StringUtils.isNotBlank(mySubordinatesDTO.getRegionName())){
                                nameList.add(mySubordinatesDTO.getRegionName());
                            }
                            if (StringUtils.isNotBlank(mySubordinatesDTO.getPersonalName())){
                                nameList.add(mySubordinatesDTO.getPersonalName());
                            }
                        }
                    }
                    user.setAuthUserName(StringUtils.join(nameList, Constants.COMMA));
                }
            }
        });
        return result;
    }
}
