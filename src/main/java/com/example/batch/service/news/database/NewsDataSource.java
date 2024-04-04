package com.example.batch.service.news.database;//package com.example.crawling.news.database;
//
//import com.zaxxer.hikari.HikariDataSource;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//@Component
//public class NewsDataSource {
////    private final NewsDataProperties newsDataProperties;
//
//
//    @ConfigurationProperties("database.datasource.news.master")
//    public DataSourceProperties newsMasterDatasourceProperties() {
//        return new DataSourceProperties();
//    }
//    @ConfigurationProperties("database.datasource.news.slave1")
//    public DataSourceProperties newsSlave1DatasourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @ConfigurationProperties("database.datasource.news.master.configure")
//    public DataSource newsMasterDatasource() {
//        return newsMasterDatasourceProperties()
//                .initializeDataSourceBuilder()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @ConfigurationProperties("database.datasource.news.slave1.configure")
//    public DataSource newsSlave1Datasource() {
//        return newsSlave1DatasourceProperties()
//                .initializeDataSourceBuilder()
//                .type(HikariDataSource.class)
//                .build();
//    }
//}
