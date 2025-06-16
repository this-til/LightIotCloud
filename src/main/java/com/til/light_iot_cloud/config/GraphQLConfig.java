package com.til.light_iot_cloud.config;

import com.til.light_iot_cloud.handler.ExtendGraphQlWebSocketHandler;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.webmvc.GraphQlWebSocketHandler;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.*;

@Configuration
public class GraphQLConfig {

    public static final GraphQLScalarType BYTE_ARRAY = GraphQLScalarType.newScalar()
            .name("Bytes") // 标量类型名称
            .description("Base64 encoded byte array") // 描述
            .coercing(new Coercing<byte[], String>() {
                @Override
                public @Nullable String serialize(@NotNull Object data, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingSerializeException {
                    if (data instanceof byte[]) {
                        return Base64.getEncoder().encodeToString((byte[]) data);
                    }
                    throw new CoercingSerializeException("Expected byte[]");
                }

                @Override
                public byte @Nullable [] parseValue(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseValueException {
                    if (input instanceof StringValue) {
                        String base64Str = ((StringValue) input).getValue();
                        return Base64.getDecoder().decode(base64Str);
                    }
                    throw new CoercingParseLiteralException("Expected Base64 string");
                }

                @Override
                public byte @Nullable [] parseLiteral(@NotNull Value<?> input, @NotNull CoercedVariables variables, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) throws CoercingParseLiteralException {
                    if (input instanceof StringValue) {
                        String base64Str = ((StringValue) input).getValue();
                        return Base64.getDecoder().decode(base64Str);
                    }
                    throw new CoercingParseLiteralException("Expected Base64 string");
                }

                @Override
                public @NotNull Value<?> valueToLiteral(@NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale) {
                    String s = serialize(input, graphQLContext, locale);
                    return StringValue.newStringValue(s).build();
                }
            })
            .build();

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            wiringBuilder.scalar(ExtendedScalars.DateTime);
            wiringBuilder.scalar(ExtendedScalars.Json);
            wiringBuilder.scalar(BYTE_ARRAY);
        };
    }

    @Bean
    public GraphQlWebSocketHandler graphQlWebSocketHandler(WebGraphQlHandler webGraphQlHandler,
                                                           GraphQlProperties properties, HttpMessageConverters converters) {
        return new ExtendGraphQlWebSocketHandler(this, webGraphQlHandler, converters, properties);
    }

    public GenericHttpMessageConverter<Object> getJsonConverter(HttpMessageConverters converters) {
        return converters.getConverters()
                .stream()
                .filter(this::canReadJsonMap)
                .findFirst()
                .map(this::asGenericHttpMessageConverter)
                .orElseThrow(() -> new IllegalStateException("No JSON converter"));
    }


    private boolean canReadJsonMap(HttpMessageConverter<?> candidate) {
        return candidate.canRead(Map.class, MediaType.APPLICATION_JSON);
    }

    @SuppressWarnings("unchecked")
    private GenericHttpMessageConverter<Object> asGenericHttpMessageConverter(HttpMessageConverter<?> converter) {
        return (GenericHttpMessageConverter<Object>) converter;
    }

}