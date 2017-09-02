package com.slj.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @author code4crafter@gmail.com
 *         Date: 16/7/12
 *         Time: 下午6:39
 */
@Slf4j
public abstract class JdbcUtils {

	public static final String COM_MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";

	public static DataSource newDataSource(String name, JdbcConfig jdbcConfig){
		ComboPooledDataSource master = new ComboPooledDataSource(name);
		try {
			master.setDriverClass(COM_MYSQL_JDBC_DRIVER);
		} catch (PropertyVetoException e) {
			log.error("init jdbc error {}", COM_MYSQL_JDBC_DRIVER, e);
			throw new RuntimeException(e);
		}
		master.setJdbcUrl(jdbcConfig.getUrl());
		master.setUser(jdbcConfig.getUser());
		master.setPassword(jdbcConfig.getPassword());
		master.setIdleConnectionTestPeriod(60);
		master.setMinPoolSize(10);
		master.setMaxPoolSize(50);
		master.setMaxIdleTime(1800);
		master.setAcquireIncrement(3);
		master.setMaxStatements(1000);
		master.setIdleConnectionTestPeriod(60);
		master.setAcquireRetryAttempts(30);
		master.setTestConnectionOnCheckin(true);
		master.setPreferredTestQuery("select 1 from dual");
		return master;
	}

}
