package com.coolcollege.intelligent.mapper.user;

import com.coolcollege.intelligent.dao.user.UserCollectStoreMapper;
import com.coolcollege.intelligent.model.user.UserCollectStoreDO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: UserCollectStoreDAO
 * @Description:
 * @date 2022-12-20 11:45
 */
@Service
public class UserCollectStoreDAO {

    @Resource
    private UserCollectStoreMapper userCollectStoreMapper;

    /**
     * 新增收藏门店
     * @param enterpriseId
     * @param userId
     * @param storeId
     */
    public void addUserCollectStore(String enterpriseId, String userId, String storeId){
        UserCollectStoreDO insert = new UserCollectStoreDO();
        insert.setUserId(userId);
        insert.setStoreId(storeId);
        userCollectStoreMapper.insertSelective(insert, enterpriseId);
    }


    /**
     * 删除收藏门店
     * @param enterpriseId
     * @param userId
     * @param storeId
     */
    public void deleteUserCollectStore(String enterpriseId, String userId, String storeId){
        userCollectStoreMapper.deleteUserCollectStore(enterpriseId, userId, storeId);
    }

    /**
     * 获取用户收藏的门店
     * @param enterpriseId
     * @param userId
     * @return
     */
    public List<UserCollectStoreDO> getUserCollectStore(String enterpriseId, String userId){
        return userCollectStoreMapper.getUserCollectStore(enterpriseId, userId);
    }

}
