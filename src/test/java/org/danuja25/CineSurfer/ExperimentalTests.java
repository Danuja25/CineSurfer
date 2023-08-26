package org.danuja25.CineSurfer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ExperimentalTests {

    @ParameterizedTest
    @ValueSource(strings = {"2022-04-12", "01-08-2021", "5/8/2122"})
    public void checkValidDate(String date) {

    }

    @ParameterizedTest
    @CsvSource({"2022-04-12",
            "01-08-2021",
            "5/8/2122"})
    public void checkValidDateCsv(String date) {

    }

    @ParameterizedTest
    @MethodSource("getDates")
    public void checkValidDateMethod(String date) {

    }

    private static Stream<Arguments> getDates() {
        return Stream.of(Arguments.of("2022-04-12"), Arguments.of("01-08-2021"), Arguments.of("5/8/2122"));
    }
}
