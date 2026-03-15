package br.com.alvorada.barbearia.security.config;

import br.com.alvorada.barbearia.security.filter.FiltroTokenAcesso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfiguracoesSeguranca {

  private final FiltroTokenAcesso filtroTokenAcesso;

  public ConfiguracoesSeguranca(FiltroTokenAcesso filtroTokenAcesso) {
    this.filtroTokenAcesso = filtroTokenAcesso;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) {
    return http
            .authorizeHttpRequests(req -> {
                      req.requestMatchers("/login/**", "/clientes/cadastrar", "/atualizar-token").permitAll();
                      req.requestMatchers(HttpMethod.GET, "/barbeiros", "/barbeiros/ativo").permitAll();
                      req.requestMatchers("/v3/api-docs/**", "/swagger-ui/index.html", "/swagger-ui/**").permitAll();

                      req.requestMatchers("/barbeiros/**").hasRole("ADMIN");
                      req.requestMatchers(HttpMethod.GET, "/usuarios/**", "/clientes", "/agendamentos").hasRole("ADMIN");
                      req.requestMatchers(HttpMethod.PATCH, "/usuarios/perfil/**", "/clientes/{id}/reativar").hasRole("ADMIN");
                      req.requestMatchers(HttpMethod.DELETE, "/agendamentos/deletar/**").hasRole("ADMIN");

                      req.anyRequest().authenticated();
                    }
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(
                    filtroTokenAcesso,
                    UsernamePasswordAuthenticationFilter.class)
            .build();
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
          AuthenticationConfiguration authenticationConfiguration
  ) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    String hierarquia = "ROLE_ADMIN > ROLE_COLABORADOR\n" +
            "ROLE_COLABORADOR > ROLE_CLIENTE";

    return RoleHierarchyImpl.fromHierarchy(hierarquia);
  }
}
