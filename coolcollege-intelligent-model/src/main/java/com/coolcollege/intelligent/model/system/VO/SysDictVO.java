package com.coolcollege.intelligent.model.system.VO;


import lombok.Data;

/**
 * 角色表
 *
 * @author 首亮
 */
@Data
public class SysDictVO {

  /**
   * 字典key
   */
  private String dictKey;
  /**
   * 字段value
   */
  private String dictValue;
  /**
   * 备注
   */
  private String remark;
}
