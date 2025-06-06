package com.til.light_iot_cloud.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SchemaResult {

    List<ExceptionInfo> errors;
    Object data;

    public SchemaResult(Exception exception) {
        this(List.of(exception));
    }

    public SchemaResult(List<Exception> errors) {
        this.errors = errors.stream().map(ExceptionInfo::new).collect(Collectors.toList());
    }

    @Data
    public static class ExceptionInfo {
        String message;
        List<Void> locations = List.of();
        List<Void> path = List.of();
        Extensions extensions;

        public ExceptionInfo(Exception e) {
            this.message = e.getMessage();
            this.extensions = new Extensions(e.getClass().getSimpleName());
        }

        @Data
        public static class Extensions {
            String classification;

            public Extensions(String classification) {
                this.classification = classification;
            }
        }
    }


}
