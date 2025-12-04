package com.coolcollege.intelligent.service.login.impl;

import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseConfigMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseMapper;
import com.coolcollege.intelligent.dao.login.LoginRecordMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDO;
import com.coolcollege.intelligent.model.login.EnterpriseLoginCountDTO;
import com.coolcollege.intelligent.service.login.LoginRecordService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.common.util.DateUtils.DATE_FORMAT_MINUTE;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/27
 */
@Service
public class LoginRecordServiceImpl implements LoginRecordService {
    @Resource
    private LoginRecordMapper loginRecordMapper;
    @Resource
    private EnterpriseConfigMapper enterpriseConfigMapper;
    @Resource
    private EnterpriseMapper enterpriseMapper;

    @Override
    public List<EnterpriseLoginCountDTO> statisticsLoginRecord() {

        List<EnterpriseLoginCountDTO> allCountList =new ArrayList<>();
        DataSourceHelper.reset();
        List<EnterpriseConfigDO> enterpriseConfigDOList = enterpriseConfigMapper.selectEnterpriseConfigAll();
        List<EnterpriseDO> enterpriseDOList = enterpriseMapper.listEnterpriseAll();
        Map<String, EnterpriseDO> enterpriseMap = ListUtils.emptyIfNull(enterpriseDOList).stream()
                .collect(Collectors.toMap(EnterpriseDO::getId, data -> data, (a, b) -> a));
        Map<String,List<EnterpriseConfigDO>> enterpriseConfigMap = enterpriseConfigDOList.stream()
                .filter(data-> StringUtils.isNotBlank(data.getDingCorpId()))
                .collect(Collectors.groupingBy(EnterpriseConfigDO::getDbName));

        for (Map.Entry<String, List<EnterpriseConfigDO>> entry : enterpriseConfigMap.entrySet()) {
            List<EnterpriseLoginCountDTO> enterpriseLoginCountDTOList = enterpriseLoginCount(entry.getKey(), entry.getValue(),enterpriseMap);
            if(CollectionUtils.isNotEmpty(enterpriseLoginCountDTOList)){
                allCountList.addAll(enterpriseLoginCountDTOList);
            }
        }
        //按照登录次数排序
       return ListUtils.emptyIfNull(allCountList)
                .stream()
                .sorted(Comparator.comparing(EnterpriseLoginCountDTO::getLoginCount).reversed())
                .collect(Collectors.toList());

    }
    private List<EnterpriseLoginCountDTO> enterpriseLoginCount(String db,List<EnterpriseConfigDO>enterpriseConfigDOList,
                                                               Map<String, EnterpriseDO> enterpriseMap){
        List<EnterpriseLoginCountDTO> enterpriseLoginCountDTOList =new ArrayList<>();
        DataSourceHelper.changeToSpecificDataSource(db);
        ListUtils.emptyIfNull(enterpriseConfigDOList).forEach(data->{
            if(StringUtils.isNotBlank(data.getEnterpriseId())){
                EnterpriseLoginCountDTO enterpriseLoginCountDTO = loginRecordMapper.countEnterpriseLogin(data.getEnterpriseId());
                if(enterpriseLoginCountDTO!=null){
                    enterpriseLoginCountDTO.setEnterpriseId(data.getEnterpriseId());
                    if(MapUtils.isNotEmpty(enterpriseMap)&&enterpriseMap.get(data.getEnterpriseId())!=null){
                        enterpriseLoginCountDTO.setEnterpriseName(enterpriseMap.get(data.getEnterpriseId()).getName());
                        enterpriseLoginCountDTO.setEnterpriseMobile(enterpriseMap.get(data.getEnterpriseId()).getMobile());
                    }
                    if(enterpriseLoginCountDTO.getLastLoginTime()!=null){
                        enterpriseLoginCountDTO.setLastLoginStr(DateUtils.convertTimeToString(enterpriseLoginCountDTO.getLastLoginTime(),DATE_FORMAT_MINUTE));
                    }
                    enterpriseLoginCountDTOList.add(enterpriseLoginCountDTO);
                }
            }
        });

        return enterpriseLoginCountDTOList;
    }

}
