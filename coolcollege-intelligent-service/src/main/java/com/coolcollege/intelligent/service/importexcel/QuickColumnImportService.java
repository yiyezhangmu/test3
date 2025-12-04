package com.coolcollege.intelligent.service.importexcel;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.patrol.CheckResultEnum;
import com.coolcollege.intelligent.common.enums.table.ColumnEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnReasonMapper;
import com.coolcollege.intelligent.mapper.metatable.TbMetaColumnCategoryDAO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaQuickColumnResultDAO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.export.ExportView;
import com.coolcollege.intelligent.model.impoetexcel.ImportConstants;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnCategoryDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnResultDO;
import com.coolcollege.intelligent.model.metatable.dto.ColumnMergeDTO;
import com.coolcollege.intelligent.model.metatable.dto.NormalColumnImportDTO;
import com.coolcollege.intelligent.model.metatable.dto.QuickColumnResultImportDTO;
import com.coolcollege.intelligent.model.metatable.dto.ResultColumnImportDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author chenyupeng
 * @since 2022/4/11
 */
@Service
@Slf4j
public class QuickColumnImportService extends ImportBaseService {

    @Resource
    private ImportTaskMapper importTaskMapper;

    @Resource
    private GenerateOssFileService generateOssFileService;

    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;

    @Resource
    private TbMetaColumnCategoryDAO tbMetaColumnCategoryDAO;

    @Resource
    private TbMetaQuickColumnResultDAO tbMetaQuickColumnResultDAO;

    @Autowired
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;
    @Resource
    private TbMetaQuickColumnReasonMapper tbMetaQuickColumnReasonMapper;

    private static final String BIG_DECIMAL_ABNORMAL = "输入内容格式不正确";

    private static final String COLUMN_NAME_BLANK = "检查项名称不能为空";

    private static final String SCORE_ABNORMAL = "普通项分值仅能输入正数";

    private static final String COLUMN_NAME_LENGTH_TOO_LONG = "检查项名称长度不能超过128";

    private static final String CATEGORY_LENGTH_TOO_LONG = "分类名称长度不能超过100";

    private static final String CATEGORY_TOO_MANY = "企业已有检查项分类超过500个";

    private static final String CATEGORY_TOO_MANY_ONE_MILLISECOND = "企业已有检查项分类超过1000个";

    private static final String COLUMN_RESULT_NAME_LENGTH_TOO_LONG = "检查项结果名称长度不能超过20";

    private static final String COLUMN_DESCRIPTION_LENGTH_TOO_LONG = "检查项描述长度不能超过1000";

    private static final String COLUMN_RESULT_ABNORMAL = "结果项维度仅能填写“合格、不合格、不适用”";

    private static final String COLUMN_RESULT_MAX_SCORE = "结果项分值不能超过100000";

    private static final String COLUMN_MAX_SCORE = "检查项分值不能超过100000";

    private static final String COLUMN_REPEAT = "检查项已重复";

    private static final String COLUMN_RESULT_AT_LEAST_ONE = "结果项至少填写一个";

    private static final String COLUMN_RESULT_DEFAULT_MONEY = "奖罚金额范围 ：-100000～100000 ，超过则无效";

    private static final String STANDARD_TITLE = "说明：\n" +
            "\n" +
            "1、检查项名称必填，支持100个字；\n" +
            "2、请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败；\n" +
            "3、不同sheet页可以导入不同属性的检查项；\n" +
            "4、高级项、红线项、否决项、加倍项均能导入结果项，结果项维度仅能填写“合格、不合格、不适用”，每个检查项的结果项最多10个，可自由设置名称、分值、维度；\n" +
            "5、红线项、否决项的不合格维度请按示例填写；\n" +
            "6、检查项分类、名称请不要超过100字，检查项描述请不要超过1000字；高级项、红线项、否决项、加倍项的结果项名称请不要超过20字；";
    private static final String TITLE = "说明：\n" +
            "\n" +
            "1、检查项名称必填，支持100个字；\n" +
            "2、请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败；\n" +
            "3、不同sheet页可以导入不同属性的检查项；\n" +
            "4、高级项、红线项、否决项、加倍项均能导入结果项，结果项维度仅能填写“合格、不合格、不适用”，每个检查项的结果项最多10个，可自由设置名称、分值、维度；\n" +
            "5、红线项、否决项的不合格维度请按示例填写；\n" +
            "6、检查项分类、名称请不要超过100字，检查项描述请不要超过1000字；高级项、红线项、否决项、加倍项的结果项名称请不要超过20字；\n" +
            "7、奖罚金额范围 ：-1000～1000 ，超过则无效";

