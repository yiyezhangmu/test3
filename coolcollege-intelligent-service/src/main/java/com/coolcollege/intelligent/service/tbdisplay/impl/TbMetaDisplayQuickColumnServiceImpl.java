package com.coolcollege.intelligent.service.tbdisplay.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.UserRangeTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.page.PageVO;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayQuickColumnMapper;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayQuickColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableDTO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableItemDTO;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnAddParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickColumnQueryParam;
import com.coolcollege.intelligent.model.tbdisplay.param.TbMetaDisplayQuickContentQuery;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.tbdisplay.TbMetaDisplayQuickColumnService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wxp
 * @date 2021-03-02 19:50
 */
@Service
@Slf4j
public class TbMetaDisplayQuickColumnServiceImpl implements TbMetaDisplayQuickColumnService {

    @Resource
    private TbMetaDisplayQuickColumnMapper tbMetaDisplayQuickColumnMapper;

    @Autowired
    private TbMetaTableService tableService;

    @Override
    public Boolean insertDisplayQuickColumn(String eid, TbMetaDisplayQuickColumnAddParam tbMetaDisplayQuickColumnAddParam) {
        CurrentUser user = UserHolder.getUser();
        String columnName = tbMetaDisplayQuickColumnAddParam.getColumnName();
        BigDecimal score = tbMetaDisplayQuickColumnAddParam.getScore() == null? new BigDecimal(10) : tbMetaDisplayQuickColumnAddParam.getScore();
        if (StrUtil.isEmpty(columnName)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查项名称不能为空！");
        }
        if (StrUtil.length(columnName) > 200) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查项名称最长200个字符！");
        }

        TbMetaDisplayQuickColumnDO exsit = tbMetaDisplayQuickColumnMapper.getByColumnNameAndCreateUserId(eid, columnName, user.getUserId());
        if (exsit != null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查项名称已存在！");
        }
        TbMetaDisplayQuickColumnDO tbMetaDisplayQuickColumnDO = TbMetaDisplayQuickColumnDO.builder()
                .columnName(columnName).description(tbMetaDisplayQuickColumnAddParam.getDescription())
                .standardPic(tbMetaDisplayQuickColumnAddParam.getStandardPic())
                .createUserName(user.getName())
                .createUserId(user.getUserId())
                .score(score).checkType(tbMetaDisplayQuickColumnAddParam.getCheckType()!=null?tbMetaDisplayQuickColumnAddParam.getCheckType():0)
                .mustPic(tbMetaDisplayQuickColumnAddParam.getMustPic())
                .build();
        tbMetaDisplayQuickColumnMapper.insert(eid, tbMetaDisplayQuickColumnDO);
        return Boolean.TRUE;
    }

    @Override
    public PageVO selectTaskSopList(String enterpriseId, TbMetaDisplayQuickColumnQueryParam query, Integer pageNum, Integer pageSize) {
        CurrentUser user = UserHolder.getUser();
        PageHelper.startPage(pageNum, pageSize);
        //如果CheckType没有传值，则默认查询快速检查项，否则按传的值查询
        query.setCheckType(query.getCheckType()!=null?query.getCheckType():0);
        List<TbMetaDisplayQuickColumnDO> tbMetaDisplayQuickColumnDOList = tbMetaDisplayQuickColumnMapper.listDisplayQuickColumn(enterpriseId, query);
        return PageHelperUtil.getPageVO(new PageInfo<>(tbMetaDisplayQuickColumnDOList));
    }

    @Override
    public Boolean deleteDisplayQuickColumn(String enterpriseId, TbMetaDisplayQuickColumnQueryParam query){
        CurrentUser user = UserHolder.getUser();
        if (query.getTbMetaDisplayQuickColumnIds()==null){
            throw new ServiceException(ErrorCodeEnum.ABNORMAL_DATA.getCode(), "检查内容id集合不能为空！");
        }
        TbMetaDisplayQuickColumnDO tbMetaDisplayQuickColumnDO =
                TbMetaDisplayQuickColumnDO.builder().deleted(true).editUserId(user.getUserId()).editUserName(user.getName()).build();
        int count = tbMetaDisplayQuickColumnMapper.updateByPrimaryKeySelective(enterpriseId, query.getTbMetaDisplayQuickColumnIds(),tbMetaDisplayQuickColumnDO);
        return count >= 1;
    }

    @Override
    public TbMetaTableDO createTableByColumnIdList(String enterpriseId, List<Long> columnIdList, CurrentUser user) {
        List<TbMetaDisplayQuickColumnDO> tbMetaDisplayQuickColumnDOList = tbMetaDisplayQuickColumnMapper.listByIdList(enterpriseId, columnIdList);
        TbDisplayTableDTO displayTableDTO = new TbDisplayTableDTO();
        List<TbDisplayTableItemDTO> tableItemList = tbMetaDisplayQuickColumnDOList.stream().map(a -> TbDisplayTableItemDTO.builder()
                        .columnName(a.getColumnName()).quickColumnId(a.getId()).score(a.getScore()).build()).collect(Collectors.toList());
        displayTableDTO.setName("陈列临时检查表");
        displayTableDTO.setDeleted(1);
        displayTableDTO.setTableItemList(tableItemList);
        displayTableDTO.setUseRange(UserRangeTypeEnum.SELF.getType());
        TbMetaTableDO tbMetaTableDO = tableService.addOrUpdateDisplayMetaTable(enterpriseId, user, displayTableDTO);
        log.info("根据快捷陈列检查项创建检查表返回信息:####" + JSON.toJSONString(tbMetaTableDO));
        // 调用创建陈列检查表的接口
        return tbMetaTableDO;
    }

    @Override
    public Boolean batchInsert(String enterpriseId, TbMetaDisplayQuickContentQuery query) {
        CurrentUser user = UserHolder.getUser();
        query.setCreateUserId(user.getUserId());
        query.setCreateUserName(user.getName());
        query.setCheckType(1);
        Boolean aBoolean = tbMetaDisplayQuickColumnMapper.batchInsert(enterpriseId, query);
        return aBoolean;
    }

    @Override
    public Boolean checkContentEdit(String enterpriseId, TbMetaDisplayQuickColumnQueryParam query) {
        CurrentUser user = UserHolder.getUser();
        if (query.getCheckContentId()==null){
            throw new ServiceException(ErrorCodeEnum.ABNORMAL_DATA.getCode(), "检查内容id不能为空！");
        }
        List<Long> idList= new ArrayList<>();
        idList.add(query.getCheckContentId());
        query.setTbMetaDisplayQuickColumnIds(idList);
        TbMetaDisplayQuickColumnDO tbMetaDisplayQuickColumnDO =
                TbMetaDisplayQuickColumnDO.builder().editUserId(user.getUserId()).editUserName(user.getName()).columnName(query.getColumnName())
                        .description(query.getDescription()).standardPic(query.getStandardPic()).mustPic(query.getMustPic()).build();
        int count = tbMetaDisplayQuickColumnMapper.updateByPrimaryKeySelective(enterpriseId, query.getTbMetaDisplayQuickColumnIds(),tbMetaDisplayQuickColumnDO);
        return count >= 1;
    }

}
