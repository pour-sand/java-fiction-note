package com.fictionNote;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MyWebAppConfigurer 
        extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	String path = System.getProperty("user.dir")+"/src/main/resources/static/images/";
        registry.addResourceHandler("/wahaha/**").addResourceLocations("file:"+path);
        super.addResourceHandlers(registry);
    }

}