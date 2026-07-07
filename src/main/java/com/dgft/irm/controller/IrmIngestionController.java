package com.dgft.irm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.dgft.irm.dto.response.ApiResponseDto;
import com.dgft.irm.dto.response.IngestionSummaryResponseDto;
import com.dgft.irm.exception.FileProcessingException;
import com.dgft.irm.service.IrmIngestionService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/dgft/irm-ingestion")
@RequiredArgsConstructor
public class IrmIngestionController {

    private final IrmIngestionService irmIngestionService;

    /** Manually trigger processing of whatever CSV files are currently in the inbound folder. */
    @PostMapping("/trigger")
    public ApiResponseDto<List<IngestionSummaryResponseDto>> triggerInboundProcessing() {
        return ApiResponseDto.ok("Inbound folder processed", irmIngestionService.processInboundFolder());
    }

    /** Upload + process a single CSV synchronously (bypasses the Quartz schedule). */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResponseDto<IngestionSummaryResponseDto> uploadAndProcess(@RequestParam("file") MultipartFile file) {
        try {
            IngestionSummaryResponseDto summary =
                    irmIngestionService.processUploadedFile(file.getOriginalFilename(), file.getInputStream());
            return ApiResponseDto.ok("File processed", summary);
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read uploaded file", e);
        }
    }
}
