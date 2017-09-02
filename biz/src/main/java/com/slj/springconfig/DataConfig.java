package com.slj.springconfig;

import com.slj.db.JdbcConfig;
import com.slj.db.JdbcUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

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
            //Properties properties = AllConfig.properties;
            JdbcConfig jdbcConfig = new JdbcConfig();
            jdbcConfig.setUser("root");
            jdbcConfig.setUrl("jdbc:mysql://23.106.142.84:3306/java?characterEncoding=UTF8&socketTimeout=60000&allowMultiQueries=true");
            jdbcConfig.setPassword("190360080");
       return JdbcUtils.newDataSource("master", jdbcConfig);
    }



}
