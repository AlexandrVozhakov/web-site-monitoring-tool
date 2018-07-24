package com.github.wsmt.api.config;

import com.github.wsmt.api.handler.BrowserStatisticsHandler;
import com.github.wsmt.api.repository.BrowserStatisticsRepository;
import com.github.wsmt.api.service.BrowserStatisticsService;
import com.github.wsmt.api.service.impl.RepositoryBrowserStatisticsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class BrowserConfig {
    @Bean
    public RouterFunction<ServerResponse> browserStatisticsRouter(BrowserStatisticsHandler browserStatisticsHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/browser"), browserStatisticsHandler);
    }

    @Bean
    public BrowserStatisticsHandler browserStatisticsHandler(BrowserStatisticsService browserStatisticsService) {
        return BrowserStatisticsHandler.byBrowserService(browserStatisticsService);
    }

    @Bean
    BrowserStatisticsService browserStatisticsService(BrowserStatisticsRepository browserStatisticsRepository) {
        return RepositoryBrowserStatisticsService.byRepository(browserStatisticsRepository);
    }
}
