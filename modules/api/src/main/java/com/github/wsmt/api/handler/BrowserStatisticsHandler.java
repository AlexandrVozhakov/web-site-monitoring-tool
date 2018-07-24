package com.github.wsmt.api.handler;

import com.github.wsmt.api.model.BrowserStatistics;
import com.github.wsmt.api.service.BrowserStatisticsService;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class BrowserStatisticsHandler implements HandlerFunction<ServerResponse> {
    private final Mono<ServerResponse> serverResponse;

    private BrowserStatisticsHandler(Mono<ServerResponse> serverResponse) {
        this.serverResponse = serverResponse;
    }

    public static BrowserStatisticsHandler byBrowserService(BrowserStatisticsService browserStatisticsService) {
        return new BrowserStatisticsHandler(
                ServerResponse.ok()
                        .body(
                                browserStatisticsService.browserStatistics(),
                                BrowserStatistics.class
                        )
        );
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {

        return serverResponse;
    }
}
