package com.coolcollege.intelligent.model.aliyun.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/14
 */
@Data
public class VdsPersonInfo {
    private String Profession;
    private String PersonId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date UpdateTime;
    private String PersonType;
    //大图
    private String SourceUrl;
    //小图
    private String TargetUrl;
    //性别 1.男 2.女
    private String Gender;

    private Integer Age;




}
