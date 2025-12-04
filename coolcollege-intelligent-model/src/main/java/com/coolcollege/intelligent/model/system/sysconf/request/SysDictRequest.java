package com.coolcollege.intelligent.model.system.sysconf.request;

import com.coolcollege.intelligent.model.system.sysconf.SysDictDO;
import lombok.Data;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/27 14:51
 */
@Data
public class SysDictRequest {
    List<SysDictDO> sysDictList;
}
