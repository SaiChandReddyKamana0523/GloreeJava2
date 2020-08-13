package com.gloree.utils;

import com.gloree.beans.Requirement;

public class SortSprintByTaskWeightDesc implements java.util.Comparator {
	 public int compare(Object obj1, Object obj2) {
		 Requirement req1 = (Requirement) obj1;
		 Requirement req2 = (Requirement) obj2;
		 
		 int taskWeight1 = 0;
		 
		 try {
			 taskWeight1 = Integer.parseInt(req1.getAttributeValue("Agile Task Weight"));
		 }
		 catch (Exception e){
		 }
		 
		 
		 int taskWeight2 = 0;
		 
		 try {
			 taskWeight2 = Integer.parseInt(req2.getAttributeValue("Agile Task Weight"));
		 }
		 catch (Exception e){
		 }		
	  return( taskWeight2 - taskWeight1);
	 
	 }
} 