    @Async("importExportThreadPool")
    public void importQuickColumn(String eid, String dbName, Future<List<NormalColumnImportDTO>> normalImportTask, Future<List<ResultColumnImportDTO>> seniorImportTask,
                                        Future<List<ResultColumnImportDTO>> redLineImportTask, Future<List<ResultColumnImportDTO>> vetoImportTask,
                                        Future<List<ResultColumnImportDTO>> doubleImportTask, String contentType, ImportTaskDO task, CurrentUser user) {
        DataSourceHelper.changeToSpecificDataSource(dbName);
        try {
            boolean lock = lock(eid, ImportConstants.QUICK_COLUMN_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<NormalColumnImportDTO> normalImportList    = ListUtils.emptyIfNull(normalImportTask.get()).stream().filter(data->!allFieldIsNULL(data)).collect(Collectors.toList());
            List<ResultColumnImportDTO> seniorImportList    = ListUtils.emptyIfNull(seniorImportTask.get()).stream().filter(data->!allFieldIsNULL(data)).collect(Collectors.toList());
            List<ResultColumnImportDTO> redLineImportList   = ListUtils.emptyIfNull(redLineImportTask.get()).stream().filter(data->!allFieldIsNULL(data)).collect(Collectors.toList());
            List<ResultColumnImportDTO> vetoImportList      = ListUtils.emptyIfNull(vetoImportTask.get()).stream().filter(data->!allFieldIsNULL(data)).collect(Collectors.toList());
            List<ResultColumnImportDTO> doubleImportList    = ListUtils.emptyIfNull(doubleImportTask.get()).stream().filter(data->!allFieldIsNULL(data)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(normalImportList) && CollectionUtils.isEmpty(seniorImportList) && CollectionUtils.isEmpty(redLineImportList)
                    && CollectionUtils.isEmpty(vetoImportList) && CollectionUtils.isEmpty(doubleImportList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }
            importDeal(eid, dbName, normalImportList, seniorImportList, redLineImportList, vetoImportList, doubleImportList, contentType, task, user);

        } catch (BaseException e) {
            log.error("检查项文件上传失败：{}" + eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());

            importTaskMapper.update(eid, task);
        } catch (Exception e) {
            log.error("检查项文件上传失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark("检查项文件上传失败");
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.QUICK_COLUMN_KEY);
        }
    }

    public void importDeal(String eid, String dbName, List<NormalColumnImportDTO> normalImportList, List<ResultColumnImportDTO> seniorImportList,
                           List<ResultColumnImportDTO> redLineImportList, List<ResultColumnImportDTO> vetoImportList,
                           List<ResultColumnImportDTO> doubleImportList, String contentType, ImportTaskDO task, CurrentUser user) {

        List<NormalColumnImportDTO> normalErrorImportList = new ArrayList<>();
        List<ResultColumnImportDTO> seniorErrorImportList = new ArrayList<>();
        List<ResultColumnImportDTO> redLineErrorImportList = new ArrayList<>();
        List<ResultColumnImportDTO> vetoErrorImportList = new ArrayList<>();
        List<ResultColumnImportDTO> doubleErrorImportList = new ArrayList<>();

        //普通项导入
        importDealNormal(eid,dbName,normalImportList,normalErrorImportList,contentType,task,user);
        //高级项导入
        importDealResultColumn(eid,dbName,seniorImportList,seniorErrorImportList,MetaColumnTypeEnum.HIGH_COLUMN.getCode(),user);
        //红线项导入
        importDealResultColumn(eid,dbName,redLineImportList,redLineErrorImportList,MetaColumnTypeEnum.RED_LINE_COLUMN.getCode(),user);
        //否决项导入
        importDealResultColumn(eid,dbName,vetoImportList,vetoErrorImportList,MetaColumnTypeEnum.VETO_COLUMN.getCode(),user);
        //加倍项导入
        importDealResultColumn(eid,dbName,doubleImportList,doubleErrorImportList,MetaColumnTypeEnum.DOUBLE_COLUMN.getCode(),user);

        DataSourceHelper.changeToSpecificDataSource(dbName);
        int totalNum = normalImportList.size() + seniorImportList.size() + redLineImportList.size() + vetoImportList.size() + doubleImportList.size();
        int errorListSize = normalErrorImportList.size() + seniorErrorImportList.size() + redLineErrorImportList.size()
                + vetoErrorImportList.size() + doubleErrorImportList.size();
        if (errorListSize != Constants.ZERO) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            if(totalNum - errorListSize > Constants.ZERO){
                task.setRemark("部分数据导入失败");
            }
            List<ExportView> exportViewList = getExportView(normalErrorImportList,seniorErrorImportList,
                    redLineErrorImportList,vetoErrorImportList,doubleErrorImportList);
            String url = generateOssFileService.generateOssExcelSheet(eid, exportViewList, contentType,"检查项导入模板");
            task.setFileUrl(url);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setTotalNum(totalNum);
        task.setSuccessNum(totalNum - errorListSize);
        importTaskMapper.update(eid, task);
    }

    /**
     * 普通项导入
     */
    public void importDealNormal(String eid, String dbName, List<NormalColumnImportDTO> normalImportList, List<NormalColumnImportDTO> normalErrorImportList, String contentType, ImportTaskDO task, CurrentUser user) {
        if(CollectionUtils.isEmpty(normalImportList)){
            return;
        }
//        List<TbMetaQuickColumnDO> quickColumnDOList = new ArrayList<>();
//        List<TbMetaQuickColumnDO> quickColumnDOUpdateList = new ArrayList<>();

        List<ColumnMergeDTO> quickColumnDOList=new ArrayList<>();
        List<ColumnMergeDTO> quickColumnDOUpdateList = new ArrayList<>();

        List<String> currentColumn = new ArrayList<>();
        TbMetaQuickColumnDO tempDO;

        //查出当前已有分类
        List<TbMetaColumnCategoryDO> metaColumnCategoryList = tbMetaColumnCategoryDAO.getMetaColumnCategoryList(eid, null);
        List<String> existCategoryList = metaColumnCategoryList.stream().map(TbMetaColumnCategoryDO::getCategoryName).collect(Collectors.toList());
        Map<String, TbMetaColumnCategoryDO> currentCategoryMap = metaColumnCategoryList.stream().collect(Collectors.toMap(TbMetaColumnCategoryDO::getCategoryName, data -> data, (a, b) -> a));
        Integer allCount = tbMetaColumnCategoryDAO.getAllCount(eid);

        List<TbMetaColumnCategoryDO> tbMetaColumnCategoryDOList = new ArrayList<>();
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO setting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);
        for (NormalColumnImportDTO dto : normalImportList) {
            tempDO = new TbMetaQuickColumnDO();
            if(StringUtils.isBlank(dto.getColumnName())){
                dto.setDec(COLUMN_NAME_BLANK);
                normalErrorImportList.add(dto);
                continue;
            }
            if(dto.getColumnName().length() > Constants.QUICK_COLUMN_NAME_MAX_LENGTH){
                dto.setDec(COLUMN_NAME_LENGTH_TOO_LONG);
                normalErrorImportList.add(dto);
                continue;
            }
            if(StringUtils.isNotBlank(dto.getCategory()) && dto.getCategory().length() > Constants.CATEGORY_NAME_MAX_LENGTH){
                dto.setDec(CATEGORY_LENGTH_TOO_LONG);
                normalErrorImportList.add(dto);
                continue;
            }
            if(StringUtils.isNotBlank(dto.getDescription()) && dto.getDescription().length() > Constants.QUICK_COLUMN_DESCRIPTION_NAME_MAX_LENGTH){
                dto.setDec(COLUMN_DESCRIPTION_LENGTH_TOO_LONG);
                normalErrorImportList.add(dto);
                continue;
            }
            BigDecimal score = bigDecimalTrans(dto.getScore());
            if(score == null){
                dto.setDec(BIG_DECIMAL_ABNORMAL);
                normalErrorImportList.add(dto);
                continue;
            }else if(score.compareTo(BigDecimal.ZERO) < 0){
                dto.setDec(SCORE_ABNORMAL);
                normalErrorImportList.add(dto);
                continue;
            }else if(score.intValue() > Constants.QUICK_COLUMN_MAX_SCORE){
                dto.setDec(COLUMN_MAX_SCORE);
                normalErrorImportList.add(dto);
                continue;
            }
            if(bigDecimalTrans(dto.getAwardMoney()) == null){
                dto.setDec(BIG_DECIMAL_ABNORMAL);
                normalErrorImportList.add(dto);
                continue;
            }
            //奖金不能超过100000
            if(bigDecimalTrans(dto.getAwardMoney()) != null && bigDecimalTrans(dto.getAwardMoney()).compareTo(new BigDecimal(100000)) > 0){
                dto.setDec(COLUMN_RESULT_DEFAULT_MONEY);
                normalErrorImportList.add(dto);
                continue;
            }
            if(bigDecimalTrans(dto.getPunishMoney()) == null){
                dto.setDec(BIG_DECIMAL_ABNORMAL);
                normalErrorImportList.add(dto);
                continue;
            }
            //罚金不能小于-100000
            if(bigDecimalTrans(dto.getPunishMoney()) != null && bigDecimalTrans(dto.getPunishMoney()).compareTo(new BigDecimal(-100000)) < 0){
                dto.setDec(COLUMN_RESULT_DEFAULT_MONEY);
                normalErrorImportList.add(dto);
                continue;
            }

            if(!existCategoryList.contains(dto.getCategory())){
                if(allCount >= Constants.ONE_MILLISECOND){
                    dto.setDec(CATEGORY_TOO_MANY_ONE_MILLISECOND);
                    normalErrorImportList.add(dto);
                    continue;
                }
                tbMetaColumnCategoryDOList.add(transTbMetaColumnCategoryDO(dto.getCategory(),user));
                allCount++;
            }
            if(StringUtils.isEmpty(dto.getCategory())){
                dto.setCategory(Constants.OTHER_CATEGORY);
            }
            String repeatKey = dto.getColumnName() + Constants.UNDERLINE + dto.getCategory() + Constants.UNDERLINE + user.getUserId();
            if(currentColumn.contains(repeatKey)){
                dto.setDec(COLUMN_REPEAT);
                normalErrorImportList.add(dto);
                continue;
            }
            currentColumn.add(repeatKey);

            tempDO.setColumnName(dto.getColumnName());
            tempDO.setDescription(dto.getDescription());
            if(score.compareTo(BigDecimal.ZERO) > -1){
                tempDO.setMaxScore(score);
                tempDO.setMinScore(BigDecimal.ZERO);
            }else {
                //允许自定义评分
                if(setting.getCustomizeGrade() != null && setting.getCustomizeGrade()){
                    tempDO.setMaxScore(BigDecimal.ZERO);
                }else {
                    tempDO.setMaxScore(score);
                }
                tempDO.setMinScore(score);
            }
            tempDO.setAwardMoney(bigDecimalTrans(dto.getAwardMoney()));
            tempDO.setPunishMoney(bigDecimalTrans(dto.getPunishMoney()));
            tempDO.setEditUserId(user.getUserId());
            tempDO.setEditUserName(user.getName());
            tempDO.setUserDefinedScore(setting.getCustomizeGrade() != null && setting.getCustomizeGrade() ? 1 : 0);

            Long columnId = null;
            if(currentCategoryMap.get(dto.getCategory()) != null){
                columnId = tbMetaQuickColumnMapper.getByNameAndCategoryAndType(eid, dto.getColumnName(), currentCategoryMap.get(dto.getCategory()).getId(), MetaColumnTypeEnum.STANDARD_COLUMN.getCode(), user.getUserId());
            }
            //如果已存在， 判重规则：检查项名称、分类、属性都相同，才是相同检查项
            if(columnId != null){
                tempDO.setId(columnId);
                tempDO.setStatus(0);
                tempDO.setEditTime(new Date());
                ColumnMergeDTO build = ColumnMergeDTO.builder().normalColumnImportDTO(dto).columnDO(tempDO).build();
                quickColumnDOUpdateList.add(build);
            }else {
                tempDO.setMinScore(new BigDecimal(Constants.ZERO));
                tempDO.setColumnType(MetaColumnTypeEnum.STANDARD_COLUMN.getCode());
                tempDO.setCategory(dto.getCategory());
                tempDO.setCreateUser(user.getUserId());
                tempDO.setCreateUserName(user.getName());
                tempDO.setCreateTime(new Date());
                tempDO.setQuestionCcType("");
                tempDO.setQuestionCcName("");
                ColumnMergeDTO build = ColumnMergeDTO.builder().normalColumnImportDTO(dto).columnDO(tempDO).build();
                quickColumnDOList.add(build);
            }

        }
        DataSourceHelper.changeToSpecificDataSource(dbName);

        //插入分类
        if(CollectionUtils.isNotEmpty(tbMetaColumnCategoryDOList)){
            tbMetaColumnCategoryDAO.batchInsertSelective(tbMetaColumnCategoryDOList.stream().distinct().collect(Collectors.toList()), eid);
        }
        metaColumnCategoryList.addAll(tbMetaColumnCategoryDOList);
        Map<String, TbMetaColumnCategoryDO> categoryDOMap = metaColumnCategoryList.stream().collect(Collectors.toMap(TbMetaColumnCategoryDO::getCategoryName, data -> data, (a, b) -> a));


        List<TbMetaQuickColumnResultDO> columnResultList=new ArrayList<>();
        //插入检查项
        if(CollectionUtils.isNotEmpty(quickColumnDOList)){
            //设置分类id
            List<TbMetaQuickColumnDO> quickColumnDOS = quickColumnDOList.stream().map(c -> c.getColumnDO()).collect(Collectors.toList());
            setCategoryId(eid,categoryDOMap,quickColumnDOS);
            tbMetaQuickColumnMapper.batchInsert(eid,quickColumnDOS);
            quickColumnDOList.stream().forEach(c-> {
                NormalColumnImportDTO importDTO = c.getNormalColumnImportDTO();
                TbMetaQuickColumnDO columnDO = c.getColumnDO();
                columnResultList.addAll(addQuickColumnResult(columnDO, importDTO, new Date()));
            });
        }
        //更新检查项
        if(CollectionUtils.isNotEmpty(quickColumnDOUpdateList)){
            //设置分类id
            List<TbMetaQuickColumnDO> quickColumnDOS = quickColumnDOUpdateList.stream().map(c -> c.getColumnDO()).collect(Collectors.toList());
            setCategoryId(eid,categoryDOMap,quickColumnDOS);
            tbMetaQuickColumnMapper.batchUpdate(eid,quickColumnDOS);
            List<Long> ids = quickColumnDOS.stream().map(c -> c.getId()).collect(Collectors.toList());
            //删除之前的结果项
            tbMetaQuickColumnResultDAO.deleteByMetaQuickColumnIds(eid,ids);
            quickColumnDOUpdateList.stream().forEach(c-> {
                NormalColumnImportDTO importDTO = c.getNormalColumnImportDTO();
                TbMetaQuickColumnDO columnDO = c.getColumnDO();
                columnResultList.addAll(addQuickColumnResult(columnDO, importDTO, new Date()));
            });
        }
//        List<TbMetaQuickColumnResultDO> columnResultList = normalGetResult(eid,quickColumnDOUpdateList,quickColumnDOUpdateList, dbName,setting);
        if(CollectionUtils.isNotEmpty(columnResultList)){
            tbMetaQuickColumnResultDAO.batchInsert(eid,columnResultList);
        }
    }

    public List<TbMetaQuickColumnResultDO> addQuickColumnResult(TbMetaQuickColumnDO data,NormalColumnImportDTO dto,Date now){

        String checkImg = dto.getCheckImg();
        String checkDec = dto.getCheckDec();
        Integer mustPic;
        if (StringUtils.isNotEmpty(checkImg)){
            switch (checkImg){
                case "强制上传图片":
                    mustPic=ColumnEnum.MUST_PICA.getNum();
                    break;
                case "强制拍照":
                    mustPic=ColumnEnum.MUST_PICB.getNum();
                    break;
                case "强制拍视频":
                    mustPic=ColumnEnum.MUST_PICD.getNum();
                    break;
                default:
                    mustPic=ColumnEnum.MUST_PICC.getNum();
            }
        }else {
            mustPic=ColumnEnum.MUST_PICC.getNum();
        }
        String description2="";
        if (StringUtils.isNotEmpty(checkDec)){
            switch (checkDec){
                case "强制":
                    description2=ColumnEnum.FORCE.getCode();
                    break;
                case "不强制":
                    description2=ColumnEnum.IGNORE.getCode();
                    break;
                default:
                    description2=ColumnEnum.IGNORE.getCode();
            }
        }else {
            description2=ColumnEnum.IGNORE.getCode();
        }

        List<TbMetaQuickColumnResultDO>  dos=new ArrayList<TbMetaQuickColumnResultDO>();
        TbMetaQuickColumnResultDO pass = TbMetaQuickColumnResultDO.builder()
                .metaQuickColumnId(data.getId())
                .mappingResult(CheckResultEnum.PASS.getCode())
                .resultName(CheckResultEnum.PASS.getDesc())
                .defaultMoney(data.getAwardMoney() == null ? new BigDecimal(Constants.ZERO_STR) : data.getAwardMoney())
                .orderNum(1)
                .maxScore(data.getMaxScore() == null ? new BigDecimal(0) : data.getMaxScore())
                .minScore(data.getMinScore() == null ? new BigDecimal(0) : data.getMinScore())
                .mustPic(mustPic)
                .description(description2)
                .createUserId(data.getCreateUser())
                .deleted(0)
                .createTime(now)
                .build();

        dos.add(pass);
        TbMetaQuickColumnResultDO fail = TbMetaQuickColumnResultDO.builder()
                .metaQuickColumnId(data.getId())
                .mappingResult(CheckResultEnum.FAIL.getCode())
                .resultName(CheckResultEnum.FAIL.getDesc())
                .defaultMoney(data.getPunishMoney() == null ? new BigDecimal(Constants.ZERO_STR) : (data.getPunishMoney().abs().multiply(new BigDecimal("-1"))))
                .minScore(new BigDecimal(Constants.ZERO_STR))
                .maxScore(new BigDecimal(Constants.ZERO_STR))
                .mustPic(mustPic)
                .orderNum(2)
                .createUserId(data.getCreateUser())
                .deleted(0)
                .description(description2)
                .createTime(now)
                .build();
        dos.add(fail);
        TbMetaQuickColumnResultDO inapplicable = TbMetaQuickColumnResultDO.builder()
                .metaQuickColumnId(data.getId())
                .mappingResult(CheckResultEnum.INAPPLICABLE.getCode())
                .resultName(CheckResultEnum.INAPPLICABLE.getDesc())
                .defaultMoney(new BigDecimal(Constants.ZERO_STR))
                .mustPic(mustPic)
                .orderNum(3)
                .minScore(new BigDecimal(Constants.ZERO_STR))
                .maxScore(new BigDecimal(Constants.ZERO_STR))
                .createUserId(data.getCreateUser())
                .deleted(0)
                .description(description2)
                .createTime(now)
                .build();
        dos.add(inapplicable);
        return dos;
    }


    public void importDealResultColumn(String eid, String dbName, List<ResultColumnImportDTO> resultColumnImportList, List<ResultColumnImportDTO> resultColumnErrorImportList, Integer columnType, CurrentUser user) {
        if(CollectionUtils.isEmpty(resultColumnImportList)){
            return;
        }


        List<TbMetaQuickColumnDO> quickColumnDOList = new ArrayList<>();
        List<TbMetaQuickColumnDO> quickColumnDOUpdateList = new ArrayList<>();

        Map<TbMetaQuickColumnDO,List<TbMetaQuickColumnReasonDO>> quickColumnDOMap = new HashMap<>();
        Map<TbMetaQuickColumnDO,List<TbMetaQuickColumnReasonDO>> quickColumnDOUpdateMap = new HashMap<>();
        List<String> currentColumn = new ArrayList<>();
        TbMetaQuickColumnDO tempDO;

        //查出当前已有分类
        List<TbMetaColumnCategoryDO> metaColumnCategoryList = tbMetaColumnCategoryDAO.getMetaColumnCategoryList(eid, null);
        List<String> existCategoryList = metaColumnCategoryList.stream().map(TbMetaColumnCategoryDO::getCategoryName).collect(Collectors.toList());
        Map<String, TbMetaColumnCategoryDO> currentCategoryMap = metaColumnCategoryList.stream().collect(Collectors.toMap(TbMetaColumnCategoryDO::getCategoryName, data -> data, (a, b) -> a));
        Integer allCount = tbMetaColumnCategoryDAO.getAllCount(eid);

        List<TbMetaColumnCategoryDO> tbMetaColumnCategoryDOList = new ArrayList<>();

        Map<String,Map<Integer, QuickColumnResultImportDTO>> quickColumnResultMap = new HashMap<>();
        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO setting = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(eid);
        DataSourceHelper.changeToSpecificDataSource(dbName);


        for (ResultColumnImportDTO dto : resultColumnImportList) {
            tempDO = new TbMetaQuickColumnDO();
            if(StringUtils.isBlank(dto.getColumnName())){
                dto.setDec(COLUMN_NAME_BLANK);
                resultColumnErrorImportList.add(dto);
                continue;
            }
            if(dto.getColumnName().length() > Constants.QUICK_COLUMN_NAME_MAX_LENGTH){
                dto.setDec(COLUMN_NAME_LENGTH_TOO_LONG);
                resultColumnErrorImportList.add(dto);
                continue;
            }
            if(StringUtils.isNotBlank(dto.getCategory()) && dto.getCategory().length() > Constants.CATEGORY_NAME_MAX_LENGTH){
                dto.setDec(CATEGORY_LENGTH_TOO_LONG);
                resultColumnErrorImportList.add(dto);
                continue;
            }
            if(StringUtils.isNotBlank(dto.getDescription()) && dto.getDescription().length() > Constants.QUICK_COLUMN_DESCRIPTION_NAME_MAX_LENGTH){
                dto.setDec(COLUMN_DESCRIPTION_LENGTH_TOO_LONG);
                resultColumnErrorImportList.add(dto);
                continue;
            }
            if(!existCategoryList.contains(dto.getCategory())){
                if(allCount >= Constants.ONE_MILLISECOND){
                    dto.setDec(CATEGORY_TOO_MANY_ONE_MILLISECOND);
                    resultColumnErrorImportList.add(dto);
                    continue;
                }
                tbMetaColumnCategoryDOList.add(transTbMetaColumnCategoryDO(dto.getCategory(),user));
                allCount++;
            }
            if(StringUtils.isEmpty(dto.getCategory())){
                dto.setCategory(Constants.OTHER_CATEGORY);
            }
            String repeatKey = dto.getColumnName() + Constants.UNDERLINE + dto.getCategory() + Constants.UNDERLINE + columnType;
            if(currentColumn.contains(repeatKey)){
                dto.setDec(COLUMN_REPEAT);
                resultColumnErrorImportList.add(dto);
                continue;
            }
            currentColumn.add(repeatKey);

            tempDO.setColumnName(dto.getColumnName());
            tempDO.setDescription(dto.getDescription());
            tempDO.setCategory(dto.getCategory());
            tempDO.setColumnType(columnType);
            tempDO.setEditUserId(user.getUserId());
            tempDO.setEditUserName(user.getName());
            tempDO.setUserDefinedScore(setting.getCustomizeGrade() != null && setting.getCustomizeGrade() ? 1 : 0);
            Long columnId;
            if(currentCategoryMap.get(dto.getCategory()) != null){
                columnId = tbMetaQuickColumnMapper.getByNameAndCategoryAndType(eid, dto.getColumnName(), currentCategoryMap.get(dto.getCategory()).getId(), columnType, user.getUserId());
            } else {
                columnId = null;
            }

            Map<Integer, QuickColumnResultImportDTO> map = dto.getMap();

            quickColumnResultMap.put(dto.getCategory() + Constants.UNDERLINE + dto.getColumnName() + Constants.UNDERLINE + columnType,map);
            boolean resultError = false;
            List<String> descList = CheckResultEnum.getDescList();
            int effectiveResultNum = 0;
            //校验结果项
            for (Map.Entry<Integer, QuickColumnResultImportDTO> entry : map.entrySet()) {
                QuickColumnResultImportDTO value = entry.getValue();
                boolean redOrVeto = (MetaColumnTypeEnum.RED_LINE_COLUMN.getCode().equals(columnType) && CheckResultEnum.FAIL.getDesc().equals(value.getResult())) ||
                        MetaColumnTypeEnum.VETO_COLUMN.getCode().equals(columnType) && CheckResultEnum.FAIL.getDesc().equals(value.getResult());
                if(StringUtils.isBlank(value.getName()) || StringUtils.isBlank(value.getResult())){
                    continue;
                }
                BigDecimal score = bigDecimalTrans(value.getScore());
                if(redOrVeto){
                    score = new BigDecimal(Constants.ZERO);
                }
                if(score == null){
                    dto.setDec(BIG_DECIMAL_ABNORMAL);
                    resultColumnErrorImportList.add(dto);
                    resultError = true;
                    break;
                }else if(score.intValue() > Constants.QUICK_COLUMN_MAX_SCORE){
                    dto.setDec(COLUMN_RESULT_MAX_SCORE);
                    resultColumnErrorImportList.add(dto);
                    resultError = true;
                    break;
                }
                if(value.getName().length() > Constants.QUICK_COLUMN_RESULT_NAME_MAX_LENGTH){
                    dto.setDec(COLUMN_RESULT_NAME_LENGTH_TOO_LONG);
                    resultColumnErrorImportList.add(dto);
                    resultError = true;
                    break;
                }
                if(!descList.contains(value.getResult())){
                    dto.setDec(COLUMN_RESULT_ABNORMAL);
                    resultColumnErrorImportList.add(dto);
                    resultError = true;
                    break;
                }
                if(Constants.QUICK_COLUMN_RESULT_MAX_MONEY.compareTo(value.getDefaultMoney()) < Constants.ZERO || Constants.QUICK_COLUMN_RESULT_MIN_MONEY.compareTo(value.getDefaultMoney()) > Constants.ZERO) {
                    dto.setDec(COLUMN_RESULT_DEFAULT_MONEY);
                    resultColumnErrorImportList.add(dto);
                    resultError = true;
                    break;
                }
                effectiveResultNum ++;
            }
            if (resultError){
                continue;
            }
            if(effectiveResultNum == 0){
                dto.setDec(COLUMN_RESULT_AT_LEAST_ONE);
                resultColumnErrorImportList.add(dto);
                continue;
            }

            //如果已存在， 判重规则：检查项名称、分类、属性都相同，才是相同检查项
            if(columnId != null){
                tempDO.setId(columnId);
                tempDO.setStatus(0);
                tempDO.setEditTime(new Date());
                String reasonNameFail = dto.getReasonNameFail();
                String reasonNameNA = dto.getReasonNameNA();
                //需要新增的原因项
                List<TbMetaQuickColumnReasonDO> haveIdReasonDOS =new ArrayList<>();
                try {
                    if (StringUtils.isNotEmpty(reasonNameFail)){
                        String[] split = StringUtils.split(reasonNameFail, ",");
                        List<String> names = Arrays.asList(split);
                        if (CollectionUtils.isNotEmpty(names)){
                            names.stream().forEach(name->{
                                TbMetaQuickColumnReasonDO reasonDO = TbMetaQuickColumnReasonDO.builder()
                                        .quickColumnId(columnId)
                                        .createUserId(user.getUserId())
                                        .reasonName(name)
                                        .mappingResult(CheckResultEnum.FAIL.getCode())
                                        .build();
                                haveIdReasonDOS.add(reasonDO);
                            });
                        }
                    }
                    if (StringUtils.isNotEmpty(reasonNameNA)){
                        String[] split = StringUtils.split(reasonNameNA, ",");
                        List<String> names = Arrays.asList(split);
                        if (CollectionUtils.isNotEmpty(names)){
                            names.stream().forEach(name->{
                                TbMetaQuickColumnReasonDO reasonDO = TbMetaQuickColumnReasonDO.builder()
                                        .quickColumnId(columnId)
                                        .createUserId(user.getUserId())
                                        .reasonName(name)
                                        .mappingResult(CheckResultEnum.INAPPLICABLE.getCode())
                                        .build();
                                haveIdReasonDOS.add(reasonDO);
                            });
                        }
                    }
                }catch (Exception e){
                    log.error("原因项导入失败,格式化问题','",e);
                    throw new RuntimeException("原因项导入失败,格式化问题','");
                }
                quickColumnDOUpdateList.add(tempDO);
                quickColumnDOUpdateMap.put(tempDO,haveIdReasonDOS);


            }else {
                tempDO.setMinScore(new BigDecimal(Constants.ZERO));
                tempDO.setCreateUser(user.getUserId());
                tempDO.setCreateTime(new Date());
                tempDO.setQuestionCcType("");
                tempDO.setQuestionCcName("");
                tempDO.setAwardMoney(new BigDecimal(Constants.ZERO));
                tempDO.setPunishMoney(new BigDecimal(Constants.ZERO));
                tempDO.setMaxScore(new BigDecimal(Constants.ZERO));

                List<TbMetaQuickColumnReasonDO> noIdReasonDOS = new ArrayList<>();
                String reasonNameFail = dto.getReasonNameFail();
                String reasonNameNA = dto.getReasonNameNA();
                try {
                    if (StringUtils.isNotEmpty(reasonNameFail)){
                        String[] split = StringUtils.split(reasonNameFail, ",");
                        List<String> names = Arrays.asList(split);
                        if (CollectionUtils.isNotEmpty(names)){
                            names.stream().forEach(name->{
                                TbMetaQuickColumnReasonDO reasonDO = TbMetaQuickColumnReasonDO.builder()
                                        .quickColumnId(null)
                                        .createUserId(user.getUserId())
                                        .reasonName(name)
                                        .mappingResult(CheckResultEnum.FAIL.getCode())
                                        .build();
                                noIdReasonDOS.add(reasonDO);
                            });
                        }
                    }
                    if (StringUtils.isNotEmpty(reasonNameNA)){
                        String[] split = StringUtils.split(reasonNameNA, ",");
                        List<String> names = Arrays.asList(split);
                        if (CollectionUtils.isNotEmpty(names)){
                            names.stream().forEach(name->{
                                TbMetaQuickColumnReasonDO reasonDO = TbMetaQuickColumnReasonDO.builder()
                                        .quickColumnId(null)
                                        .createUserId(user.getUserId())
                                        .reasonName(name)
                                        .mappingResult(CheckResultEnum.INAPPLICABLE.getCode())
                                        .build();
                                noIdReasonDOS.add(reasonDO);
                            });
                        }
                    }
                }catch (Exception e){
                    log.error("原因项导入失败,格式化问题','",e);
                    throw new RuntimeException("原因项导入失败,格式化问题','");
                }
                quickColumnDOList.add(tempDO);
                quickColumnDOMap.put(tempDO,noIdReasonDOS);
            }


        }
        DataSourceHelper.changeToSpecificDataSource(dbName);
        //插入分类
        if(CollectionUtils.isNotEmpty(tbMetaColumnCategoryDOList)){
            tbMetaColumnCategoryDAO.batchInsertSelective(tbMetaColumnCategoryDOList.stream().distinct().collect(Collectors.toList()), eid);
        }
        metaColumnCategoryList.addAll(tbMetaColumnCategoryDOList);
        Map<String, TbMetaColumnCategoryDO> categoryDOMap = metaColumnCategoryList.stream().collect(Collectors.toMap(TbMetaColumnCategoryDO::getCategoryName, data -> data, (a, b) -> a));



        //插入检查项
        if(!quickColumnDOList.isEmpty()){

            //设置分类id
            setCategoryId(eid,categoryDOMap,quickColumnDOList);
            tbMetaQuickColumnMapper.batchInsert(eid,quickColumnDOList);

            Map<List<TbMetaQuickColumnResultDO>, List<TbMetaQuickColumnReasonDO>> columnResultList = getColumnResultList(eid, quickColumnDOMap, null, quickColumnResultMap, user, dbName, setting);

            for (Map.Entry<List<TbMetaQuickColumnResultDO>, List<TbMetaQuickColumnReasonDO>> listListEntry :columnResultList.entrySet()) {
                //插入结果项
                List<TbMetaQuickColumnResultDO> key = listListEntry.getKey();
                if(CollectionUtils.isNotEmpty(key)){
                    tbMetaQuickColumnResultDAO.batchInsert(eid,key);
                }
                //插入原因项
                List<TbMetaQuickColumnReasonDO> reasonDOS1 = listListEntry.getValue();
                if (CollectionUtils.isNotEmpty(reasonDOS1)){
                    tbMetaQuickColumnReasonMapper.batchInsert(eid,reasonDOS1);
                }
            }

        }
        //更新检查项
        if(!quickColumnDOUpdateList.isEmpty()){

            //设置分类id
            setCategoryId(eid,categoryDOMap,quickColumnDOUpdateList);
            tbMetaQuickColumnMapper.batchUpdate(eid,quickColumnDOUpdateList);

            Map<List<TbMetaQuickColumnResultDO>, List<TbMetaQuickColumnReasonDO>> columnResultList = getColumnResultList(eid, null, quickColumnDOUpdateMap, quickColumnResultMap, user, dbName, setting);

            for (Map.Entry<List<TbMetaQuickColumnResultDO>, List<TbMetaQuickColumnReasonDO>> listListEntry : columnResultList.entrySet()) {
                List<TbMetaQuickColumnResultDO> key = listListEntry.getKey();
                List<Long> ids = quickColumnDOUpdateList.stream().map(c -> c.getId()).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(ids)){
                    //删除之前的结果项
                    tbMetaQuickColumnResultDAO.deleteByMetaQuickColumnIds(eid,ids);
                    tbMetaQuickColumnResultDAO.batchInsert(eid,key);
                }
                //插入原因项
                List<TbMetaQuickColumnReasonDO> value = listListEntry.getValue();
                if (CollectionUtils.isNotEmpty(value)){
                    tbMetaQuickColumnReasonMapper.deleteByQuickColumnIds(eid,ids);
                    tbMetaQuickColumnReasonMapper.batchInsert(eid,value);
                }
            }


        }


    }

