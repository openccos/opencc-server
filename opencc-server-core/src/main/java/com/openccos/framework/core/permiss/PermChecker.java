package com.openccos.framework.core.permiss;

import java.util.HashMap;
import java.util.Set;

public class PermChecker extends HashMap<String, PermCheckItem> {
  private static final PermChecker CHECKER_ZERO = new PermChecker();

  private PermChecker(){
    super();
  }

  private PermChecker(int size){
    super(size);
  }

  public static PermChecker build(Set<String> permits) {
    if (permits != null) {
      PermChecker result = new PermChecker(permits.size());

      for (String perm: permits) {
        if (perm.endsWith(":*")) {
          perm = perm.substring(0, perm.length() - 2);
          result.put(perm, PermCheckItem.prefixMatch);
        } else {
          result.putIfAbsent(perm, PermCheckItem.allMatch);
        }
      }

      return result;
    }

    return CHECKER_ZERO;
  }
}
