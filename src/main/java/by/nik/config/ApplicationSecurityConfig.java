package by.nik.config;

import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;


@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().httpBasic();

    }

/*
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authentication)
            throws Exception
    {
        authentication.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder().encode("nida"))
                .authorities("ROLE_USER");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
*/


// by me

/*    @Bean(name = "dataSource")
    public BasicDataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost/leverxDB");
        ds.setUsername("postgres");
        ds.setPassword("admin");
        return ds;
    }
*/


 /*       PGSimpleDataSource ds = new PGSimpleDataSource();
    {
            ds.setServerName("localhost");
            ds.setDatabaseName("leverxDB");
            ds.setUser("postgres");
            ds.setPassword("admin");
            ds.setSslMode("require");
            ds.setSslfactory("org.postgresql.ssl.NonValidatingFactory");
    }
*/
    /*BasicDataSource ds = new BasicDataSource();

    {
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost/leverxDB");
        ds.setUsername("postgres");
        ds.setPassword("admin");
    }
*/
    @Bean
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }


    final
    DataSource dataSource;
    public ApplicationSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery(
                        "select email, password, 'true' from users where email=?")
                .authoritiesByUsernameQuery(
                        "select email, role from users where email=?");
    }
//return NoOpPasswordEncoder.getInstance();
// .passwordEncoder(passwordEncoder())
// user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
/*
********************************************************************
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(
                        "select login, password, 'true' from my_user " +
                                "where login=?")
                .authoritiesByUsernameQuery(
                        "select login, authority from my_user " +
                                "where login=?");
    }********************************************************************
 */

}