    private BigDecimal bigDecimalTrans(String value){
        try {
            if(StringUtils.isBlank(value)){
                return new BigDecimal(Constants.ZERO);
            }
            return new BigDecimal(value);
        }catch (Exception e){
            return null;
        }
    }

    private TbMetaColumnCategoryDO transTbMetaColumnCategoryDO(String category,CurrentUser user){
        TbMetaColumnCategoryDO insert = new TbMetaColumnCategoryDO();
        insert.setCategoryName(category);
        insert.setIsDefault(false);
        insert.setCreateId(user.getUserId());
        insert.setOrderNum(Constants.INDEX_ZERO);
        return insert;
    }

    private void setCategoryId(String eid,Map<String, TbMetaColumnCategoryDO> categoryDOMap,List<TbMetaQuickColumnDO> quickColumnDOList){
        Long otherCategoryId = tbMetaColumnCategoryDAO.getOtherCategoryId(eid);
        for (TbMetaQuickColumnDO columnDO : quickColumnDOList) {
            TbMetaColumnCategoryDO categoryDO = categoryDOMap.get(columnDO.getCategory());
            if(categoryDO != null){
                columnDO.setCategoryId(categoryDO.getId());
            }else {
                columnDO.setCategoryId(otherCategoryId);
            }
        }
    }

