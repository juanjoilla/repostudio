package com.artesluis.artesluis_backend.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties() {
            @Override
            public String determineUrl() {
                String databaseUrl = System.getenv("DATABASE_URL");
                if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
                    try {
                        URI uri = URI.create(databaseUrl);
                        String host = uri.getHost();
                        int port = uri.getPort();
                        // Si no hay puerto especificado, usar el puerto por defecto de PostgreSQL
                        if (port == -1) {
                            port = 5432;
                        }
                        String database = uri.getPath().substring(1); // Remove leading '/'
                        
                        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s?sslmode=require", host, port, database);
                        System.out.println("Converted URL: " + jdbcUrl);
                        return jdbcUrl;
                    } catch (Exception e) {
                        System.err.println("Error parsing DATABASE_URL: " + e.getMessage());
                        return super.determineUrl();
                    }
                }
                return super.determineUrl();
            }

            @Override
            public String determineUsername() {
                String databaseUrl = System.getenv("DATABASE_URL");
                if (databaseUrl != null) {
                    try {
                        URI uri = URI.create(databaseUrl);
                        String userInfo = uri.getUserInfo();
                        if (userInfo != null) {
                            String username = userInfo.split(":")[0];
                            System.out.println("Parsed username: " + username);
                            return username;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing username from DATABASE_URL: " + e.getMessage());
                    }
                }
                return super.determineUsername();
            }

            @Override
            public String determinePassword() {
                String databaseUrl = System.getenv("DATABASE_URL");
                if (databaseUrl != null) {
                    try {
                        URI uri = URI.create(databaseUrl);
                        String userInfo = uri.getUserInfo();
                        if (userInfo != null && userInfo.contains(":")) {
                            String password = userInfo.split(":", 2)[1];
                            System.out.println("Parsed password: " + (password != null ? "***" : "null"));
                            return password;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing password from DATABASE_URL: " + e.getMessage());
                    }
                }
                return super.determinePassword();
            }
        };
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
}