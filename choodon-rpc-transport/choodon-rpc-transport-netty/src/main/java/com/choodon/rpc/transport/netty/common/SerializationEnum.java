package com.choodon.rpc.transport.netty.common;
public enum SerializationEnum {
	defaultSerialization((byte) 1), 
	protostuff((byte) 1), 
	kryo((byte) 2), 
	json((byte) 3),
	serialization1("protostuff"), 
	serialization2("kryo"), 
	serialization3("json");
	private Object value;

	private SerializationEnum(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
