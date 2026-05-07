package org.scottishtecharmy.wishaw_java.service.importer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportBatchState {
    private List<ImportRowState> rows = new ArrayList<>();
    private Map<String, Long> playerMappings = new HashMap<>();
    private List<String> unmappedPlayers = new ArrayList<>();
}