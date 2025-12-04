package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2021/1/14 20:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunVdsAgeDataDTO {

    private String date;

    List<AliyunVdsDataDTO> ageList;
}
