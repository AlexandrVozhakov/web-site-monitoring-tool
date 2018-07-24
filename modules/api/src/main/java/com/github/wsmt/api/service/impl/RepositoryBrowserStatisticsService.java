package com.github.wsmt.api.service.impl;

import com.github.wsmt.api.model.BrowserStatistics;
import com.github.wsmt.api.repository.BrowserStatisticsRepository;
import com.github.wsmt.api.service.BrowserStatisticsService;
import reactor.core.publisher.Flux;

public class RepositoryBrowserStatisticsService implements BrowserStatisticsService {
    private final BrowserStatisticsRepository browserStatisticsRepository;

    private RepositoryBrowserStatisticsService(BrowserStatisticsRepository browserStatisticsRepository) {
        this.browserStatisticsRepository = browserStatisticsRepository;
    }

    public static BrowserStatisticsService byRepository(BrowserStatisticsRepository browserStatisticsRepository) {
        return new RepositoryBrowserStatisticsService(browserStatisticsRepository);
    }

    @Override
    public Flux<BrowserStatistics> browserStatistics() {
        return Flux.fromIterable(
                browserStatisticsRepository.findAll()
        );
    }
}
