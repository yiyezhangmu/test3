package com.coolcollege.intelligent.model.unifytask.dto;

import com.coolcollege.intelligent.model.metatable.dto.TbMetaTableDTO;
import lombok.Data;

import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/3 18:48
 */
@Data
public class TaskStoreLatestDataDTO {

    /**
     * 门店id
     */
    private String storeId;
    /**
     * 检查表信息
     */
    private List<TbMetaTableDTO> metaTables;


}
