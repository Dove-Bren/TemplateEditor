package com.smanzana.templateeditor.test;

import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;

public class SimpleObject {
	
	@DataLoaderDescription
	public String description;
	
	@DataLoaderData
	public int value;

	@DataLoaderName
	private String name;
	
	@DataLoaderData(name="SecretValue",description="A super secret value")
	public boolean enabled;
	
//	@DataLoaderData
//	public List<String> strlist;
	
	public SimpleObject(String name, String description, int value, boolean enabled) {
		super();
		this.name = name;
		this.description = description;
		this.value = value;
		this.enabled = enabled;
		
//		strlist = new LinkedList<>();
	}
	
	@Override
	public String toString() {
		return "Name: " + name + "\t"
			 + "Description: " + description + "\t"
			 + "Value: " + value + "\t"
			 + "Enabled: " + enabled + "\t";
	}
	
}
