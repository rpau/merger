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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class CollectionUtil {

  public static Object findObject(Collection<?> c, Object o, Comparator cmp) {
    if (c != null && o != null && cmp != null) {
      Iterator<?> it = c.iterator();
      while (it.hasNext()) {
        Object current = it.next();
        if (o.getClass().equals(current.getClass())) {
          if (cmp.compare(current, o) == 0) {
            return current;
          }
        }
      }
    }
    return null;
  }
}
