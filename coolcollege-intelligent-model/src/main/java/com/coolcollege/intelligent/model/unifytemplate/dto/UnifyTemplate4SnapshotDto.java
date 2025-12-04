package com.coolcollege.intelligent.model.unifytemplate.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.coolcollege.intelligent.model.unifytemplate.vo.UnifyCheckItemVO;
import lombok.Data;

import java.util.List;

/**
 * @author LiZhuo
 */
@Data
public class UnifyTemplate4SnapshotDto {
    private Long snapshotId;
    private Long id;
    private String name;
    @JSONField(name="checkItems")
    private List<UnifyCheckItemVO> checkItemDOS;
    private Integer deleteIs;
    private Long createTime;
    private String createUserId;
    private Long updateTime;
    private String updateUserId;
    private String createUserName;
    private String updateUserName;

}
