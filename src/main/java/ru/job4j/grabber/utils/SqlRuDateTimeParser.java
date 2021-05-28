package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MM yy, HH:mm");
    private static final Map<String, String> MONTHS = new HashMap<>();

    static {
        MONTHS.put("янв", "01");
        MONTHS.put("фев", "02");
        MONTHS.put("мар", "03");
        MONTHS.put("апр", "04");
        MONTHS.put("май", "05");
        MONTHS.put("июн", "06");
        MONTHS.put("июл", "07");
        MONTHS.put("авг", "08");
        MONTHS.put("сен", "09");
        MONTHS.put("окт", "10");
        MONTHS.put("ноя", "11");
        MONTHS.put("дек", "12");
    }

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime rsl;
        String[] parts = parse.split(",");
        String[] date = parts[0].split(" ");
        String[] time = parts[1].trim().split(":");
        if (date[0].equals("сегодня")) {
            rsl = today(time[0], time[1]);
        } else if (date[0].equals("вчера")) {
            rsl = today(time[0], time[1]).minusDays(1);
        } else {
            StringBuilder textDate = new StringBuilder();
            textDate.append(date[0]).append(" ");
            textDate.append(MONTHS.get(date[1])).append(" ");
            textDate.append(date[2]).append(",");
            textDate.append(parts[1]);
            rsl = LocalDateTime.parse(textDate.toString(), FORMATTER);
        }
        return rsl;
    }

    public LocalDateTime today(String hours, String minutes) {
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(Integer.parseInt(hours), Integer.parseInt(minutes)));
    }

    public static void main(String[] args) {
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        String dd = "вчера, 17:16";
        LocalDateTime ddd = parser.parse(dd);
        System.out.println(ddd);
    }
}
