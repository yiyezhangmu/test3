package com.coolcollege.intelligent.model.impoetexcel.vo;

import com.coolcollege.intelligent.model.impoetexcel.dto.StoreRangeDTO;
import lombok.Data;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/27 15:12
 * @Version 1.0
 */
@Data
public class StoreRangeVO {

   List<StoreRangeDTO> successList;

   List<StoreRangeDTO> failList;
}
