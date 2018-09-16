package com.pp.toptal.soccermanager.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@PropertySource("classpath:/internal.properties")
@ImportResource("classpath:/spring-security.xml") // TODO: rewrite to support configuration over annotations only
public class SecurityConfiguration {
    /**
     * Bean for switching off auto-configuration
     * @return
     */
    @Bean
    public AuthorizationServerConfigurer getAuthorizationServerConfigurer() {
        return new AuthorizationServerConfigurerAdapter();
    }

    @Bean("tokenStore")
    public JdbcTokenStore getTokenStore(DataSource datasource) {

        JdbcTokenStore tokenStore = new JdbcTokenStore(datasource);
        tokenStore.setSelectAccessTokenSql(
                "SELECT token_id, token FROM oauth_access_token WHERE token_id = ? FOR UPDATE");
        tokenStore.setSelectAccessTokenFromAuthenticationSql (
                "SELECT token_id, token FROM oauth_access_token WHERE authentication_id = ? FOR UPDATE");
        
        return tokenStore;
    }
    
    @Bean("passwordEncoder")
    public PasswordEncoder getPasswordEncoder() {
        return new StandardPasswordEncoder("EinTannenbaumStehtEinsam!");
    }
    
}
