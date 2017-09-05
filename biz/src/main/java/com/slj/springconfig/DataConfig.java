package com.slj.springconfig;

import com.slj.config.AllConfig;
import com.slj.db.JdbcConfig;
import com.slj.db.JdbcUtils;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.Properties;

@Configuration
public class DataConfig {

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        return sqlSessionFactory;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("com.slj.**.dao");
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return configurer;
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() throws PropertyVetoException {
            Properties properties = AllConfig.properties;
            JdbcConfig jdbcConfig = new JdbcConfig();
            jdbcConfig.setUser(properties.getProperty("slj.jdbc.user"));
            jdbcConfig.setUrl(properties.getProperty("slj.jdbc.url"));
            jdbcConfig.setPassword(properties.getProperty("slj.jdbc.password"));
       return JdbcUtils.newDataSource("master", jdbcConfig);
    }



}
