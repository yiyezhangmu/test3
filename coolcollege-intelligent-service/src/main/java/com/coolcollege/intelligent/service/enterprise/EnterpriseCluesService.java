package com.coolcollege.intelligent.service.enterprise;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseCluesDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseCluesRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCluesExportVO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseCluesVO;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.EnterpriseCluesImportDTO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 企业线索
 *
 * @author chenyupeng
 * @since 2021/11/23
 */
public interface EnterpriseCluesService {

    EnterpriseCluesDTO saveEnterpriseClues(EnterpriseCluesDTO dto, BossLoginUserDTO user);

    void updateEnterpriseClues(EnterpriseCluesDTO dto, BossLoginUserDTO user);

    void deleteEnterpriseClues(Long id);

    PageInfo<EnterpriseCluesVO> listEnterpriseClues(EnterpriseCluesRequest request, BossLoginUserDTO user);

    Integer importEnterpriseClues(Future<List<EnterpriseCluesImportDTO>> importTask, String originalFilename, BossLoginUserDTO user);

    List<EnterpriseCluesExportVO> exportEnterpriseClues(EnterpriseCluesRequest request);

    Integer syncEnterprise(BossLoginUserDTO user);
}
