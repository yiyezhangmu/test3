package com.coolcollege.intelligent.model.position.queryDto;

import com.coolcollege.intelligent.model.page.PageBase;
import lombok.Data;

import java.util.List;

/**
 * @ClassName PositionQueryDTO
 * @Description 岗位查询条件
 */
@Data
public class PositionQueryDTO extends PageBase {

    private String keyword;

    private String source;

    private List<String> mark;

    private String type;

    private String positionId;

    private Integer isValid;

    private List<String> storeIds;
}
