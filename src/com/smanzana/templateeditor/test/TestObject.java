package com.smanzana.templateeditor.test;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;

public class TestObject {
	
	private static class NestObject {
		@DataLoaderName
		private String name;
		
		@DataLoaderData
		private int temperature;
		
		public NestObject(String name, int temperature) {
			this.name = name;
			this.temperature = temperature;
		}
	}
	
	@DataLoaderDescription
	public String description;
	
	@DataLoaderData
	public int value;

	@DataLoaderName
	private String name;
	
	@DataLoaderData(name="SecretValue",description="A super secret value")
	public boolean enabled;
	
	@DataLoaderData
	public List<String> strlist;
	
	public NestObject template;
	
	@DataLoaderData(name="Nested Stuff")
	@DataLoaderList(templateName = "template")
	public List<NestObject> nestlist;

	public TestObject(String name, String description, int value, boolean enabled) {
		super();
		this.name = name;
		this.description = description;
		this.value = value;
		this.enabled = enabled;
		template = new NestObject("NestBase", 55);
		
		strlist = new LinkedList<>();
		nestlist = new LinkedList<>();
		nestlist.add(new NestObject("Nest1", 1));
	}
	
}
