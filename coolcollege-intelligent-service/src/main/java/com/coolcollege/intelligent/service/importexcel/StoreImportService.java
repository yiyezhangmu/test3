package com.coolcollege.intelligent.service.importexcel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.authentication.UserAuthMappingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.dao.store.StoreGroupMapper;
import com.coolcollege.intelligent.dao.store.StoreMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreSettingDO;
import com.coolcollege.intelligent.model.enums.StoreIsDeleteEnum;
import com.coolcollege.intelligent.model.enums.StoreStatusEnum;
import com.coolcollege.intelligent.model.export.dto.StoreBaseInfoExportDTO;
import com.coolcollege.intelligent.model.export.dto.StoreBaseInfoExportErrorDTO;
import com.coolcollege.intelligent.model.impoetexcel.ImportConstants;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.StoreImportDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.StoreRangeDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.TaskStoreRangeDTO;
import com.coolcollege.intelligent.model.impoetexcel.vo.ImportDistinctVO;
import com.coolcollege.intelligent.model.impoetexcel.vo.ImportStoreDistinctVO;
import com.coolcollege.intelligent.model.impoetexcel.vo.StoreRangeVO;
import com.coolcollege.intelligent.model.impoetexcel.vo.TaskStoreRangeVO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.AuthVisualDTO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.model.store.StoreGroupDO;
import com.coolcollege.intelligent.model.store.dto.ExtendFieldInfoDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.queryDto.StoreGroupQueryDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.authentication.AuthVisualService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.store.StoreService;
import com.coolcollege.intelligent.util.DateFormatUtil;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.Constants.BASE_STORE_TITLE;
import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_SEC;
import static com.coolcollege.intelligent.common.util.DateUtils.TIME_FORMAT_SEC2;
import static java.util.regex.Pattern.matches;

/**
 * @author 邵凌志
 * @date 2020/12/14 11:15
 */
@Service
@Slf4j
public class StoreImportService extends ImportBaseService{

    @Resource
    private ImportTaskMapper importTaskMapper;

    @Resource
    private EnterpriseUserMapper userMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private StoreMapper storeMapper;

    @Resource
    private StoreGroupMapper groupMapper;

    @Resource
    private UserAuthMappingMapper userAuthMappingMapper;

    @Autowired
    private GenerateOssFileService generateOssFileService;

    @Autowired
    private StoreService storeService;



    @Autowired
    @Lazy
    private StoreImportService storeImportService;

    @Resource
    private EnterpriseStoreSettingMapper enterpriseStoreSettingMapper;

    @Autowired
    private StoreDao storeDao;

    @Autowired
    private RegionService regionService;

    @Resource
    private SimpleMessageService simpleMessageService;

    @Resource
    private AuthVisualService authVisualService;


    private static SimpleDateFormat SDF = new SimpleDateFormat(TIME_FORMAT_SEC2);


    private static final String STORE_ID_NOT_BLANK = "门店ID不能为空";
    private static final String STORE_ID_NOT_EXIST = "门店ID不存在";


    private static final String NOT_BLANK = "门店名称不能为空";
    private static final String NAME_TOO_LANG = "门店名称长度超过100个字符";
    private static final String NUM_TOO_LANG = "门店编号长度超过20个字符";
    private static final String EXIST_STORE = "门店名称已经存在";
    private static final String EXIST_NUM = "该门店编号已经存在";
    private static final String NUM_NOT_ZH = "门店编号不能包含中文";
    private static final String NO_NULL_NUM = "门店编号不能为空";
    private static final String MUCH_STORE = "该门店名称在系统中存在多个，请按照门店编号去重";
    private static final String MUCH_NUM = "该门店编号在系统中存在多个，请先在系统中订正历史数据";
    private static final String NOT_EXIST_GROUP = "[%s]门店分组名称不存在";
    private static final String NOT_EXIST_USER = "[%s]人员不存在";
    private static final String MUCH_USER = "[%s]人员存在多个";
    private static final String NOT_EXIST_REGION = "区域名称不存在";
    private static final String REMARK_TOO_LANG = "备注长度大于400";
    private static final String DATE_ERROR = "时间格式错误";



    private static final String STORE_TITLE = "说明：\n" +
            "1、门店名称必填\n" +
            "2、门店编号为空则自动忽略\n" +
            "3、门店分组，门店人员支持多字段， 多个字段之间请用逗号（英文半角字符）隔开；\n" +
            "4、手机号码暂只支持一个；\n" +
            "5、时间格式为XX：XX（英文冒号间隔，中间无空格）；\n" +
            "6、门店编号不支持中文；\n" +
            "7、备注最多支持 400 个字\n" +
            "8、门店分组、区域名称、人员必须是系统已存在的信息，如果不存在，则会导入失败\n" +
            "9、请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败；";


    @Async("importExportThreadPool")
    public void importData(String eid, ImportDistinctVO distinct, CurrentUser user, Future<List<StoreImportDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            log.info("开始获取门店导入redis锁，企业id：{}", eid);
            // 加所操作
            boolean lock = lock(eid, ImportConstants.STORE_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<StoreImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }
            log.info("总条数：{}", importList.size());
            importStore(eid, distinct, user, importList, contentType, task);
        }  catch (BaseException e) {
            log.error("门店文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            log.error("门店文件上传失败：{}" + eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR+e.getMessage());
            importTaskMapper.update(eid, task);
        } finally {
            log.info("释放门店导入redis锁，企业id：{}", eid);
            unlock(eid, ImportConstants.STORE_KEY);
        }
    }

