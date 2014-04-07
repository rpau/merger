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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class AppendMergePolicy<T extends IdentificableNode>
		extends
			ObjectMergePolicy<T> implements MergeEngineAware {

	private MergeEngine mergeConfiguration;

	@SuppressWarnings("unchecked")
	@Override
	public void apply(T localObject, T remoteObject, List<T> resultList) {
		if (localObject == null) {
			resultList.add(remoteObject);
		} else {
			if(localObject instanceof Mergeable){
				((Mergeable)localObject).merge(remoteObject, mergeConfiguration);
			}
			resultList.add(localObject);
		}
	}

	@Override
	public void setMergeEngine(MergeEngine mergeConfiguration) {
		this.mergeConfiguration = mergeConfiguration;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MergeEngine getMergeEngine() {
		return mergeConfiguration;
	}

	@Override
	public T apply(T localObject, T remoteObject) {
		if (localObject == null) {
			return remoteObject;
		} else {
			if(localObject instanceof Mergeable){
				((Mergeable)localObject).merge(remoteObject, mergeConfiguration);
			}
			return localObject;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void apply(List<T> localList, List<T> remoteList,
			@SuppressWarnings("rawtypes") List resultList) {
		if (remoteList != null) {
			//this list only permits to add elements without affecting the final result
			List<T> updatedLocalObjects = new LinkedList<T>();
			//this list permits to know which elements must be appended
			List<T> addedRemoteObjects = new LinkedList<T>();
			for (T remoteObject : remoteList) {
				Comparator<?> comparator = remoteObject.getIdentityComparator();
				@SuppressWarnings("unchecked")
				T localObject = (T) CollectionUtil.findObject(localList,
						remoteObject, comparator);
				if (localObject != null) {
					this.apply((T) localObject, remoteObject,
							updatedLocalObjects);
				} else {
					this.apply((T) localObject, remoteObject,
							addedRemoteObjects);
				}
			}
			if (localList != null) {
				for (T local : localList) {
					//inner local contents must be updated previously by recursivity
					resultList.add(local);
				}
			}
			for (T remoteAdded : addedRemoteObjects) {
				resultList.add(remoteAdded);
			}
		}
		else{
			resultList.addAll(localList);
		}
		
	}
	
}
