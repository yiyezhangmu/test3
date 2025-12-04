package com.coolcollege.intelligent.model.qyy;

import com.coolcollege.intelligent.model.storework.vo.WeeklyNewspaperCountVO;
import jdk.internal.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyWeeklyCountDO implements Serializable {
    private List<String> nameList;

    private String rate;

    private Integer openNum;

    private Integer closeNum;

    private List<WeeklyNewspaperCountVO> hqData;


}
