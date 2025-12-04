package com.coolcollege.intelligent.model.metatable.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SopDTO {

    private String id;

    private String nodeName;

    private String nodePath;

    private String sopType;

    private int countNum;

    private String connectPath;

    private List<SopDTO> subSopList = new ArrayList<>();

}
