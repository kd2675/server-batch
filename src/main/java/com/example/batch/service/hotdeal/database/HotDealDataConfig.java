package com.example.batch.service.hotdeal.database;

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
        basePackages = "com.example.batch.service.hotdeal.database.rep.jpa",
        entityManagerFactoryRef = "hotdealEntityManagerFactory",
        transactionManagerRef = "hotdealTransactionManager"
)
public class HotDealDataConfig {

    @Bean
    @ConfigurationProperties("database.datasource.hotdeal.master")
    public DataSourceProperties hotdealMasterDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.hotdeal.master.configure")
    public DataSource hotdealMasterDatasource() {
        return hotdealMasterDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @ConfigurationProperties("database.datasource.hotdeal.slave1")
    public DataSourceProperties hotdealSlave1DatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("database.datasource.hotdeal.slave1.configure")
    public DataSource hotdealSlave1Datasource() {
        return hotdealSlave1DatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean hotdealEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("hotdealMasterDatasource") DataSource masterDataSource,
            @Qualifier("hotdealSlave1Datasource") DataSource SlaveDataSource
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
                .packages("com.example.batch.service.hotdeal.database.rep.jpa")
                .properties(properties)
                .persistenceUnit("hotdealEntityManagerFactory")
                .build();
    }

    @Bean(name = "hotdealTransactionManager")
    public PlatformTransactionManager hotdealTransactionManager(
            final @Qualifier("hotdealEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
    ) {
        return new JpaTransactionManager(localContainerEntityManagerFactoryBean.getObject());
    }
}