//package net.coolcollege.cms.test.service.export;
//
//import cn.hutool.json.JSONUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
//import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
//import com.coolcollege.intelligent.model.export.request.ExportMsgSendRequest;
//import com.coolcollege.intelligent.model.export.request.FileExportBaseRequest;
//import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
//import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
//import com.coolcollege.intelligent.model.userholder.UserHolder;
//import com.coolcollege.intelligent.service.export.ExportUtil;
//import com.coolcollege.intelligent.service.importexcel.ExportAsyncService;
//import com.coolcollege.intelligent.service.importexcel.GenerateOssFileService;
//import com.coolcollege.intelligent.service.jms.JmsMessageSendService;
//import net.coolcollege.cms.test.IntelligentMainTest;
//import org.junit.Assert;
//import org.junit.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.anyObject;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//
///**
// * @author shuchang.wei
// * @date 2021/6/29 11:06
// */
//public class ExportUtilTest extends IntelligentMainTest {
//    @Resource
//    private ExportUtil exportUtil;
//    @MockBean
//    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;
//    @MockBean
//    private JmsMessageSendService jmsMessageSendService;
//    @MockBean
//    private GenerateOssFileService generateOssFileService;
//
//    @Test
//    @Transactional
//    @Rollback
//    public void testExportFile(){
//        FileExportBaseRequest request = new FileExportBaseRequest();
//        request.setExportServiceEnum(ExportServiceEnum.EXPORT_QUICK_COLUMN);
//        when(tbMetaQuickColumnMapper.countAll(eid)).thenReturn(new Long(100));
//        when(jmsMessageSendService.sendMessage(anyString(),anyObject())).thenReturn(1);
//        ImportTaskDO importTaskDO = exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
//        Assert.assertNotNull(importTaskDO);
//    }
//
//    @Test
//    @Transactional
//    @Rollback
//    public void testDoExport(){
//        FileExportBaseRequest request = new FileExportBaseRequest();
//        request.setExportServiceEnum(ExportServiceEnum.EXPORT_QUICK_COLUMN);
//        ImportTaskDO importTaskDO = new ImportTaskDO();
//        importTaskDO.setId(1L);
//        ExportMsgSendRequest exportMsgSendRequest = new ExportMsgSendRequest();
//        exportMsgSendRequest.setDbName(UserHolder.getUser().getDbName());
//        exportMsgSendRequest.setEnterpriseId(eid);
//        exportMsgSendRequest.setTotalNum(110L);
//        exportMsgSendRequest.setRequest(JSONObject.parseObject(JSONUtil.toJsonStr(request)));
//        exportMsgSendRequest.setImportTaskDO(importTaskDO);
//        when(tbMetaQuickColumnMapper.selectAllColumnList(eid)).thenReturn(Collections.singletonList(new TbMetaQuickColumnDO()));
//        when(generateOssFileService.generateOssWorkBookExcel(anyString(),anyObject(),anyString())).thenReturn("");
//        exportUtil.doExport(exportMsgSendRequest);
//    }
//}
