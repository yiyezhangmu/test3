package com.coolcollege.intelligent.model.metatable.dto;

import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * 检查表DTO
 * </p>
 *
 * @author wangff
 * @since 2025/4/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolMetaDTO {

    /**
     * 检查表DO
     */
    private TbMetaTableDO tbMetaTableDO;

    /**
     * 使用人列表
     */
    List<String> useUserIds;

    /**
     * 共同编辑人
     */
    List<String> commonEditUserIds;
    
    /**
     * 结果查看人
     */
    List<String> resultViewUserIds;
}
