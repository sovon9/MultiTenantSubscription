package com.plantapps.MultiTenantSubscription.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

import com.plantapps.MultiTenantSubscription.config.SinkManager;
import com.plantapps.MultiTenantSubscription.model.SiteEvent;
import com.plantapps.MultiTenantSubscription.service.KafkaListenerService;

import reactor.core.publisher.Flux;

@Controller
public class EventSubscriptionController
{
	 private final SinkManager sinkManager;
	    private final KafkaListenerService kafkaListenerService;

	    public EventSubscriptionController(SinkManager sinkManager, KafkaListenerService kafkaListenerService) {
	        this.sinkManager = sinkManager;
	        this.kafkaListenerService = kafkaListenerService;
	    }

//	    @SubscriptionMapping
//	    public Flux<SiteEvent> event(@ContextValue("site-id") String siteId) {
//	    	//String siteId = "site_1";
//	        kafkaListenerService.startKafkaListener(siteId);
//	        return sinkManager.getSink(siteId).asFlux();
//	    }
	    
	    @SubscriptionMapping
	    public Flux<SiteEvent> event(@ContextValue("site-id") String siteId, @Argument Map<String,Object> where) {
	    	//String siteId = "site_1";
	        kafkaListenerService.startKafkaListener(siteId);
	        return sinkManager.getSink(siteId).asFlux().filter(event->filterEvent(event, where));
	    }

		private boolean filterEvent(SiteEvent event, Map<String,Object> filter)
		{
			for (Map.Entry<String, Object> entry : filter.entrySet()) {
		        String key = entry.getKey();         // e.g., "eventDate", "eventType", "and", "or"
		        Object value = entry.getValue();     // A map (for comparison), or list (for and/or)

		        if ("and".equals(key) || "or".equals(key)) {
		            List<Map<String, Object>> subFilters = (List<Map<String, Object>>) value;

		            boolean subResult = "and".equals(key)
		                ? subFilters.stream().allMatch(sub -> filterEvent(event, sub))
		                : subFilters.stream().anyMatch(sub -> filterEvent(event, sub));

		            if (!subResult) return false;   // If AND/OR fails, skip this event
		            continue;
		        }

		        if (!(value instanceof Map)) continue;

		        Map<String, Object> comparison = (Map<String, Object>) value;
		        Object fieldValue = extractFieldValue(event, key);   // e.g., event.getEventDate()

		        if (!evaluateComparison(fieldValue, comparison)) {
		            return false;
		        }
		    }

		    return true;
		}
		
		private Object extractFieldValue(SiteEvent event, String fieldName) {
		    switch (fieldName) {
		        case "eventDate":
		            return event.getTimeStamp();  // assuming `eventDate` maps to `timestamp`
		        case "eventType":
		            return event.getEventType();
		        // Add more fields here as needed
		        default:
		            return null;
		    }
		}
		
		private boolean evaluateComparison(Object fieldValue, Map<String, Object> comparison) {
		    if (fieldValue == null) return false;

		    for (Map.Entry<String, Object> comp : comparison.entrySet()) {
		        String op = comp.getKey();             // "eq", "gt", "lt"
		        Object compValue = comp.getValue();    // Value to compare against

		        if (fieldValue instanceof Instant) {
		            Instant field = (Instant) fieldValue;
		            Instant compInstant = Instant.parse(compValue.toString());

		            switch (op) {
		                case "eq": if (!field.equals(compInstant)) return false; break;
		                case "gt": if (!field.isAfter(compInstant)) return false; break;
		                case "lt": if (!field.isBefore(compInstant)) return false; break;
		            }

		        } else if (fieldValue instanceof String) {
		            String field = fieldValue.toString();
		            String compStr = compValue.toString();

		            if ("eq".equals(op) && !field.equalsIgnoreCase(compStr)) return false;

		            // Extend with contains, startsWith, etc. if needed
		        }

		        // Add support for other types: numbers, booleans, etc. if needed
		    }

		    return true;
		}


}
