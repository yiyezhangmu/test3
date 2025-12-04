package com.coolcollege.intelligent.util;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import com.coolcollege.intelligent.model.patrolstore.records.PatrolStoreRecordsTableAndPicDTO;
import com.coolcollege.intelligent.model.store.dto.StoreDTO;
import com.coolcollege.intelligent.model.store.vo.ExportStoreBaseVO;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 邵凌志
 * @date 2021/2/2 15:44
 */
@Slf4j
public class DynamicExcelUtil {
    // 数据行高和宽度
    private static final SimpleRowHeightStyleStrategy ROW_HEIGHT = new SimpleRowHeightStyleStrategy((short)25, (short)100);
    // 表头行高
    private static final SimpleColumnWidthStyleStrategy WIDTH_HEIGHT = new SimpleColumnWidthStyleStrategy(20);

    public static List<List<String>> tableRecordsHead() {
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<>(1);
        head0.add("门店区域");
        List<String> head1 = new ArrayList<>(1);
        head1.add("门店名称");
        List<String> head2 = new ArrayList<>(1);
        head2.add("检查表");
        List<String> head3 = new ArrayList<>(1);
        head3.add("总检查项数");
        List<String> head4 = new ArrayList<>(1);
        head4.add("不适用项数");
        List<String> head5 = new ArrayList<>(1);
        head5.add("巡店人/处理人");
        List<String> head6 = new ArrayList<>(1);
        head6.add("审批人");
        List<String> head7 = new ArrayList<>(1);
        head7.add("复审人");
        List<String> head8 = new ArrayList<>(1);
        head8.add("门店得分");
        List<String> head9 = new ArrayList<>(1);
        head9.add("门店评价");
        List<String> head10 = new ArrayList<>(1);
        head10.add("是否过期完成");
        List<String> head11 = new ArrayList<>(1);
        head11.add("巡店开始时间");
        List<String> head12 = new ArrayList<>(1);
        head12.add("巡店结束时间");
        List<String> head13 = new ArrayList<>(1);
        head13.add("巡店时长");
        List<String> head14 = new ArrayList<>(1);
        head14.add("巡店签到地址");
        List<String> head15 = new ArrayList<>(1);
        head15.add("巡店签退地址");
        List<String> head16 = new ArrayList<>(1);
        head16.add("签退地址异常");
        List<String> head17 = new ArrayList<>(1);
        head17.add("签到地址异常");
        List<String> head18 = new ArrayList<>(1);
        head18.add("类型");
        List<String> head19 = new ArrayList<>(1);
        head19.add("任务名称");
        List<String> head20 = new ArrayList<>(1);
        head20.add("有效期");
        List<String> head21 = new ArrayList<>(1);
        head21.add("创建人");
        List<String> head22 = new ArrayList<>(1);
        head22.add("创建时间");
        List<String> head23 = new ArrayList<>(1);
        head23.add("任务说明");
        List<String> head24 = new ArrayList<>(1);
        head24.add("任务状态");

        addAll(result, head0, head1, head2, head3, head4, head5);
        addAll(result, head6, head7, head8, head9, head10, head11);
        addAll(result, head12, head13, head14, head15, head16, head17);
        addAll(result,  head18, head19, head20, head21, head22, head23);
        result.add(head24);
        return result;
    }

