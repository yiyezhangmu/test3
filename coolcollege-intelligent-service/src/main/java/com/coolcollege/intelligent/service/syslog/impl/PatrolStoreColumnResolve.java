package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.mapper.metatable.TbMetaQuickColumnDAO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.request.ColumnCategoryRequest;
import com.coolcollege.intelligent.model.metatable.request.TbMetaColumnUpdateStatusRequest;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnVO;
import com.coolcollege.intelligent.model.patrolstore.request.QuickTableColumnRequest;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.text.MessageFormat;
import java.util.List;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.*;

/**
 * describe: 巡店检查项操作内容处理
 *
 * @author wangff
 * @date 2025/1/22
 */
@Service
@Slf4j
public class PatrolStoreColumnResolve extends AbstractOpContentResolve {
    @Resource
    private TbMetaQuickColumnDAO tbMetaQuickColumnDAO;

    @Override
    protected void init() {
        super.init();
        funcMap.put(OpTypeEnum.SOP_COLUMN_BATCH_AUTH_SETTING, this::batchAuthSetting);
        funcMap.put(OpTypeEnum.INSERT_GROUP, this::insertGroup);
        funcMap.put(OpTypeEnum.UPDATE_GROUP, this::updateGroup);
        funcMap.put(OpTypeEnum.DELETE_GROUP, this::deleteGroup);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SOP_COLUMN;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getRespParams());
        TbMetaQuickColumnVO response = jsonObject.getObject("data", TbMetaQuickColumnVO.class);
        String result = MessageFormat.format("{0}({1})", response.getColumnName(), String.valueOf(response.getId()));
        return SysLogHelper.buildContent(INSERT_TEMPLATE, "检查项", result);
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        QuickTableColumnRequest request = jsonObject.getObject("quickTableColumnRequest", QuickTableColumnRequest.class);
        String result = MessageFormat.format("{0}({1})", request.getColumnName(), String.valueOf(request.getId()));
        return SysLogHelper.buildContent(UPDATE_TEMPLATE, "检查项", result);
    }

    @Override
    protected String archive(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        TbMetaColumnUpdateStatusRequest request = jsonObject.getObject("request", TbMetaColumnUpdateStatusRequest.class);
        List<TbMetaQuickColumnDO> columnList = tbMetaQuickColumnDAO.getByIds(enterpriseId, request.getIds());
        if (CollectionUtils.isEmpty(columnList)) {
            log.info("archive#检查项为空");
            return null;
        }
        String result = SysLogHelper.buildBatchContentItem(columnList, TbMetaQuickColumnDO::getColumnName, TbMetaQuickColumnDO::getId);
        if (request.getStatus().equals(0)) {
            // 因为归档和恢复使用是同一个接口，恢复使用在这里特殊处理
            sysLogDO.setMenus(OpModuleEnum.SOP_ARCHIVES.getMenus());
            sysLogDO.setOpType(OpTypeEnum.RECOVERY.getType());
            sysLogDO.setFunc("恢复使用检查项");
            return SysLogHelper.buildContent(RECOVERY_TEMPLATE, "检查项", result);
        } else {
            return SysLogHelper.buildContent(ARCHIVE_TEMPLATE, "检查项", result);
        }
    }

    /**
     * 批量权限配置
     */
    private String batchAuthSetting(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        QuickTableColumnRequest request = jsonObject.getObject("request", QuickTableColumnRequest.class);
        List<TbMetaQuickColumnDO> columnList = tbMetaQuickColumnDAO.getByIds(enterpriseId, request.getColumnIdList());
        if (CollectionUtils.isEmpty(columnList)) {
            log.info("batchAuthSetting#检查项为空");
        }
        String result = SysLogHelper.buildBatchContentItem(columnList, TbMetaQuickColumnDO::getColumnName, TbMetaQuickColumnDO::getId);
        return SysLogHelper.buildContent(SOP_COLUMN_BATCH_AUTH_SETTING, result);
    }

    /**
     * 新增分类
     */
    private String insertGroup(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        ColumnCategoryRequest request = jsonObject.getObject("request", ColumnCategoryRequest.class);
        return SysLogHelper.buildContent(INSERT_TEMPLATE, "检查项分类", request.getCategoryName());
    }

    /**
     * 编辑分类
     */
    private String updateGroup(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        ColumnCategoryRequest request = jsonObject.getObject("request", ColumnCategoryRequest.class);
        return SysLogHelper.buildContent(UPDATE_TEMPLATE, "检查项分类", request.getCategoryName());
    }

    /**
     * 删除分类
     */
    private String deleteGroup(String enterpriseId, SysLogDO sysLogDO) {
        JSONObject jsonObject = JSONObject.parseObject(sysLogDO.getReqParams());
        ColumnCategoryRequest request = jsonObject.getObject("request", ColumnCategoryRequest.class);
        return SysLogHelper.buildContent(DELETE_TEMPLATE, "检查项分类", request.getCategoryName());
    }
}
