package com.dgft.irm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.IrmMessageMasterResponseDto;
import com.dgft.irm.service.IrmMessageMasterService;

import java.util.List;

/**
 * Read access to dgft_irm_message_master. This table is populated by the
 * downstream "DGFT IRM Push Service Scheduler" (step 2 of your flow), which
 * is a separate job from CSV ingestion - happy to build that job next.
 */
@RestController
@RequestMapping("/api/dgft/irm-message-master")
@RequiredArgsConstructor
public class IrmMessageMasterController {

    private final IrmMessageMasterService irmMessageMasterService;

    @GetMapping
    public ApiResponseDto<List<IrmMessageMasterResponseDto>> getAll() {
        return ApiResponseDto.ok("Fetched", irmMessageMasterService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponseDto<IrmMessageMasterResponseDto> getById(@PathVariable String id) {
        return ApiResponseDto.ok("Fetched", irmMessageMasterService.getById(id));
    }
}
