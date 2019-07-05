package com.atolcd.document.metadata;

import com.sun.istack.NotNull;

public class CustomProperty {
	
	public enum PropertyType {
		STRING,
		LONG,
		DOUBLE,
		BOOLEAN,
		DATE
	}

	private String name;
	private Object value;
	private PropertyType type;
	
	protected CustomProperty(@NotNull String name, @NotNull PropertyType type, Object value) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public PropertyType getType() {
		return type;
	}
	
}
