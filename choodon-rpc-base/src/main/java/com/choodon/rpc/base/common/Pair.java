package com.choodon.rpc.base.common;

public class Pair<Left, Right> {

	private Left left;
	private Right right;

	public static <T1, T2> Pair<T1, T2> bulid(T1 left, T2 right) {
		return new Pair<>(left, right);
	}

	private Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}

	public Left getLeft() {
		return left;
	}

	public void setLeft(Left left) {
		this.left = left;
	}

	public Right getRight() {
		return right;
	}

	public void setRight(Right right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return "Pair{" + "left=" + left + ", right=" + right + '}';
	}
}