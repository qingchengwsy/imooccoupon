package com.imooc.distribution.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

/**
 * <h1>Jackson 的自定义配置<h1/>
 */

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper getObkectMapper(){
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper;
    }
}
