package main.generic;

import java.io.Serializable;

public class TestMessage implements Serializable{

	String name;
	int number;
	
	public TestMessage(String str, int num) {
		name = str;
		number = num;
	}

}
