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

    public static int load() {
        int rsl = 0;
        try (InputStream in = AlertRabbit.class
                .getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            rsl = Integer.parseInt(config.getProperty("rabbit.interval"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return rsl;
    }

    public static Connection getConnection() {
        Connection cn;
        try (InputStream in = AlertRabbit.class
                .getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
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
