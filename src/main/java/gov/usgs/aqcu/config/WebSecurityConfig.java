package gov.usgs.aqcu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and()
			.csrf().disable()
			.cors()
			.and()
			.authorizeRequests()
				.antMatchers("/swagger-resources/**", "/v2/**", "/public").permitAll()
				.antMatchers("/version", "/info**", "/health/**", "/favicon.ico", "/swagger-ui.html").permitAll()
				.antMatchers("/actuator/health**", "/actuator/info**").permitAll()
				.anyRequest().permitAll()
			.and()
				.logout().permitAll()
		;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}
}
