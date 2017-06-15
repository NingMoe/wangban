package app.icity.search.engine.impl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于保存分组统计后每个字段的分组结果
 */
public class GroupField {
 /**
  * 字段名
  */
 private String name;

 /**
  * 所有可能的分组字段值，排序按每个字段值的文档个数大小排序
  */
 private List<String> values = new ArrayList<String>();

 /**
  * 保存字段值和文档个数的对应关系
  */
 private Map<String, Integer> countMap = new HashMap<String, Integer>();

 public Map<String, Integer> getCountMap() {
  return countMap;
 }

 public void setCountMap(Map<String, Integer> countMap) {
  this.countMap = countMap;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public List<String> getValues() {
  Collections.sort(values, new ValueComparator());
  return values;
 }

 public void setValues(List<String> values) {
  this.values = values;
 }

 public void addValue(String value) {
  if (value == null || "".equals(value))
   return;
   if (countMap.get(value) == null) {
    countMap.put(value, 1);
    values.add(value);
   } else {
    countMap.put(value, countMap.get(value) + 1);
   }
 }

 class ValueComparator implements Comparator<String> {

  public int compare(String value0, String value1) {
   if (countMap.get(value0) > countMap.get(value1)) {
    return -1;
   } else if (countMap.get(value0) < countMap.get(value1)) {
    return 1;
   }
   return 0;
  }
 }
}