    private TbMetaQuickColumnResultDO transTbMetaQuickColumnResultDO(QuickColumnResultImportDTO resultImportDTO,Long columnId,CurrentUser user,Boolean customizeGrade){
        TbMetaQuickColumnResultDO insert = new TbMetaQuickColumnResultDO();
        insert.setResultName(resultImportDTO.getName());
        BigDecimal score = resultImportDTO.getScore() == null ? new BigDecimal(Constants.ZERO) : new BigDecimal(resultImportDTO.getScore());
        if(score.compareTo(BigDecimal.ZERO) > -1){
            insert.setMaxScore(score);
            insert.setMinScore(BigDecimal.ZERO);
        }else {
            if(customizeGrade != null && customizeGrade){
                insert.setMaxScore(BigDecimal.ZERO);
            }else {
                insert.setMaxScore(score);
            }
            insert.setMinScore(score);
        }
        insert.setDefaultMoney(resultImportDTO.getDefaultMoney());
        insert.setDefaultMoney(resultImportDTO.getDefaultMoney());
        insert.setDescription(resultImportDTO.getCheckDec());
        insert.setMustPic(resultImportDTO.getCheckPic());
        insert.setOrderNum(Constants.INDEX_ONE);
        insert.setMappingResult(CheckResultEnum.getByDesc(resultImportDTO.getResult()));
        insert.setCreateUserId(user.getUserId());
        insert.setUpdateUserId(user.getUserId());
        insert.setCreateTime(new Date());
        insert.setUpdateTime(new Date());
        insert.setMetaQuickColumnId(columnId);

        return insert;
    }


