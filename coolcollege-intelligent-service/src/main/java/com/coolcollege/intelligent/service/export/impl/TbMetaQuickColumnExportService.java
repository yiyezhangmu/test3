package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.table.ColumnEnum;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnCategoryMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnReasonMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnResultMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnCategoryDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnResultDO;
import com.coolcollege.intelligent.model.metatable.dto.NormalColumnExportDTO;
import com.coolcollege.intelligent.model.metatable.dto.QuickColumnResultImportDTO;
import com.coolcollege.intelligent.model.metatable.dto.ResultColumnExportDTO;
import com.coolcollege.intelligent.model.metatable.request.TbMetaQuickColumnExportRequest;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chenyupeng
 * @since 2022/4/13
 */
@Service
public class TbMetaQuickColumnExportService implements BaseExportService {

    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;

    @Resource
    private TbMetaColumnCategoryMapper tbMetaColumnCategoryMapper;

    @Resource
    private TbMetaQuickColumnResultMapper tbMetaQuickColumnResultMapper;

    @Resource
    private TbMetaQuickColumnReasonMapper tbMetaQuickColumnReasonMapper;

    @Autowired
    private SysRoleService sysRoleService;

    private static final String TITLE = "说明：\n" +
            "1、请从第4行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败；\n" +
            "2、检查项名称必填，支持100个字；\n" +
            "3、检查项分类请不要超过100字，检查项描述请不要超过1000字，非必填，如果不填写，则默认导入后将该项放入“其他”分类中；\n" +
            "4、点评选项（不适用原因选项）、不适用原因选项如果有多个时，请用英文,分开，非必填。\n" +
            "5、结果项，每个检查项的结果项最多10个。各字段要求如下：\n" +
            "1）结果项名称：必填，请不要超过20字；\n" +
            "2）分值，奖罚金额范围 ：-100000~100000 ，超过则无效；\n" +
            "3）检查图片是否必填、检查描述是否强制填写，检查图片、检查描述非必填，当未填写时导入系统后默认为“不强制”；\n" +
            "4）统计维度：仅能填写“合格、不合格、不适用”，非必填，不填写则默认为“合格”；\n" +
            "6、不同sheet页可以导入不同属性的检查项；";

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        TbMetaQuickColumnExportRequest exportRequest = (TbMetaQuickColumnExportRequest) request;
        Integer status = exportRequest.getStatus();
        String createUserId = null;
        //是否只查创建人创建的
        CurrentUser user = exportRequest.getUser();
        String userId = user.getUserId();
        Boolean create = exportRequest.isCreate();
        if(create != null && create){
            createUserId = userId;
        }
        //判断是否为管理员
        Boolean adminIs = sysRoleService.checkIsAdmin(enterpriseId, userId);
        String useUserId = null;
        if(adminIs != null && !adminIs){
            useUserId = userId;
        }
        List<TbMetaQuickColumnDO> quickColumnDOList = tbMetaQuickColumnMapper.selectQuickTableColumnList(enterpriseId, exportRequest.getColumnName(), null, null, null, status, null, createUserId, useUserId, null, null);
        return (long) ListUtils.emptyIfNull(quickColumnDOList).size();
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_QUICK_COLUMN_NEW;
    }

    @Override
    public List<?> exportList(String enterpriseId, JSONObject request, int pageSize, int pageNum) {
        return null;
    }

    @Override
    public Map<String, List<?>> exportListSheet(String enterpriseId, JSONObject request) {
        TbMetaQuickColumnExportRequest exportRequest = JSONObject.toJavaObject(request, TbMetaQuickColumnExportRequest.class);
        List<Integer> columnTypes = new ArrayList<>();
        if(Objects.isNull(exportRequest.getColumnType())){
            columnTypes = MetaColumnTypeEnum.getDefaultColumnTypes();
        }
        if(Objects.nonNull(exportRequest.getTableProperty())){
            columnTypes = MetaTablePropertyEnum.getTableColumnTypes(exportRequest.getTableProperty());
        }
        Integer status = exportRequest.getStatus();
        String createUserId = null;
        //是否只查创建人创建的
        CurrentUser user = exportRequest.getUser();
        String userId = user.getUserId();
        Boolean create = exportRequest.isCreate();
        if(create != null && create){
            createUserId = userId;
        }
        //判断是否为管理员
        Boolean adminIs = sysRoleService.checkIsAdmin(enterpriseId, userId);
        String useUserId = null;
        if(adminIs != null && !adminIs){
            useUserId = userId;
        }

        List<TbMetaQuickColumnDO> quickColumnDOList = tbMetaQuickColumnMapper .selectQuickTableColumnList(enterpriseId, exportRequest.getColumnName(), exportRequest.getColumnType(), columnTypes, exportRequest.getCategoryId(), status, null, createUserId, useUserId, null, null);

        List<TbMetaColumnCategoryDO> metaColumnCategoryList = tbMetaColumnCategoryMapper.getMetaColumnCategoryList(enterpriseId, null);
        Map<Long, TbMetaColumnCategoryDO> categoryDOMap = ListUtils.emptyIfNull(metaColumnCategoryList).stream()
                .collect(Collectors.toMap(TbMetaColumnCategoryDO::getId, data -> data, (a, b) -> a));

        List<QuickColumnResultImportDTO> allColumnResultImportList = tbMetaQuickColumnResultMapper.getAllColumnResultImportList(enterpriseId);
        Map<Long, List<QuickColumnResultImportDTO>> columnResultMap = ListUtils.emptyIfNull(allColumnResultImportList).stream()
                .collect(Collectors.groupingBy(QuickColumnResultImportDTO::getMetaQuickColumnId));

        Map<String, List<?>> resultMap = new LinkedHashMap<>();
        resultMap.put(MetaColumnTypeEnum.STANDARD_COLUMN.getName(),
                getNormalColumnList(quickColumnDOList,categoryDOMap,MetaColumnTypeEnum.STANDARD_COLUMN.getCode(),enterpriseId));
        resultMap.put(MetaColumnTypeEnum.HIGH_COLUMN.getName(),
                getResultColumnList(quickColumnDOList,categoryDOMap,columnResultMap,MetaColumnTypeEnum.HIGH_COLUMN.getCode(),enterpriseId));
        resultMap.put(MetaColumnTypeEnum.RED_LINE_COLUMN.getName(),
                getResultColumnList(quickColumnDOList,categoryDOMap,columnResultMap,MetaColumnTypeEnum.RED_LINE_COLUMN.getCode(),enterpriseId));
        resultMap.put(MetaColumnTypeEnum.VETO_COLUMN.getName(),
                getResultColumnList(quickColumnDOList,categoryDOMap,columnResultMap,MetaColumnTypeEnum.VETO_COLUMN.getCode(),enterpriseId));
        resultMap.put(MetaColumnTypeEnum.DOUBLE_COLUMN.getName(),
                getResultColumnList(quickColumnDOList,categoryDOMap,columnResultMap,MetaColumnTypeEnum.DOUBLE_COLUMN.getCode(),enterpriseId));
        return resultMap;
    }
    @Override
    public Boolean sheetExport(){
        return true;
    }

    @Override
    public Map<String,String> getTitleSheet(){
        Map<String,String> titleMap = new HashMap<>();
        titleMap.put(MetaColumnTypeEnum.STANDARD_COLUMN.getName(),TITLE);
        titleMap.put(MetaColumnTypeEnum.HIGH_COLUMN.getName(),TITLE);
        titleMap.put(MetaColumnTypeEnum.RED_LINE_COLUMN.getName(),TITLE);
        titleMap.put(MetaColumnTypeEnum.VETO_COLUMN.getName(),TITLE);
        titleMap.put(MetaColumnTypeEnum.DOUBLE_COLUMN.getName(),TITLE);
        return titleMap;
    }

    public List<NormalColumnExportDTO> getNormalColumnList(List<TbMetaQuickColumnDO> quickColumnDOList,
                                                           Map<Long, TbMetaColumnCategoryDO> categoryDOMap,
                                                           Integer columnType,String eid){
        List<TbMetaQuickColumnDO> collect = quickColumnDOList.stream().filter(e -> columnType.equals(e.getColumnType())).collect(Collectors.toList());
        List<NormalColumnExportDTO> resultList = new ArrayList<>();
        NormalColumnExportDTO dto;
        for (TbMetaQuickColumnDO quickColumnDO : ListUtils.emptyIfNull(collect)) {
            dto = new NormalColumnExportDTO();
            dto.setScore(quickColumnDO.getMaxScore() == null ? "" : String.valueOf(quickColumnDO.getMaxScore()));
            dto.setDescription(quickColumnDO.getDescription());
            dto.setColumnName(quickColumnDO.getColumnName());
            dto.setPunishMoney(String.valueOf(quickColumnDO.getPunishMoney()));
            dto.setAwardMoney(String.valueOf(quickColumnDO.getAwardMoney()));
            if(categoryDOMap.get(quickColumnDO.getCategoryId()) != null){
                dto.setCategory(categoryDOMap.get(quickColumnDO.getCategoryId()).getCategoryName());
            }
            //根据id查结果项
            Long id = quickColumnDO.getId();
            List<TbMetaQuickColumnResultDO> columnResultList = tbMetaQuickColumnResultMapper.getColumnResultList(eid, Arrays.asList(id));
            if (CollectionUtils.isNotEmpty(columnResultList)){
                TbMetaQuickColumnResultDO resultDO = columnResultList.get(0);
                Integer mustPic = resultDO.getMustPic();
                dto.setCheckImg(ColumnEnum.getMsgByNum(mustPic));
                String description = resultDO.getDescription();
                dto.setCheckDec(ColumnEnum.getMsgByCode(description));
            }
            resultList.add(dto);
        }
        return resultList;
    }

    public List<ResultColumnExportDTO> getResultColumnList(List<TbMetaQuickColumnDO> quickColumnDOList,
                                                           Map<Long, TbMetaColumnCategoryDO> categoryDOMap,
                                                           Map<Long, List<QuickColumnResultImportDTO>> columnResultMap,
                                                           Integer columnType,String enterpriseId){
        List<TbMetaQuickColumnDO> collect = quickColumnDOList.stream().filter(e -> columnType.equals(e.getColumnType())).collect(Collectors.toList());
        List<ResultColumnExportDTO> resultList = new ArrayList<>();
        ResultColumnExportDTO dto;
        for (TbMetaQuickColumnDO quickColumnDO : ListUtils.emptyIfNull(collect)) {
            dto = new ResultColumnExportDTO();
            dto.setDescription(quickColumnDO.getDescription());
            dto.setColumnName(quickColumnDO.getColumnName());
            //搜索该项的不合格原因和不适用原因用,隔开
            Long id = quickColumnDO.getId();
            List<String> nameNA = tbMetaQuickColumnReasonMapper.getNamesByColumnIdAndType(enterpriseId, id, CheckResultEnum.INAPPLICABLE.getCode());
            if (CollectionUtils.isNotEmpty(nameNA)){
                String nameNaString = nameNA.stream().collect(Collectors.joining(","));
                dto.setReasonNameNA(nameNaString);
            }
            List<String> nameFail = tbMetaQuickColumnReasonMapper.getNamesByColumnIdAndType(enterpriseId, id, CheckResultEnum.FAIL.getCode());
            if (CollectionUtils.isNotEmpty(nameFail)){
                String nameFailString = nameFail.stream().collect(Collectors.joining(","));
                dto.setReasonNameFail(nameFailString);
            }
            if(categoryDOMap.get(quickColumnDO.getCategoryId()) != null){
                dto.setCategory(categoryDOMap.get(quickColumnDO.getCategoryId()).getCategoryName());
            }
            List<QuickColumnResultImportDTO> columnResultImportDTOS = ListUtils.emptyIfNull(columnResultMap.get(quickColumnDO.getId()));
            ResultColumnExportDTO.setValue(dto,columnResultImportDTOS);
            resultList.add(dto);
        }
        return resultList;
    }

}
