package FIT.CMS.N4.security;

import FIT.CMS.N4.entity.User;
import FIT.CMS.N4.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // if you ever use @PreAuthorize
public class SecurityConfig
{

	private final UserRepository userRepo;

	public SecurityConfig(UserRepository userRepo)
	{
		this.userRepo = userRepo;
	}

	// 1) How to load users from the database
	@Bean
	public UserDetailsService userDetailsService()
	{
		return username ->
		{
			User user = userRepo.findByUsername(username);
			if (user == null)
			{
				throw new UsernameNotFoundException("No such user: " + username);
			}
			return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
					.password(user.getPassword()).roles("ADMIN").build();
		};
	}

	// 2) Plain‐text passwords (for demo only!). Note: NoOpPasswordEncoder is
	// deprecated but still available.
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return NoOpPasswordEncoder.getInstance();
	}

	// 3) AuthenticationProvider wiring our UserDetailsService + PasswordEncoder
	@Bean
	public DaoAuthenticationProvider authProvider()
	{
		var provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	// 4) HTTP security rules
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http
				// require login for /admin/**
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/admin/**").authenticated().anyRequest().permitAll())
				// form‐based login
				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/admin/index", true).permitAll())
				// logout
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").permitAll())
				// session = until browser close
				.sessionManagement(sess -> sess.invalidSessionUrl("/login"))
				// allow static resources, CSRF on by default
				.csrf(Customizer.withDefaults());
		return http.build();
	}
}
