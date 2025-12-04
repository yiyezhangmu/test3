package com.coolcollege.intelligent.model.aliyun;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邵凌志
 * @date 2020/8/26 16:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliyunCorpGroupDTO {

    /**
     * 标签分组
     */
    private String group;
    /**
     * 标签值
     */
    private String value;
}
