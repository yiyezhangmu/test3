package com.coolcollege.intelligent.model.store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author 邵凌志
 * @date 2020/11/18 15:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreKeyDTO {

    private String key;

    private List<SelectStoreDTO> stores;
}
