package com.dgft.irm.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.dgft.irm.dto.request.PurposeCodeMasterRequestDto;
import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.PurposeCodeMasterResponseDto;
import com.dgft.irm.service.PurposeCodeMasterService;

import java.util.List;

/**
 * Full CRUD for dgft_purpose_code_master since this reference table needs to
 * be seeded/maintained by admins (Purpose of Remittance validation looks
 * codes up here).
 */
@RestController
@RequestMapping("/api/dgft/purpose-code-master")
@RequiredArgsConstructor
public class PurposeCodeMasterController {

    private final PurposeCodeMasterService purposeCodeMasterService;

    @PostMapping
    public ApiResponseDto<PurposeCodeMasterResponseDto> create(@Valid @RequestBody PurposeCodeMasterRequestDto request) {
        return ApiResponseDto.ok("Created", purposeCodeMasterService.create(request));
    }

    @GetMapping
    public ApiResponseDto<List<PurposeCodeMasterResponseDto>> getAll() {
        return ApiResponseDto.ok("Fetched", purposeCodeMasterService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponseDto<PurposeCodeMasterResponseDto> getById(@PathVariable String id) {
        return ApiResponseDto.ok("Fetched", purposeCodeMasterService.getById(id));
    }

    @PutMapping("/{id}")
    public ApiResponseDto<PurposeCodeMasterResponseDto> update(@PathVariable String id,
                                                                @Valid @RequestBody PurposeCodeMasterRequestDto request) {
        return ApiResponseDto.ok("Updated", purposeCodeMasterService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponseDto<Void> delete(@PathVariable String id) {
        purposeCodeMasterService.delete(id);
        return ApiResponseDto.ok("Deleted", null);
    }
}

