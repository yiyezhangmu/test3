package com.coolcollege.intelligent.dao.metatable;

import java.io.Serializable;

import org.apache.ibatis.annotations.Param;

public interface MyBatisBaseDao<Model, PK extends Serializable> {

    int deleteByPrimaryKey(@Param("enterpriseId")String enterpriseId,PK id);

    int insert(@Param("enterpriseId") String enterpriseId, @Param("model") Model record);

    int insertSelective(@Param("enterpriseId")String enterpriseId,@Param("model") Model record);

    Model selectByPrimaryKey(@Param("enterpriseId")String enterpriseId,PK id);

    int updateByPrimaryKey(@Param("enterpriseId")String enterpriseId,@Param("model") Model record);

    int updateByPrimaryKeySelective(@Param("enterpriseId")String enterpriseId,@Param("model") Model record);

//    long countByExample( @Param("example") E example,@Param("enterpriseId")String enterpriseId);
//    int deleteByExample(@Param("enterpriseId")String enterpriseId, @Param("example") E example);
//    int updateByExampleSelective(@Param("enterpriseId")String enterpriseId,@Param("record") Model record, @Param("example") E example);
//    List<Model> selectByExample(@Param("enterpriseId")String enterpriseId, @Param("example") E example);
//    int updateByExample(@Param("enterpriseId")String enterpriseId,@Param("record") Model record, @Param("example") E example);
}