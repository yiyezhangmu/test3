package com.coolcollege.intelligent.model.metatable.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/8 11:05
 */
@Data
public class StoreMetaTableDTO {

    /**
     * 选择的门店
     */
    @NotEmpty(message = "选择的门店不能为空")
    private String storeId;

    /**
     * 选择的检查表数据
     */
    @NotNull(message = "选择的检查表不能为空")
    List<TbMetaTableDTO> metaTables;

}
