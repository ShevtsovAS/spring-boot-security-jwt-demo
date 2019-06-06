package com.spring.boot.security.jwt.example.demo.controller;

import com.spring.boot.security.jwt.example.demo.model.Dictionary;
import com.spring.boot.security.jwt.example.demo.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.spring.boot.security.jwt.example.demo.controller.DictionaryController.DICTIONARY_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(DICTIONARY_API)
public class DictionaryController {
    static final String DICTIONARY_API = "/api/v1/dictionaries";

    private final DictionaryService dictionaryService;

    @GetMapping("/roles")
    public ResponseEntity<Dictionary> getRolesDictionary() {
        return ResponseEntity.ok(dictionaryService.getRolesDictionary());
    }
}
