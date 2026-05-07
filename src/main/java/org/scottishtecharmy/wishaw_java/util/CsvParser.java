package org.scottishtecharmy.wishaw_java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple CSV parser using Java standard library only.
 * Supports commas, quoted values, empty cells, and line-by-line parsing.
 *
 * Limitations:
 * - Does not support escaped quotes within quoted fields (e.g., "He said ""hello""")
 * - Does not support multi-line values within quotes
 * - Designed for simple tabular CSV data typical of spreadsheet exports
 */
public final class CsvParser {

    private CsvParser() {}

    public static List<String[]> parse(String csvContent) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                rows.add(parseLine(line));
            }
        }
        return rows;
    }

    public static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    // Check for escaped quote
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString().trim());
                    current = new StringBuilder();
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString().trim());

        return fields.toArray(new String[0]);
    }
}
