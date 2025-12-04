package com.coolcollege.intelligent.facade.dto.openApi.video;

import lombok.Data;

import java.util.List;

/**
 * @author byd
 * @date 2023-07-31 15:31
 */
@Data
public class LiveWatchUserListApiDTO {

    public List<LiveUserApiDTO> orgUsesList;

    public List<OutOrgUserApiDTO> outOrgUserList;
}
