package com.coolcollege.intelligent.model.qywx.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class QyWechatDepartmentListVo {


    @JsonProperty("errcode")
    private Integer errcode;
    @JsonProperty("errmsg")
    private String errmsg;
    @JsonProperty("department")
    private List<DepartmentDTO> department;

    @NoArgsConstructor
    @Data
    public static class DepartmentDTO {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
    }
}
