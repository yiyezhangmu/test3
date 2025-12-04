package com.coolcollege.intelligent.service.help.impl;

import com.coolcollege.intelligent.common.util.ValidateUtil;
import com.coolcollege.intelligent.dao.help.HelpDescMapper;
import com.coolcollege.intelligent.model.help.HelpDescDO;
import com.coolcollege.intelligent.service.help.HelpDescService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/8 14:03
 */
@Service
@Slf4j
public class HelpDescServiceImpl implements HelpDescService {

    @Resource
    private HelpDescMapper helpDescMapper;
    @Override
    public void insertHelpDesc(HelpDescDO helpDescDO) {
        helpDescMapper.insertHelpDesc(helpDescDO);
    }

    @Override
    public void updateHelpDescById(HelpDescDO helpDescDO) {
        ValidateUtil.validateObj(helpDescDO.getId());
        helpDescMapper.updateHelpDescById(helpDescDO);
    }

    @Override
    public void updateHelpDescByPath(HelpDescDO helpDescDO) {
        ValidateUtil.validateObj(helpDescDO.getPath());
        helpDescMapper.updateHelpDescByPath(helpDescDO);
    }

    @Override
    public void deleteHelpDescById(Long id) {
        ValidateUtil.validateObj(id);
        helpDescMapper.deleteHelpDescById(id);
    }

    @Override
    public List<HelpDescDO> getHelpDesc(Long id, String path) {
        HelpDescDO descDO = new HelpDescDO();
        descDO.setId(id);
        descDO.setPath(path);
        return helpDescMapper.getHelpDesc(descDO);
    }
}
