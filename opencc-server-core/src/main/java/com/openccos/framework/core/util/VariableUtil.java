package com.openccos.framework.core.util;

/**
 * 变量命名规则工具类
 */
public class VariableUtil {
  private VariableUtil() {}

  /***
   * 下划线命名转为驼峰命名
   *
   * @param para
   *        下划线命名的字符串
   */
  public static String underlineToHump(String para){
    StringBuilder result = new StringBuilder();
    String[] a = para.split("_");
    for(String s:a){
      if(result.length()==0){
        result.append(s.toLowerCase());
      }else{
        result.append(s.substring(0, 1).toUpperCase());
        result.append(s.substring(1).toLowerCase());
      }
    }
    return result.toString();
  }

  /***
   * 下划线命名转为大写驼峰命名
   *   大驼峰式命名法(upper camel case)
   *
   * @param para
   *        下划线命名的字符串
   */
  public static String underlineToUpperHump(String para){
    StringBuilder result=new StringBuilder();
    String[] a = para.split("_");
    for(String s : a){
      result.append(s.substring(0, 1).toUpperCase());
      result.append(s.substring(1).toLowerCase());
    }
    return result.toString();
  }


  /***
   * 驼峰命名转为下划线命名
   *
   * @param para
   *        驼峰命名的字符串
   */
  public static String humpToUnderline(String para){
    StringBuilder sb = new StringBuilder(para);
    //偏移量，第i个下划线的位置是 当前的位置+ 偏移量（i-1）,第一个下划线偏移量是0
    int temp = 0;
    for(int i = 0; i<para.length(); i++){
      if(Character.isUpperCase(para.charAt(i))){
        sb.insert(i+temp, "_");
        temp ++;
      }
    }
    return sb.toString().toLowerCase();
  }

//  /***
//   * 下划线命名转为驼峰命名
//   *
//   * @param para
//   *        下划线命名的字符串
//   */
//  public static String UnderlineToHump(String para) {
//    StringBuilder result = new StringBuilder();
//    String a[] = para.split("_");
//    for (String s : a) {
//      if (!para.contains("_")) {
//        result.append(s);
//        continue;
//      }
//      if (result.length() == 0) {
//        result.append(s.toLowerCase());
//      } else {
//        result.append(s.substring(0, 1).toUpperCase());
//        result.append(s.substring(1).toLowerCase());
//      }
//    }
//    return result.toString();
//  }
//
//
//  /***
//   * 驼峰命名转为下划线命名
//   *
//   * @param para
//   *        驼峰命名的字符串
//   */
//
//  public static String HumpToUnderline(String para) {
//    StringBuilder sb = new StringBuilder(para);
//    int temp = 0;//定位
//    if (!para.contains("_")) {
//      for (int i = 0; i < para.length(); i++) {
//        if (Character.isUpperCase(para.charAt(i))) {
//          sb.insert(i + temp, "_");
//          temp += 1;
//        }
//      }
//    }
//    return sb.toString().toUpperCase();
//  }
}
