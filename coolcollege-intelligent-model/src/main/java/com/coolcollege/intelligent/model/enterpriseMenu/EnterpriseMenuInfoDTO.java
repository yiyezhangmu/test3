package com.coolcollege.intelligent.model.enterpriseMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseMenuInfoDTO {

    private String menuInfo;

    private String enterpriseId;
}
