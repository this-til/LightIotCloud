package com.til.light_iot_cloud.data;

import com.til.light_iot_cloud.enums.ResultType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Result<T> {
    private ResultType resultType;
    private String message;
    @Nullable
    private T data;

    public static <T> Result<T> successful(@Nullable String message) {
        return new Result<>(ResultType.SUCCESSFUL, message == null
                ? ""
                : message, null);
    }

    public static <T> Result<T> fail(@Nullable String message) {
        return new Result<>(ResultType.FAIL, message == null
                ? ""
                : message, null);
    }

    public static <T> Result<T> error(@Nullable String message) {
        return new Result<>(ResultType.ERROR, message == null
                ? ""
                : message, null);
    }

    public static <T> Result<T> ofBool(boolean success, @Nullable String successMessage, @Nullable String failureMessage) {
        return new Result<>(
                success
                        ? ResultType.SUCCESSFUL
                        : ResultType.FAIL,
                success
                        ? Objects.requireNonNullElse(successMessage, "")
                        : Objects.requireNonNullElse(failureMessage, ""),
                null
        );
    }

    public static <T> Result<T> ofBool(boolean success) {
        return ofBool(success, null, null);
    }
}
