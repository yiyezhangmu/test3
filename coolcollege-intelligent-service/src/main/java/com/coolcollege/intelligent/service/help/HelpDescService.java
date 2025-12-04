package com.coolcollege.intelligent.service.help;

import com.coolcollege.intelligent.model.help.HelpDescDO;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/8 14:02
 */
public interface HelpDescService {

    /**
     * 插入
     * @param helpDescDO
     * @param user
     */
    void insertHelpDesc(HelpDescDO helpDescDO);

    /**
     * 更新
     * @param helpDescDO
     */
    void updateHelpDescById(HelpDescDO helpDescDO);

    /**
     * 更新
     * @param helpDescDO
     */
    void updateHelpDescByPath(HelpDescDO helpDescDO);

    /**
     * 删除
     * @param id
     */
    void deleteHelpDescById(Long id);

    /**
     * 查询
     * @param id
     * @param path
     * @return
     */
    List<HelpDescDO> getHelpDesc(Long id, String path);

}
