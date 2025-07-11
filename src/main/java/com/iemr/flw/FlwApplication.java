package com.iemr.flw;

import com.iemr.flw.utils.FLWApplBeans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.iemr.flw.*")
@EntityScan(basePackages = {"com.iemr.flw.domain.identity", "com.iemr.flw.domain.iemr"})
public class FlwApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(applicationClass, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    private static Class<FlwApplication> applicationClass = FlwApplication.class;

    @Bean
    public FLWApplBeans instantiateBeans(){

        return new FLWApplBeans();
    }
}
