package org.scottishtecharmy.wishaw_java.service.importer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportRowState {
    private int rowNumber;
    private String status;
    private String message;
    private Map<String, String> data = new LinkedHashMap<>();
}