package com.plantapps.MultiTenantSubscription.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.plantapps.MultiTenantSubscription.model.SiteEvent;

import reactor.core.publisher.Sinks;

@Component
public class SinkManager
{
	private final Map<String, Sinks.Many<SiteEvent>> sinkMap = new ConcurrentHashMap<>();

    public Sinks.Many<SiteEvent> getSink(String siteId) {
        return sinkMap.computeIfAbsent(siteId,
                id -> Sinks.many().multicast().onBackpressureBuffer());
    }

    public void emitEvent(String siteId, SiteEvent event) {
        Sinks.Many<SiteEvent> sink = sinkMap.get(siteId);
        if (sink != null) sink.tryEmitNext(event);
    }
}
