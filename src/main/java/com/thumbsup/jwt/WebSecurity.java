package com.thumbsup.jwt;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;

import static com.thumbsup.jwt.SecurityConstants.SIGN_UP_URL;

import java.util.Arrays;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurity(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
	        .authorizeRequests()
//	        .antMatchers(HttpMethod.GET, "/api/usercheck/**").permitAll()
//	        .antMatchers("/**").permitAll()
//	        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//	        .antMatchers(HttpMethod.PUT, "/**").permitAll()
	        .antMatchers("/open/**").permitAll()
	        .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
	        .anyRequest().authenticated()        
	        .and()
	        .addFilter(new JWTAuthenticationFilter(authenticationManager()))
	        .addFilter(new JWTAuthorizationFilter(authenticationManager()))
	        // this disables session creation on Spring Security
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

	@Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
	configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", 
			"DELETE", "PATCH"));
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}