    public static List<List<String>> displayHead() {
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> head0 = new ArrayList<>(1);
        head0.add("门店区域");
        List<String> head1 = new ArrayList<>(1);
        head1.add("门店名称");
        List<String> head2 = new ArrayList<>(1);
        head2.add("检查表");
        List<String> head3 = new ArrayList<>(1);
        head3.add("总检查项数");
        List<String> head4 = new ArrayList<>(1);
        head4.add("不适用项数");
        List<String> head5 = new ArrayList<>(1);
        head5.add("处理人");
        List<String> head6 = new ArrayList<>(1);
        head6.add("审批人");
        List<String> head7 = new ArrayList<>(1);
        head7.add("复审人");
        List<String> head8 = new ArrayList<>(1);
        head8.add("门店得分");
        List<String> head9 = new ArrayList<>(1);
        head9.add("门店评价");
        List<String> head10 = new ArrayList<>(1);
        head10.add("是否过期完成");
        List<String> head11 = new ArrayList<>(1);
        head11.add("结束时间");
        List<String> head12 = new ArrayList<>(1);
        head12.add("检查时长");
        List<String> head13 = new ArrayList<>(1);
        head13.add("任务名称");
        List<String> head14 = new ArrayList<>(1);
        head14.add("有效期");
        List<String> head15 = new ArrayList<>(1);
        head15.add("创建人");
        List<String> head16 = new ArrayList<>(1);
        head16.add("创建时间");
        List<String> head17 = new ArrayList<>(1);
        head17.add("任务说明");
        List<String> head18 = new ArrayList<>(1);
        head18.add("流程状态");
        addAll(result, head0, head1, head2, head3, head4, head5);
        addAll(result, head6, head7, head8, head9, head10, head11);
        addAll(result, head12, head13, head14, head15, head16, head17);
        result.add(head18);
        return result;
    }




    private static void fillPicToData(List<Object> data, List<String> picList) {
        if (CollUtil.isNotEmpty(picList)) {
            for (String picUrl : picList) {
                try {
                    // 按照高度100进行等比缩放，图片不会扭曲
                    data.add(new URL(picUrl + "?x-oss-process=image/resize,h_100,m_lfit"));
                } catch (MalformedURLException e) {
                    log.error("获取网络图片失败：", e);
                }
            }
        }
    }

    public static List<List<Object>> tableRecordsData(List<PatrolStoreRecordsTableAndPicDTO> data) {
        List<List<Object>> result = new ArrayList<>();
        for (PatrolStoreRecordsTableAndPicDTO datum : data) {
            List<Object> list = new ArrayList<>(25);
            list.add(datum.getAreaName());
            list.add(datum.getStoreName());
            list.add(datum.getTableName());
            list.add(datum.getTotalColumnCount());
            list.add(datum.getUnQualifiedCount());
            list.add(datum.getSupervisorName());
            list.add(datum.getHandler());
            list.add(datum.getReChecker());
            list.add(datum.getScore());
            list.add(datum.getStoreEvaluation());
            list.add(datum.getIsOverdue());
            list.add(datum.getSignInTime());
            list.add(datum.getSignOutTime());
            list.add(datum.getPatrolTime());
            list.add(datum.getSignInAddress());
            list.add(datum.getSignEndAddress());
            list.add(datum.getSignOutStatus());
            list.add(datum.getIsAddException());
            list.add(datum.getRecordType());
            list.add(datum.getTaskName());
            list.add(datum.getEffectiveTime());
            list.add(datum.getCreateUserName());
            list.add(datum.getCreateTime());
            list.add(datum.getNote());
            list.add(datum.getTaskStatus());
            List<String> picList = datum.getPicList();
            fillPicToData(list, picList);
            result.add(list);
        }
        return result;
    }

    public static void addAll(List<List<String>> result, List<String> head0, List<String> head1, List<String> head2, List<String> head3, List<String> head4, List<String> head5) {
        result.add(head0);
        result.add(head1);
        result.add(head2);
        result.add(head3);
        result.add(head4);
        result.add(head5);
    }

    public static void expansionHead(List<List<String>> head, List<List<Object>> data) {
        // 获取动态数据最大的size
        Optional<List<Object>> max = data.stream().max(Comparator.comparing(List::size));
        AtomicInteger maxSize = new AtomicInteger();
        max.ifPresent(objects -> maxSize.set(objects.size()));
        // 动态添加图片表头
        for (int i = head.size(); i < maxSize.get(); i++) {
            List<String>  expansionHead = new ArrayList<>(1);
            expansionHead.add("检查项图片" + (i - head.size() + 1));
            head.add(expansionHead);
        }
    }

    public static byte[] getDataByte(String shellName, List<List<String>> head, List<List<Object>> data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        EasyExcel.write(bos, null)
                .sheet(shellName)
                .registerWriteHandler(ROW_HEIGHT)
                .registerWriteHandler(WIDTH_HEIGHT)
                .head(head)
                .doWrite(data);
        byte[] bytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            log.error("关闭数据流失败：", e);
        }
        return bytes;
    }
}
