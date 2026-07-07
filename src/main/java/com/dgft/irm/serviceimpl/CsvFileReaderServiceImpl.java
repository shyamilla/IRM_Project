package com.dgft.irm.serviceimpl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dgft.irm.dto.request.IrmCsvRequestDto;
import com.dgft.irm.service.CsvFileReaderService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

@Service
public class CsvFileReaderServiceImpl implements CsvFileReaderService {

    @Value("${dgft.csv.inbound-path}")
    private String inboundPath;

    @Value("${dgft.csv.processed-path}")
    private String processedPath;

    @Value("${dgft.csv.delimiter}")
    private String delimiter;

    @Override
    public List<Path> listInboundFiles() throws IOException {
        Path dir = Paths.get(inboundPath);
        Files.createDirectories(dir);

        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
                    .sorted(Comparator.comparingLong(path -> path.toFile().lastModified()))
                    .toList();
        }
    }

    @Override
    public List<IrmCsvRequestDto> parse(Path file) throws IOException {
        try (Reader reader = Files.newBufferedReader(file)) {
            return parseInternal(reader);
        }
    }

    @Override
    public List<IrmCsvRequestDto> parse(InputStream inputStream) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return parseInternal(reader);
        }
    }

    @Override
    public void rewriteFile(Path file, List<IrmCsvRequestDto> remainingRecords) throws IOException {

        List<String> lines = new ArrayList<>();

        lines.add(String.join(delimiter,
                "IRMREFNUMBER",
                "REMITTANCEAMOUNT",
                "REMITTANCEDATE",
                "ADCODE",
                "REMITTANCECURRENCY",
                "IECODE",
                "REMITTERNAME",
                "REMITTERCOUNTRY",
                "PURPOSEOFREMITTANCE",
                "IFSCCODE",
                "INRCREDITAMOUNT",
                "PANNUMBER",
                "BANKREFNUMBER",
                "BANKACCNUM",
                "REMITTANCEISSUEDATE"));

        for (IrmCsvRequestDto record : remainingRecords) {
            lines.add(String.join(delimiter,
                    safe(record.getIrmRefNumber()),
                    safe(record.getRemittanceAmount()),
                    safe(record.getRemittanceDate()),
                    safe(record.getAdCode()),
                    safe(record.getRemittanceCurrency()),
                    safe(record.getIeCode()),
                    safe(record.getRemitterName()),
                    safe(record.getRemitterCountry()),
                    safe(record.getPurposeOfRemittance()),
                    safe(record.getIfscCode()),
                    safe(record.getInrCreditAmount()),
                    safe(record.getPanNumber()),
                    safe(record.getBankRefNumber()),
                    safe(record.getBankAccNum()),
                    safe(record.getRemittanceIssueDate())));
        }

        Files.write(file, lines, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private List<IrmCsvRequestDto> parseInternal(Reader reader) throws IOException {

        List<IrmCsvRequestDto> records = new ArrayList<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setDelimiter(delimiter.charAt(0))
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build();

        try (CSVParser parser = format.parse(reader)) {

            int rowNumber = 1;

            for (CSVRecord csvRecord : parser) {

                rowNumber++;

                IrmCsvRequestDto dto = new IrmCsvRequestDto();

                dto.setRowNumber(rowNumber);
                dto.setIrmRefNumber(get(csvRecord, "IRMREFNUMBER"));
                dto.setRemittanceAmount(get(csvRecord, "REMITTANCEAMOUNT"));
                dto.setRemittanceDate(get(csvRecord, "REMITTANCEDATE"));
                dto.setAdCode(get(csvRecord, "ADCODE"));
                dto.setRemittanceCurrency(get(csvRecord, "REMITTANCECURRENCY"));
                dto.setIeCode(get(csvRecord, "IECODE"));
                dto.setRemitterName(get(csvRecord, "REMITTERNAME"));
                dto.setRemitterCountry(get(csvRecord, "REMITTERCOUNTRY"));
                dto.setPurposeOfRemittance(get(csvRecord, "PURPOSEOFREMITTANCE"));
                dto.setIfscCode(get(csvRecord, "IFSCCODE"));
                dto.setInrCreditAmount(get(csvRecord, "INRCREDITAMOUNT"));
                dto.setPanNumber(get(csvRecord, "PANNUMBER"));
                dto.setBankRefNumber(get(csvRecord, "BANKREFNUMBER"));
                dto.setBankAccNum(get(csvRecord, "BANKACCNUM"));
                dto.setRemittanceIssueDate(get(csvRecord, "REMITTANCEISSUEDATE"));

                records.add(dto);
            }
        }

        return records;
    }

    private String get(CSVRecord record, String columnName) {
        return record.isMapped(columnName) ? record.get(columnName) : null;
    }

    @Override
    public void moveToProcessed(Path file) throws IOException {
        Path dir = Paths.get(processedPath);
        Files.createDirectories(dir);

        Files.move(
                file,
                dir.resolve(file.getFileName()),
                StandardCopyOption.REPLACE_EXISTING);
    }
}