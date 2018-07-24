package com.github.wsmt.api.service;

import com.github.wsmt.api.model.BrowserStatistics;
import reactor.core.publisher.Flux;

public interface BrowserStatisticsService {
    Flux<BrowserStatistics> browserStatistics();
}
