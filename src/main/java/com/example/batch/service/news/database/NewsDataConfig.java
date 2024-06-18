package com.example.batch.service.news.database;

import com.zaxxer.hikari.HikariDataSource;
import org.example.database.common.RoutingDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.batch.service.news.database.rep.jpa",
        entityManagerFactoryRef = "newsEntityManagerFactory",
        transactionManagerRef = "newsTransactionManager"
)
public class NewsDataConfig {
//    private final NewsDataSource newsDataSource;

    @Bean
    @ConfigurationProperties("database.datasource.news.master")
    public DataSourceProperties newsMasterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.news.master.configure")
    public DataSource newsMasterDatasource() {
        return newsMasterDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("database.datasource.news.slave1")
    public DataSourceProperties newsSlave1DatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.news.slave1.configure")
    public DataSource newsSlave1Datasource() {
        return newsSlave1DatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean newsEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("newsMasterDatasource") DataSource masterDataSource,
            @Qualifier("newsSlave1Datasource") DataSource SlaveDataSource
    ) {
        RoutingDataSource routingDataSource = new RoutingDataSource();
        Map<Object, Object> datasourceMap = new HashMap<Object, Object>() {
            {
                put("master", masterDataSource);
                put("slave", SlaveDataSource);
            }
        };

        routingDataSource.setTargetDataSources(datasourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.afterPropertiesSet();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
//        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.default_batch_fetch_size", 1000);
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.use_sql_comments", true);

        return builder.dataSource(new LazyConnectionDataSourceProxy(routingDataSource))
                .packages("com.example.batch.service.news.database.rep.jpa")
                .properties(properties)
                .persistenceUnit("newsEntityManagerFactory")
                .build();
    }

    @Bean(name = "newsTransactionManager")
    public PlatformTransactionManager newsTransactionManager(
            final @Qualifier("newsEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }
}