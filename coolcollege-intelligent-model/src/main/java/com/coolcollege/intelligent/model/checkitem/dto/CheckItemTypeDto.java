package com.coolcollege.intelligent.model.checkitem.dto;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 检查项类型
 *
 * @author 首亮
 */
@Data
public class CheckItemTypeDto {

  /**
   * ID
   */
  @JSONField(name="id")
  private Long id;
  /**
   * 排序
   */
  @JSONField(name="sort")
  private Integer sort;
  /**
   * 检查项类型名称
   */
  @JSONField(name="item_type_name")
  private String itemTypeName;
}
