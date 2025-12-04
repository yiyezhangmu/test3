package com.coolcollege.intelligent.model.usergroup.dto;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import lombok.Data;

/**
 * @author byd
 * @date 2023-08-02 10:56
 */
@Data
public class ImportUserGroupDTO {

    String errMsg;

    ImportTaskDO importTaskDO;
}
