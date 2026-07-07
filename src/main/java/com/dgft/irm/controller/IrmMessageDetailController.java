package com.dgft.irm.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.IrmMessageDetailResponseDto;
import com.dgft.irm.service.IrmMessageDetailService;

import java.util.List;

@RestController
@RequestMapping("/api/dgft/irm-message-detail")
@RequiredArgsConstructor
public class IrmMessageDetailController {

    private final IrmMessageDetailService irmMessageDetailService;

    @GetMapping("/by-message-master/{messageMasterId}")
    public ApiResponseDto<List<IrmMessageDetailResponseDto>> getByMessageMasterId(@PathVariable String messageMasterId) {
        return ApiResponseDto.ok("Fetched", irmMessageDetailService.getByMessageMasterId(messageMasterId));
    }

    @GetMapping("/by-irm-number/{irmNumber}")
    public ApiResponseDto<List<IrmMessageDetailResponseDto>> getByIrmNumber(@PathVariable String irmNumber) {
        return ApiResponseDto.ok("Fetched", irmMessageDetailService.getByIrmNumber(irmNumber));
    }
}
