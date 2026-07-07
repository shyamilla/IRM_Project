package com.dgft.irm.service;


import java.io.InputStream;
import java.util.List;

import com.dgft.irm.dto.response.IngestionSummaryResponseDto;

public interface IrmIngestionService {

    List<IngestionSummaryResponseDto> processInboundFolder();

    IngestionSummaryResponseDto processUploadedFile(String fileName, InputStream inputStream);
}