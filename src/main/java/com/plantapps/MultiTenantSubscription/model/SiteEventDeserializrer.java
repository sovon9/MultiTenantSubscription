package com.plantapps.MultiTenantSubscription.model;

import java.io.IOException;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SiteEventDeserializrer implements Deserializer<SiteEvent>
{
	ObjectMapper mapper=new ObjectMapper();
	
	@Override
	public SiteEvent deserialize(String topic, byte[] data)
	{
		try
		{
			return mapper.readValue(data, SiteEvent.class);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

}
