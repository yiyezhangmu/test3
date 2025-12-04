package com.coolcollege.intelligent.facade.dto.openApi.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * describe: 陈列基础VO
 *
 * @author wangff
 * @date 2024/10/25
 */
@Data
public class DisplayUnifyVO<T> implements Serializable {
    
    /**
     * 工号
     */
    private String jobnumber;

    /**
     * 结果列表
     */
    private List<T> list;
}
