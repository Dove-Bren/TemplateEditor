package com.smanzana.templateeditor.test;

import java.util.LinkedList;
import java.util.List;

public class TestObject {

	private String name;
	
	public String description;
	
	public int value;
	
	public boolean enabled;
	
	public List<String> strlist;

	public TestObject(String name, String description, int value, boolean enabled) {
		super();
		this.name = name;
		this.description = description;
		this.value = value;
		this.enabled = enabled;
		
		strlist = new LinkedList<>();
	}
	
}
