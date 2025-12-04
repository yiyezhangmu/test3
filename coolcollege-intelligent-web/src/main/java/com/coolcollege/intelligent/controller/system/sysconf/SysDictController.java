package com.coolcollege.intelligent.controller.system.sysconf;

import com.coolcollege.intelligent.common.annotation.BaseResponse;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.enums.DictTypeEnum;
import com.coolcollege.intelligent.model.system.sysconf.EnterpriseDictMappingDO;
import com.coolcollege.intelligent.model.system.sysconf.SysDictDO;
import com.coolcollege.intelligent.model.system.sysconf.request.SysDictRequest;
import com.coolcollege.intelligent.service.system.sysconf.EnterpriseDictMappingService;
import com.coolcollege.intelligent.service.system.sysconf.SysDictService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/3/26 10:52
 */
@RestController
@RequestMapping("/v3/system/sysconf/sysDict")
@BaseResponse
public class SysDictController {
    @Resource
    private SysDictService sysDictService;
    @Resource
    private EnterpriseDictMappingService enterpriseDictMappingService;

    /**
     * 获取自定义模块名称自定义字典
     *
     * @return
     */
    @GetMapping("listSysDict")
    public ResponseResult listSysDict(@RequestParam(value = "dictKey",required = false) String dictKey) {
        DataSourceHelper.reset();
        return ResponseResult.success(sysDictService.listSysDict(DictTypeEnum.MODEL_NAME_DEFINE.getCode(), dictKey));
    }

    /**
     * 新增企业
     *
     * @param sysDictDO
     * @return
     */
    @PostMapping("addDict")
    public ResponseResult addDict(@RequestBody SysDictDO sysDictDO) {
        DataSourceHelper.reset();
        List<SysDictDO> dictList = sysDictService.listSysDict(DictTypeEnum.MODEL_NAME_DEFINE.getCode(), sysDictDO.getDictKey());
        if (dictList.size() > 0) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "字典项已存在");
        }
        sysDictDO.setBusinessType(DictTypeEnum.MODEL_NAME_DEFINE.getCode());
        return ResponseResult.success(sysDictService.addDict(sysDictDO));
    }

    /**
     * 自定义模块名称批量添加
     * @param request
     * @return
     */
    @PostMapping("batchAddDict")
    public ResponseResult batchAddDict(@RequestBody SysDictRequest request){
        DataSourceHelper.reset();
        return ResponseResult.success(sysDictService.batchAddDict(request, DictTypeEnum.MODEL_NAME_DEFINE));
    }

    /**
     * 修改自定义模块名称自定义value值
     * @param sysDictDO
     * @return
     */
    @PostMapping("updateDict")
    public ResponseResult updateDict(@RequestBody SysDictDO sysDictDO) {
        DataSourceHelper.reset();
        return ResponseResult.success(sysDictService.updateDict(sysDictDO));
    }

    /**
     * 根据id删除字典项
     *
     * @param sysDictDO
     * @return
     */
    @PostMapping("deleteDict")
    public ResponseResult deleteDict(@RequestBody SysDictDO sysDictDO) {
        DataSourceHelper.reset();
        return ResponseResult.success(sysDictService.deleteDict(sysDictDO));
    }

    /**
     * 获取模块名称自定义企业字典配置
     * @param enterpriseId
     * @param dingCorpId
     * @param dictKey
     * @return
     */
    @GetMapping("listDictMapping")
    public ResponseResult listDictMapping(@RequestParam(value = "enterpriseId", required = false) String enterpriseId,
                                          @RequestParam(value = "dingCorpId", required = false) String dingCorpId,
                                          @RequestParam(value = "dictKey",required = false) String dictKey){
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseDictMappingService.listDictMapping(enterpriseId,dingCorpId,dictKey,DictTypeEnum.MODEL_NAME_DEFINE.getCode()));
    }

    /**
     * 企业新增自定义模块名称自定字典
     * @param enterpriseDictMappingDO
     * @return
     */
    @PostMapping("addDictMapping")
    public ResponseResult addDictMapping(@RequestBody EnterpriseDictMappingDO enterpriseDictMappingDO){
        DataSourceHelper.reset();
        List<EnterpriseDictMappingDO> list = enterpriseDictMappingService.listDictMapping(enterpriseDictMappingDO.getEnterpriseId(),
                null,
                enterpriseDictMappingDO.getDictKey(),
                DictTypeEnum.MODEL_NAME_DEFINE.getCode());
        if(list.size()>0){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "字典项已存在");
        }
        enterpriseDictMappingDO.setBusinessType(DictTypeEnum.MODEL_NAME_DEFINE.getCode());
        return ResponseResult.success(enterpriseDictMappingService.addDictMapping(enterpriseDictMappingDO));
    }

    /**
     * 根据id删除企业字典映射
     * @param enterpriseDictMappingDO
     * @return
     */
    @PostMapping("deleteDictMapping")
    public ResponseResult deleteDictMapping(@RequestBody EnterpriseDictMappingDO enterpriseDictMappingDO){
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseDictMappingService.deleteDictMapping(enterpriseDictMappingDO));
    }

    /**
     * 修改企业字典映射
     * @param enterpriseDictMappingDO
     * @return
     */
    @PostMapping("updateDictMapping")
    public ResponseResult updateDictMapping(@RequestBody EnterpriseDictMappingDO enterpriseDictMappingDO){
        DataSourceHelper.reset();
        return ResponseResult.success(enterpriseDictMappingService.updateDictMapping(enterpriseDictMappingDO));
    }

}
