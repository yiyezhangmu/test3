package com.coolcollege.intelligent.model.storework.dto;

import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.store.dto.StoreAreaDTO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkDO;
import com.coolcollege.intelligent.model.storework.SwStoreWorkTableMappingDO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/12/7 14:45
 * @Version 1.0
 */
@Data
public class StoreWorkSingleStoreResolveDTO {

    private List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS;

    private String enterpriseId;

    private List<String> storeIds;

    private StoreAreaDTO storeAreaDTO;

    private SwStoreWorkDO swStoreWorkDO;

    private Date currentDate;

    private BigDecimal totalScore;

    private List<TbMetaTableDO> tbMetaTableDOS;

    private  Integer finalCollectColumnNum;

    private Integer finalTotalColumnNum;

    private List<TbMetaDefTableColumnDO> TbMetaDefTableColumnDOS;

    private List<TbMetaStaTableColumnDO> TbMetaStaTableColumnDOS;

    private Boolean reissueFlag;

    private Boolean manualReissue;

    /**
     * 店务是否使用AI分析
     */
    private Boolean storeWorkIsAiCheck;
}
