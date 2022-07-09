package com.kk.spring.config;

import com.kk.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * @Description:放行静态文件
 * @Author:Spike Wong
 * @Date:2022/6/13
 */
@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Value("${swagger.prefix}")
    private String swaggerPrefix;

    /**
     * set static resources mapper
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Static resource mapping........");
        if (isPrefixSet()) {
            registry.addResourceHandler(swaggerPrefix + "/swagger-ui.html*").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler(swaggerPrefix + "/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        if (isPrefixSet()) {
            registry.addRedirectViewController(swaggerPrefix + "/v2/api-docs", "/v2/api-docs").setKeepQueryParams(true);
            registry.addRedirectViewController(swaggerPrefix + "/swagger-resources", "/swagger-resources");
            registry.addRedirectViewController(swaggerPrefix + "/swagger-resources/configuration/ui", "/swagger-resources/configuration/ui");
            registry.addRedirectViewController(swaggerPrefix + "/swagger-resources/configuration/security", "/swagger-resources/configuration/security");
            registry.addRedirectViewController("/swagger-ui.html", "/404");
        }
    }


    /**
     * Expand message converters of mvc frame to prevent missing precision.
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Extended message converter .........");
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        converters.add(0, messageConverter);
    }

    private boolean isPrefixSet() {
        return swaggerPrefix != null && !"".equals(swaggerPrefix) && !"/".equals(swaggerPrefix);
    }
}
