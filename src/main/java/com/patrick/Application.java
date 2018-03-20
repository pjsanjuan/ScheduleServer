package com.patrick;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication // equivalent to@Configuration,@EnableAutoConfiguration,@ComponentScan
// @SpringBootApplication also used for searching for @*Test: https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#getting-started-first-application-auto-configuration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //https://stackoverflow.com/questions/28324352/how-to-customise-the-jackson-json-mapper-implicitly-used-by-spring-boot
    //https://stackoverflow.com/questions/39263553/how-to-customise-jackson-in-spring-boot-1-4/40051029
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addHibernate5Module() {
        return (jacksonObjectMapperBuilder) ->
                jacksonObjectMapperBuilder.modulesToInstall(new Hibernate5Module());
    }
}