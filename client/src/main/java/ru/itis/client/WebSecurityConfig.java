package ru.itis.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //todo закрыть доступ
//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring().anyRequest();
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/external/api/v1/**").permitAll()
//                todo закрыть доступ
//                .anyRequest()
//                .permitAll();
//        http.csrf()
//                .disable();
//    }
//
    static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    static final MediaType DEFAULT_FORM_DATA_MEDIA_TYPE =
            new MediaType(MediaType.APPLICATION_FORM_URLENCODED, DEFAULT_CHARSET);

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        restTemplate.getMessageConverters().add(new OAuth2UserInfoHttpMessageConverter());

        return restTemplate;
    }

    @Bean
    DefaultOAuth2UserService defaultOAuth2UserService() {
        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
        defaultOAuth2UserService.setRequestEntityConverter(new OAuth2UserInfoRequestEntityConverter());
        defaultOAuth2UserService.setRestOperations(restTemplate());

        return defaultOAuth2UserService;
    }

    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/login", "/oauth/authorize")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(defaultOAuth2UserService());
    }
}