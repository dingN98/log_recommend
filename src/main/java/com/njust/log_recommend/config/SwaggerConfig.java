package com.njust.log_recommend.config;

import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档相关配置
 * Created by macro on 2018/4/26.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {

        /*com/njust/log_recommend/controller*/
        return SwaggerProperties.builder()
                .apiBasePackage("com.njust.log_recommend.controller")
                .title("HS系统")
                .description("HS相关接口文档")
                .contactName("某")
                .version("1.0")
                .enableSecurity(false)
                .build();
    }
}
