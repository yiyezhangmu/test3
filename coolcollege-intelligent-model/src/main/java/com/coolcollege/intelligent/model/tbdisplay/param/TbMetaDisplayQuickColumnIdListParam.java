package com.coolcollege.intelligent.model.tbdisplay.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TbMetaDisplayQuickColumnIdListParam {

    @NotNull
    private List<Long> columnIdList;
}
