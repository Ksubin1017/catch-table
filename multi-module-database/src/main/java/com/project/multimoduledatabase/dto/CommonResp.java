package com.project.multimoduledatabase.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class CommonResp<T> {
    private int code;
    private String message;
    private T data;

    @Builder
    public CommonResp(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

}
