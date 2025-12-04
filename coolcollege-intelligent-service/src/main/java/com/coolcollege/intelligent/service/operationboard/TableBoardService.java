package com.coolcollege.intelligent.service.operationboard;

import java.util.List;

import com.coolcollege.intelligent.model.operationboard.dto.TableBoardRankDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardStatisticDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TableBoardTrendDTO;
import com.coolcollege.intelligent.model.operationboard.query.TableBoardQuery;

/**
 * 方案运营看板
 * 
 * @author yezhe
 * @date 2021/01/08
 */
public interface TableBoardService {

    TableBoardStatisticDTO statistics(String enterpriseId, TableBoardQuery query);

    List<TableBoardRankDTO> rank(String enterpriseId, TableBoardQuery query);

    List<TableBoardTrendDTO> trend(String enterpriseId, TableBoardQuery query);
}
