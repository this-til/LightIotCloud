package com.til.light_iot_cloud.exception;

import com.til.light_iot_cloud.data.Result;
import com.til.light_iot_cloud.data.SchemaResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public SchemaResult handler(Exception e) {
        logger.error("An exception occurred: ", e);
        return new SchemaResult(e);
        /*//noinspection DataFlowIssue
        return Map.of(
                "errors", List.of(
                        Map.of(
                                "message", e.getMessage(),
                                "extensions", Map.of(
                                        "classification", "INTERNAL_ERROR"
                                )
                        )
                ),
                "data", null
        );*/
    }

}
