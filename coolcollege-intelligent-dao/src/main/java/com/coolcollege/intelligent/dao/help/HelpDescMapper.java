package com.coolcollege.intelligent.dao.help;

import com.coolcollege.intelligent.model.help.HelpDescDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/8 13:42
 */
@Mapper
public interface HelpDescMapper {

    /**
     * 插入
     * @param helpDescDO
     */
    void insertHelpDesc(@Param("help") HelpDescDO helpDescDO);

    /**
     * 更新
     * @param helpDescDO
     * @param helpDescDO
     */
    void updateHelpDescById(@Param("help") HelpDescDO helpDescDO);

    /**
     * 更新
     * @param helpDescDO
     * @param helpDescDO
     */
    void updateHelpDescByPath(@Param("help") HelpDescDO helpDescDO);

    /**
     * 删除
     * @param id
     */
    void deleteHelpDescById( @Param("id") Long id);

    /**
     * 查询
     * @param helpDescDO
     * @return
     */
    List<HelpDescDO> getHelpDesc(@Param("help") HelpDescDO helpDescDO);
}
