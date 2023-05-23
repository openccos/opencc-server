package com.openccos.framework.core.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {

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
        int year = value.getYear() + 1900;
        int month = value.getMonth() + 1;
        int day = value.getDate();
        String yearString;
        String monthString;
        String dayString;
        String yearZeros = "0000";
        StringBuffer timestampBuf;

        if (year < 1000) {
            // Add leading zeros
            yearString = "" + year;
            yearString = yearZeros.substring(0, (4-yearString.length())) +
                yearString;
        } else {
            yearString = "" + year;
        }
        if (month < 10) {
            monthString = "0" + month;
        } else {
            monthString = Integer.toString(month);
        }
        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = Integer.toString(day);
        }

        // do a string buffer here instead.
        timestampBuf = new StringBuffer(10);
        timestampBuf.append(yearString);
        timestampBuf.append("-");
        timestampBuf.append(monthString);
        timestampBuf.append("-");
        timestampBuf.append(dayString);

        return (timestampBuf.toString());
    }

}
