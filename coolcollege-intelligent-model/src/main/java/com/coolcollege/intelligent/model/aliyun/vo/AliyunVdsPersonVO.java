package com.coolcollege.intelligent.model.aliyun.vo;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/14
 */
@Data
public class AliyunVdsPersonVO {
    private String personId;
    private Long shotTime;
    //小图
    private String targetPersonUrl;
    //大图
    private String sourcePersonUrl;

    //性别 1.男
    private Integer gender;

    private String age;


}
