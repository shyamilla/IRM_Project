package com.dgft.irm.service;



import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import com.dgft.irm.dto.request.IrmCsvRequestDto;

public interface CsvFileReaderService {

    List<Path> listInboundFiles() throws IOException;

    List<IrmCsvRequestDto> parse(Path file) throws IOException;

    List<IrmCsvRequestDto> parse(InputStream inputStream) throws IOException;

    void moveToProcessed(Path file) throws IOException;

    void rewriteFile(Path sourcePathOrNull, List<IrmCsvRequestDto> remaining) throws IOException;
}