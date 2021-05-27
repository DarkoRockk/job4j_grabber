package ru.job4j.grabber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SqlRuDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        Map<String, String> months = new HashMap<>();
        months.put("янв", "01");
        months.put("фев", "02");
        months.put("мар", "03");
        months.put("апр", "04");
        months.put("май", "05");
        months.put("июн", "06");
        months.put("июл", "07");
        months.put("авг", "08");
        months.put("сен", "09");
        months.put("окт", "10");
        months.put("ноя", "11");
        months.put("дек", "12");

        LocalDateTime rsl = null;
        String[] parts = parse.split(",");
        String[] date = parts[0].split(" ");
        String[] time = parts[1].trim().split(":");
        if (date[0].equals("сегодня")) {
            rsl = today(time[0], time[1]);
        } else if (date[0].equals("вчера")) {
            rsl = today(time[0], time[1]).minusDays(1);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MM yy, HH:mm");
            StringBuilder textDate = new StringBuilder();
            textDate.append(date[0]).append(" ");
            textDate.append(months.get(date[1])).append(" ");
            textDate.append(date[2]).append(",");
            textDate.append(parts[1]);
            rsl = LocalDateTime.parse(textDate.toString(), formatter);
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
