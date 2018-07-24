package com.github.wsmt.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

@Configuration
public class IndexFilterConfig {
    @Bean
    public WebFilter indexFilter() {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String path = request.getURI()
                    .getPath();

            if (path.charAt(path.length() - 1) == '/') {
                return chain.filter(exchange.mutate()
                        .request(request.mutate()
                                .path(path + "index.html")
                                .build())
                        .build());
            }

            return chain.filter(exchange);
        };
    }
}
