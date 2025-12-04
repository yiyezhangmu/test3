package com.coolcollege.intelligent.service.export.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.service.export.BaseExportService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/6/4 14:40
 */
@Service
public class QuickMetaColumnExportService implements BaseExportService {

    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;

    @Override
    public void validParam(FileExportBaseRequest fileExportBaseRequest) {

    }

    @Override
    public Long getTotalNum(String enterpriseId, FileExportBaseRequest request) {
        return tbMetaQuickColumnMapper.countAll(enterpriseId);
    }

    @Override
    public ExportServiceEnum getExportServiceEnum() {
        return ExportServiceEnum.EXPORT_QUICK_COLUMN;
    }

    @Override
    public List<TbMetaQuickColumnDO> exportList(String enterpriseId, JSONObject request,int pageSize, int pageNum) {
        PageHelper.startPage(pageNum,pageSize,false);
        return tbMetaQuickColumnMapper.selectAllColumnList(enterpriseId);
    }
}
