package com.coolcollege.intelligent.model.tbdisplay.vo;

import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wxp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableRecordPageVO {

    private PageInfo<TbDisplayTaskDataVO> recordInfo;

    private List<TbMetaDisplayTableColumnDO> columnList;

    private List<TbMetaDisplayTableColumnDO> contentList;

}
