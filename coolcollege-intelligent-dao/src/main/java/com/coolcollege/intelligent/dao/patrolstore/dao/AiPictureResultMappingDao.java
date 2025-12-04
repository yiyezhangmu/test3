package com.coolcollege.intelligent.dao.patrolstore.dao;

import com.coolcollege.intelligent.dao.patrolstore.AiPictureResultMappingMapper;
import com.coolcollege.intelligent.model.patrolstore.AiPictureResultMappingDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/4/1
 */
@Slf4j
@Service
public class AiPictureResultMappingDao {

    @Resource
    AiPictureResultMappingMapper aiPictureResultMappingMapper;
    /**
     * 插入
     */
    public Integer insert(String enterpriseId, AiPictureResultMappingDO entity){
        return aiPictureResultMappingMapper.insert(enterpriseId,entity);
    }

    /**
     * 批量插入
     */
    public Integer batchInsert( String enterpriseId, List<AiPictureResultMappingDO> list){
        return aiPictureResultMappingMapper.batchInsert(enterpriseId,list);
    }

    /**
     * 根据图片id查询
     */
    public List<AiPictureResultMappingDO> selectByPictureIdList(String enterpriseId, List<Long> pictureIdList){
        return aiPictureResultMappingMapper.selectByPictureIdList(enterpriseId,pictureIdList);
    }
}
