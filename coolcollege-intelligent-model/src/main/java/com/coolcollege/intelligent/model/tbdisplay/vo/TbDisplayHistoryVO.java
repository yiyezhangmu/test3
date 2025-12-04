package com.coolcollege.intelligent.model.tbdisplay.vo;

import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.TbDisplayHistoryDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wxp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayHistoryVO extends TbDisplayHistoryDO {

    private String avatar;

    private List<TbDisplayHistoryColumnDO> approvalDataNew;

    private List<TbDisplayHistoryUserVO> historyUserVOList;

    private BigDecimal score;

    /**
     * 附件图片列表
     */
    private List<String> approveImageList;
}
