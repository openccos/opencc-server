package com.openccos.framework.core;

import com.openccos.framework.core.util.MapUtil;

import java.util.HashMap;
import java.util.Set;

/**
 * 通用map对象，用于无具体类型的传值
 * @author kevin
 */
public class CcMap extends HashMap<String, Object> {
  public CcMap() {}

  public CcMap(int initialCapacity) {
    super(initialCapacity);
  }

  public String readString(String name) {
    return MapUtil.readString(this, name, null);
  }

  public String readString(String name, String defaultValue) {
    return MapUtil.readString(this, name, defaultValue);
  }

  public Long readLong(String name) {
    return MapUtil.readLong(this, name, null);
  }

  public Long readLong(String name, Long defaultValue) {
    return MapUtil.readLong(this, name, defaultValue);
  }

  public Long[] readLongArray(String name) {
    return MapUtil.readLongArray(this, name, null);
  }

  public Long[] readLongArray(String name, Long[] defaultValue) {
    return MapUtil.readLongArray(this, name, defaultValue);
  }

  public Set<Long> readLongSet(String name) {
    return MapUtil.readLongSet(this, name);
  }

  public Integer readInt(String name) {
    return MapUtil.readInt(this, name, null);
  }

  public Integer readInt(String name, Integer defaultValue) {
    return MapUtil.readInt(this, name, defaultValue);
  }

  public Float readFloat(String name) {
    return MapUtil.readFloat(this, name, null);
  }

  public Float readFloat(String name, Float defaultValue) {
    return MapUtil.readFloat(this, name, defaultValue);
  }

  public Double readDouble(String name) {
    return MapUtil.readDouble(this, name, null);
  }

  public Double readDouble(String name, Double defaultValue) {
    return MapUtil.readDouble(this, name, defaultValue);
  }

  public Boolean readBool(String name) {
    return MapUtil.readBool(this, name, null);
  }

  public Boolean readBool(String name, Boolean defaultValue) {
    return MapUtil.readBool(this, name, defaultValue);
  }

  @Override
  public CcMap put(String name, Object value) {
    if (value != null) {
      super.put(name, value);
    } else {
      super.remove(name);
    }

    return this;
  }

  public static CcMap of(String name, Object value) {
    return new CcMap().put(name, value);
  }
}
