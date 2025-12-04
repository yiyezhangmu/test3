package com.coolcollege.intelligent.dao.importexcel;

import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.ImportDistinctDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/9 17:58
 */
@Mapper
public interface ImportTaskMapper {

    Integer insert(@Param("eid") String eid, @Param("task")ImportTaskDO task);

    List<ImportTaskDO> getAllImportTask(@Param("eid") String eid, @Param("fileType") String fileType,
                                        @Param("userId") String userId, @Param("isImport") Boolean isImport,
                                        @Param("status") Integer status);

    Integer update(@Param("eid") String eid, @Param("task")ImportTaskDO task);

    List<ImportDistinctDTO> getUniqueFieldByType(@Param("eid") String eid, @Param("fileType") String fileType);

    ImportTaskDO getImportTaskById(@Param("eid") String eid, @Param("id")Long id);
}
