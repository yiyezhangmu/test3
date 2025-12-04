package com.coolcollege.intelligent.model.video;

import lombok.Data;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2021/10/12
 */
@Data
public class SmallVideoInfoDTO {

    /**
     * 视频集合
     */
    private List<SmallVideoDTO> videoList;

    /**
     * 音频合计
     */
    private List<String> soundRecordingList;

}
