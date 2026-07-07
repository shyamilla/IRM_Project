package com.dgft.irm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecordResultDto {
    private int rowNumber;
    private String irmRefNumber;
    private boolean success;
    private String generatedId;      // populated only on success
    private List<String> reasons;    // populated only on failure
}