package com.coolcollege.intelligent.model.metatable;

import lombok.Data;

import java.util.List;

/**
 * 删除检查表request
 * @author zhangnan
 * @date 2022-03-08 15:42
 */
@Data
public class MetaTableDeleteRequest {

    private List<Long> metaTableIds;
}
