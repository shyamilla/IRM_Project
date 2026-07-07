package com.dgft.irm.serviceimpl;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.request.IrmCsvRequestDto;
import com.dgft.irm.dto.response.IngestionSummaryResponseDto;
import com.dgft.irm.dto.response.RecordResultDto;
import com.dgft.irm.entity.IrmMaster;
import com.dgft.irm.mapper.IrmMasterHisMapper;
import com.dgft.irm.repository.IrmMasterHisRepository;
import com.dgft.irm.repository.IrmMasterRepository;
import com.dgft.irm.repository.PurposeCodeMasterRepository;
import com.dgft.irm.service.AckFileWriterService;
import com.dgft.irm.service.CsvFileReaderService;
import com.dgft.irm.service.IrmIngestionService;
import com.dgft.irm.util.IdGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class IrmIngestionServiceImpl implements IrmIngestionService {

    private final CsvFileReaderService csvFileReaderService;
    private final AckFileWriterService ackFileWriterService;
    private final IrmMasterRepository irmMasterRepository;
    private final IrmMasterHisRepository irmMasterHisRepository;
    private final PurposeCodeMasterRepository purposeCodeMasterRepository;
    private final Validator validator;

    @Value("${dgft.csv.batch-size}")
    private int batchSize;

    @Value("${dgft.csv.date-format}")
    private String dateFormatPattern;

    @Value("${dgft.validation.cutoff-date}")
    private String cutoffDateStr;

    @Value("${dgft.audit.system-user}")
    private String systemUser;

    @Override
    public List<IngestionSummaryResponseDto> processInboundFolder() {

        List<IngestionSummaryResponseDto> summaries = new ArrayList<>();

        try {
            List<Path> files = csvFileReaderService.listInboundFiles();

            if (files.isEmpty()) {
                log.info("No inbound CSV files found.");
                return summaries;
            }

            for (Path file : files) {
                List<IrmCsvRequestDto> records = csvFileReaderService.parse(file);

                IngestionSummaryResponseDto summary = processFile(
                        file.getFileName().toString(),
                        records,
                        file);

                summaries.add(summary);
            }

        } catch (IOException exception) {
            log.error("Failed while processing inbound folder", exception);
        }

        return summaries;
    }

    @Override
    public IngestionSummaryResponseDto processUploadedFile(String fileName, InputStream inputStream) {

        try {
            List<IrmCsvRequestDto> records = csvFileReaderService.parse(inputStream);
            return processFile(fileName, records, null);

        } catch (IOException exception) {
            log.error("Failed to parse uploaded file: {}", fileName, exception);

            return new IngestionSummaryResponseDto(
                    fileName,
                    0,
                    0,
                    0,
                    null,
                    Collections.emptyList());
        }
    }

    private IngestionSummaryResponseDto processFile(
            String fileName,
            List<IrmCsvRequestDto> records,
            Path sourcePathOrNull) {

        log.info("Processing file: {} with {} records", fileName, records.size());

        List<RecordResultDto> allResults = new ArrayList<>();

        int end = Math.min(batchSize, records.size());

        List<IrmCsvRequestDto> batch = records.subList(0, end);

        List<IrmCsvRequestDto> remaining = records.subList(end, records.size());
        log.info("=====================================");
        log.info("Batch Size Property : {}", batchSize);
        log.info("Total Records       : {}", records.size());
        log.info("Batch Records       : {}", batch.size());
        log.info("Remaining Records   : {}", remaining.size());
        log.info("=====================================");

        allResults.addAll(processBatch(batch, fileName));

        String ackFilePath = null;

        try {
            ackFilePath = ackFileWriterService.writeAck(fileName, allResults).toString();
        } catch (IOException exception) {
            log.error("Failed to write ACK file for {}", fileName, exception);
        }

        // if (sourcePathOrNull != null) {
        // try {
        // if (remaining.isEmpty()) {
        // csvFileReaderService.moveToProcessed(sourcePathOrNull);
        // log.info("File {} fully processed and moved to processed folder", fileName);
        // } else {
        // csvFileReaderService.rewriteFile(sourcePathOrNull, remaining);
        // log.info("File {} partially processed. Remaining records: {}", fileName,
        // remaining.size());
        // }
        // } catch (IOException exception) {
        // log.error("Failed to update source file after batch processing: {}",
        // fileName, exception);
        // }
        // }

        // if (sourcePathOrNull != null) {
        // try {
        // csvFileReaderService.moveToProcessed(sourcePathOrNull);
        // } catch (IOException exception) {
        // log.error("Failed to move file to processed folder: {}", fileName,
        // exception);
        // }
        // }

        if (sourcePathOrNull != null) {
            try {
                if (remaining.isEmpty()) {
                    csvFileReaderService.moveToProcessed(sourcePathOrNull);
                    log.info("File {} fully processed and moved to processed folder", fileName);
                } else {
                    csvFileReaderService.rewriteFile(sourcePathOrNull, remaining);
                    log.info("File {} partially processed. Remaining records: {}", fileName, remaining.size());
                }
            } catch (IOException exception) {
                log.error("Failed to update source file after batch processing: {}", fileName, exception);
            }
        }

        long passCount = allResults.stream()
                .filter(RecordResultDto::isSuccess)
                .count();

        long failCount = allResults.size() - passCount;

        return new IngestionSummaryResponseDto(
                fileName,
                allResults.size(),
                passCount,
                failCount,
                ackFilePath,
                allResults);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<RecordResultDto> processBatch(List<IrmCsvRequestDto> batch, String fileName) {

        List<RecordResultDto> results = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);

        for (IrmCsvRequestDto record : batch) {

            List<String> errors = validateRecord(record, formatter);

            if (!errors.isEmpty()) {
                results.add(new RecordResultDto(
                        record.getRowNumber(),
                        record.getIrmRefNumber(),
                        false,
                        null,
                        errors));
                continue;
            }

            try {
                LocalDateTime now = LocalDateTime.now();

                // Create a new IRM Master entity to store the validated CSV record.
                IrmMaster entity = new IrmMaster();

                // Generate a unique ID for the IRM record.
                entity.setId(IdGenerator.next());

                // Map validated CSV fields to the IRM Master entity.
                entity.setIrmNumber(record.getIrmRefNumber());
                entity.setRemittanceAmount(new BigDecimal(record.getRemittanceAmount().trim()));
                entity.setRemittanceDate(LocalDate.parse(record.getRemittanceDate().trim(), formatter));
                entity.setAdCode(record.getAdCode());
                entity.setCurrency(record.getRemittanceCurrency());
                entity.setIeCode(record.getIeCode());
                entity.setRemitterName(record.getRemitterName());
                entity.setRemitterCountry(record.getRemitterCountry());
                entity.setPurposeOfRemittance(record.getPurposeOfRemittance());
                entity.setIfscCode(record.getIfscCode());

                // INR Credit Amount is optional, so populate only when available.
                if (record.getInrCreditAmount() != null && !record.getInrCreditAmount().isBlank()) {
                    entity.setInrCreditAmount(new BigDecimal(record.getInrCreditAmount().trim()));
                }

                entity.setPanNumber(record.getPanNumber());
                entity.setBankReferenceNumber(record.getBankRefNumber());
                entity.setBankAccountNumber(record.getBankAccNum());

                // IRM Issue Date is optional, so parse only if provided.
                if (record.getRemittanceIssueDate() != null && !record.getRemittanceIssueDate().isBlank()) {
                    entity.setIrmIssueDate(LocalDate.parse(record.getRemittanceIssueDate().trim(), formatter));
                }

                // Initialize default workflow and DGFT statuses for a newly uploaded IRM.
                entity.setFlag(AppConstants.FLAG_NEW_WITH_WORKFLOW);
                entity.setStatus(AppConstants.STATUS_ACTIVE);
                entity.setDgftFlag(AppConstants.DGFT_FLAG_FRESH);
                entity.setDgftStatus(AppConstants.DGFT_STATUS_AWAITING_REQUEST_INITIATED);

                // Store audit information for traceability.
                entity.setInboundFileName(fileName);
                entity.setAddedBy(systemUser);
                entity.setAddedDate(now);

                // Save the validated IRM record as the current (live) record.
                irmMasterRepository.save(entity);

                // Save a snapshot of the newly created IRM into the history table.
                // This preserves the initial state for audit and future tracking.
                irmMasterHisRepository.save(
                        IrmMasterHisMapper.fromMaster(
                                entity,
                                AppConstants.TRIGGER_STATUS_NEW_RECORD_INSERTED,
                                IdGenerator.next(),
                                now));

                // Record successful processing for ACK file generation.
                results.add(new RecordResultDto(
                        record.getRowNumber(),
                        record.getIrmRefNumber(),
                        true,
                        entity.getId(),
                        null));

            } catch (Exception exception) {
                log.error("Unexpected error while inserting IRM {}", record.getIrmRefNumber(), exception);

                results.add(new RecordResultDto(
                        record.getRowNumber(),
                        record.getIrmRefNumber(),
                        false,
                        null,
                        List.of("Unexpected error during insert: " + exception.getMessage())));
            }
        }

        return results;
    }

    private List<String> validateRecord(IrmCsvRequestDto record, DateTimeFormatter formatter) {

        List<String> errors = new ArrayList<>();

        Set<ConstraintViolation<IrmCsvRequestDto>> violations = validator.validate(record);

        for (ConstraintViolation<IrmCsvRequestDto> violation : violations) {
            errors.add(violation.getMessage());
        }

        if (!errors.isEmpty()) {
            return errors;
        }

        try {
            BigDecimal remittanceAmount = new BigDecimal(record.getRemittanceAmount().trim());

            if (remittanceAmount.compareTo(BigDecimal.ONE) <= 0) {
                errors.add("Remittance Amount must be greater than 1");
            }

        } catch (NumberFormatException exception) {
            errors.add("Remittance Amount must be numeric");
        }

        LocalDate remittanceDate = null;
        LocalDate irmIssueDate = null;

        try {
            remittanceDate = LocalDate.parse(record.getRemittanceDate().trim(), formatter);

            if (remittanceDate.isAfter(LocalDate.now())) {
                errors.add("Remittance Date cannot be in the future");
            }

            LocalDate cutoffDate = LocalDate.parse(cutoffDateStr);

            if (remittanceDate.isBefore(cutoffDate)) {
                errors.add("Remittance Date cannot be before cutoff date " + cutoffDateStr);
            }

        } catch (DateTimeParseException exception) {
            errors.add("Remittance Date is not a valid date. Expected format is " + dateFormatPattern);
        }

        if (record.getRemittanceIssueDate() != null && !record.getRemittanceIssueDate().isBlank()) {
            try {
                irmIssueDate = LocalDate.parse(record.getRemittanceIssueDate().trim(), formatter);
            } catch (DateTimeParseException exception) {
                errors.add("IRM Issue Date is not a valid date. Expected format is " + dateFormatPattern);
            }
        }

        if (remittanceDate != null && irmIssueDate != null && remittanceDate.isAfter(irmIssueDate)) {
            errors.add("Remittance Date cannot be after IRM Issue Date");
        }

        if (irmMasterRepository.existsByIrmNumber(record.getIrmRefNumber())) {
            errors.add("IRM Number already exists in DGFT_IRM_MASTER");
        }

        if (!purposeCodeMasterRepository.existsByCodeAndStatus(
                record.getPurposeOfRemittance(),
                AppConstants.PURPOSE_CODE_STATUS_ACTIVE)) {
            errors.add("Purpose of Remittance does not exist or is inactive in DGFT_PURPOSE_CODE_MASTER");
        }

        return errors;
    }
}