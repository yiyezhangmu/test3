package com.coolcollege.intelligent.model.metatable;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户检查表和项的权限
 * @author   zhangchenbiao
 * @date   2025-04-18 06:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbMetaTableUserAuthDO implements Serializable {

    public static final String TABLE_BUSINESS_TYPE = "table";
    public static final String COLUMN_BUSINESS_TYPE = "column";

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("检查表id或者快速检查项id")
    private String businessId;

    @ApiModelProperty("检查表table、检查项column")
    private String businessType;

    @ApiModelProperty("使用权限 0无 1有")
    private Boolean useAuth;

    @ApiModelProperty("编辑权限 0无 1有")
    private Boolean editAuth;

    @ApiModelProperty("查看权限 0无 1有")
    private Boolean viewAuth;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public static List<TbMetaTableUserAuthDO> buildUserAuthList(List<String> editorList, List<String> useList, List<String> viewerList, Long businessId, String businessType) {
        Map<String, TbMetaTableUserAuthDO> userAuthMap = new HashMap<>();
        for (String userId : ListUtils.emptyIfNull(editorList)) {
            if (StringUtils.isBlank(userId)) continue;
            userAuthMap.put(userId, TbMetaTableUserAuthDO.builder()
                    .userId(userId)
                    .businessId(businessId.toString())
                    .businessType(businessType)
                    .useAuth(true)
                    .editAuth(true)
                    .viewAuth(true)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build());
        }

        for (String userId : ListUtils.emptyIfNull(useList)) {
            if (StringUtils.isBlank(userId)) continue;
            if (!userAuthMap.containsKey(userId)) {
                userAuthMap.put(userId, TbMetaTableUserAuthDO.builder()
                        .userId(userId)
                        .businessId(businessId.toString())
                        .businessType(businessType)
                        .useAuth(true)
                        .editAuth(false)
                        .viewAuth(false)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            } else {
                TbMetaTableUserAuthDO userAuthDO = userAuthMap.get(userId);
                userAuthDO.setUseAuth(true);
            }
        }

        for (String userId : ListUtils.emptyIfNull(viewerList)) {
            if (StringUtils.isBlank(userId)) continue;
            if (!userAuthMap.containsKey(userId)) {
                userAuthMap.put(userId, TbMetaTableUserAuthDO.builder()
                        .userId(userId)
                        .businessId(businessId.toString())
                        .businessType(businessType)
                        .useAuth(false)
                        .editAuth(false)
                        .viewAuth(true)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            } else {
                TbMetaTableUserAuthDO userAuthDO = userAuthMap.get(userId);
                userAuthDO.setViewAuth(true);
            }
        }
        return new ArrayList<>(userAuthMap.values());
    }


    public static List<TbMetaTableUserAuthDO> buildUserAuthList(List<String> editorList, List<String> useList, Long businessId, String businessType) {
        Map<String, TbMetaTableUserAuthDO> userAuthMap = new HashMap<>();
        for (String userId : ListUtils.emptyIfNull(editorList)) {
            if (StringUtils.isBlank(userId)) continue;
            userAuthMap.put(userId, TbMetaTableUserAuthDO.builder()
                    .userId(userId)
                    .businessId(businessId.toString())
                    .businessType(businessType)
                    .useAuth(true)
                    .editAuth(true)
                    .viewAuth(true)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build());
        }

        for (String userId : ListUtils.emptyIfNull(useList)) {
            if (StringUtils.isBlank(userId)) continue;
            if (!userAuthMap.containsKey(userId)) {
                userAuthMap.put(userId, TbMetaTableUserAuthDO.builder()
                        .userId(userId)
                        .businessId(businessId.toString())
                        .businessType(businessType)
                        .useAuth(true)
                        .editAuth(false)
                        .viewAuth(false)
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build());
            } else {
                TbMetaTableUserAuthDO userAuthDO = userAuthMap.get(userId);
                userAuthDO.setUseAuth(true);
            }
        }
        return new ArrayList<>(userAuthMap.values());
    }
}