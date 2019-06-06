package com.spring.boot.security.jwt.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dictionary {

    private List<DictionaryProperty> properties;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DictionaryProperty {
        private String name;
        private String value;
    }
}
