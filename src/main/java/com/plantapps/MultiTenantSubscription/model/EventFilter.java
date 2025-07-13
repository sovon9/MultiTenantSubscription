package com.plantapps.MultiTenantSubscription.model;

import java.time.Instant;
import java.util.List;

public class EventFilter
{
	private List<String> eventTypes;
    private Instant timeAfter;
    private Instant timeBefore;
    private List<EventFilter> and;
    private List<EventFilter> or;
	public List<String> getEventTypes()
	{
		return eventTypes;
	}
	public void setEventTypes(List<String> eventTypes)
	{
		this.eventTypes = eventTypes;
	}
	public Instant getTimeAfter()
	{
		return timeAfter;
	}
	public void setTimeAfter(Instant timeAfter)
	{
		this.timeAfter = timeAfter;
	}
	public Instant getTimeBefore()
	{
		return timeBefore;
	}
	public void setTimeBefore(Instant timeBefore)
	{
		this.timeBefore = timeBefore;
	}
	public List<EventFilter> getAnd()
	{
		return and;
	}
	public void setAnd(List<EventFilter> and)
	{
		this.and = and;
	}
	public List<EventFilter> getOr()
	{
		return or;
	}
	public void setOr(List<EventFilter> or)
	{
		this.or = or;
	}
    
}
