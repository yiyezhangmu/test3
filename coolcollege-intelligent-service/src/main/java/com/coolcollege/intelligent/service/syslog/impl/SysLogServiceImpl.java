package com.coolcollege.intelligent.service.syslog.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.syslog.SysLogMapper;
import com.coolcollege.intelligent.model.syslog.SysLogDO;
import com.coolcollege.intelligent.model.syslog.request.SysLogRequest;
import com.coolcollege.intelligent.model.syslog.vo.SysLogVO;
import com.coolcollege.intelligent.service.syslog.SysLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * describe: 系统日志 服务实现类
 *
 * @author wangff
 * @date 2025/1/20
 */
@Service
public class SysLogServiceImpl implements SysLogService {
    @Resource
    private SysLogMapper sysLogMapper;

    @Override
    public PageInfo<SysLogVO> getPage(String enterpriseId, SysLogRequest request) {
        if (StringUtils.isNotBlank(request.getModule())) {
            request.setModules(Arrays.asList(StringUtils.split(request.getModule(), Constants.COMMA)));
        }
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<SysLogDO> sysLogDOList = sysLogMapper.selectByParams(enterpriseId, request);
        PageInfo result = new PageInfo<>(sysLogDOList);
        result.setList(CollStreamUtil.toList(sysLogDOList, SysLogVO::convert));
        return result;
    }

}
