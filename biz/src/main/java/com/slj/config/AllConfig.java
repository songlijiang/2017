package com.slj.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
public class AllConfig {
    public static final Properties properties = System.getProperties();
    public static final AtomicBoolean inited = new AtomicBoolean(false);

    private static String configFile =  getEnv() + ".config";

    public static String getEnv() {
        return System.getProperty("slj.system.env", "beta");
    }

    static {
        init();
    }

    private static String getFromFile(String key) {
        return properties.getProperty(key);
    }

    public static String getZkServers() {

        //return getFromFile("slj.zk.servers");

        return "23.106.142.84:2181";
    }

    public static void init() {
        if (inited.compareAndSet(false,true)){
            if (System.getProperty("slj.system.config") != null) {
                configFile = System.getProperty("slj.system.config");
                try (InputStream propFile = new FileInputStream(new File(configFile))) {
                    properties.load(propFile);
                } catch (IOException e) {
                    throw new RuntimeException("fail to read " + configFile, e);
                }
            } else {
                InputStream propFile;
                try  {
                    propFile = AllConfig.class.getClassLoader().getResourceAsStream(configFile);
                    properties.load(propFile);
                } catch (Exception e) {
                    throw new RuntimeException("fail to read " + configFile, e);
                }
               log.info("error class load so pass ");
            }
        }
    }
}
