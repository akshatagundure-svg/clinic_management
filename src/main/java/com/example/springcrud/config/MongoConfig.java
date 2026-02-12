package com.example.springcrud.config;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        // Handle Date types from MongoDB
        converters.add(new DateToOffsetDateTimeConverter());
        converters.add(new OffsetDateTimeToDateConverter());
        // Handle String types from MongoDB (The fix for your current error)
        converters.add(new StringToOffsetDateTimeConverter());
        return new MongoCustomConversions(converters);
    }

    // 1. Convert MongoDB Date -> Java OffsetDateTime
    class DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(Date source) {
            return source.toInstant().atOffset(java.time.ZoneOffset.UTC);
        }
    }

    // 2. Convert Java OffsetDateTime -> MongoDB Date
    class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {
        @Override
        public Date convert(OffsetDateTime source) {
            return Date.from(source.toInstant());
        }
    }

    // 3. FIX: Convert String -> Java OffsetDateTime
    class StringToOffsetDateTimeConverter implements Converter<String, OffsetDateTime> {
        @Override
        public OffsetDateTime convert(String source) {
            // This handles the "2024-04-30T18:30:00.000+00:00" format
            return OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
    }
}