package com.slj.springconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan({"com.slj.zk","com.slj.config","com.slj"})
public class SpringServiceConfig {
    static {
      log.info("spring  config  inited ");
    }
}
