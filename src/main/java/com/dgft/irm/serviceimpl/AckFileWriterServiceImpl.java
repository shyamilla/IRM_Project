package com.dgft.irm.serviceimpl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dgft.irm.constants.AppConstants;
import com.dgft.irm.dto.response.RecordResultDto;
import com.dgft.irm.service.AckFileWriterService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AckFileWriterServiceImpl implements AckFileWriterService {

    @Value("${dgft.csv.ack-path}")
    private String ackPath;

    private static final DateTimeFormatter FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public Path writeAck(String sourceFileName, List<RecordResultDto> results) throws IOException {

        Path ackDir = Paths.get(ackPath);
        Files.createDirectories(ackDir);

        String baseName = sourceFileName.contains(".")
                ? sourceFileName.substring(0, sourceFileName.lastIndexOf('.'))
                : sourceFileName;

        String ackFileName = baseName + "_ACK_" +
                LocalDateTime.now().format(FILE_TIMESTAMP) + ".txt";

        Path ackFile = ackDir.resolve(ackFileName);

        StringBuilder builder = new StringBuilder();

        builder.append("IRM_NUMBER|ROW_NUMBER|STATUS|REASON")
                .append(System.lineSeparator());

        long passCount = 0;
        long failCount = 0;

        for (RecordResultDto result : results) {

            boolean success = result.isSuccess();

            String status = success
                    ? AppConstants.ACK_STATUS_PASS
                    : AppConstants.ACK_STATUS_FAIL;

            String reason = success
                    ? "Inserted successfully with ID=" + result.getGeneratedId()
                    : String.join("; ", result.getReasons());

            builder.append(result.getIrmRefNumber()).append("|")
                    .append(result.getRowNumber()).append("|")
                    .append(status).append("|")
                    .append(reason)
                    .append(System.lineSeparator());

            if (success) {
                passCount++;
            } else {
                failCount++;
            }
        }

        builder.append("---").append(System.lineSeparator());
        builder.append("TOTAL=").append(results.size())
                .append(" PASS=").append(passCount)
                .append(" FAIL=").append(failCount)
                .append(System.lineSeparator());

        Files.writeString(ackFile, builder.toString(), StandardCharsets.UTF_8);

        return ackFile;
    }
}