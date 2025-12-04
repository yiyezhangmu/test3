package com.coolcollege.intelligent.facade.setting;

import com.coolcollege.intelligent.model.setting.dto.SettingDTO;
import com.coolcollege.intelligent.model.setting.vo.SettingVO;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/04/08
 */
@Service
public class SettingFacade {

    public List<SettingDTO> facadeGetVideoSetting(List<SettingVO> vo) {
        return ListUtils.emptyIfNull(vo)
                .stream()
                .map(this::mapSettingDTO)
                .collect(Collectors.toList());
    }

    public SettingDTO facadeGetVideoSetting1(List<SettingVO> vo) {
         return ListUtils.emptyIfNull(vo)
                .stream()
                .map(this::mapSettingDTO)
                .findFirst().orElse(null);
    }

    private SettingDTO mapSettingDTO(SettingVO vo) {
        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setEid(vo.getEid());
        settingDTO.setYunType(vo.getYunType());
        settingDTO.setAliyunCorpId(vo.getAliyunCorpId());
        settingDTO.setOpenVideoStreaming(vo.getOpenVideoStreaming());
        settingDTO.setRootVdsCorpId(vo.getRootVdsCorpId());
        settingDTO.setOpenDataAnalysis(vo.getOpenDataAnalysis());
        settingDTO.setOpenAlarmEvent(vo.getOpenAlarmEvent());
        settingDTO.setOpenYunControl(vo.getOpenYunControl());
        settingDTO.setVideoPlaybackType(vo.getVideoPlaybackType());
        settingDTO.setHasOpen(vo.getHasOpen());
        return settingDTO;
    }

}
