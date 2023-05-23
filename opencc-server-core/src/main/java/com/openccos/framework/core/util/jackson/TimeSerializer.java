package com.openccos.framework.core.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

public class TimeSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator gen,
			SerializerProvider serializers) throws IOException,
            JsonProcessingException {
		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeString(toString(value));
//			gen.writeNumber(value);
//			gen.writeString("\"");
		}
	}

    @SuppressWarnings("deprecation")
    public String toString (Date value) {
        int hour = value.getHours();
        int minute = value.getMinutes();
        int second = value.getSeconds();
        String hourString;
        String minuteString;
        String secondString;
        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = Integer.toString(hour);
        }
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = Integer.toString(minute);
        }
        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = Integer.toString(second);
        }

        // do a string buffer here instead.
        StringBuffer timestampBuf = new StringBuffer(8);
        timestampBuf.append(hourString);
        timestampBuf.append(":");
        timestampBuf.append(minuteString);
        timestampBuf.append(":");
        timestampBuf.append(secondString);

        return (timestampBuf.toString());
    }

}
