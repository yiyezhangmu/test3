package com.coolcollege.intelligent.model.system.VO;

import com.coolcollege.intelligent.model.menu.SysMenuDO;
import lombok.Data;

import java.io.Serializable;

@Data
public class SysMenuVO extends SysMenuDO implements Serializable {
    /**
     * 是否选中
     */
    private String  checked;
}
