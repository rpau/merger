/*
 * Copyright (C) 2013 Raquel Pau and Albert Coroleu.
 * 
 * Walkmod is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Walkmod is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Walkmod. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package org.walkmod.merger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MergeEngine {

  private Map<Class<?>, MergePolicy> map;

  private MergePolicy<?> defaultObjectMergePolicy = new AppendMergePolicy();

  private MergePolicy<?> defaultTypeMergePolicy = new UnmodifyMergePolicy();

  public void setPolicyConfiguration(Map<Class<?>, MergePolicy> map) {
    this.map = map;
  }

  public MergeEngine() {
    map = new HashMap<Class<?>, MergePolicy>();
  }

  public MergePolicy getMergePolicy(Object obj) {
    return getMergePolicy(obj.getClass());
  }

  public MergePolicy getMergePolicy(Class<?> clazz) {
    if (map.containsKey(clazz)) {
      return map.get(clazz);
    } else if (clazz.equals(Object.class)) {
      return null;
    } else {
      return getMergePolicy(clazz.getSuperclass());
    }
  }

  public Object apply(Object localObject, Object remoteObject, Class<?> clazz) {
    MergePolicy policy = getMergePolicy(clazz);
    if (policy == null) {
      if (Mergeable.class.isAssignableFrom(clazz)) {
        policy = defaultObjectMergePolicy;
      } else {
        policy = defaultTypeMergePolicy;
      }
    }
    return policy.apply(localObject, remoteObject);
  }

  @SuppressWarnings("unchecked")
  public void apply(List localList, List remoteList, List resultList, Class<?> genericClass) {
    if (localList == null && remoteList == null) {
      return;
    }
    if ((localList != null && localList.isEmpty()) && (remoteList != null && remoteList.isEmpty())) {
      return;
    }
    MergePolicy policy = getMergePolicy(genericClass);
    if (policy == null) {
      if (IdentificableNode.class.isAssignableFrom(genericClass)) {
        policy = defaultObjectMergePolicy;
      } else {
        policy = defaultTypeMergePolicy;
      }
    }
    if (policy instanceof MergeEngineAware) {
      ((MergeEngineAware) policy).setMergeEngine(this);
    }
    Map<Class<?>, List<?>> localSubTypesMap = buildSubTypesListMap(localList, genericClass);
    Map<Class<?>, List<?>> remoteSubTypesMap = buildSubTypesListMap(remoteList, genericClass);
    Set<Class<?>> remoteKeys = remoteSubTypesMap.keySet();
    for (Class<?> remoteKey : remoteKeys) {
      if (!localSubTypesMap.containsKey(remoteKey)) {
        localSubTypesMap.put(remoteKey, null);
      }
    }
    for (Entry<Class<?>, List<?>> entry : localSubTypesMap.entrySet()) {
      this.apply(entry.getValue(), remoteSubTypesMap.get(entry.getKey()), resultList,
          entry.getKey());
    }
    policy.apply(localList, remoteList, resultList);
  }

  private <T> Map<Class<?>, List<?>> buildSubTypesListMap(List<T> originalList,
      Class<?> genericClass) {
    Map<Class<?>, List<?>> subTypesLists = new LinkedHashMap<Class<?>, List<?>>();
    List removalList = new LinkedList();
    if (originalList != null) {
      for (Object localObject : originalList) {
        if (!localObject.getClass().equals(genericClass)) {
          List list = subTypesLists.get(localObject.getClass());
          if (list == null) {
            list = new LinkedList();
            subTypesLists.put(localObject.getClass(), list);
          }
          list.add(localObject);
          removalList.add(localObject);
        }
      }
      originalList.removeAll(removalList);
    }
    return subTypesLists;
  }

  public MergePolicy<?> getDefaultObjectMergePolicy() {
    return defaultObjectMergePolicy;
  }

  public void setDefaultObjectMergePolicy(MergePolicy<?> defaultObjectMergePolicy) {
    this.defaultObjectMergePolicy = defaultObjectMergePolicy;
  }

  public MergePolicy<?> getDefaultTypeMergePolicy() {
    return defaultTypeMergePolicy;
  }

  public void setDefaultTypeMergePolicy(MergePolicy<?> defaultTypeMergePolicy) {
    this.defaultTypeMergePolicy = defaultTypeMergePolicy;
  }
}
