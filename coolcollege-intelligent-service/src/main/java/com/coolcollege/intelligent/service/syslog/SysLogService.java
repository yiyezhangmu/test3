package com.coolcollege.intelligent.service.syslog;

import com.coolcollege.intelligent.model.syslog.request.SysLogRequest;
import com.coolcollege.intelligent.model.syslog.vo.SysLogVO;
import com.github.pagehelper.PageInfo;

/**
 * describe: 系统日志
 *
 * @author wangff
 * @date 2025/1/20
 */
public interface SysLogService {

    /**
     * 分页查询
     * @param enterpriseId 企业id
     * @param request 查询条件
     * @return 分页对象
     */
    PageInfo<SysLogVO> getPage(String enterpriseId, SysLogRequest request);
}
