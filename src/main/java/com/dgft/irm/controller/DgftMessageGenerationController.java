package com.dgft.irm.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.service.DgftMessageGenerationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dgft/messages")
@RequiredArgsConstructor
public class DgftMessageGenerationController {

    private final DgftMessageGenerationService dgftMessageGenerationService;

    @PostMapping("/generate-json")
    public DgftIrmOutboundRequestDto generateJson() {
        return dgftMessageGenerationService.generateFreshIrmJson();
    }
}