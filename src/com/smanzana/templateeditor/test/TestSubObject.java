package com.smanzana.templateeditor.test;

import com.smanzana.templateeditor.api.annotations.DataLoaderData;

public class TestSubObject extends TestObject {

	@DataLoaderData
	private int subvalue;
	
	@DataLoaderData
	public TestObject omgtripplenested;
	
	public TestSubObject(String name, String desc, int value, boolean enabled, int subvalue) {
		super(name, desc, value, enabled);
		this.subvalue = subvalue;
		omgtripplenested = new TestObject("h1", "h2", 12, false);
	}
	
	@Override
	public String toString() {
		String buf = super.toString();
		
		buf += "\nSubvalue: " + subvalue + "\t"
				+ "Nested:\n===========================\n"
				+ omgtripplenested.toString()
				+ "\n=============================";
		
		return buf;
	}
}
