package com.smanzana.templateeditor.test;

import java.util.LinkedList;
import java.util.List;

public class TestSubObject extends TestObject {

	private int subvalue;
	
	public List<Integer> intList;
	
	public TestSubObject(String name, String desc, int value, boolean enabled, int subvalue) {
		super(name, desc, value, enabled);
		this.subvalue = subvalue;
		this.intList = new LinkedList<>();
		intList.add(55);
		intList.add(67);
	}
	
}
