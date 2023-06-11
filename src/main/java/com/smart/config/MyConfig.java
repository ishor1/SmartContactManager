package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class MyConfig{
	
@Bean
public UserDetailsService getUserDetailsService() {
	return new UserDetailsServiceImp();
}

@Bean
public BCryptPasswordEncoder paswordEncoder() {
	return new BCryptPasswordEncoder();
}

@Bean
public DaoAuthenticationProvider authenticationProvider() {
	DaoAuthenticationProvider daoAuthenticationProvider=new DaoAuthenticationProvider();
	daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
	daoAuthenticationProvider.setPasswordEncoder(paswordEncoder());
	return daoAuthenticationProvider;
}

//
////configure method....
////public void configure(AuthenticationManagerBuilder auth) throws Exception{
////	auth.authenticationProvider(authenticationProvider());)
////}
//
////public void configure(HttpSecurity http) throws Exception{
////	http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMINE")
////	.antMatchers("/user/**").hasRole("USER").antMatchers("/**")
////	.permitAll().and().formLogin().and().csrf().disable();
////}

@Bean
public SecurityFilterChain filterChain(HttpSecurity h) throws Exception {
	h.csrf().disable().authorizeHttpRequests()
	.requestMatchers("/admin/**").hasRole("ADMIN")
	.requestMatchers("/user/**").hasRole("USER")
	.requestMatchers("/**").permitAll().and().formLogin()
	.loginPage("/signin")
	.loginProcessingUrl("/dologin")
	.defaultSuccessUrl("/user/index");
	return h.build(); 
}


}
