package com.coolcollege.intelligent.model.picture.vo;

import com.coolcollege.intelligent.model.metatable.vo.MetaStaColumnVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author chenyupeng
 * @Date 2021/8/2
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PictureCenterTableVO {
    /**
     * 检查表id
     */
    private Long metaTableId;

    /**
     * 检查表名称
     */
    private String metaTableName;

    /**
     * 巡店记录id
     */
    private Long businessId;

    /**
     * 图片集合
     */
    private List<PictureCenterColumnVO> pictureCenterColumnList;

    private List<MetaStaColumnVO> metaStaColumns;
}
