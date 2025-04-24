package org.example.merchant.config;


import org.example.merchant.filter.AuthInterceptor;
import org.example.merchant.filter.MerchantAuthInterceptor;
import org.example.merchant.filter.PlatformAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final PlatformAuthInterceptor platformAuthInterceptor;


    private final MerchantAuthInterceptor merchantAuthInterceptor;

    public WebMvcConfig(PlatformAuthInterceptor platformAuthInterceptor,  MerchantAuthInterceptor merchantAuthInterceptor) {
        this.platformAuthInterceptor = platformAuthInterceptor;
        this.merchantAuthInterceptor = merchantAuthInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(platformAuthInterceptor)
                .addPathPatterns("/esg/platform/**");



        registry.addInterceptor(merchantAuthInterceptor)
                .addPathPatterns("/esg/merchant/**");


    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
