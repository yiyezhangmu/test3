package com.coolcollege.intelligent.model.region.dto;

import java.util.List;

public class RegionSearchRespDTO {
    private List<RegionNode> regions;

    public RegionSearchRespDTO(List<RegionNode> regions) {
        this.regions = regions;
    }

    public List<RegionNode> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionNode> regions) {
        this.regions = regions;
    }
}
