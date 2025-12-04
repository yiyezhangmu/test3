package com.coolcollege.intelligent.model.sop.param;

import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TaskSopDelParam {

    @NotNull
    private List<Long> sopIdList;
}
