package com.openccos.framework.core.util.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class LongDeserializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        if (jsonParser != null && StringUtils.isNotBlank(jsonParser.getText())) {
            try {
                return Long.valueOf(jsonParser.getText());
            } catch (NumberFormatException e) {
                throw new InvalidFormatException(jsonParser, e.getMessage(), jsonParser.getText(), Long.class);
            }
        } else {
            return null;
        }
    }
}
