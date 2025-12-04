package com.coolcollege.intelligent.service.metatable.impl;

import com.alibaba.fastjson.JSONArray;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.OnePartyConstants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.metatable.TbMetaColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.mapper.metatable.TbMetaTableUserAuthDAO;
import com.coolcollege.intelligent.model.metatable.TbMetaColumnResultDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableUserAuthDO;
import com.coolcollege.intelligent.model.metatable.dto.CheckTableDTO;
import com.coolcollege.intelligent.model.metatable.request.CheckTableMoveSortRequest;
import com.coolcollege.intelligent.model.metatable.request.MoveSortRequest;
import com.coolcollege.intelligent.model.metatable.request.TablePageRequest;
import com.coolcollege.intelligent.model.metatable.request.TbMetaTableRequest;
import com.coolcollege.intelligent.model.metatable.vo.MetaColumnTypeVO;
import com.coolcollege.intelligent.model.metatable.vo.MetaTableTypeVO;
import com.coolcollege.intelligent.model.oneparty.dto.OnePartyBusinessRestrictionsDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.metatable.TableService;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.oneparty.OnePartyService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/4/6 10:34
 * @Version 1.0
 */
@Slf4j
@Service
public class TableServiceImpl implements TableService {


    @Autowired
    TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;

    @Autowired
    TbMetaTableMapper tbMetaTableMapper;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private OnePartyService onePartyService;
    @Resource
    private TbMetaTableUserAuthDAO tbMetaTableUserAuthDAO;

    @Override
    public List<MetaTableTypeVO> getMetaTablePropertyList(String enterpriseId, String appType) {
        List<MetaTableTypeVO> list = new ArrayList<>();
        if(AppTypeEnum.ONE_PARTY_APP.getValue().equals(appType)) {
            OnePartyBusinessRestrictionsDTO restrictionsDTO = onePartyService.getBusinessRestrictions(enterpriseId, OnePartyConstants.SET_MEAL_META_TABLE_PROPERTIES);
            if(StringUtils.isNotBlank(restrictionsDTO.getAvailableValue())) {
                List<MetaTableTypeVO> metaTableTypeVOS = JSONArray.parseArray(restrictionsDTO.getAvailableValue(), MetaTableTypeVO.class);
                return metaTableTypeVOS.stream().filter(x->!x.getCode().equals(MetaTablePropertyEnum.WEIGHT_TABLE.getCode())).collect(Collectors.toList());
            }
        }
        Arrays.stream(MetaTablePropertyEnum.values()).forEach(typeEnum -> {
            MetaTableTypeVO metaTableTypeVO = new MetaTableTypeVO();
            //过滤权重表
            if (typeEnum.getCode().equals(MetaTablePropertyEnum.WEIGHT_TABLE.getCode())){
                return;
            }
            metaTableTypeVO.setCode(typeEnum.getCode());
            metaTableTypeVO.setName(typeEnum.getName());
            List<MetaColumnTypeEnum> columnTypes = typeEnum.getColumnTypes();
            List<MetaColumnTypeVO> columnTypeList = null;
            if(CollectionUtils.isNotEmpty(columnTypes)){
                columnTypeList = new ArrayList<>();
                for (MetaColumnTypeEnum columnType : columnTypes) {
                    columnTypeList.add(MetaColumnTypeVO.builder().code(columnType.getCode()).name(columnType.getName()).build());
                }
            }
            metaTableTypeVO.setColumnTypes(columnTypeList);
            list.add(metaTableTypeVO);
        });
        return list;
    }

