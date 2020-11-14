package com.imooc.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Jarvis(Tang Hui)
 * @version 1.0
 * @date 2020/4/4 20:13
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 实线静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**")
//                .addResourceLocations("classpath:/META-INF/resources/") // 映射swagger2
//                .addResourceLocations("file:D:\\workspaces\\faces\\");  // 映射本地静态资源
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/") // 映射swagger2
                .addResourceLocations("file:/workspaces/images/");  // 映射本地静态资源
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){

        return builder.build();
    }

}
