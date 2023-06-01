package com.openccos.framework.core.permiss;

public class PermCheckItem {
  // 全匹配
  private static final int TYPE_ALL_MATCH = 0;
  // 前缀匹配
  private static final int TYPE_PREFIX_MATCH = 1;

  private int type;

  public static final PermCheckItem allMatch = new PermCheckItem(TYPE_ALL_MATCH);
  public static final PermCheckItem prefixMatch = new PermCheckItem(TYPE_PREFIX_MATCH);

  private PermCheckItem(int type) {
    this.type = type;
  }

  public boolean isPrefixMath() {
    return this.type == TYPE_PREFIX_MATCH;
  }
}
