package com.coolcollege.intelligent.model.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
public class BasicsAreaDTO {

    private Long taskId;
    private String id;
    private String name;
    private String type;

    public BasicsAreaDTO(Long taskId, String id, String name, String type) {
        this.taskId = taskId;
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
