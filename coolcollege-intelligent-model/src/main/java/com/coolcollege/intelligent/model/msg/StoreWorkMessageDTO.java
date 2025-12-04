package com.coolcollege.intelligent.model.msg;

import lombok.Data;

/**
 * @author wxp
 * @FileName: StoreWorkMessageDTO
 * @Description:
 * @date 2022-10-13 16:46
 */
@Data
public class StoreWorkMessageDTO {

    private String title;

    private String content;

    public StoreWorkMessageDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
