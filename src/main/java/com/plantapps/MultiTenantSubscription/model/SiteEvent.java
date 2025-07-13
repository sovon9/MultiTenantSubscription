package com.plantapps.MultiTenantSubscription.model;

import java.time.Instant;

public class SiteEvent
{
	private Long id;
	private String data;
	private String eventType;
	private Instant timeStamp;
	public SiteEvent()
	{
		super();
	}
	public SiteEvent(Long id, String data)
	{
		super();
		this.id = id;
		this.data = data;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getData()
	{
		return data;
	}
	public void setData(String data)
	{
		this.data = data;
	}
	public String getEventType()
	{
		return eventType;
	}
	public void setEventType(String eventType)
	{
		this.eventType = eventType;
	}
	public Instant getTimeStamp()
	{
		return timeStamp;
	}
	public void setTimeStamp(Instant timeStamp)
	{
		this.timeStamp = timeStamp;
	}
}
