package com.coolcollege.intelligent.service.system.sysconf;

import com.coolcollege.intelligent.model.enums.DictTypeEnum;
import com.coolcollege.intelligent.model.system.sysconf.SysDictDO;
import com.coolcollege.intelligent.model.system.sysconf.request.SysDictRequest;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/26 10:59
 */
public interface SysDictService {
    /**
     * 通过类型和Key获取字典
     * @param businessType
     * @param dictKey
     * @return
     */
    List<SysDictDO> listSysDict(String businessType , String dictKey);

    /**
     * 新增字典项
     * @param sysDictDO
     * @return
     */
    SysDictDO addDict(SysDictDO sysDictDO);

    SysDictDO updateDict(SysDictDO sysDictDO);

    Integer deleteDict(SysDictDO sysDictDO);

    Boolean batchAddDict(SysDictRequest request, DictTypeEnum dictTypeEnum);
}
