package com.slj.springconfig;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

/**
 * @author code4crafter@gmail.com
 *         Date: 16/4/29
 *         Time: 上午11:28
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class SpringCacheConfig extends CachingConfigurerSupport {




	@Bean
	public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
		return null;
	}



}
