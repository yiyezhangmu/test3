package com.coolcollege.intelligent.model.tbdisplay.vo;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayTableDataColumnDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableDataColumnVO extends TbDisplayTableDataColumnDO {

    private String columnName;
    private String standardPic;
    private String description;
    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * meta信息的score
     */
    private BigDecimal metaScore;

    @ApiModelProperty("审核记录列表")
    private List<TbDisplayHistoryColumnVO> historyColumnVOList;

    /**
     * 检查图片,0不强制1强制
     */
    private Integer mustPic;
}
