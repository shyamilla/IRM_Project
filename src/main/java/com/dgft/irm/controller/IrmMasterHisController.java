package com.dgft.irm.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.IrmMasterHisResponseDto;
import com.dgft.irm.service.IrmMasterHisService;

import java.util.List;

@RestController
@RequestMapping("/api/dgft/irm-master-history")
@RequiredArgsConstructor
public class IrmMasterHisController {

    private final IrmMasterHisService irmMasterHisService;

    @GetMapping("/{irmId}")
    public ApiResponseDto<List<IrmMasterHisResponseDto>> getHistory(@PathVariable String irmId) {
        return ApiResponseDto.ok("Fetched", irmMasterHisService.getHistoryByIrmId(irmId));
    }
}
