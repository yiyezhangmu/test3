package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.request.AppMenuCustomizeRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseUserAppMenuInfoVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseUserAppMenuVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import jdk.nashorn.internal.ir.IdentNode;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
public interface EnterpriseUserAppMenuService {


    EnterpriseUserAppMenuVO getUserAppMenu(String eid);

    /**
     * 更新App二级菜单排序
     * @param eid
     * @param request
     * @return
     */
    Boolean updateUserAppMenu(String eid, AppMenuCustomizeRequest request,Integer menuLevel);

    /**
     * 获取用户自定义的菜单
     * @param enterpriseId
     * @param user
     * @param isOld
     * @return
     */
    List<EnterpriseUserAppMenuInfoVO> getUserDefinedMenuApp(String enterpriseId, CurrentUser user, Boolean isOld, Integer menuLevel);

    List<EnterpriseUserAppMenuInfoVO> getUserDefinedMenuAppNew(String enterpriseId, CurrentUser user, Boolean isOld, Integer menuLevel);

}
