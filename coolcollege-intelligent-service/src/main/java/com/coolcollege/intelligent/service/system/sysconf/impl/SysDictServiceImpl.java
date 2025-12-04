package com.coolcollege.intelligent.service.system.sysconf.impl;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.sysconf.EnterpriseDictMappingMapper;
import com.coolcollege.intelligent.dao.sysconf.SysDictMapper;
import com.coolcollege.intelligent.model.enums.DictTypeEnum;
import com.coolcollege.intelligent.model.system.sysconf.SysDictDO;
import com.coolcollege.intelligent.model.system.sysconf.request.SysDictRequest;
import com.coolcollege.intelligent.service.system.sysconf.SysDictService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2021/3/26 11:01
 */
@Service
public class SysDictServiceImpl implements SysDictService {
    @Resource
    private SysDictMapper sysDictMapper;
    @Resource
    private EnterpriseDictMappingMapper dictMappingMapper;
    @Override
    public List<SysDictDO> listSysDict(String businessType, String dictKey) {
        return sysDictMapper.listByTypeKey(businessType,dictKey);
    }

    @Override
    public SysDictDO addDict(SysDictDO sysDictDO) {
        sysDictDO.setHasDelete(Boolean.FALSE);
        sysDictDO.setRemark(DictTypeEnum.getDescribeByCode(sysDictDO.getBusinessType()));
        sysDictMapper.batchInsert(Collections.singletonList(sysDictDO));
        return sysDictDO;
    }

    @Override
    public SysDictDO updateDict(SysDictDO sysDictDO) {
        sysDictMapper.updateValueByIdOrKeyType(sysDictDO);
        return sysDictDO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteDict(SysDictDO sysDictDO) {
        dictMappingMapper.deleteByDictId(sysDictDO.getId());
        return sysDictMapper.deleteById(sysDictDO.getId());
    }

    @Override
    public Boolean batchAddDict(SysDictRequest request, DictTypeEnum dictTypeEnum) {
        List<SysDictDO> dictList = request.getSysDictList();
        if(CollectionUtils.isEmpty(dictList)){
            return Boolean.TRUE;
        }
        String type = dictTypeEnum.getCode();
        if(StringUtils.isBlank(type)){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "类型不能为空");
        }
        List<SysDictDO> existList = sysDictMapper.listByTypeKey(type,null);
        Set<String> set = existList.stream().map(data -> data.getDictKey()).collect(Collectors.toSet());
        List<SysDictDO> finalDictList = dictList.stream().filter(data ->{
            if(set.contains(data.getDictKey())){
                return Boolean.FALSE;
            }
            data.setRemark(dictTypeEnum.getDescribe());
            data.setBusinessType(dictTypeEnum.getCode());
            return Boolean.TRUE;
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(finalDictList)){
            return Boolean.TRUE;
        }
        sysDictMapper.batchInsert(finalDictList);
        return Boolean.TRUE;
    }

}
