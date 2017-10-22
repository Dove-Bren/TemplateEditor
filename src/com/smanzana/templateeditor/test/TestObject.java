package com.smanzana.templateeditor.test;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.templateeditor.api.DataLoaderData;
import com.smanzana.templateeditor.api.DataLoaderDescription;
import com.smanzana.templateeditor.api.DataLoaderName;

public class TestObject {
	
	@DataLoaderDescription
	public String description;
	
	@DataLoaderData
	public int value;

	@DataLoaderName
	private String name;
	
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
