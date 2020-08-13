package com.gloree.beans;
/*
 * 
 * It finds all users who do not have a sample project and clones a sample project for them
 * 
 */
public class Test{

	public static void main(String[] args) {
		try {
			
			String input = "cisco|wired.com|news";
			
			System.out.println(input);
			
			for (String individualFilter : input.split("[|]") ){

				System.out.println(individualFilter);
			}
			System.out.println(input);
		}
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
		
	}
	 	
}
