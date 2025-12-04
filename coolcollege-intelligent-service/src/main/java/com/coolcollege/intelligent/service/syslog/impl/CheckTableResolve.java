package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.common.util.LogUtil;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaTableDao;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaStaTableDTO;
import com.coolcollege.intelligent.model.metatable.request.TbMetaTableRequest;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.util.SysLogHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.constant.SysLogConstant.Template.RECOVERY_TEMPLATE2;
import static com.coolcollege.intelligent.common.enums.syslog.OpTypeEnum.*;

/**
 * @Author: huhu
 * @Date: 2025/1/22 14:54
 * @Description:
 */
@Service
@Slf4j
public class CheckTableResolve extends AbstractOpContentResolve{

    @Resource
    private TbMetaTableDao tbMetaTableDao;

    @PostConstruct
    @Override
    protected void init() {
        super.init();
        funcMap.put(BATCH_ARCHIVE, this::batchArchive);
    }

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.CHECK_TABLE;
    }

    @Override
    protected String insert(String enterpriseId, SysLogDO sysLogDO) {
        String content = "新增了检查表「%s」";
        TbMetaStaTableDTO tbMetaStaTableDTO = LogUtil.paresString(sysLogDO.getReqParams(), "metaStaTableDTO", TbMetaStaTableDTO.class);
        String tableName = "";
        if (Objects.nonNull(tbMetaStaTableDTO)) {
            tableName = tbMetaStaTableDTO.getTableName();
        }
        content = String.format(content, tableName);
        return content;
    }

    @Override
    protected String edit(String enterpriseId, SysLogDO sysLogDO) {
        String content = "编辑了检查表「%s」";
        TbMetaStaTableDTO tbMetaStaTableDTO = LogUtil.paresString(sysLogDO.getReqParams(), "metaStaTableDTO", TbMetaStaTableDTO.class);
        String tableName = "";
        if (Objects.nonNull(tbMetaStaTableDTO)) {
            tableName = tbMetaStaTableDTO.getTableName();
        }
        content = String.format(content, tableName);
        return content;
    }

    @Override
    protected String delete(String enterpriseId, SysLogDO sysLogDO) {
        return super.delete(enterpriseId, sysLogDO);
    }

    @Override
    protected String archive(String enterpriseId, SysLogDO sysLogDO) {
        String content = "归档了检查表「%s」";
        String tableName = "";
        TbMetaTableRequest tbMetaTableRequest = LogUtil.paresString(sysLogDO.getReqParams(), "tbMetaTableRequest", TbMetaTableRequest.class);
        if (Objects.nonNull(tbMetaTableRequest)) {
            TbMetaTableDO tbMetaTableDO = tbMetaTableDao.selectById(enterpriseId, tbMetaTableRequest.getId());
            if (Objects.nonNull(tbMetaTableDO)) {
                tableName = tbMetaTableDO.getTableName();
            }
        }
        if (tbMetaTableRequest.getPigeonholeStatus().equals(1)) {
            content = String.format(content, tableName);
        } else {
            // 因为归档和恢复使用是同一个接口，恢复使用在这里特殊处理
            sysLogDO.setMenus(OpModuleEnum.SOP_ARCHIVES.getMenus());
            sysLogDO.setOpType(RECOVERY.getType());
            sysLogDO.setFunc("恢复使用检查表");
            content = SysLogHelper.buildContent(RECOVERY_TEMPLATE2, "检查表", tableName);
        }
        return content;
    }

    protected String batchArchive(String enterpriseId, SysLogDO sysLogDO) {
        String content = "归档了检查表%s";
        String tableNames = "";
        JSONObject reqJson = JSONObject.parseObject(sysLogDO.getReqParams());
        if (Objects.nonNull(reqJson)) {
            String ids = reqJson.getString("ids");
            if (StringUtils.isNotBlank(ids)) {
                List<Long> idList = Arrays.stream(ids.split(","))
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
                List<TbMetaTableDO> tbMetaTableDOList = tbMetaTableDao.selectByIds(enterpriseId, idList);
                tableNames = tbMetaTableDOList.stream().map(t -> String.format("「%s」", t.getTableName())).collect(Collectors.joining("、"));
            }
        }
        return String.format(content, tableNames);
    }
}
