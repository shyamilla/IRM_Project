package com.dgft.irm.serviceimpl;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.request.DgftIrmDto;
import com.dgft.irm.dto.request.DgftIrmOutboundRequestDto;
import com.dgft.irm.entity.IrmMaster;
import com.dgft.irm.repository.IrmMasterRepository;
import com.dgft.irm.service.DgftMessageGenerationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DgftMessageGenerationServiceImpl implements DgftMessageGenerationService {

    private final IrmMasterRepository irmMasterRepository;

    private static final DateTimeFormatter DGFT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override
    @Transactional
    public DgftIrmOutboundRequestDto generateFreshIrmJson() {

        List<IrmMaster> irmRecords =
                irmMasterRepository.findTop20ByDgftFlagAndDgftStatusOrderByAddedDateAsc(
                        AppConstants.DGFT_FLAG_FRESH,
                        AppConstants.DGFT_STATUS_AWAITING_REQUEST_INITIATED
                );

        String uniqueTxId = generateUniqueTxId();

        List<DgftIrmDto> irmList = irmRecords.stream()
                .map(this::mapToDgftIrmDto)
                .toList();

        return new DgftIrmOutboundRequestDto(uniqueTxId, irmList);
    }

    private DgftIrmDto mapToDgftIrmDto(IrmMaster irm) {

        return DgftIrmDto.builder()
                .bankRefNumber(irm.getBankReferenceNumber())
                .irmIssueDate(irm.getIrmIssueDate() != null
                        ? irm.getIrmIssueDate().format(DGFT_DATE_FORMAT)
                        : null)
                .irmNumber(irm.getIrmNumber())
                .irmStatus("F")
                .ifscCode(irm.getIfscCode())
                .remittanceAdCode(irm.getAdCode())
                .remittanceDate(irm.getRemittanceDate().format(DGFT_DATE_FORMAT))
                .remittanceFCC(irm.getCurrency())
                .remittanceFCAmount(irm.getRemittanceAmount())
                .inrCreditAmount(irm.getInrCreditAmount())
                .iecCode(irm.getIeCode() == null ? "" : irm.getIeCode())
                .panNumber(irm.getPanNumber())
                .remitterName(irm.getRemitterName())
                .remitterCountry(irm.getRemitterCountry())
                .purposeOfRemittance(irm.getPurposeOfRemittance())
                .bankAccountNo(irm.getBankAccountNumber())
                .build();
    }

    private String generateUniqueTxId() {
        return "TXN" + System.currentTimeMillis();
    }
}
