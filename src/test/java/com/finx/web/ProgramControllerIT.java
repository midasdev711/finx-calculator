package com.finx.web;

import com.finx.domain.cars.Car;
import com.finx.domain.programs.Program;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProgramControllerIT {

    @LocalServerPort
    int localPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @Disabled("Currently there is a randomized deal return")
    void testController() {

        String url = "/api/programs/RBC";

        ResponseEntity<Program> responseEntity = testRestTemplate.getForEntity(url, Program.class);

        Program result = responseEntity.getBody();

        System.out.println(result);
    }

    @ParameterizedTest
    @MethodSource("yearsExpectedPrograms")
    @DisplayName("Programs are correctly retrieved according to year of vehicle")
    void testRetrievePrograms(long year, Set<String> programs) {
        String url = "/api/programs";

        Car car = Car.builder()
                .year(year)
                .build();

        ResponseEntity<String[]> responseEntity = testRestTemplate.postForEntity(url, car, String[].class);

        assertThat(responseEntity.getBody())
                .containsExactlyInAnyOrderElementsOf(programs);
    }

    private static Stream<Arguments> yearsExpectedPrograms() {
        return Stream.of(
                arguments(2010, Collections.emptySet()),
                arguments(2011, Set.of("RBC")),
                arguments(2015, Set.of("RBC")),
                arguments(2016, Set.of("RBC", "BMO Fixed Rate")),
                arguments(2020, Set.of("RBC", "BMO Fixed Rate")),
                arguments(2022, Set.of("RBC", "BMO Fixed Rate", "New Subvented"))
        );
    }
}