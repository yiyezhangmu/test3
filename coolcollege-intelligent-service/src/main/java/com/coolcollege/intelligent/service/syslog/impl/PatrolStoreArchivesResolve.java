package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaTableDao;
import com.coolcollege.intelligent.mapper.metatable.TbMetaQuickColumnDAO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaStaTableDTO;
import com.coolcollege.intelligent.model.patrolstore.request.QuickTableColumnRequest;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.DELETE_TEMPLATE2;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
* describe: 巡店档案库操作内容处理
*
* @author wangff
* @date 2025-01-23
*/
@Service
@Slf4j
public class PatrolStoreArchivesResolve extends AbstractOpContentResolve {

    @Resource
    private TbMetaQuickColumnDAO tbMetaQuickColumnDAO;
    @Resource
    private TbMetaTableDao tbMetaTableDao;

    @Override
    protected void init() {
        super.init();
        funcMap.put(SOP_ARCHIVES_COLUMN_DELETE, this::sopArchivesColumnDelete);
        funcMap.put(SOP_ARCHIVES_TABLE_DELETE, this::sopArchivesTableDelete);
    }


    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.SOP_ARCHIVES;
    }


    /**
     * 检查项删除
     */
    private String sopArchivesColumnDelete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    /**
     * 检查表删除
     */
    private String sopArchivesTableDelete(String enterpriseId, SysLogDO sysLogDO) {
        return SysLogHelper.getPreprocessResultByExtendInfoStr(sysLogDO.getExtendInfo());
    }

    @Override
    public String preprocess(String enterpriseId, Map<String, Object> reqParams, OpTypeEnum typeEnum) {
        switch (typeEnum) {
                case SOP_ARCHIVES_COLUMN_DELETE:
                    return sopArchivesColumnDeletePreprocess(enterpriseId, reqParams);
                case SOP_ARCHIVES_TABLE_DELETE:
                    return sopArchivesTableDeletePreprocess(enterpriseId, reqParams);
        }
        return null;
    }

    /**
     * SOP_ARCHIVES_COLUMN_DELETE前置操作逻辑
     */
    private String sopArchivesColumnDeletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        QuickTableColumnRequest request = jsonObject.getObject("quickTableColumnRequest", QuickTableColumnRequest.class);
        List<TbMetaQuickColumnDO> columnList = tbMetaQuickColumnDAO.getByIds(enterpriseId, request.getColumnIdList());
        if (CollectionUtil.isEmpty(columnList)) {
            log.info("sopArchivesColumnDeletePreprocess#检查项为空");
            return null;
        }
        String result = SysLogHelper.buildBatchContentItem(columnList, TbMetaQuickColumnDO::getColumnName, TbMetaQuickColumnDO::getId);
        return SysLogHelper.buildContent(DELETE_TEMPLATE2, "检查项", result);
    }

    /**
     * SOP_ARCHIVES_TABLE_DELETE前置操作逻辑
     */
    private String sopArchivesTableDeletePreprocess(String enterpriseId, Map<String, Object> reqParams) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(reqParams));
        TbMetaStaTableDTO tbMetaStaTableDTO = jsonObject.getObject("tbMetaStaTableDTO", TbMetaStaTableDTO.class);
        List<TbMetaTableDO> tableList = tbMetaTableDao.selectByIds(enterpriseId, tbMetaStaTableDTO.getMetaTableIds());
        if (CollectionUtil.isEmpty(tableList)) {
            log.info("sopArchivesTableDeletePreprocess#检查表为空");
            return null;
        }
        String result = SysLogHelper.buildBatchContentItem(tableList, TbMetaTableDO::getTableName, TbMetaTableDO::getId);
        return SysLogHelper.buildContent(DELETE_TEMPLATE2, "检查表", result);
    }

}
