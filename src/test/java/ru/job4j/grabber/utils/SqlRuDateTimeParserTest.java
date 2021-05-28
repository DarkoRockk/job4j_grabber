package ru.job4j.grabber.utils;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlRuDateTimeParserTest {

    @Test
    public void whenToday() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        String date = "сегодня, 17:16";
        LocalDateTime rsl = parser.parse(date);
        assertThat(rsl, is(LocalDateTime.of(LocalDate.now(), LocalTime.of(17,16))));
    }

    @Test
    public void whenYesterday() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        String date = "вчера, 17:16";
        LocalDateTime rsl = parser.parse(date);
        assertThat(rsl, is(LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(17,16))));
    }

    @Test
    public void whenAnyDate() {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        String date = "17 май 20, 17:16";
        LocalDateTime rsl = parser.parse(date);
        assertThat(rsl, is(LocalDateTime.of(2020, 5,17,17,16)));
    }


}