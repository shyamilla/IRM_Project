package com.dgft.irm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

//  Response DTO containing the summary of a CSV ingestion process.
//   Includes processing statistics, ACK file location, and record-wise results.
@Data
@AllArgsConstructor
public class IngestionSummaryResponseDto {
    private String fileName;
    private int totalRecords;
    private long passCount;
    private long failCount;
    private String ackFilePath;
    private List<RecordResultDto> results;
}
