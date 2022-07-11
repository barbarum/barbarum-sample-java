package com.barbarum.sample.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

@Slf4j
@SpringBootTest
public class CsvTest {

    @Test
    public void testCsvRead() throws IOException {
        File source = ResourceUtils.getFile("classpath:security/rbac/policies.csv");

        List<CSVRecord> records = CSVFormat.DEFAULT
            .withCommentMarker('#')
            .parse(new FileReader(source))
            .getRecords();
        
        for (CSVRecord record : records) {
            List<String> values = IntStream.range(0, record.size())
                .mapToObj(record::get)
                .collect(Collectors.toList());
            log.info("Records ({}, {}): {}", record.getRecordNumber(), record.size(), values);
        }
        assertThat(records).hasSize(4);
    }

}
