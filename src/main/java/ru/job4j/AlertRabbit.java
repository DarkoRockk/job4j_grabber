package ru.job4j;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    public static void main(String[] args) {

        try (Connection cn = getConnection()) {
            create(cn);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(AlertRabbit.load())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        private static Properties config;

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement statement = cn.prepareStatement(
                    "insert into rabbit(created_date) values (current_date);")) {
                statement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static Properties getProperties() {
        if (Rabbit.config == null) {
            try (InputStream in = AlertRabbit.class
                    .getClassLoader().getResourceAsStream("rabbit.properties")) {
                Rabbit.config = new Properties();
                Rabbit.config.load(in);

            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return Rabbit.config;
    }

    public static int load() {
        int rsl = 0;
        getProperties();
        rsl = Integer.parseInt(Rabbit.config.getProperty("rabbit.interval"));
        return rsl;
    }

    public static Connection getConnection() {
        Connection cn = null;
        getProperties();
        try {
            Class.forName(Rabbit.config.getProperty("driver-class-name"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            cn = DriverManager.getConnection(
                        Rabbit.config.getProperty("url"),
                        Rabbit.config.getProperty("username"),
                        Rabbit.config.getProperty("password")
                );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return cn;
    }

    public static void create(Connection cn) {
        try (Statement statement = cn.createStatement()) {
            String sql = String.format(
                    "create table if not exists rabbit(%s);",
                    "created_date date"
            );
            statement.execute(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
