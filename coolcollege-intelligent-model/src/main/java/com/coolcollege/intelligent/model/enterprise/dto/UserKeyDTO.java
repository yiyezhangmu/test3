package com.coolcollege.intelligent.model.enterprise.dto;

import com.coolcollege.intelligent.model.enterprise.dto.SelectUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/11/18 14:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserKeyDTO {

    private String key;

    private List<SelectUserDTO> users;
}
