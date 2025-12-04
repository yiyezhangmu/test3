package com.coolcollege.intelligent.model.position.dto;

import java.util.List;

public class PositionSearchRespDTO {
    private List<PositionDTO> positions;

    public PositionSearchRespDTO(List<PositionDTO> positions) {
        this.positions = positions;
    }

    public List<PositionDTO> getPositions() {
        return positions;
    }

    public void setPositions(List<PositionDTO> positions) {
        this.positions = positions;
    }
}
