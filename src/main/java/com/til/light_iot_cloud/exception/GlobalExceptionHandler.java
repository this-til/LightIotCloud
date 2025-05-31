package com.til.light_iot_cloud.exception;

import com.til.light_iot_cloud.data.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public Result<?> handler(Exception e) {
        logger.error("An exception occurred: ", e);
        return Result.error(e.getClass().getName() + ":" + e.getMessage());
    }

}
