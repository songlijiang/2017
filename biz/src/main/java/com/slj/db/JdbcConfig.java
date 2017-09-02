package com.slj.db;

import lombok.Data;

/**
 * @author code4crafter@gmail.com
 *         Date: 16/7/13
 *         Time: 上午10:39
 */
@Data
public class JdbcConfig {

	private String url;

	private String user;

	private String password;
}
