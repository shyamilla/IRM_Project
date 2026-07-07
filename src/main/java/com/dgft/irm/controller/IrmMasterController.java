package com.dgft.irm.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.IrmMasterResponseDto;
import com.dgft.irm.serviceimpl.IrmMasterService;

import java.util.List;

@RestController
@RequestMapping("/api/dgft/irm-master")
@RequiredArgsConstructor
public class IrmMasterController {

    private final IrmMasterService irmMasterService;

    @GetMapping
    public ApiResponseDto<List<IrmMasterResponseDto>> getAll() {
        return ApiResponseDto.ok("Fetched", irmMasterService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponseDto<IrmMasterResponseDto> getById(@PathVariable String id) {
        return ApiResponseDto.ok("Fetched", irmMasterService.getById(id));
    }

    @GetMapping("/by-irm-number/{irmNumber}")
    public ApiResponseDto<IrmMasterResponseDto> getByIrmNumber(@PathVariable String irmNumber) {
        return ApiResponseDto.ok("Fetched", irmMasterService.getByIrmNumber(irmNumber));
    }

    @GetMapping("/by-status/{status}")
    public ApiResponseDto<List<IrmMasterResponseDto>> getByStatus(@PathVariable String status) {
        return ApiResponseDto.ok("Fetched", irmMasterService.getByStatus(status));
    }
}
