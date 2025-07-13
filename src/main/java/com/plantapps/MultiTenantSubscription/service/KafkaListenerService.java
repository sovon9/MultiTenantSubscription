package com.plantapps.MultiTenantSubscription.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import com.plantapps.MultiTenantSubscription.config.SinkManager;
import com.plantapps.MultiTenantSubscription.model.SiteEvent;

@Service
public class KafkaListenerService
{
	private final ConcurrentKafkaListenerContainerFactory<String, SiteEvent> factory;
    private final SinkManager sinkManager;
    private final KafkaListenerEndpointRegistry registry;
    private final Set<String> activeListeners = ConcurrentHashMap.newKeySet();
    
	public KafkaListenerService(ConcurrentKafkaListenerContainerFactory<String, SiteEvent> factory,
			SinkManager sinkManager, KafkaListenerEndpointRegistry registry)
	{
		super();
		this.factory = factory;
		this.sinkManager = sinkManager;
		this.registry = registry;
	}
    
	public synchronized void startKafkaListener(String siteId) {
        if (activeListeners.contains(siteId)) return;

        String topic = "site-topic-" + siteId;

        ContainerProperties props = new ContainerProperties(topic);
        props.setMessageListener((MessageListener<String, SiteEvent>) record -> {
            SiteEvent event = record.value();
            sinkManager.emitEvent(siteId, event);
        });

        ConcurrentMessageListenerContainer<String, SiteEvent> container =
                new ConcurrentMessageListenerContainer<>(factory.getConsumerFactory(), props);
        container.setBeanName("kafkaSiteListener-" + siteId);
        container.start();

        activeListeners.add(siteId);
    }
    
}
