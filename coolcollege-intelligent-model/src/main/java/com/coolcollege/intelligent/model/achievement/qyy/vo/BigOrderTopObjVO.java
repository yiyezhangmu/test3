package com.coolcollege.intelligent.model.achievement.qyy.vo;

import lombok.Data;

import java.util.List;

@Data
public class BigOrderTopObjVO {

    private Long etlTm;

    private List<BigOrderTopVO> bigOrderTopVOS;
}
