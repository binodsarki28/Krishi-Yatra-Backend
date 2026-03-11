package com.krishiYatra.krishiYatra.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.mysql.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.mysql.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.mysql.password}")
    private String mysqlPassword;

    @Bean
    public DataSource dataSource() {
        String branch = getCurrentGitBranch();
        System.out.println("Current Git Branch: " + branch);

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        if ("main".equals(branch) || "mysql".equals(System.getProperty("db.type"))) {
            // MySQL
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(mysqlUrl);
            dataSource.setUsername(mysqlUsername);
            dataSource.setPassword(mysqlPassword);
        } else {
            // H2
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:krishi_yatra;DB_CLOSE_DELAY=-1;MODE=MySQL");
            dataSource.setUsername("sa");
            dataSource.setPassword("");
        }

        return dataSource;
    }

    private String getCurrentGitBranch() {
        try {
            java.nio.file.Path dotGitHead = Paths.get(".git/HEAD");
            if (!Files.exists(dotGitHead)) {
                return "unknown";
            }
            String head = Files.readString(dotGitHead).trim();
            if (head.startsWith("ref:")) {
                String[] parts = head.split("/");
                return parts[parts.length - 1]; // Get 'main' from 'ref: refs/heads/main'
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown";
    }
}
