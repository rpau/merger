/* 
  Copyright (C) 2013 Raquel Pau and Albert Coroleu.
 
 Walkmod is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 Walkmod is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/
package org.walkmod.merger;

import java.util.List;

public class OverrideMergePolicy<T extends Mergeable> extends
		ObjectMergePolicy<T> {

	@Override
	public void apply(T localObject, T remoteObject, List<T> resultList) {
		if (remoteObject != null) {
			resultList.add(remoteObject);
		}
	}

	@Override
	public T apply(T localObject, T remoteObject) {
		return remoteObject;
	}
}
