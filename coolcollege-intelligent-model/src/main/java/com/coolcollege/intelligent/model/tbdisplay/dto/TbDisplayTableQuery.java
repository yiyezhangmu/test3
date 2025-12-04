package com.coolcollege.intelligent.model.tbdisplay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author byd
 */
@Data
public class TbDisplayTableQuery implements Serializable {

    private static final long serialVersionUID = -7833701301412356205L;

    /**
     *
     */
    @JsonProperty(value= "pageNumber")
    private Integer pageNumber = 1;
    /**
     *
     */
    @JsonProperty(value= "pageSize")
    private Integer pageSize = 20;
    /**
     * 件很崇拜id
     */
    @JsonProperty(value= "displayTableId")
    private Long displayTableId;

}
