package ru.job4j.html;

import com.sun.security.jgss.GSSUtil;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = cnn.prepareStatement(
                "insert into post(name, text, link, created) values(?, ?, ?, ?);"
        )) {
            statement.setString(1, post.getName());
            statement.setString(2, post.getText());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public List<Post> getAll() {
        List<Post> rsl = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post"
        )) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Post post = new Post();
                    post.setId(resultSet.getInt("id"));
                    post.setName(resultSet.getString("name"));
                    post.setLink(resultSet.getString("link"));
                    post.setText(resultSet.getString("text"));
                    post.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                    rsl.add(post);
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rsl;
    }

    @Override
    public Post findById(String id) {
        Post rsl = new Post();
        try (PreparedStatement statement = cnn.prepareStatement(
                "select * from post where id = ?"
        )) {
            statement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    rsl.setId(resultSet.getInt("id"));
                    rsl.setName(resultSet.getString("name"));
                    rsl.setLink(resultSet.getString("link"));
                    rsl.setText(resultSet.getString("text"));
                    rsl.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rsl;
    }

    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            config.load(in);
        }
        PsqlStore store = new PsqlStore(config);
        SqlRuParse parser = new SqlRuParse();
        List<Post> list = parser.list("https://www.sql.ru/forum/job-offers/");
        store.save(list.get(0));
        store.save(list.get(1));
        List<Post> rsl = store.getAll();
        System.out.println(rsl.size());
        Post post = rsl.get(0);
        System.out.println(post.getId());
        System.out.println(post.getName());
        System.out.println(post.getText());
        System.out.println(post.getLink());
        System.out.println(post.getCreated());
        System.out.println(store.findById(Integer.toString(post.getId())).getName());

    }
}
