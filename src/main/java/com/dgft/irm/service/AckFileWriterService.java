package com.dgft.irm.service;


import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.dgft.irm.dto.response.RecordResultDto;

public interface AckFileWriterService {

    Path writeAck(String sourceFileName, List<RecordResultDto> results) throws IOException;
}