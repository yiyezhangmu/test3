package com.coolcollege.intelligent.model.export.request;

import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import lombok.Data;

/**
 * @Description 文件导出请求标志类
 * @author shuchang.wei
 * @date 2021/6/4 10:56
 */
@Data
public class FileExportBaseRequest {
    ExportServiceEnum exportServiceEnum;
}
