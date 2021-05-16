package com.dhl.pizer.vo;

import lombok.Data;

@Data
public class DiResult {

    private Integer id;

    private String source;

    private boolean status;

    private boolean valid;

    public boolean isStatus() {
        return status;
    }

    public boolean isValid() {
        return valid;
    }

}
