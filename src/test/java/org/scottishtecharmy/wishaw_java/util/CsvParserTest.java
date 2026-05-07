package org.scottishtecharmy.wishaw_java.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvParserTest {

    @Test
    void parseLineHandlesQuotedValuesCommasAndEscapedQuotes() {
        String[] fields = CsvParser.parseLine("player1,\"Game, Mastery\",\"He said \"\"hello\"\"\"");

        assertThat(fields).containsExactly("player1", "Game, Mastery", "He said \"hello\"");
    }

    @Test
    void parseLinePreservesEmptyCells() {
        String[] fields = CsvParser.parseLine("alpha,,charlie,");

        assertThat(fields).containsExactly("alpha", "", "charlie", "");
    }

    @Test
    void parseSkipsBlankLinesAndParsesRowContent() throws IOException {
        List<String[]> rows = CsvParser.parse("header1,header2\n\nvalue1,value2\n   \nvalue3,\"quoted, value\"\n");

        assertThat(rows).hasSize(3);
        assertThat(rows.get(0)).containsExactly("header1", "header2");
        assertThat(rows.get(1)).containsExactly("value1", "value2");
        assertThat(rows.get(2)).containsExactly("value3", "quoted, value");
    }
}