    private  List<TbMetaQuickColumnReasonDO> getReasonDOS(CurrentUser user, String result,Long columnId, List<String> reasonName) {
        List<TbMetaQuickColumnReasonDO> reasonDOS=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reasonName)){
            reasonName.stream().forEach(name->{
                TbMetaQuickColumnReasonDO reasonDO = new TbMetaQuickColumnReasonDO();
                reasonDO.setCreateUserId(user.getUserId());
                reasonDO.setReasonName(name);
                reasonDO.setMappingResult(result);
                reasonDO.setQuickColumnId(columnId);
                reasonDOS.add(reasonDO);
            });
        }
        return reasonDOS;
    }

    private Map<List<TbMetaQuickColumnResultDO>,List<TbMetaQuickColumnReasonDO>> getColumnResultList(String eid,Map<TbMetaQuickColumnDO,List<TbMetaQuickColumnReasonDO>> quickColumnDOList,Map<TbMetaQuickColumnDO,List<TbMetaQuickColumnReasonDO>> quickColumnDOUpdateList,
                                                                Map<String,Map<Integer, QuickColumnResultImportDTO>> quickColumnResultMap,CurrentUser user,String dbName,EnterpriseStoreCheckSettingDO setting){
//        Integer mustPic = getMustPic(setting);

        List<TbMetaQuickColumnResultDO> resultDOS = new ArrayList<>();
        List<TbMetaQuickColumnReasonDO> allReasonDOS=new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource(dbName);
        Map<List<TbMetaQuickColumnResultDO>,List<TbMetaQuickColumnReasonDO>> result=new HashMap<>();
        if(quickColumnDOList != null &&!quickColumnDOList.isEmpty()){
            for (Map.Entry<TbMetaQuickColumnDO, List<TbMetaQuickColumnReasonDO>> merge : quickColumnDOList.entrySet()) {
                TbMetaQuickColumnDO tbMetaQuickColumnDO = merge.getKey();
                List<TbMetaQuickColumnReasonDO> reasonDOS = merge.getValue();

                if (CollectionUtils.isNotEmpty(reasonDOS)){
                    reasonDOS.stream().forEach(reasonDO -> {
                        reasonDO.setQuickColumnId(tbMetaQuickColumnDO.getId());
                    });
                    allReasonDOS.addAll(reasonDOS);
                }
                String key = tbMetaQuickColumnDO.getCategory()+ Constants.UNDERLINE + tbMetaQuickColumnDO.getColumnName()+ Constants.UNDERLINE + tbMetaQuickColumnDO.getColumnType();
                Map<Integer, QuickColumnResultImportDTO> integerQuickColumnResultImportDTOMap = quickColumnResultMap.get(key);
                if(integerQuickColumnResultImportDTOMap == null){
                    continue;
                }
                for (Map.Entry<Integer, QuickColumnResultImportDTO> entry : integerQuickColumnResultImportDTOMap.entrySet()) {
                    QuickColumnResultImportDTO value = entry.getValue();
                    if(value == null){
                        continue;
                    }
                    if(StringUtils.isBlank(value.getResult()) || StringUtils.isBlank(value.getName())){
                        continue;
                    }
                    boolean redOrVeto = (MetaColumnTypeEnum.RED_LINE_COLUMN.getCode().equals(tbMetaQuickColumnDO.getColumnType()) && CheckResultEnum.FAIL.getDesc().equals(value.getResult())) ||
                            MetaColumnTypeEnum.VETO_COLUMN.getCode().equals(tbMetaQuickColumnDO.getColumnType()) && CheckResultEnum.FAIL.getDesc().equals(value.getResult());
                    if(StringUtils.isBlank(value.getName()) || StringUtils.isBlank(value.getResult())){
                        continue;
                    }
                    if(redOrVeto){
                        value.setScore(Constants.ZERO_STR);
                    }
                    resultDOS.add(transTbMetaQuickColumnResultDO(value, tbMetaQuickColumnDO.getId(), user, setting.getCustomizeGrade()));
                }
            }
        }

        if(quickColumnDOUpdateList != null &&!quickColumnDOUpdateList.isEmpty()){
            for (Map.Entry<TbMetaQuickColumnDO, List<TbMetaQuickColumnReasonDO>> merge : quickColumnDOUpdateList.entrySet()) {
                TbMetaQuickColumnDO updateQuickColumnDO = merge.getKey();
                List<TbMetaQuickColumnReasonDO> reasonDOS = merge.getValue();
                if (CollectionUtils.isNotEmpty(reasonDOS)){
                    allReasonDOS.addAll(reasonDOS);
                }
                String key = updateQuickColumnDO.getCategory() + Constants.UNDERLINE + updateQuickColumnDO.getColumnName() + Constants.UNDERLINE + updateQuickColumnDO.getColumnType();
                Map<Integer, QuickColumnResultImportDTO> integerQuickColumnResultImportDTOMap = quickColumnResultMap.get(key);
                if(integerQuickColumnResultImportDTOMap == null){
                    continue;
                }
                for (Map.Entry<Integer, QuickColumnResultImportDTO> entry : integerQuickColumnResultImportDTOMap.entrySet()) {
                    QuickColumnResultImportDTO value = entry.getValue();
                    if(value == null){
                        continue;
                    }
                    if(StringUtils.isBlank(value.getResult()) || StringUtils.isBlank(value.getName())){
                        continue;
                    }
                    resultDOS.add(transTbMetaQuickColumnResultDO(value, updateQuickColumnDO.getId(), user, setting.getCustomizeGrade()));
                }
            }
        }
        result.put(resultDOS,allReasonDOS);
        return result;
    }

    public List<ExportView> getExportView(List<NormalColumnImportDTO> normalErrorImportList, List<ResultColumnImportDTO> seniorErrorImportList,
                           List<ResultColumnImportDTO> redLineErrorImportList, List<ResultColumnImportDTO> vetoErrorImportList,
                           List<ResultColumnImportDTO> doubleErrorImportList){
        List<ExportView> exportViewList = new ArrayList<>();
        exportViewList.add(new ExportView(new ExportParams(STANDARD_TITLE,MetaColumnTypeEnum.STANDARD_COLUMN.getName(), ExcelType.XSSF),
                normalErrorImportList,NormalColumnImportDTO.class));
        exportViewList.add(new ExportView(new ExportParams(TITLE,MetaColumnTypeEnum.HIGH_COLUMN.getName(), ExcelType.XSSF),
                seniorErrorImportList,ResultColumnImportDTO.class));
        exportViewList.add(new ExportView(new ExportParams(TITLE,MetaColumnTypeEnum.RED_LINE_COLUMN.getName(), ExcelType.XSSF),
                redLineErrorImportList,ResultColumnImportDTO.class));
        exportViewList.add(new ExportView(new ExportParams(TITLE,MetaColumnTypeEnum.VETO_COLUMN.getName(), ExcelType.XSSF),
                vetoErrorImportList,ResultColumnImportDTO.class));
        exportViewList.add(new ExportView(new ExportParams(TITLE,MetaColumnTypeEnum.DOUBLE_COLUMN.getName(), ExcelType.XSSF),
                doubleErrorImportList,ResultColumnImportDTO.class));
        return exportViewList;
    }

    public List<TbMetaQuickColumnResultDO> normalGetResult(String eid,List<TbMetaQuickColumnDO> quickColumnDOList,List<TbMetaQuickColumnDO> updateQuickColumnDOList,String dbName,EnterpriseStoreCheckSettingDO setting){
        Integer mustPic = getMustPic(setting);
        List<TbMetaQuickColumnResultDO> quickColumnResult = new ArrayList<>();
        Date now = new Date(System.currentTimeMillis());
        ListUtils.emptyIfNull(quickColumnDOList).forEach(data -> {
            addQuickColumnResult(quickColumnResult,data,mustPic,now);
        });
        DataSourceHelper.changeToSpecificDataSource(dbName);
        ListUtils.emptyIfNull(updateQuickColumnDOList).forEach(data -> {
            //删除之前的结果项
            tbMetaQuickColumnResultDAO.deleteByMetaQuickColumnId(eid,data.getId());
            addQuickColumnResult(quickColumnResult,data,mustPic,now);
        });

        return quickColumnResult;
    }

    public void addQuickColumnResult(List<TbMetaQuickColumnResultDO> quickColumnResult,TbMetaQuickColumnDO data,Integer mustPic,Date now){

        TbMetaQuickColumnResultDO pass = TbMetaQuickColumnResultDO.builder()
                .metaQuickColumnId(data.getId())
                .mappingResult(CheckResultEnum.PASS.getCode())
                .resultName(CheckResultEnum.PASS.getDesc())
                .defaultMoney(data.getAwardMoney() == null ? new BigDecimal(Constants.ZERO_STR) : data.getAwardMoney())
                .mustPic(mustPic)
                .orderNum(1)
                .maxScore(data.getMaxScore() == null ? new BigDecimal(0) : data.getMaxScore())
                .minScore(data.getMinScore() == null ? new BigDecimal(0) : data.getMinScore())
                .createUserId(data.getCreateUser())
                .deleted(0)
                .description("ignore")
                .createTime(now)
                .build();
        quickColumnResult.add(pass);
        TbMetaQuickColumnResultDO fail = TbMetaQuickColumnResultDO.builder()
                .metaQuickColumnId(data.getId())
                .mappingResult(CheckResultEnum.FAIL.getCode())
                .resultName(CheckResultEnum.FAIL.getDesc())
                .defaultMoney(data.getPunishMoney() == null ? new BigDecimal(Constants.ZERO_STR) : (data.getPunishMoney().abs().multiply(new BigDecimal("-1"))))
                .minScore(new BigDecimal(Constants.ZERO_STR))
                .maxScore(new BigDecimal(Constants.ZERO_STR))
                .mustPic(mustPic)
                .orderNum(2)
                .createUserId(data.getCreateUser())
                .deleted(0)
                .description("ignore")
                .createTime(now)
                .build();
        quickColumnResult.add(fail);
        TbMetaQuickColumnResultDO inapplicable = TbMetaQuickColumnResultDO.builder()
                .metaQuickColumnId(data.getId())
                .mappingResult(CheckResultEnum.INAPPLICABLE.getCode())
                .resultName(CheckResultEnum.INAPPLICABLE.getDesc())
                .defaultMoney(new BigDecimal(Constants.ZERO_STR))
                .mustPic(mustPic)
                .orderNum(3)
                .minScore(new BigDecimal(Constants.ZERO_STR))
                .maxScore(new BigDecimal(Constants.ZERO_STR))
                .createUserId(data.getCreateUser())
                .deleted(0)
                .description("ignore")
                .createTime(now)
                .build();
        quickColumnResult.add(inapplicable);
    }

    public Integer getMustPic(EnterpriseStoreCheckSettingDO setting){
        Boolean uploadImgNeed = setting != null && setting.getUploadImgNeed();
        Boolean uploadLocalImg = setting != null && setting.getUploadLocalImg();
        int mustPic = 0;
        if(uploadImgNeed && uploadLocalImg){
            mustPic = 2;
        }else if(uploadImgNeed){
            mustPic = 1;
        }
        return mustPic;
    }

    public boolean allFieldIsNULL(Object o) {
        try {
            for (Field field : o.getClass().getDeclaredFields()) {
                field.setAccessible(true);//把私有属性公有化
                Object object = field.get(o);
                if (object instanceof CharSequence) {
                    if (!ObjectUtils.isEmpty((String) object)) {
                        return false;
                    }
                } else {
                    if (null != (object)) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            log.info("导入检测对象所有字段是否为空失败");
        }
        return true;

    }
}
