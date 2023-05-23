package com.openccos.framework.core.util;

import com.openccos.framework.core.SwMap;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MapUtil {
  private MapUtil() {}

  public static String readString(Map map, String name) {
    return readString(map, name, null);
  }

  public static String readString(Map map, String name, String defaultValue) {
    Object s = map.get(name);

    if (s != null) {
      return s.toString();
    }

    return defaultValue;
  }

  public static Long readLong(Map map, String name) {
    return readLong(map, name, null);
  }

  public static Long readLong(Map map, String name, Long defaultValue) {
    Object s = map.get(name);

    if (s != null) {
      if (s instanceof Number) {
        return ((Number) s).longValue();
      } else {
        String value = s.toString();
        if (StringUtils.isNotBlank(value)) {
          return Long.parseLong(value);
        }
      }
    }

    return defaultValue;
  }

  public static Long[] readLongArray(Map map, String name) {
    return readLongArray(map, name, null);
  }

  public static Long[] readLongArray(Map map, String name, Long[] defaultValue) {
    Object value = map.get(name);

    if (value != null) {
      if (value instanceof Long[]) {
        return (Long[]) value;
      } else if (value instanceof Object[]) {
        Object[] items = (Object[])value;
        List<Long> result = new ArrayList<>(items.length);

        for (Object item: items) {
          if (item != null) {
            String s = item.toString();
            if (StringUtils.isNotBlank(s)) {
              result.add(Long.valueOf(s));
            }
          }
        }

        if (result.size() > 0) {
          return result.toArray(new Long[result.size()]);
        }
      } else if (value instanceof List) {
        List items = (List)value;
        List<Long> result = new ArrayList<>(items.size());

        for (Object item: items) {
          if (item != null) {
            String s = item.toString();
            if (StringUtils.isNotBlank(s)) {
              result.add(Long.valueOf(s));
            }
          }
        }

        if (result.size() > 0) {
          return result.toArray(new Long[result.size()]);
        }
      } else if (value instanceof String) {
        String[] ary = value.toString().split(",");
        if (ary.length > 0) {
          List<Long> result = new ArrayList<>(ary.length);

          for (String item: ary) {
            if (StringUtils.isNotBlank(item)) {
              result.add(Long.valueOf(item));
            }
          }

          if (result.size() > 0) {
            return result.toArray(new Long[result.size()]);
          }
        }
      }
    }

    return defaultValue;
  }

  public static Integer readInt(Map map, String name) {
    return readInt(map, name, null);
  }

  public static Integer readInt(Map map, String name, Integer defaultValue) {
    Object s = map.get(name);

    if (s != null) {
      if (s instanceof Number) {
        return ((Number) s).intValue();
      } else {
        String value = s.toString();
        if (StringUtils.isNotBlank(value)) {
          return Integer.parseInt(value);
        }
      }
    }

    return defaultValue;
  }

  public static Float readFloat(Map map, String name) {
    return readFloat(map, name, null);
  }

  public static Float readFloat(Map map, String name, Float defaultValue) {
    Object s = map.get(name);

    if (s != null) {
      if (s instanceof Number) {
        return ((Number) s).floatValue();
      } else {
        String value = s.toString();
        if (StringUtils.isNotBlank(value)) {
          return Float.parseFloat(value);
        }
      }
    }

    return defaultValue;
  }

  public static Double readDouble(Map map, String name) {
    return readDouble(map, name, null);
  }

  public static Double readDouble(Map map, String name, Double defaultValue) {
    Object s = map.get(name);

    if (s != null) {
      if (s instanceof Number) {
        return ((Number) s).doubleValue();
      } else {
        String value = s.toString();
        if (StringUtils.isNotBlank(value)) {
          return Double.parseDouble(value);
        }
      }
    }

    return defaultValue;
  }

  public static Boolean readBool(Map map, String name) {
    return readBool(map, name, null);
  }

  public static Boolean readBool(Map map, String name, Boolean defaultValue) {
    Object s = map.get(name);

    if (s != null) {
      if (s instanceof Boolean) {
        return (Boolean) s;
      } else {
        String value = s.toString();
        if ("true".equalsIgnoreCase(value)) {
          return Boolean.TRUE;
        }
      }
    }

    return defaultValue;
  }

  public static Set<Long> readLongSet(SwMap swMap, String name) {
    Object value = swMap.get(name);
    if (value != null) {
      String[] ary = value.toString().split(",");
      if (ary.length > 0) {
        Set<Long> result = new HashSet<>(ary.length);

        for (String item : ary) {
          if (StringUtils.isNotBlank(item)) {
            result.add(Long.valueOf(item));
          }
        }

        return result;
      }
    }

    return null;
  }
}
