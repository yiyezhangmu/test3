package com.coolcollege.intelligent.model.patrolstore.records;

import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/2/2 14:21
 */
@Data
public class PatrolStoreRecordsTableAndPicDTO extends PatrolStoreRecordsTableDTO{
    public PatrolStoreRecordsTableAndPicDTO() {
        super();
    }
    private List<String> picList;
}
