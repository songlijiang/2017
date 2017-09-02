package com.slj.api.config;

import com.slj.springconfig.DataConfig;
import com.slj.springconfig.SpringServiceConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpringInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                SpringMVCConfiguration.class,
                SpringServiceConfig.class,
                DataConfig.class,
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }


}