    @Async("importExportThreadPool")
    public void importBaseData(String eid, ImportDistinctVO distinct, CurrentUser user, Future<List<Map>> importTask,
                               String contentType, ImportTaskDO task,Boolean enableDingSync) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            log.info("开始获取门店导入redis锁，企业id：{}", eid);
            // 加所操作
            boolean lock = lock(eid, ImportConstants.BASE_STORE_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<Map> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }
            log.info("总条数：{}", importList.size());
            importBaseStore(eid, distinct, user, importList, contentType, task,enableDingSync);
        }catch (ServiceException e) {
            log.error("门店信息补全导入业务错误：{}",eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(e.getErrorMessage());
            importTaskMapper.update(eid, task);
        } catch (Exception e) {
            log.error("门店文件上传失败：{}", eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR+e.getMessage());
            importTaskMapper.update(eid, task);
        } finally {
            log.info("释放门店导入redis锁，企业id：{}", eid);
            unlock(eid, ImportConstants.BASE_STORE_KEY);
        }
    }
    public void importBaseStore(String eid, ImportDistinctVO distinct, CurrentUser user, List<Map> importList,
                                String contentType, ImportTaskDO task,Boolean enableDingSync){

        // 需要新增的数据
        List<StoreDO> stores = new ArrayList<>();
        // 门店-门店分组关联列表
        List<StoreGroupQueryDTO> storeGroupList = new ArrayList<>();
        // 有错误的数据
        List<StoreBaseInfoExportErrorDTO> errorList = new ArrayList<>();
        //校验门店编号
        String pat = "[\u4E00-\u9FA5]+";
        long currentTime = System.currentTimeMillis();
        // 地图缓存
        Map<String, String> mapCache = new HashMap<>();

        // 门店分组名称-分组id
        List<StoreGroupDO> storeGroups = groupMapper.listStoreGroup(eid);
        Map<String, String> groupNameIdMap = storeGroups.stream()
                .filter(a -> a.getGroupName() != null && a.getGroupId() != null)
                .collect(Collectors.toMap(StoreGroupDO::getGroupName, StoreGroupDO::getGroupId,(a,b)->a));

        List<StoreDTO> allStoresByStoreNum = storeMapper.getAllStoresByStoreNum(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
        Map<String, String> storeNameMap = ListUtils.emptyIfNull(allStoresByStoreNum)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getStoreId()) && StringUtils.isNotBlank(data.getStoreName()))
                .collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getStoreName, (a, b) -> a));
        Map<String, String> extendFieldMap = ListUtils.emptyIfNull(allStoresByStoreNum)
                .stream()
                .filter(data -> StringUtils.isNotBlank(data.getStoreId()) && StringUtils.isNotBlank(data.getExtendField()))
                .collect(Collectors.toMap(StoreDTO::getStoreId, StoreDTO::getExtendField, (a, b) -> a));
        if(MapUtils.isEmpty(storeNameMap)){
            throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "企业中不存在门店，无法导入补全信息！");
        }
        DataSourceHelper.reset();
        List<ExtendFieldInfoDTO> extendFieldInfoDTOList = null;
        EnterpriseStoreSettingDO storeSettingDO = enterpriseStoreSettingMapper.getEnterpriseStoreSetting(eid);
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        String extendFieldInfo = storeSettingDO.getExtendFieldInfo();
        if(StringUtils.isNotEmpty(extendFieldInfo)){
            try {
                extendFieldInfoDTOList = JSONObject.parseArray(extendFieldInfo,ExtendFieldInfoDTO.class);
            } catch (Exception e) {
                log.error("扩展字段信息json转换异常！{}",e.getMessage(),e);
                throw new ServiceException(ErrorCodeEnum.FAIL.getCode(), "扩展字段信息json转换异常");
            }
        }


        log.info("校验门店基础信息，时间:{}" ,currentTime);
        StoreBaseInfoExportDTO store;
        Map<String,String> tempMap;
        //可以识别的字段名称
        List<String> identifyFields;
        for (Map map : importList) {
            identifyFields = new ArrayList<>();
            Class clazz = StoreBaseInfoExportDTO.class;
            Field[] fields = clazz.getDeclaredFields();
            tempMap = new HashMap<>();
            for (Field field : fields) {
                if (field.getAnnotation(Excel.class) != null) {
                    Excel excel = field.getAnnotation(Excel.class);
                    if(map.containsKey(excel.name())){
                        if(map.get(excel.name()) != null)
                        tempMap.put(field.getName(),String.valueOf(map.get(excel.name())));
                        identifyFields.add(excel.name());
                    }
                }
            }

            store = JSON.parseObject(JSON.toJSONString(tempMap), StoreBaseInfoExportDTO.class);

            // 门店Id不能为空
            String storeId = store.getStoreId();

            // 门店-门店分组关联列表
            List<StoreGroupQueryDTO> currStoreGroupList = new ArrayList<>();

            if (StrUtil.isBlank(storeId)) {
                errorList.add(mapStoreBaseInfoExportErrorDTO(store,STORE_ID_NOT_BLANK));
                continue;
            }
            if(StringUtils.isBlank(storeNameMap.get(storeId))){
                errorList.add(mapStoreBaseInfoExportErrorDTO(store,STORE_ID_NOT_EXIST));
                continue;
            }

            // 门店名称不能为空 未开启钉钉同步名称不能为空   开启钉钉同步会把这个值变成空值而不更新
            String storeName = store.getStoreName();
            if (StrUtil.isBlank(storeName)&&!enableDingSync) {
                errorList.add(mapStoreBaseInfoExportErrorDTO(store,NOT_BLANK));
                continue;
            }
            // 门店名称长度不能超过100
            if (!enableDingSync&&storeName.length() > 100) {
                errorList.add(mapStoreBaseInfoExportErrorDTO(store,NAME_TOO_LANG));
                continue;
            }
            if (enableDingSync) {
                store.setStoreName(storeNameMap.get(storeId));
            }

            String remark = store.getRemark();
            if (StrUtil.isNotBlank(remark) && remark.length() > 400) {
                errorList.add(mapStoreBaseInfoExportErrorDTO(store,REMARK_TOO_LANG));
                continue;
            }
            String storeNum = store.getStoreNum();
            if (StrUtil.isNotBlank(storeNum)) {
                storeNum = storeNum.trim();
                if (matches(pat, storeNum)) {
                    errorList.add(mapStoreBaseInfoExportErrorDTO(store,NUM_NOT_ZH));
                    continue;
                }
                if (storeNum.length() > 20) {
                    errorList.add(mapStoreBaseInfoExportErrorDTO(store,NUM_TOO_LANG));
                    continue;
                }
            }

            // 设置门店分组信息
            StoreGroupQueryDTO groupQueryDTO = new StoreGroupQueryDTO();
            groupQueryDTO.setStoreId(storeId);
            String storeGroup = store.getGroupName();
            if (StrUtil.isNotBlank(storeGroup)) {
                String[] groupArray = storeGroup.split(",");
                List<String> groupIds = new ArrayList<>();
                // 不存在门店分组信息
                List<String> errorInfo = new ArrayList<>();
                for (String groupName : groupArray) {
                    String groupId = groupNameIdMap.get(groupName);
                    if (StrUtil.isBlank(groupId)) {
                        errorList.add(mapStoreBaseInfoExportErrorDTO(store,NOT_EXIST_GROUP));
                        continue;
                    }
                    groupIds.add(groupId);
                }
                groupQueryDTO.setGroupIdList(groupIds);
            }
            currStoreGroupList.add(groupQueryDTO);
            // 设置门店基础信息
            StoreDO newStore = new StoreDO();
            newStore.setStoreId(storeId);
            BeanUtil.copyProperties(store, newStore);
            newStore.setLocationAddress(store.getStoreAddress());
            newStore.setStoreAddress(store.getStoreAddress());
            if (StrUtil.isNotBlank(newStore.getStoreAddress())) {
                String longitudeLatitude = mapCache.get(newStore.getStoreAddress().trim());
                if (StrUtil.isBlank(longitudeLatitude)) {
                    longitudeLatitude = storeService.getLocationByAddress(eid, newStore.getStoreAddress());
                }
                if (StringUtils.isNotEmpty(longitudeLatitude)) {
                    newStore.setLongitudeLatitude(longitudeLatitude);
                    newStore.setLongitude(longitudeLatitude.substring(0, longitudeLatitude.indexOf(",")));
                    newStore.setLatitude(longitudeLatitude.substring(longitudeLatitude.indexOf(",") + 1));
                    mapCache.put(newStore.getStoreAddress().trim(), longitudeLatitude);
                    List<String> list = Arrays.asList(longitudeLatitude.split(","));
                    newStore.setAddressPoint("POINT("+list.get(0)+" "+list.get(1)+")");
                }
            }
            // 时间转时间戳存入数据库，转成原来的格式存入错误列表  =。 =
            if (StrUtil.isNotBlank(store.getBusinessStartTime()) && StrUtil.isNotBlank(store.getBusinessEndTime())) {
                try {

                    newStore.setBusinessHours(stringToLong(store.getBusinessStartTime()) + ","
                            + stringToLong(store.getBusinessEndTime()));
                } catch (Exception e) {
                    log.error("时间转换错误", e);
                    errorList.add(mapStoreBaseInfoExportErrorDTO(store,DATE_ERROR));
                    continue;

                }
            }
            if(CollectionUtils.isNotEmpty(extendFieldInfoDTOList)){
                String extendField = extendFieldMap.get(store.getStoreId());
                for (ExtendFieldInfoDTO fieldInfo : extendFieldInfoDTOList) {
                     identifyFields.add(fieldInfo.getExtendFieldName());
                     if(map.containsKey(fieldInfo.getExtendFieldName())){
                         if(map.get(fieldInfo.getExtendFieldName()) != null){
                             extendField = updateExtendField(extendField,fieldInfo.getExtendFieldKey(),String.valueOf(map.get(fieldInfo.getExtendFieldName())));
                         }else {
                             extendField = updateExtendField(extendField,fieldInfo.getExtendFieldKey(),"");
                         }
                     }
                }
                newStore.setExtendField(extendField);

            }
            StringBuilder remarkSb = new StringBuilder();
            for (Object o : map.keySet()) {
                if(!identifyFields.contains(o)){
                    remarkSb.append("\""+o+"\"字段导入失败，系统无法识别；");
                }
            }if(remarkSb.length()>0){
                task.setRemark(remarkSb.toString());
            }
            //默认是营业状态
            String storeStatus =Constants.STORE_STATUS.STORE_STATUS_OPEN;
            if(StringUtils.isNotEmpty(store.getStoreStatus())){
                storeStatus = StoreStatusEnum.getCode(store.getStoreStatus());
            }
            newStore.setStoreStatus(storeStatus);
            newStore.setStoreNum(store.getStoreNum());
            newStore.setStoreAddress(store.getStoreAddress());
            newStore.setStoreName(store.getStoreName());
            newStore.setTelephone(store.getTelephone());
            newStore.setStoreAcreage(store.getStoreAcreage());
            newStore.setStoreBandwidth(store.getStoreBandwidth());
            newStore.setRemark(store.getRemark());
            // 正常的数据导入
            stores.add(newStore);
            storeGroupList.addAll(currStoreGroupList);
        }
        updateStoreBaseInfo(eid, importList.size(), stores, errorList,storeGroupList, contentType, task, user);

    }

    public String updateExtendField(String extendFieldInfo,String extendFieldKey,String extendFieldValue){
        JSONObject jsonObject;
        if(StringUtils.isNotEmpty(extendFieldInfo)){
            jsonObject = JSONObject.parseObject(extendFieldInfo);
        }else {
            jsonObject = new JSONObject();
        }
        jsonObject.put(extendFieldKey,extendFieldValue);
        return JSONObject.toJSONString(jsonObject);
    }

    private Long stringToLong(String time) throws ParseException {
        String inputString =  "1970-01-01 "+time + ":00"; //Added to string to make HH:mm:ss.SSS format
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(DATE_FORMAT_SEC);
        LocalDateTime parse = LocalDateTime.parse(inputString, ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();

    }
    public void updateStoreBaseInfo(String eid, int totalNum, List<StoreDO> stores, List<StoreBaseInfoExportErrorDTO> errorList,List<StoreGroupQueryDTO> groupList,

                                    String contentType, ImportTaskDO task, CurrentUser user) {
        Integer limitStoreCount = storeService.getLimitStoreCount(eid);
        if (CollUtil.isNotEmpty(stores)) {
            log.info("eid={},userId={}开始更新门店基本信息！",eid,user.getUserId());
            Lists.partition(stores, 200).forEach(f ->storeDao.batchUpdateStoreWithoutNull(eid,f,user.getUserId(), limitStoreCount));
        }
        if (CollUtil.isNotEmpty(groupList)) {
            log.info("更新门店分组信息，时间：" + System.currentTimeMillis());
            storeService.modifyStoreGroupList(eid, user.getUserId(), groupList);
        }
        int successNum = totalNum - errorList.size();
        task.setSuccessNum(successNum);
        if (CollUtil.isNotEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            try {
                String url = generateOssFileService.generateOssExcel(errorList, eid, BASE_STORE_TITLE, "出错门店列表", contentType, StoreBaseInfoExportErrorDTO.class);
                task.setFileUrl(url);
            }catch (Exception e){
                task.setRemark("错误列表上传oss失败！");
                importTaskMapper.update(eid, task);
            }
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setTotalNum(totalNum);
        importTaskMapper.update(eid, task);
        log.info("eid={},userId={}更新门店基本信息完成！",eid,user.getUserId());
    }
    private StoreBaseInfoExportErrorDTO mapStoreBaseInfoExportErrorDTO(StoreBaseInfoExportDTO  storeBaseInfoExportDTO,String desc){

        StoreBaseInfoExportErrorDTO storeBaseInfoExportErrorDTO =new StoreBaseInfoExportErrorDTO();
        storeBaseInfoExportErrorDTO.setDec(desc);
        storeBaseInfoExportErrorDTO.setStoreId(storeBaseInfoExportDTO.getStoreId());
        storeBaseInfoExportErrorDTO.setStoreName(storeBaseInfoExportDTO.getStoreName());
        storeBaseInfoExportErrorDTO.setStoreNum(storeBaseInfoExportDTO.getStoreNum());
        storeBaseInfoExportErrorDTO.setStoreAddress(storeBaseInfoExportDTO.getStoreAddress());
        storeBaseInfoExportErrorDTO.setTelephone(storeBaseInfoExportDTO.getTelephone());
        storeBaseInfoExportErrorDTO.setBusinessHours(storeBaseInfoExportDTO.getBusinessHours());
        storeBaseInfoExportErrorDTO.setBusinessStartTime(storeBaseInfoExportDTO.getBusinessStartTime());
        storeBaseInfoExportErrorDTO.setBusinessEndTime(storeBaseInfoExportDTO.getBusinessEndTime());
        storeBaseInfoExportErrorDTO.setStoreAcreage(storeBaseInfoExportDTO.getStoreAcreage());
        storeBaseInfoExportErrorDTO.setStoreBandwidth(storeBaseInfoExportDTO.getStoreBandwidth());
        storeBaseInfoExportErrorDTO.setRemark(storeBaseInfoExportDTO.getRemark());
        storeBaseInfoExportErrorDTO.setGroupName(storeBaseInfoExportDTO.getGroupName());
//        storeBaseInfoExportErrorDTO.setUserName(storeBaseInfoExportDTO.getUserName());
//        storeBaseInfoExportErrorDTO.setRegionName(storeBaseInfoExportDTO.getRegionName());
        return storeBaseInfoExportErrorDTO;
    }


    public void importStore(String eid, ImportDistinctVO distinct, CurrentUser user, List<StoreImportDTO> importList, String contentType, ImportTaskDO task) {

        // 需要新增的数据
        List<StoreDO> stores = new ArrayList<>();
        // 有错误的数据
        List<StoreImportDTO> errorList = new ArrayList<>();
        // 门店-门店分组关联列表
        List<StoreGroupQueryDTO> StoreGroupList = new ArrayList<>();
        // 区域名称-id
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        Map<String, Long> regionNameIdMap = allRegion.stream()
                .filter(a -> a.getName() != null && a.getId() != null)
                .collect(Collectors.toMap(RegionDO::getName, RegionDO::getId,(a,b)->a));

        Map<Long, RegionDO> regionMap = allRegion.stream().collect(Collectors.toMap(RegionDO::getId,o->o));

        // 门店分组名称-分组id
        List<StoreGroupDO> storeGroups = groupMapper.listStoreGroup(eid);
        Map<String, String> groupNameIdMap = storeGroups.stream()
                .filter(a -> a.getGroupName() != null && a.getGroupId() != null)
                .collect(Collectors.toMap(StoreGroupDO::getGroupName, StoreGroupDO::getGroupId,(a,b)->a));


        List<StoreDO> allStores = storeMapper.getAllStoreIds(eid, StoreIsDeleteEnum.EFFECTIVE.getValue());
//        System.out.println("allStores集合大小:" + (Arrays.toString(allStores.toArray()).getBytes().length/1024) + "KB");
        // 门店名称或编号与id映射关系
        Map<String, List<String>> storeNameIdMap = allStores.stream().collect(Collectors.groupingBy(StoreDO::getStoreName,
                Collectors.mapping(StoreDO::getStoreId, Collectors.toList())));
//        System.out.println("名称映射大小：" + storeNameIdMap.toString().getBytes().length / 1024 + "kb");
        // 去重字段
        boolean isName = distinct.getUniqueField().equals("storeName");
        Map<String, List<String>> storeNumIdMap = allStores.stream().filter(f -> StrUtil.isNotBlank(f.getStoreNum())).collect(Collectors.groupingBy(StoreDO::getStoreNum,
                Collectors.mapping(StoreDO::getStoreId, Collectors.toList())));
//        System.out.println("编号映射大小：" + storeNumIdMap.toString().getBytes().length / 1024 + "kb");
        // 新数据门店名称与id映射关系
        Map<String, String> newStoreNameIdMap = new HashMap<>();

        // 新数据门店编号与id映射关系
        Map<String, String> newStoreNumIdMap = new HashMap<>();
        // 是否覆盖
        boolean cover = distinct.isCover();
        // 中文匹配
        String pat = "[\u4E00-\u9FA5]+";
        long currTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        // 地图缓存
        Map<String, String> mapCache = new HashMap<>();
        for (StoreImportDTO store : importList) {
            // 门店-门店分组关联列表
            List<StoreGroupQueryDTO> currStoreGroupList = new ArrayList<>();
            Map<String, String> currStoreNameIdMap = new HashMap<>();
            Map<String, String> currStoreNumIdMap = new HashMap<>();
            // 门店名称不能为空
            log.info("校验门店基础信息，时间：" + System.currentTimeMillis());
            String storeName = store.getStoreName();
            if (StrUtil.isBlank(storeName)) {
                store.setDec(NOT_BLANK);
                errorList.add(store);
                continue;
            }
            if (storeName.length() > 100) {
                store.setDec(NAME_TOO_LANG);
                errorList.add(store);
                continue;
            }
            storeName = storeName.trim();
            String remark = store.getRemark();
            if (StrUtil.isNotBlank(remark) && remark.length() > 400) {
                store.setDec(REMARK_TOO_LANG);
                errorList.add(store);
                continue;
            }
            String storeNum = store.getStoreNum();
            if (StrUtil.isNotBlank(storeNum)) {
                storeNum = storeNum.trim();
                if (matches(pat, storeNum)) {
                    store.setDec(NUM_NOT_ZH);
                    errorList.add(store);
                    continue;
                }
                if (storeNum.length() > 20) {
                    store.setDec(NUM_TOO_LANG);
                    errorList.add(store);
                    continue;
                }
            }
            // 校验数据有效性  并获取门店id
            String check = isName ? storeName : storeNum;
            String storeId = checkStoreIds(check, cover, isName, store, storeNameIdMap, storeNumIdMap, newStoreNameIdMap, newStoreNumIdMap, errorList);
            if (StrUtil.isBlank(storeId)) {
                continue;
            }
            if (isName) {
                if (StrUtil.isNotBlank(storeNum)) {
                    List<String> list = storeNumIdMap.get(storeNum);
                    // 判断门店编号是否已经存在
                    if (CollUtil.isNotEmpty(list) && !list.contains(storeId)) {
                        store.setDec(EXIST_NUM);
                        errorList.add(store);
                        continue;
                    }
                }
                currStoreNameIdMap.put(storeName, storeId);
            } else {
                // 如果按照门店编号去重
                if (StrUtil.isBlank(storeNum)) {
                    store.setDec(NO_NULL_NUM);
                    errorList.add(store);
                    continue;
                }
                currStoreNumIdMap.put(storeNum, storeId);
            }
//            if (StrUtil.isBlank(storeId)) {
//                continue;
//            }
            // 设置门店分组信息
            StoreGroupQueryDTO groupQueryDTO = new StoreGroupQueryDTO();
            groupQueryDTO.setStoreId(storeId);
            String storeGroup = store.getStoreGroup();
            if (StrUtil.isNotBlank(storeGroup)) {
                String[] groupArray = storeGroup.split(",");
                List<String> groupIds = new ArrayList<>();
                // 不存在门店分组信息
                List<String> errorInfo = new ArrayList<>();
                for (String groupName : groupArray) {
                    String groupId = groupNameIdMap.get(groupName);
                    if (StrUtil.isBlank(groupId)) {
                        errorInfo.add(String.format(NOT_EXIST_GROUP, groupName));
                        continue;
                    }
                    groupIds.add(groupId);
                }
                if (CollUtil.isNotEmpty(errorInfo)) {
                    store.setDec(String.join(",", errorInfo));
                    errorList.add(store);
                    continue;
                }
                groupQueryDTO.setGroupIdList(groupIds);
            }
            currStoreGroupList.add(groupQueryDTO);
            // 设置门店区域
            String regionName = store.getRegionName();
            Long regionId = null;
            if (StrUtil.isNotBlank(regionName)) {
                regionId = regionNameIdMap.get(regionName);
                if (regionId == null) {
                    store.setDec(NOT_EXIST_REGION);
                    errorList.add(store);
                    continue;
                }
            }
            // 设置门店基础信息
            StoreDO newStore = new StoreDO();
            newStore.setStoreId(storeId);
            BeanUtil.copyProperties(store, newStore);
            RegionDO rRegion = regionId==null ?  null :regionMap.get(regionId);
            newStore.setRegionId(regionId);
            if (rRegion != null) {
                newStore.setRegionId(regionId);
                newStore.setRegionPath(rRegion.getFullRegionPath());
            } else {
                newStore.setRegionId(1L);
                newStore.setRegionPath("/1/");
            }
            newStore.setLocationAddress(newStore.getStoreAddress());
            if (StrUtil.isNotBlank(newStore.getStoreAddress())) {
                String longitudeLatitude = mapCache.get(newStore.getStoreAddress().trim());
                if (StrUtil.isBlank(longitudeLatitude)) {
                    longitudeLatitude = storeService.getLocationByAddress(eid, newStore.getStoreAddress());
                }
                if (StringUtils.isNotEmpty(longitudeLatitude)) {
                    newStore.setLongitudeLatitude(longitudeLatitude);
                    newStore.setLongitude(longitudeLatitude.substring(0, longitudeLatitude.indexOf(",")));
                    newStore.setLatitude(longitudeLatitude.substring(longitudeLatitude.indexOf(",") + 1));
                    mapCache.put(newStore.getStoreAddress().trim(), longitudeLatitude);
                }
            }
            // 时间转时间戳存入数据库，转成原来的格式存入错误列表  =。 =
            if (StrUtil.isNotBlank(store.getStartTime()) && StrUtil.isNotBlank(store.getEndTime())) {
                Date parse1;
                Date parse2;
                try {
                    parse1 = DateFormatUtil.parse(store.getStartTime(), "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                    parse2 = DateFormatUtil.parse(store.getEndTime(), "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                } catch (Exception e) {
                    log.error("时间转换错误", e);
                    store.setDec(DATE_ERROR);
                    errorList.add(store);
                    continue;
                }
                // 转成HH:mm格式
                String startTime = formatter.format(parse1);
                // 获取毫秒
                long startTimeLong = DateUtils.getDateTime(startTime);
                store.setStartTime(startTime);
                String endTime = formatter.format(parse2);
                long endTimeLong = DateUtils.getDateTime(endTime);
                store.setEndTime(endTime);
                newStore.setBusinessHours(startTimeLong + "," + endTimeLong);
            }
            if(store.getOpenDate() != null){
                newStore.setOpenDate(store.getOpenDate());
            }
            newStore.setIsDelete(StoreIsDeleteEnum.EFFECTIVE.getValue());
            newStore.setCreateTime(currTime);
            newStore.setCreateName(user.getName());
            newStore.setCreateUser(user.getUserId());
            newStore.setUpdateTime(currTime);
            newStore.setUpdateName(user.getName());
            newStore.setUpdateUser(user.getUserId());

            // 正常的数据导入
            stores.add(newStore);
            StoreGroupList.addAll(currStoreGroupList);
            newStoreNameIdMap.putAll(currStoreNameIdMap);
            newStoreNumIdMap.putAll(currStoreNumIdMap);
        }
        storeImportService.updateStoreInfo(eid, importList.size(), stores, errorList, StoreGroupList, contentType, task, user);
    }

    @Transactional
    public void updateStoreInfo(String eid, int totalNum, List<StoreDO> stores, List<StoreImportDTO> errorList,
                                List<StoreGroupQueryDTO> StoreGroupList,
                                String contentType, ImportTaskDO task, CurrentUser user) {

        Map<String, RegionDO> storeRegionMap = Maps.newHashMap();
        if (CollUtil.isNotEmpty(stores)) {
            log.info("插入门店信息，时间：" + System.currentTimeMillis());
            regionService.batchInsertStoreRegion(eid, stores, user);
            List<String> storeIds = stores.stream().map(StoreDO::getStoreId).collect(Collectors.toList());
            List<RegionDO> storeRegionList = regionService.listRegionByStoreIds(eid, storeIds);
            storeRegionMap = ListUtils.emptyIfNull(storeRegionList).stream()
                    .filter(a -> a.getStoreId() != null)
                    .collect(Collectors.toMap(data -> data.getStoreId(), data -> data, (a, b) -> a));
            Map<String, RegionDO> finalStoreRegionMap = storeRegionMap;
            stores.forEach(store -> {
                if(finalStoreRegionMap.get(store.getStoreId()) != null){
                    store.setRegionPath(finalStoreRegionMap.get(store.getStoreId()).getFullRegionPath());
                }
            });
            Integer limitStoreCount = storeService.getLimitStoreCount(eid);
            Lists.partition(stores, 1000).forEach(f -> storeDao.batchInsertStoreByImport(eid, f, limitStoreCount));
        }
        if (CollUtil.isNotEmpty(StoreGroupList)) {
            log.info("更新门店分组信息，时间：" + System.currentTimeMillis());
            storeService.modifyStoreGroupList(eid, user.getUserId(), StoreGroupList);
        }
        int successNum = totalNum - errorList.size();
        if (CollUtil.isNotEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            log.info("获取错误文件地址，时间：" + System.currentTimeMillis());
            String url = generateOssFileService.generateOssExcel(errorList, eid, STORE_TITLE, "出错门店列表", contentType, StoreImportDTO.class);
            task.setFileUrl(url);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setSuccessNum(successNum);
        task.setTotalNum(totalNum);
        importTaskMapper.update(eid, task);
        log.info("门店导入完必，时间：" + System.currentTimeMillis());
    }

    private String checkStoreIds(String check, boolean cover, boolean isName, StoreImportDTO store, Map<String, List<String>> storeNameIdMap,
                                 Map<String, List<String>> storeNumIdMap, Map<String, String> newStoreNameIdMap,
                                 Map<String, String> newStoreNumIdMap, List<StoreImportDTO> errorList) {
        // 筛选是否在其他行出现过
        if (isName) {
            String newStoreId = newStoreNameIdMap.get(check);
            if (StrUtil.isNotBlank(newStoreId)) {
                store.setDec(EXIST_STORE);
                errorList.add(store);
                return null;
            }
        } else {
            String newStoreId = newStoreNumIdMap.get(check);
            if (StrUtil.isNotBlank(newStoreId)) {
                store.setDec(EXIST_NUM);
                errorList.add(store);
                return null;
            }
        }

        String storeId;
        List<String> storeIds;
        if (isName) {
            storeIds = storeNameIdMap.get(check);
        } else {
            storeIds = storeNumIdMap.get(check);
        }
        if (cover) {
            if (CollUtil.isEmpty(storeIds)) {
                storeId = UUIDUtils.get32UUID();
            } else if (storeIds.size() > 1) {
                store.setDec(isName ? MUCH_STORE: MUCH_NUM);
                errorList.add(store);
                // 异常情况返回null
                return null;
            } else {
                storeId = storeIds.get(0);
            }
        } else {
            // 不覆盖  如果已经存在  则跳过
            if (CollUtil.isNotEmpty(storeIds)) {
                return null;
            }
            storeId = UUIDUtils.get32UUID();
        }
        return storeId;
    }

    private void getStoreArea(List<String> parentIds, String regionId, Map<Long, String> regionMap) {
        Long id = Long.parseLong(regionId);
        if (!regionMap.containsKey(id)) {
            return;
        }
        String s = regionMap.get(id);
        parentIds.add(0, s);
        if (regionMap.containsKey(Long.valueOf(s))) {
            getStoreArea(parentIds, s, regionMap);
        }
    }

    /**
     * 导入门店范围数据处理
     * @param list
     * @return
     */
    public StoreRangeVO handleStoreRangeList(String eid ,List<StoreRangeDTO> list){
        StoreRangeVO storeRangeVO = new StoreRangeVO();
        if (CollectionUtils.isEmpty(list)){
            storeRangeVO.setFailList(Collections.emptyList());
            storeRangeVO.setSuccessList(Collections.emptyList());
            return storeRangeVO;
        }
        //序号
        Integer index = 2;
        List<StoreRangeDTO> failList = new ArrayList<>();
        List<StoreRangeDTO> storeRangeDTOS = new ArrayList<>();
        Set<String> storeSet = new HashSet<>();
        for (StoreRangeDTO storeRangeDTO:list) {
            storeRangeDTO.setIndex(index++);
            if (!storeSet.contains(storeRangeDTO.getStoreId())){
                storeRangeDTOS.add(storeRangeDTO);
                storeSet.add(storeRangeDTO.getStoreId());
            }else {
                //门店ID重复
                storeRangeDTO.setFailMsg("有重复的门店ID 请删除");
                failList.add(storeRangeDTO);
            }
        }

        //校验ID或者名称为NULL
        List<StoreRangeDTO> storeIdOrNameIsNullList = storeRangeDTOS.stream().filter(x -> StringUtils.isEmpty(x.getStoreId()) || StringUtils.isEmpty(x.getStoreName())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(storeIdOrNameIsNullList)){
            storeIdOrNameIsNullList.forEach(x->x.setFailMsg("门店ID或者门店名称为空"));
            failList.addAll(storeIdOrNameIsNullList);
        }
        //校验门店ID不在系统中
        List<StoreRangeDTO> storeIdOrNameIsNotNullList = storeRangeDTOS.stream().filter(x -> StringUtils.isNotEmpty(x.getStoreId()) && StringUtils.isNotEmpty(x.getStoreName())).collect(Collectors.toList());
        List<String> fileStoreIds = storeIdOrNameIsNotNullList.stream().map(StoreRangeDTO::getStoreId).collect(Collectors.toList());
        List<StoreDTO> storeListByStoreIds = storeMapper.getStoreListByStoreIds(eid, fileStoreIds);
        List<String> databaseStoreIds = storeListByStoreIds.stream().map(StoreDTO::getStoreId).collect(Collectors.toList());
        //文件不为空的ID 减去查询到的数据  就是不存在的ID
        fileStoreIds = fileStoreIds.stream().filter(e -> {
            return !databaseStoreIds.contains(e);
        }).collect(Collectors.toList());
        Map<String, StoreRangeDTO> map = storeIdOrNameIsNotNullList.stream().collect(Collectors.toMap(StoreRangeDTO::getStoreId, data -> data));
        if (CollectionUtils.isNotEmpty(fileStoreIds)){
            for (String storeId:fileStoreIds) {
                StoreRangeDTO storeRangeDTO = map.get(storeId);
                storeRangeDTO.setFailMsg("系统不存在该门店ID");
                failList.add(storeRangeDTO);
            }
        }
        List<String> failTrimList = new ArrayList<>();
        for (StoreRangeDTO storeRangeDTO:failList) {
            failTrimList.add(storeRangeDTO.getStoreId());
        }
        List<StoreRangeDTO> successList = new ArrayList<>();
        successList = storeRangeDTOS.stream().filter(e -> {
            return !failTrimList.contains(e.getStoreId());
        }).collect(Collectors.toList());


        storeRangeVO.setSuccessList(successList);
        storeRangeVO.setFailList(failList);
        return storeRangeVO;
    }


    /**
     * 导入门店范围数据处理
     * @param list
     * @return
     */
    public TaskStoreRangeVO handleTaskStoreRangeList(String eid, List<TaskStoreRangeDTO> list, ImportStoreDistinctVO distinctVO, String userId) {
        TaskStoreRangeVO storeRangeVO = new TaskStoreRangeVO();
        if (CollectionUtils.isEmpty(list)) {
            storeRangeVO.setFailList(Collections.emptyList());
            storeRangeVO.setSuccessList(Collections.emptyList());
            return storeRangeVO;
        }
        //序号
        AtomicInteger index = new AtomicInteger(6);
        List<TaskStoreRangeDTO> failList = new ArrayList<>();
        List<TaskStoreRangeDTO> sucList = new ArrayList<>();
        Set<String> storeIdSet = new HashSet<>();
        Set<String> storeNameSet = new HashSet<>();
        Set<String> storeNumSet = new HashSet<>();
        List<StoreDTO> storeList = new ArrayList<>();
        Map<String, List<StoreDTO>> uniqueFieldMap = new HashMap<>();
        //权限可视化控制
        AuthVisualDTO authVisualDTO = authVisualService.authRegionStoreByRole(eid, userId);
        List<String> authStoreIdList = authVisualDTO.getStoreIdList();
        boolean isAllStore = authVisualDTO.getIsAllStore();
        if (CollectionUtils.isEmpty(authStoreIdList) && !isAllStore) {
            TaskStoreRangeDTO taskStoreRangeDTO = new TaskStoreRangeDTO();
            taskStoreRangeDTO.setFailMsg("当前用户无门店权限");
            failList.add(taskStoreRangeDTO);
            storeRangeVO.setFailList(failList);
            storeRangeVO.setSuccessList(sucList);
            return storeRangeVO;
        }
        switch (distinctVO.getUniqueField()) {
            case "storeId":
                storeIdSet = list.stream().map(TaskStoreRangeDTO::getStoreId).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(storeIdSet)) {
                    storeList = storeMapper.getStoreListByStoreIds(eid, new ArrayList<>(storeIdSet));
                    uniqueFieldMap = storeList.stream().collect(Collectors.groupingBy(StoreDTO::getStoreId));
                }
                break;
            case "storeName":
                storeNameSet = list.stream().map(TaskStoreRangeDTO::getStoreName).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(storeNameSet)) {
                    storeList = storeMapper.getStoreListByStoreNameList(eid, new ArrayList<>(storeNameSet));
                    uniqueFieldMap = storeList.stream().collect(Collectors.groupingBy(StoreDTO::getStoreName));
                }
                break;
            default:
                storeNumSet = list.stream().map(TaskStoreRangeDTO::getStoreNum).collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(storeNumSet)) {
                    storeList = storeMapper.getStoreListByStoreNumList(eid, new ArrayList<>(storeNumSet));
                    uniqueFieldMap = storeList.stream().collect(Collectors.groupingBy(StoreDTO::getStoreNum));
                }
                break;
        }
        if (CollectionUtils.isEmpty(storeIdSet) && CollectionUtils.isEmpty(storeNameSet) && CollectionUtils.isEmpty(storeNumSet)) {
            throw new ServiceException("导入数据为空");
        }

        Map<String, List<StoreDTO>> finalUniqueFieldMap = uniqueFieldMap;
        List<TaskStoreRangeDTO> finalSucList = sucList;
        list.forEach(taskStoreRangeDTO -> {
            taskStoreRangeDTO.setIndex(index.getAndIncrement());

            List<StoreDTO> storeDTOList = new ArrayList<>();
            switch (distinctVO.getUniqueField()) {
                case "storeId":
                    if (StringUtils.isBlank(taskStoreRangeDTO.getStoreId())) {
                        taskStoreRangeDTO.setFailMsg("门店ID为空");
                        failList.add(taskStoreRangeDTO);
                        return;
                    }
                    storeDTOList = finalUniqueFieldMap.get(taskStoreRangeDTO.getStoreId());
                    if (CollectionUtils.isEmpty(storeDTOList)) {
                        taskStoreRangeDTO.setFailMsg("系统不存在该门店ID");
                        failList.add(taskStoreRangeDTO);
                        return;
                    }
                    break;
                case "storeName":
                    if (StringUtils.isBlank(taskStoreRangeDTO.getStoreName())) {
                        taskStoreRangeDTO.setFailMsg("门店名称为空");
                        failList.add(taskStoreRangeDTO);
                        return;
                    }
                    storeDTOList = finalUniqueFieldMap.get(taskStoreRangeDTO.getStoreName());
                    if (CollectionUtils.isEmpty(storeDTOList)) {
                        taskStoreRangeDTO.setFailMsg("系统不存在该门店名称");
                        failList.add(taskStoreRangeDTO);
                        return;
                    }
                    break;
                default:
                    if (StringUtils.isBlank(taskStoreRangeDTO.getStoreNum())) {
                        taskStoreRangeDTO.setFailMsg("门店编码为空");
                        failList.add(taskStoreRangeDTO);
                        return;
                    }
                    storeDTOList = finalUniqueFieldMap.get(taskStoreRangeDTO.getStoreNum());
                    if (CollectionUtils.isEmpty(storeDTOList)) {
                        taskStoreRangeDTO.setFailMsg("系统不存在该门店编码");
                        failList.add(taskStoreRangeDTO);
                        return;
                    }
                    break;
            }
            if (CollectionUtils.isNotEmpty(storeDTOList)) {
                storeDTOList.forEach(storeDTO -> {
                    TaskStoreRangeDTO taskStoreRange = new TaskStoreRangeDTO();
                    taskStoreRange.setIndex(taskStoreRangeDTO.getIndex());
                    taskStoreRange.setStoreId(storeDTO.getStoreId());
                    taskStoreRange.setStoreName(storeDTO.getStoreName());
                    taskStoreRange.setStoreNum(storeDTO.getStoreNum());
                    if(!isAllStore && !authStoreIdList.contains(storeDTO.getStoreId())){
                        taskStoreRange.setFailMsg("当前用户无门店权限");
                        failList.add(taskStoreRange);
                        return;
                    }
                    finalSucList.add(taskStoreRange);
                });
            }
        });
        sucList = finalSucList.stream().distinct().collect(Collectors.toList());

        storeRangeVO.setSuccessList(sucList);
        storeRangeVO.setFailList(failList);
        return storeRangeVO;
    }
    
}
