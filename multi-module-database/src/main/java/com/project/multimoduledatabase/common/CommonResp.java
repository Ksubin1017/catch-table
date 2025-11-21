package com.project.multimoduledatabase.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResp<T> {
    private int code;
    private String message;
    private T data;
}
