package com.coolcollege.intelligent.model.help;

import lombok.Data;

import java.util.Date;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/8 13:45
 */
@Data
public class HelpDescDO {
    /**
     *
     */
    private Long id;
    /**
     * 路径
     */
    private String path;
    /**
     * 内容
     */
    private String content;
    /**
     *
     */
    private Date createTime;
    /**
     *
     */
    private Date updateTime;
}
