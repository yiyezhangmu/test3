package com.coolcollege.intelligent.service.syslog.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.syslog.OpModuleEnum;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * describe: 导入导出操作内容处理
 *
 * @author wangff
 * @date 2025/1/20
 */
@Service
public class ImportExportResolve extends AbstractOpContentResolve {

    @Override
    public OpModuleEnum getOpModule() {
        return OpModuleEnum.IMPORT_EXPORT;
    }

    @Override
    protected String exportFile(String enterpriseId, SysLogDO sysLogDO) {
        String fileName = getFileName(sysLogDO.getRespParams());
        return "导出文件：" + fileName;
    }

    @Override
    protected String importFile(String enterpriseId, SysLogDO sysLogDO) {
        String fileName = getFileName(sysLogDO.getRespParams());
        return "导入文件：" + fileName;
    }

    /**
     * 获取文件名称，兼容不同返回信息
     * @param param
     * @return
     */
    private String getFileName(String param) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        String fileName = jsonObject.getString("fileName");
        if (StringUtils.isBlank(fileName)) {
            JSONObject data = jsonObject.getJSONObject("data");
            if (Objects.nonNull(data)) {
                return data.getString("fileName");
            }
        }
        return fileName;
    }
}
