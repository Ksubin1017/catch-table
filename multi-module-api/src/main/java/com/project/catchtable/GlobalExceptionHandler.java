package com.project.catchtable;

import com.project.multimoduledatabase.common.CommonMessage;
import com.project.multimoduledatabase.common.CommonResp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResp<Object>> handleException(Exception e) {

        // 로그 출력
        e.printStackTrace();

        CommonResp<Object> resp = new CommonResp<>(
                1001,
                CommonMessage.CALL_WAITING_FAIL,
                null
        );

        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
