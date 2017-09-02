package com.slj.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

@EnableAspectJAutoProxy
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.slj..* ","com.slj.api.controller"})
public class SpringMVCConfiguration extends WebMvcConfigurerAdapter {



    private static final Charset UTF8 = Charset.forName("UTF-8");


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(UTF8);
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        converters.add(stringHttpMessageConverter);
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.failOnEmptyBeans(false);
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.featuresToDisable(SerializationFeature.WRITE_NULL_MAP_VALUES);

        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
        //converters.add(new FormHttpMessageConverter());
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static");

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(4 * 1024 * 1024);
        return multipartResolver;
    }
    // @Bean
    // public ViewResolver viewResolver() {
    // InternalResourceViewResolver viewResolver = new
    // InternalResourceViewResolver();
    // viewResolver.setViewClass(JstlView.class);
    // viewResolver.setPrefix("/WEB-INF/views/");
    // viewResolver.setSuffix(".jsp");
    //
    // return viewResolver;
    // }
}
