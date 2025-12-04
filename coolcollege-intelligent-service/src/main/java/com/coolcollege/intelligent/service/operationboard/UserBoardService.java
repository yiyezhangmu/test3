package com.coolcollege.intelligent.service.operationboard;

import com.coolcollege.intelligent.model.operationboard.dto.PatrolTypeStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.TaskStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserDetailStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.dto.UserStatisticsDTO;
import com.coolcollege.intelligent.model.operationboard.query.UserDetailStatisticsQuery;
import com.coolcollege.intelligent.model.operationboard.query.UserStatisticsQuery;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/1/8 11:35
 * @Description 运营看板service
 */
public interface UserBoardService {

    UserStatisticsDTO userStatistics(String enterpriseId, UserStatisticsQuery userStatisticsQuery);

    PageInfo<UserDetailStatisticsDTO> userDetailStatistics(String enterpriseId, UserDetailStatisticsQuery query);

    TaskStatisticsDTO taskStatistics(String enterpriseId,UserStatisticsQuery userStatisticsQuery);

    PatrolTypeStatisticsDTO patrolTypeStatistics(String enterpriseId, UserStatisticsQuery query);

}
