package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> rsl = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            Document doc = Jsoup.connect(link + i).get();
            Elements row = doc.select(".postslisttopic");
            for (Element element : row) {
                Element href = element.child(0);
                Post post = new Post();
                post.setLink(href.attr("href"));
                post.setName(href.text());
                post.setTextDate(href.attr("href"));
                rsl.add(post);
            }
        }
        return rsl;
    }

    @Override
    public Post detail(String link) throws IOException {
        Post rsl = new Post();
        rsl.setTextDate(link);
        rsl.setLink(link);
        Document doc = Jsoup.connect(link).get();
        Elements row = doc.select(".messageHeader");
        Element text = row.get(0);
        rsl.setName(text.text());
        return rsl;
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse parser = new SqlRuParse();
        Post post = parser.detail("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        System.out.println(post.getName());
        List<Post> list = parser.list("https://www.sql.ru/forum/job-offers/");
        System.out.println(list.size());
        System.out.println(list.get(100).getLink());
        System.out.println(list.get(100).getName());
        System.out.println(list.get(100).getText());
        System.out.println(list.get(100).getCreated());
    }
}
