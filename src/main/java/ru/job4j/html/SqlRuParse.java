package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SqlRuParse {

    public static void main(String[] args) throws Exception {
        for (int i = 1; i < 6; i++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + i).get();
            Elements row = doc.select(".postslisttopic");
            Elements dates = doc.select(".altCol");
            int count = 1;
            for (Element element : row) {
                Element href = element.child(0);
                Element d = dates.get(count);
                count += 2;
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(d.text());
            }
        }
    }
}
