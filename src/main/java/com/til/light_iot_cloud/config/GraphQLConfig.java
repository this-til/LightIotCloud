package com.til.light_iot_cloud.config;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.Base64;
import java.util.Locale;

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
            wiringBuilder.scalar(BYTE_ARRAY);
        };
    }


}