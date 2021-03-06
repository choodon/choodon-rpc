package com.choodon.rpc.base.common;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ConcurrentHashSet<E> extends AbstractSet<E> implements Serializable {

	private static final long serialVersionUID = 5568484093098961308L;

	private final ConcurrentMap<E, Boolean> map;

	public ConcurrentHashSet() {
		map = new ConcurrentHashMap<>();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean add(E o) {
		return map.putIfAbsent(o, Boolean.TRUE) == null;
	}



	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}
}