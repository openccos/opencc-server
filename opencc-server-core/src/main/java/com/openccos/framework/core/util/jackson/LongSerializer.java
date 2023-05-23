package com.openccos.framework.core.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class LongSerializer extends JsonSerializer<Long> {

	@Override
	public void serialize(Long value, JsonGenerator gen,
			SerializerProvider serializers) throws IOException{
		if (value == null) {
			gen.writeNull();
		} else {
      gen.writeString(value.toString());
		}
	}
}
