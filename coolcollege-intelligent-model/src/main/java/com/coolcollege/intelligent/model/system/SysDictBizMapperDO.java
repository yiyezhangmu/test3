package com.coolcollege.intelligent.model.system;


import lombok.Data;

/**
 * 业务编码字典映射表
 *
 * @author 首亮
 */
@Data
public class SysDictBizMapperDO {

  /**
   * ID
   */
  private Long id;
  /**
   * 字典ID
   */
  private Long dictId;
  /**
   * 业务编码
   */
  private String bizTypeCode;
}
