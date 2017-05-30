package main.miner;

import java.io.Serializable;

public class TestMessage implements Serializable{
	
	double amount;
	String alice;
	String bob;
	double signature;
	
	public TestMessage(double amnt,String a,String b,double sig){
		amount = amnt;
		alice = a;
		bob = b;
		signature = sig;
	}
}