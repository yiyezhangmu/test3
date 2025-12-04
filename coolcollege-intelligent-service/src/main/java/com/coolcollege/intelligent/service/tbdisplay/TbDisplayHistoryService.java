package com.coolcollege.intelligent.service.tbdisplay;


import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayHistoryVO;

import java.util.List;

/**
 *
 * @author wxp
 */
public interface TbDisplayHistoryService {

    List<TbDisplayHistoryVO> listHistoryByTaskSubId(String enterpriseId, Long taskSubId);

}
