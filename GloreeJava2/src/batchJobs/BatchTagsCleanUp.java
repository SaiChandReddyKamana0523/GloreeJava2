package batchJobs;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/*
 * 
 * It also crunches through all the data to figure out which Requiremetns have been tested.
 */
public class BatchTagsCleanUp{

	public static void main(String[] args) {
		java.sql.Connection con = null;
		
		// this program expects some input parameters. if they are empty, then it can not run
		// eg : userName, passWor
		String userName = "";
		String password = "";
		String debug = "no";
		
   		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         
	         if (inputParam.contains("userName:")){
	        	 userName = inputParam.replace("userName:", "");
	         }
	         if (inputParam.contains("password:")){
	        	 password = inputParam.replace("password:", "");
	         }
	         
	         if (inputParam.contains("debug:")){
	        	 debug = inputParam.replace("debug:", "");
	         }
	         
	     } 
	    
   		 
   		 
   		 String correctSyntax = "BatchTagsCleanUp  userName:dbuserId password:dbPassword \n " ;
   		
   		 
		 if (userName.equals("")){
			 System.out.println("Error : userName is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (password.equals("")){
			 System.out.println("Error : password is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		
		 if (debug.toLowerCase().contains("yes")){
			 debug = "yes";
		 }
		 else {
			 debug = "no";
		 }

		System.out.println("debug is " + debug + ";");
		try {
    		Calendar cal = Calendar.getInstance();
		    // get a db connection.
			// options for this are mySQL , oracle
			try {
			       String url = "jdbc:mysql://localhost/gloree";
			       Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			       con = DriverManager.getConnection (url, userName, password);
			       System.out.println ("Database connection established");
		    }
		    catch (Exception e) {
		    		e.printStackTrace();
		        	System.err.println ("Cannot connect to database server");
		    }

		    
		    // lets fix tag
		    String sql = "select p.name 'project_name ', p.id 'project_id', " +
		    		" r.id 'requirement_id', r.tag, r.full_tag, rt.short_name , substring(r.full_tag,instr(r.full_tag,'-')+1) 'new_tag'" +
		    		" from gr_requirements r,  gr_requirement_types rt, gr_projects p " +
		    		" where r.requirement_type_id = rt.id " +
		    		" and r.project_id = p.id "+ 
		    		"and r.full_tag <> concat(rt.short_name, '-', r.tag) "; 
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			ResultSet rs = prepStmt.executeQuery();
			int i=0;
		    while (rs.next()) {
		    	i++;
		    	// for each project in the system, we will calculate the Project, Release, Baseline and Folder trends.
		    	int requirementId = rs.getInt("requirement_id");
		    	int projectId = rs.getInt("project_id");
	    		String tag = rs.getString("tag");
	    		String fullTag = rs.getString("full_tag");
	    		String newTag = rs.getString("new_tag");
	    		
	    		System.out.println(cal.getTimeInMillis() + " : " + "#project id " + " : " + projectId + "#requirement id  : " + requirementId + "#tag : " + tag + "#fulltag : " +  fullTag + "#newfulltag : " +  newTag );
	    		
	    		String sql2 = "update gr_requirements set tag = '"+ newTag +"' where id = " + requirementId + " and full_tag = '" + fullTag + "'";
	    		System.out.println("srt sql2 is " + sql2);
	    		PreparedStatement prepStmt2 = con.prepareStatement(sql2);
	    		prepStmt2.execute();
		    }
			
		    System.out.println(" updated " + i + " rows for tag update");

		    
		    
		    
		    // lets fix tag_level4
		    sql = " select   " + 
		    		" project_id, id 'requirement_id', folder_id, " +
		    		" replace(substring_index(tag,'.', 4)  , concat(substring_index(tag,'.', 3) , '.') , '') 'new_tag_level_4', "+
		    		" tag_level1,tag_level2, tag_level3,tag_level4,tag "+
		    		" from gr_requirements "+
		    		" where  "+
		    		" (tag_level4 is not null and tag_level4 > 0) "+
		    		" and "+
		    		" tag_level4 <> replace(substring_index(tag,'.', 4)  , concat(substring_index(tag,'.', 3) , '.') , '') " ; 
			
			prepStmt = con.prepareStatement(sql);
			rs = prepStmt.executeQuery();
			i=0;
		    while (rs.next()) {
		    	i++;
		    	// for each project in the system, we will calculate the Project, Release, Baseline and Folder trends.
		    	int requirementId = rs.getInt("requirement_id");
		    	int projectId = rs.getInt("project_id");
		    	
		    	String tag = rs.getString("tag");
		    	String tagLevel1 = rs.getString("tag_level1");
		    	String tagLevel2 = rs.getString("tag_level2");
		    	String tagLevel3 = rs.getString("tag_level3");
	    		String tagLevel4 = rs.getString("tag_level4");
	    		String newTagLevel4 = rs.getString("new_tag_level_4");
	    		
	    		System.out.println(cal.getTimeInMillis() + " : " + "#project id " + " : " + projectId + "#requirement id  : " + requirementId + "#tag : " + tag + "#tag_level1 : " +  tagLevel1 +  "#tag_level2 : " +  tagLevel2 + "#tag_level3 : " +  tagLevel3 + "#tag_level4 : " +  tagLevel4 +   "#newTagLevel4 : " +  newTagLevel4 );
	    		
	    		String sql2 = "update gr_requirements set tag_level4 = '"+ newTagLevel4 +"' where id = " + requirementId ;
	    		System.out.println("srt sql2 is " + sql2);
	    		PreparedStatement prepStmt2 = con.prepareStatement(sql2);
	    		prepStmt2.execute();
		    }
			
		    System.out.println(" updated " + i + " rows for tag update");
		    
		    
		    
		    rs.close();
		    con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (con != null) {
				con = null;
			}
		}
		
	}

	
		
}
