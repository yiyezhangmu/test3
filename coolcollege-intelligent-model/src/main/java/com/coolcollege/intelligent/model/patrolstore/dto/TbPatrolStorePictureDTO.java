package com.coolcollege.intelligent.model.patrolstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author byd
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbPatrolStorePictureDTO {
    /**
     *巡店记录id  tb_patrol_store_record
     */
    @NotNull
    private Long businessId;

    /**
     *操作人
     */
    @NotNull
    private Long storeSceneId;
    /**
     *图片
     */
    @NotEmpty
    private List<String> pictureList;
}
