package com.coolcollege.intelligent.service.oaPlugin.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.dao.store.StoreDao;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.oaPlugin.vo.OptionDataVO;
import com.coolcollege.intelligent.model.store.StoreDO;
import com.coolcollege.intelligent.service.oaPlugin.OaPluginService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service(value = "oaPluginService")
@Slf4j
public class OaPluginServiceImpl implements OaPluginService {

    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;

    @Resource
    private StoreDao storeDao;

    @Override
    public OptionDataVO initOaPluginData(String enterpriseId) {
        OptionDataVO optionDataVO = new OptionDataVO();
        List<OptionDataVO.InitOptionData> dataList = Lists.newArrayList();
        List<StoreDO> storeDOList = storeDao.listStoreForOaPlugin(enterpriseId);
        List<OptionDataVO.Option> storeOptions = ListUtils.emptyIfNull(storeDOList).stream().map(e -> {
                    OptionDataVO.Option option = new OptionDataVO.Option();
                    option.setKey(e.getStoreId());
                    option.setValue(e.getStoreName());
                    return option;
                }).collect(Collectors.toList());
        String placeholder = CollectionUtils.isNotEmpty(storeOptions) ? "请选择" : "暂无门店，请联系管理员添加门店";
        OptionDataVO.InitOptionData store = fillInitOptionData("store",placeholder, "整改门店", storeOptions);
        dataList.add(store);
        List<TbMetaQuickColumnDO> columnDOList = tbMetaQuickColumnMapper.listColumnForOaPlugin(enterpriseId);
        List<OptionDataVO.Option> columnOptions = ListUtils.emptyIfNull(columnDOList).stream().map(e -> {
            OptionDataVO.Option option = new OptionDataVO.Option();
            option.setKey(String.valueOf(e.getId()));
            option.setValue(e.getColumnName());
            return option;
        }).collect(Collectors.toList());
        placeholder = CollectionUtils.isNotEmpty(columnOptions) ? "请选择" : "暂无检查项，请联系管理员添加检查项";
        OptionDataVO.InitOptionData columnOptionData = fillInitOptionData("checkItem",placeholder, "关联检查项", columnOptions);
        dataList.add(columnOptionData);
        JSONObject data = new JSONObject();
        data.put("dataList", dataList);
        optionDataVO.setData(data);
        return optionDataVO;
    }

    private OptionDataVO.InitOptionData fillInitOptionData(String bizAlias, String placeholder, String label, List<OptionDataVO.Option> options) {
        OptionDataVO.Prop props = new OptionDataVO.Prop();
        props.setPlaceholder(placeholder);
        props.setLabel(label);
        props.setOptions(options);
        OptionDataVO.InitOptionData initOptionData = new OptionDataVO.InitOptionData();
        initOptionData.setBizAlias(bizAlias);
        initOptionData.setProps(props);
        return  initOptionData;
    }

}
