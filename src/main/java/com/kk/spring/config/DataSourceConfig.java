package com.kk.spring.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

@Configuration
@ComponentScan
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableTransactionManagement
//@MapperScan(basePackages = "com.kiseki.model.persistence", sqlSessionFactoryRef = "db1sqlSessionFactory")
@MapperScan(basePackages = "com.kk.model.persistence")
public class  DataSourceConfig implements TransactionManagementConfigurer {
    @Bean(name = "dataSource")
    @Qualifier(value = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder.create().type(com.mchange.v2.c3p0.ComboPooledDataSource.class).build();
        return dataSource;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}


