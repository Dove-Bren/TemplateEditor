package com.smanzana.templateeditor.test;

import java.util.LinkedList;
import java.util.List;

import com.smanzana.templateeditor.api.ICustomData;
import com.smanzana.templateeditor.api.annotations.DataLoaderData;
import com.smanzana.templateeditor.api.annotations.DataLoaderDescription;
import com.smanzana.templateeditor.api.annotations.DataLoaderFactory;
import com.smanzana.templateeditor.api.annotations.DataLoaderList;
import com.smanzana.templateeditor.api.annotations.DataLoaderName;
import com.smanzana.templateeditor.editor.fields.EditorField;
import com.smanzana.templateeditor.editor.fields.IntField;

public class TestObject {
	
	@DataLoaderFactory("makenew")
	public static class NestObject implements ICustomData {
		@DataLoaderName
		private String name;
		
		@DataLoaderData
		private int temperature;
		
		public NestObject(String name, int temperature) {
			this.name = name;
			this.temperature = temperature;
		}
		
		@Override
		public String toString() {
			return "[Name: " + name + "|temp: " + temperature +"]";
		}
		
		public static NestObject makenew() {
			return new NestObject("nest", 0);
		}

		@Override
		public EditorField<?> getField() {
			return new IntField(temperature);
		}

		@Override
		public void fillFromField(EditorField<?> field) {
			this.temperature = (Integer) field.getObject();
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
	
	@Override
	public String toString() {
		String buf =  "Name: " + name + "\t"
			 + "Description: " + description + "\t"
			 + "Value: " + value + "\t"
			 + "Enabled: " + enabled + "\t";
		
		buf += "Strlist:" + "\t";
		for (String s : strlist)
			buf += s + ",";
		
		buf += "\tNestlist: " + "\t";
		for (NestObject n : nestlist) {
			buf += n.toString() + ",";
		}
		
		return buf;
	}
	
}
