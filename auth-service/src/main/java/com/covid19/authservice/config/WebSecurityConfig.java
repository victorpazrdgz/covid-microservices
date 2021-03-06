package com.covid19.authservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private JwtAuthEntryPoint jwtAuthenticationEntryPoint;
        @Autowired
        private UserDetailsService jwtUserDetailsService;
        @Autowired
        private JwtRequestFilter jwtRequestFilter;
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
// configure AuthenticationManager so that it knows from where to load
// user for matching credentials
// Use BCryptPasswordEncoder
            auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
            System.out.println( auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder()));
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

   /* @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST","PUT", "DELETE");
            }
        };
    }*/
        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
// We don't need CSRF for this example
            httpSecurity.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/users/login").permitAll()
                    .antMatchers("/users/register").permitAll()
                    .antMatchers("/users/ovejitasDolly").permitAll()
                    .antMatchers("/users/getAll").permitAll()
                    .antMatchers("/users/getJson").permitAll()
                    /**
                     * Lo siguiente viene copiado de stack overflow, lo dejo comentado a modo de inspiración
                     * para nuestro y futuro
                     */
//                    .antMatchers("/high_level_url_A/sub_level_2").hasRole('USER2')
//                    .somethingElse() // for /high_level_url_A/**
//                    .antMatchers("/high_level_url_A/**").authenticated()
//                    .antMatchers("/high_level_url_B/sub_level_1").permitAll()
//                    .antMatchers("/high_level_url_B/sub_level_2").hasRole('USER3')
//                    .somethingElse() // for /high_level_url_B/**
//                    .antMatchers("/high_level_url_B/**").authenticated()
//                    .anyRequest().permitAll()


// all other requests need to be authenticated
       .anyRequest().authenticated().and().
// make sure we use stateless session; session won't be used to
// store user's state.
        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
// Add a filter to validate the tokens with every request
            httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        }
}
