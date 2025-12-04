package com.coolcollege.intelligent.facade.passenger;

import com.coolcollege.intelligent.facade.dto.BaseResultDTO;
import com.coolcollege.intelligent.facade.dto.passenger.PassengerSyncDTO;

/**
 * @Author suzhuhong
 * @Date 2023/2/20 16:56
 * @Version 1.0
 */
public interface PassengerFacade {

    /**
     * 云眸客流同步
     * @param passengerSyncDTO
     * @return
     */
    BaseResultDTO passengerSync(PassengerSyncDTO passengerSyncDTO);
}
