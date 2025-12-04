package com.coolcollege.intelligent.model.ai;

import lombok.Data;

@Data
public class HlsAIResolveRequestDTO {

    private String enterpriseId;

    private String id;

    private String task_id;

    private String store_id;

    private String region_id;

    private String meta_column_name;

    private String code;

    private String check_pics;

    private Boolean check_status;

    private String routingKey;

    public HlsAIResolveRequestDTO(Long id, Long task_id, String store_id, Long region_id, String meta_column_name, String code, String check_pics, Boolean check_status) {
        this.id = String.valueOf(id);
        this.task_id = String.valueOf(task_id);
        this.store_id = store_id;
        this.region_id = String.valueOf(region_id);
        this.meta_column_name = meta_column_name;
        this.code = code;
        this.check_pics = check_pics;
        this.check_status = check_status;
    }

    public HlsAIResolveRequestDTO() {
    }
}
