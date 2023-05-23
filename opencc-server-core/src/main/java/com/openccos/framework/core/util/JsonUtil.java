package com.openccos.framework.core.util;

import com.openccos.framework.core.exception.JsonParseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openccos.framework.core.util.jackson.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

@Slf4j
public class JsonUtil {
  public final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public final static ObjectMapper API_OBJECT_MAPPER = new ObjectMapper();

  static {
    init(OBJECT_MAPPER);
    // 不序列化空值
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
//        // 设置将MAP转换为JSON时候只转换值不等于NULL的
//        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

    init(API_OBJECT_MAPPER);
    API_OBJECT_MAPPER.getSerializerProvider().setNullValueSerializer(new NullSerializer());
  }

  private static void init(ObjectMapper mapper) {
    // JSON转化为对象时忽略未对应的属性
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    SimpleModule module = new SimpleModule();
    LongSerializer longSerializer = new LongSerializer();
    module.addSerializer(Long.class, longSerializer);
    module.addSerializer(Long.TYPE, longSerializer);

    DateSerializer dateSerializer = new DateSerializer();
    module.addSerializer(java.sql.Date.class, dateSerializer);
    TimeSerializer timeSerializer = new TimeSerializer();
    module.addSerializer(java.sql.Time.class, timeSerializer);

    module.addDeserializer(Timestamp.class, new TimestampDeserializer());
    module.addDeserializer(java.util.Date.class, new DateDeserializer());

    mapper.registerModule(module);
  }

  public static JsonNode readTree(String body) {
    try {
      return OBJECT_MAPPER.readTree(body);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }

    return null;
  }

  // 创建新节点
  public static ObjectNode createObjectNode() {
    return OBJECT_MAPPER.createObjectNode();
  }

  // 将 JsonNode 对象转成 json
  public static String writeValueAsString(JsonNode newNode) {
    try {
      return OBJECT_MAPPER.writeValueAsString(newNode);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
    }

    return null;
  }

  // 将对象存入输出流
  public static void writeValue(OutputStream os, Object object) {
    try {
      OBJECT_MAPPER.writeValue(os, object);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  // 将对象存入文件
  public static void writeValue(File file, Object object) {
    try {
      OBJECT_MAPPER.writeValue(file, object);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static <T> T parse(String str, Class<T> clazz) {
    try {
      if (StringUtils.isBlank(str)) {
        return null;
      } else {
        return OBJECT_MAPPER.readValue(str, clazz);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to " + clazz + " type", e);
    }
  }

  public static <T> T parse(String str, TypeReference<T> typeReference) {
    try {
      if (StringUtils.isBlank(str)) {
        return null;
      } else {
        return OBJECT_MAPPER.readValue(str, typeReference);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to " + typeReference + " typeRef", e);
    }
  }
  /**
   * 将Map对象转换为Bean对象
   * @param map Map对象
   * @param clazz Bean对象类
   * @param <T> Bean对象类型
   * @return Bean对象实例
   */
  public static <T> T parse(Map map, Class<T> clazz) {
    try {
      if (map == null || map.isEmpty()) {
        return null;
      } else {
        // 对象实例化
        T bean = BeanUtils.instantiateClass(clazz);
        // 循环设置对象属性
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
          String properName = propertyDescriptor.getName();
          // 过滤class属性
          if (!"class".equals(properName)) {
            if (map.containsKey(properName)) {
              Method writeMethod = propertyDescriptor.getWriteMethod();
              if (writeMethod != null) {
                Object value = map.get(properName);
                if (!writeMethod.isAccessible()) {
                  writeMethod.setAccessible(true);
                }
                // 处理JSON对象的Long是字符格式的情况
                if (value instanceof String) {
                  Class<?> parameterType = writeMethod.getParameterTypes()[0];
                  switch (parameterType.getSimpleName()) {
                    case "long":
                      value = toLong((String)value, 0L);
                      break;
                    case "Long":
                      value = toLong((String)value, null);
                      break;
                    case "int":
                      value = toInt((String)value, 0);
                      break;
                    case "Integer":
                      value = toInt((String)value, null);
                      break;
                  }
                }

                writeMethod.invoke(bean, value);
              }
            }
          }
        }

        return bean;
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert map to " + clazz + " type; " + e.getMessage(), e);
    }
  }

  private static Long toLong(String value, Long defaultValue) {
    if (StringUtils.isNotBlank(value)) {
      return Long.valueOf(value);
    } else {
      return defaultValue;
    }
  }

  private static Integer toInt(String value, Integer defaultValue) {
    if (StringUtils.isNotBlank(value)) {
      return Integer.valueOf(value);
    } else {
      return defaultValue;
    }
  }

  public static <T> T parse(InputStream is, Class<T> clazz) {
    try {
      if (is == null) {
        return null;
      } else {
        return OBJECT_MAPPER.readValue(is, clazz);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to " + clazz + " type", e);
    }
  }

  public static <T> T parse(byte[] str, Class<T> clazz) {
    try {
      if (str == null || str.length == 0) {
        return null;
      } else {
        return OBJECT_MAPPER.readValue(str, clazz);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to " + clazz + " type", e);
    }
  }

  public static JsonNode parse(String str) {
    try {
      if (StringUtils.isBlank(str)) {
        return null;
      } else {
        return OBJECT_MAPPER.readTree(str);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to JsonNode", e);
    }
  }

  public static <T> T parse(File file, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(file, clazz);
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to " + clazz + " type", e);
    }
  }


  /**
   * 转换json文本为对象列表
   * @param is json文本流
   * @param <T> 列表中对象类
   * @param clazz 列表中的对象类
   * @return 列表对象
   */
  public static <T> List<T> parseList(InputStream is, Class<T> clazz) {
    try {
      if (is == null) {
        return null;
      } else {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        return OBJECT_MAPPER.readValue(is, javaType);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to list<T> type", e);
    }
  }

  /**
   * 转换json文本为对象列表
   * @param str json文本字节数组
   * @param <T> 列表中对象类
   * @param clazz 列表中的对象类
   * @return 列表对象
   */
  public static <T> List<T> parseList(byte[] str, Class<T> clazz) {
    try {
      if (str == null || str.length == 0) {
        return null;
      } else {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        return OBJECT_MAPPER.readValue(str, javaType);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to list<T> type", e);
    }
  }

  /**
   * 转换json文本为对象列表
   * @param str json文本
   * @param <T> 列表中对象类
   * @param clazz 列表中的对象类
   * @return 列表对象
   */
  public static <T> List<T> parseList(String str, Class<T> clazz) {
    try {
      if (StringUtils.isBlank(str)) {
        return null;
      } else {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clazz);
        return OBJECT_MAPPER.readValue(str, javaType);
      }
    } catch (Exception e) {
      throw new JsonParseException("can't convert this json to list<" + clazz.getSimpleName() + "> type", e);
    }
  }

  public static String encodeString(Object obj) {
    try {
      return OBJECT_MAPPER.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new JsonParseException("can't convert type " + obj.getClass() + " to json string", e);
    }
  }

  public static byte[] encodeBytes(Object obj) {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(obj);
    } catch (JsonProcessingException e) {
      throw new JsonParseException("can't convert type " + obj.getClass() + " to json string", e);
    }
  }
}
