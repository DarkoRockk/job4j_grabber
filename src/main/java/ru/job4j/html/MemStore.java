package ru.job4j.html;

import ru.job4j.AlertRabbit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class MemStore implements Store {

    private static Properties config;

    public static Properties getProperties() {
        if (MemStore.config == null) {
            try (InputStream in = AlertRabbit.class
                    .getClassLoader().getResourceAsStream("grabber.properties")) {
                MemStore.config = new Properties();
                MemStore.config.load(in);

            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return MemStore.config;
    }

    public static Connection getConnection() {
        Connection cn = null;
        getProperties();
        try {
            Class.forName(MemStore.config.getProperty("driver-class-name"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            cn = DriverManager.getConnection(
                    MemStore.config.getProperty("url"),
                    MemStore.config.getProperty("username"),
                    MemStore.config.getProperty("password")
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return cn;
    }

    public static void createSchema(Connection cn) {
        try (Statement statement = cn.createStatement()) {
            String sql = String.format(
                    "create table if not exists post(%s, %s, %s, %s, %s);",
                    "id serial primary key",
                    "name varchar(255)",
                    "text varchar(2500)",
                    "link varchar(255)",
                    "created date"
            );
            statement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {

    }

    @Override
    public List<Post> getAll() {
        return null;
    }

    @Override
    public Post findById(String id) {
        return null;
    }

    public static void main(String[] args) throws Exception {
        try (Connection cn = getConnection()) {
            createSchema(cn);
        }
    }
}
