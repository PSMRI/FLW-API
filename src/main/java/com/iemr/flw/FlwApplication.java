package com.iemr.flw;

import com.iemr.flw.masterEnum.GroupName;
import com.iemr.flw.utils.FLWApplBeans;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.iemr.flw.*")
@EntityScan(basePackages = {"com.iemr.flw.domain.identity", "com.iemr.flw.domain.iemr"})
@EnableScheduling
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
