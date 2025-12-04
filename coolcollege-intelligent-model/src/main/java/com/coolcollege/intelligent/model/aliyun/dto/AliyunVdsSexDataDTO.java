package com.coolcollege.intelligent.model.aliyun.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 邵凌志
 * @date 2021/1/14 10:47
 */
@Data
@NoArgsConstructor
public class AliyunVdsSexDataDTO extends AliyunVdsDataDTO {

    private String percent;

    public AliyunVdsSexDataDTO(String code, int num, String percent) {
        super(code, num);
        this.percent = percent;
    }
}
