package com.dgft.irm.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DgftIrmOutboundRequestDto {

    private String uniqueTxId;

    private List<DgftIrmDto> irmList;
}
