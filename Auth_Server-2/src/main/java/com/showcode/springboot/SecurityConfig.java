package com.showcode.springboot;


import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
@Order(-20)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override

    protected void configure(HttpSecurity http) throws Exception { // @formatter:off
//        http.requestMatchers()
//            .antMatchers("/login", "/oauth/authorize")
//            .and()
//            .authorizeRequests()
//            .anyRequest()
//            .authenticated()
//            .and()
//            .formLogin().loginPage("/customlogin")
//            .permitAll()
//            .and().csrf().disable();
//    	  http.formLogin().loginPage("/login").permitAll()
//          .and()
//         .authorizeRequests().antMatchers("/css/**", "/js/**", "/images/**","/resources/**","/public/**","src/main/resources/public/resources/**").permitAll()
//          .and()
//          .requestMatchers()
//          .antMatchers("/oauth/token","/", "/oauth/authorize", "/oauth/confirm_access")
//          .and()
//          .authorizeRequests()
//          .anyRequest().authenticated();
    	
    	 http
         .authorizeRequests()
             .antMatchers("/console/**", "/reset", "/login","/login.html").permitAll()
         .and()
             .formLogin()
             .loginPage("/login")
             .permitAll()
         .and()
             .requestMatchers()
             .antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/reset")
         .and()
             .authorizeRequests()
             .anyRequest()
             .authenticated();
       
    } // @formatter:on
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception { // @formatter:off
        auth.inMemoryAuthentication()
            .withUser("user")
            .password("password")
            .roles("USER");
    } // @formatter:on

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Configuration
    static class MvcConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("login").setViewName("login");
            
             
            
        }}
}