    @Override
    public Boolean tableTop(String enterpriseId, TbMetaTableRequest tbMetaTableRequest) {
        if (tbMetaTableRequest.getTopStatus()==null||tbMetaTableRequest.getId()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        Integer countTop = tbMetaTableMapper.countTop(enterpriseId);
        //取消置顶不需要校验
        if (countTop>=10&&tbMetaTableRequest.getTopStatus()){
            throw new ServiceException(ErrorCodeEnum.TOP_COUNT_LIMIT);
        }
        Date topTime = new Date();
        if (!tbMetaTableRequest.getTopStatus()){
            topTime = null;
        }
        tbMetaTableMapper.updateTopOrPigeonhole(enterpriseId,tbMetaTableRequest.getId(),tbMetaTableRequest.getTopStatus(),topTime,null);
        return Boolean.TRUE;
    }

    @Override
    public Boolean pigeonhole(String enterpriseId, TbMetaTableRequest tbMetaTableRequest) {
        if (tbMetaTableRequest.getPigeonholeStatus()==null||tbMetaTableRequest.getId()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //置顶的检查表归档，取消归档再恢复使用后不置顶
        tbMetaTableMapper.updateTopOrPigeonhole(enterpriseId,tbMetaTableRequest.getId(),null,null,tbMetaTableRequest.getPigeonholeStatus());
        return Boolean.TRUE;
    }

    @Override
    public boolean pigeonholeMany(String enterpriseId, List<Long> id) {
        if (CollectionUtils.isEmpty(id)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        tbMetaTableMapper.pigeonholeMany(enterpriseId,id);
        return true;
    }

    @Override
    public Boolean columnInCheckTableFreeze(String enterpriseId, TbMetaTableRequest tbMetaTableRequest) {
        if (tbMetaTableRequest.getFreezeStatus()==null|| CollectionUtils.isEmpty(tbMetaTableRequest.getColumnIds())||tbMetaTableRequest.getId()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        tbMetaStaTableColumnMapper.updateColumnFreeze(enterpriseId,tbMetaTableRequest.getId(),tbMetaTableRequest.getColumnIds(),tbMetaTableRequest.getFreezeStatus());

        //更新检查表中检查表的分值和分类集合
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, tbMetaTableRequest.getId());

        List<TbMetaColumnResultDO> tbMetaColumnResultDOS = tbMetaColumnResultMapper.listByMetaTableIdRemoveFrozenCulumnResultColumn(enterpriseId, tbMetaTableRequest.getId(),tbMetaTableRequest.getColumnIds());

        BigDecimal tableTotalScore = AbstractColumnObserver.getTableTotalScore(tbMetaTableDO, tbMetaColumnResultDOS);

        tbMetaTableDO.setTotalScore(tableTotalScore);

        tbMetaTableMapper.updateByPrimaryKeySelective(enterpriseId,tbMetaTableDO);

        return Boolean.TRUE;
    }

    @Override
    public Boolean moveSort(String enterpriseId, CheckTableMoveSortRequest request,CurrentUser user) {
        String userId = user.getUserId();
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> tableTypeList = new ArrayList<>();
        tableTypeList.add("STANDARD");
        TablePageRequest tablePageRequest = new TablePageRequest();
        tablePageRequest.setName(request.getName());
        tablePageRequest.setPageNumber(request.getPageNum());
        tablePageRequest.setPageSize(request.getPageSize());
        if(StringUtils.isNotBlank(request.getTableProperty())){
            tablePageRequest.setTablePropertyList(Arrays.asList(request.getTableProperty().split(Constants.COMMA)));
        }
        if(!isAdmin){
            List<TbMetaTableUserAuthDO> userAuthMetaTableList = tbMetaTableUserAuthDAO.getUserAuthMetaTableList(enterpriseId, userId);
            if(CollectionUtils.isEmpty(userAuthMetaTableList)){
                return false;
            }
            List<String> authMetaTableIds = userAuthMetaTableList.stream().map(TbMetaTableUserAuthDO::getBusinessId).distinct().collect(Collectors.toList());
            tablePageRequest.setAuthTableIds(authMetaTableIds);
        }
        tablePageRequest.setTableTypeList(tableTypeList);
        tablePageRequest.setStatusFilterCondition("using");
        //查询该页面数据
        PageHelper.startPage(request.getPageNum(),request.getPageSize());
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectListV2(enterpriseId, tablePageRequest);
        List<TbMetaTableDO> needUpdateSortList = null;
        try {
            needUpdateSortList = getNeedUpdateSortList(tbMetaTableDOS, request.getTargetId(), request.getStartIndex(), request.getEndIndex(), Boolean.FALSE);
        } catch (Exception e) {
            log.info("getNeedUpdateSortList_fail");
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(needUpdateSortList)){
            tbMetaTableMapper.batchUpdate(enterpriseId,needUpdateSortList);
        }
        return Boolean.TRUE;
    }

    /**
     * @param allList    排序序 的集合(跟列表顺序保持一致)
     * @param targetId   目标对象的Id
     * @param startIndex 对象在列表的开始索引值[索引值从0 开始]
     * @param endIndex   移动后的目标索引值[索引值从0 开始]
     * @param isAsc      集合列表 是否正序 TRUE 正序
     * @Description: 构建 需要 排序的 list集合
     * @Return: java.util.List<T> 返回sort值变动的数据集合
     */
    public static <T> List<T> getNeedUpdateSortList(List<T> allList, Long targetId, Integer startIndex, Integer endIndex, boolean isAsc) throws NoSuchFieldException, IllegalAccessException {
        List<T> buildList = new ArrayList<>();
        if (targetId == null || startIndex == null || endIndex == null) {
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        if (CollectionUtils.isEmpty(allList) || startIndex.equals(endIndex)) {
            return buildList;
        }
        //需要 修改排序值得 数据集合 不包含 目标对象 本身
        List<T> excludeList = new ArrayList();
        //目标 对象
        T targetBean = null;
        //截取的 开始 索引  和 结束 索引 之间的 list 包含 targetBean(拖拽排序目标自身) 的一个 集合
        List<T> subList = null;

        //需改排序规则 (只修改 索引区间的值)
        //== ================获取 索引区间的 数据集合   begin ==================
        boolean flag = endIndex > startIndex;
        if (flag) {
            //下移
            subList = allList.subList(Integer.parseInt(startIndex.toString()), Integer.parseInt(endIndex.toString()) + 1);
        } else {
            // 上移  subList左闭右开
            subList = allList.subList(Integer.parseInt(endIndex.toString()), Integer.parseInt(startIndex.toString()) + 1);
        }
        //== ================获取 索引区间的 数据集合   end  ==================

        if (CollectionUtils.isEmpty(subList)) {
            return buildList;
        }

        //===================   获取 排序区间 里面的 目标对象 + 排出目标对象之后的集合 begin =============
        for (T bean : subList) {
            Field idField = bean.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object currentId = idField.get(bean);
            if (targetId.equals((Long) (currentId))) {
                targetBean = bean;
            } else {
                excludeList.add(bean);
            }
        }
        if (targetBean == null) {
            throw new ServiceException(ErrorCodeEnum.CHECKTABLE_IS_NULL);
        }
        //===================   获取 排序区间 里面的 目标对象 + 排出目标对象之后的集合 end  =============

        //============获取 拖拽对象  上移/下移 之后的 sort 值   begin========================
        //需要修改自己的排序值
        //需要 对索引区间的排序值(不包含排序对象自身) 进行 加1 或者 减 1
        T tempBean = null;
        if (flag) {
            //下移
            tempBean = excludeList.get(excludeList.size() - 1);
        } else {
            //上移动
            tempBean = excludeList.get(0);
        }
        Field sortField = tempBean.getClass().getDeclaredField("orderNum");
        sortField.setAccessible(true);
        Integer sort = (Integer) sortField.get(tempBean);
        //============获取 拖拽对象  上移/下移 之后的 sort 值   end========================

        //===================修改 目标对象  排序值  begin ==============
        Field targetBeanSortField = targetBean.getClass().getDeclaredField("orderNum");
        targetBeanSortField.setAccessible(true);
        //保存tagBean的orderNum值
        targetBeanSortField.set(targetBean, sort);
        //===================修改 目标对象  排序值  begin ==============

        //================修改  需要排序 区间(排序目标对象自身) 排序值  begin ========
        //处理
        for (T bean : excludeList) {
            sortField = bean.getClass().getDeclaredField("orderNum");
            sortField.setAccessible(true);
            sort = (Integer) sortField.get(bean);
            //flag true : 下移 非拖动对象 自身 排序索引值 需要 -1; false 反之
            if (isAsc) {
                //正序
                sortField.set(bean, flag ? (sort - 1) : (sort + 1));
            } else {
                //倒叙
                sortField.set(bean, flag ? (sort + 1) : (sort - 1));
            }
        }


        //================修改  需要排序 区间(排序目标对象自身) 排序值  end  ========
        buildList.addAll(excludeList);
        buildList.add(targetBean);
        return buildList;
    }

    @Override
    public Boolean moveSortCheckTable(String enterpriseId, MoveSortRequest request) {
        List<TbMetaTableDO> needUpdateSortList = null;
        try {
            needUpdateSortList = getNeedUpdateSortList(request);
        } catch (Exception e) {
            log.info("getNeedUpdateSortList_fail");
            throw new ServiceException(ErrorCodeEnum.CHECK_TABLE_MOVE_SORT_FAIL);
        }
        // 批量更新
        if (CollectionUtils.isNotEmpty(needUpdateSortList)){
            tbMetaTableMapper.batchUpdate(enterpriseId,needUpdateSortList);
        }
        return Boolean.TRUE;
    }

    /**
     * 检查表排序处理 方案2
     * @param request
     * @return
     */
    public static List<TbMetaTableDO> getNeedUpdateSortList(MoveSortRequest request){
        List<TbMetaTableDO> list = new ArrayList<>();
        List<CheckTableDTO> afterMoveSortCheckTableList = request.getAfterMoveSortCheckTableList();
        List<CheckTableDTO> preMoveSortCheckTableList = request.getPreMoveSortCheckTableList();
        if (CollectionUtils.isEmpty(afterMoveSortCheckTableList)||CollectionUtils.isEmpty(preMoveSortCheckTableList)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        for (int i = Constants.INDEX_ONE; i < afterMoveSortCheckTableList.size(); i++) {
            TbMetaTableDO tbMetaTableDO = new TbMetaTableDO();
            tbMetaTableDO.setId(afterMoveSortCheckTableList.get(i).getMetaTableId());
            tbMetaTableDO.setOrderNum(preMoveSortCheckTableList.get(i).getOrderNum());
            list.add(tbMetaTableDO);
        }
        return list;
    }

}
