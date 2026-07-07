package com.dgft.irm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.IrmMsgTxStatusLogResponseDto;
import com.dgft.irm.service.IrmMsgTxStatusLogService;

import java.util.List;

@RestController
@RequestMapping("/api/dgft/irm-msg-tx-status-log")
@RequiredArgsConstructor
public class IrmMsgTxStatusLogController {

    private final IrmMsgTxStatusLogService irmMsgTxStatusLogService;

    @GetMapping("/by-message-master/{messageMasterId}")
    public ApiResponseDto<List<IrmMsgTxStatusLogResponseDto>> getByMessageMasterId(@PathVariable String messageMasterId) {
        return ApiResponseDto.ok("Fetched", irmMsgTxStatusLogService.getByMessageMasterId(messageMasterId));
    }
}
