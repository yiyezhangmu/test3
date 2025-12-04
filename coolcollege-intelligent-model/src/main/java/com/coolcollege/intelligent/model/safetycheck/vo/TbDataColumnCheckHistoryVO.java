package com.coolcollege.intelligent.model.safetycheck.vo;

import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaTableInfoVO;
import com.coolcollege.intelligent.model.patrolstore.vo.TbDataStaTableColumnVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 
 * @author   wxp
 * @date   2023-08-14 07:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDataColumnCheckHistoryVO {

    private TbMetaTableInfoVO metaTable;

    private MetaStaColumnVO metaStaColumn;

    private List<TbDataStaTableColumnVO> dataStaColumns;

}