package com.coolcollege.intelligent.model.metatable.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 14:37
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaColumnTypeVO {

    private Integer code;

    private String name;

}
