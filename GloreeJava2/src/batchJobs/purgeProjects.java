package batchJobs;


import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

/*
 * 
 * It finds all users who do not have a sample project and clones a sample project for them
 * 
 */
public class purgeProjects{

	public static void main(String[] args) {
		java.sql.Connection con = null;

		// this program expects some input parameters. if they are empty, then it can not run
		// eg : userName, passWord
		String userName = "";
		String password = "";
		String projectIds = "";
		
   		 for (int i = 0; i < args.length; i++){
	         String inputParam = args[i];
	         if (inputParam.contains("userName:")){
	        	 userName = inputParam.replace("userName:", "");
	         }
	         if (inputParam.contains("password:")){
	        	 password = inputParam.replace("password:", "");
	         }
	         if (inputParam.contains("projectIds:")){
	        	 projectIds = inputParam.replace("projectIds:", "");
	         }
	     }    	
   		String correctSyntax = "purgeProjects userName:dbuserId password:dbPassword \n or \n "+
		 "purgeProjects userName:dbuserId password:dbPassword projectIds:1,2,3 " ;
   		
		 
		 if (userName.equals("")){
			 System.out.println("Error : userName is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (password.equals("")){
			 System.out.println("Error : password is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (projectIds.equals("")){
			 System.out.println("Error : projectIds is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		
		
			// get a db connection.
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
		    //////////////////////////////////////////////////////////////////////
		    //                                                                  //
		    //	Configure these two params. They set which project gets cloned  //
		    //                                                                  //
		    //////////////////////////////////////////////////////////////////////
		    
			String [] projectIdsArray = projectIds.split(",");
		    for (int i=0;i<= projectIdsArray.length ; i++) {
		    	try {
			    	int projectId = Integer.parseInt(projectIdsArray[i].trim());
			    	
			    	
			    	String sql = "";
					PreparedStatement prepStmt ;
					 
					
					// lets delete the req baselines
					sql = "select id, name from gr_projects where id =  " + projectId;
					prepStmt = con.prepareStatement(sql);
					ResultSet rs = prepStmt.executeQuery();
					while (rs.next()){
						String name = rs.getString("name");
						int id = rs.getInt("id");
						if  (id != 0){
							// lets try to purge
					    	//System.out.println("Should I  delete projectId (yes/no) " + projectId);
					    	//String input = System.console().readLine();
					    	//if (input.trim().equals("yes")){
					    		System.out.println("srt deleting..." + projectId + " name is " + name);
					    		purgeProject(con, projectId);
					    	//}	
					    	//else {
					    	//	System.out.println("srt skipping deleting..." + projectId);
					    	//}
							
						}
					}
					prepStmt.close();
					
					
					
			    	
			    	
		    	}
		    	catch (Exception e){
		    		// ignore
		    	}
		    }
		
	}
	
	
	
	
	
	private static void purgeProject(java.sql.Connection con, int projectId){
		try {
			
			String sql = "";
			PreparedStatement prepStmt ;
			 
			
			// lets delete the req baselines
			sql = "delete from gr_requirement_baselines " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// we have to do the deletes in a certain sequence, due to Foreign / Primary key relationships.
			// lets delete the req versions
			sql = "delete from gr_requirement_versions " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets delete the req comments
			sql = "delete from gr_requirement_comments " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets delete the req log
			sql = "delete from gr_requirement_log " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			

			
			
			// lets delete the req approval history 
			sql = "delete from gr_requirement_approval_h" +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();

			
			// lets delete the req attribute value
			sql = "delete from gr_r_attribute_values " +
				" where requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" ) ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets delete the req traces
			sql = "delete from gr_traces " +
				" where from_requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" )  " +
				"or " +
				" to_requirement_id in (select id from gr_requirements where project_id = " +
				projectId + 
				" )  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets iterate through all the attachments attachments and drop them.
			sql = "select file_path " +
				" from gr_requirement_attachments a, gr_requirements r" +
				" where a.requirement_id = r.id " +
				" and r.project_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			ResultSet rs = prepStmt.executeQuery();
			while (rs.next()) {
				String attachmentFilePath = rs.getString("file_path");
				File file = new File(attachmentFilePath);
				if (file != null){
					File dir = file.getParentFile();
					// lets drop the file.
					file.delete();
					
					if (dir != null) {
						dir.delete();
					}
				}
			}
			
			// now lets delete all requirement attachment entries in the db.
			sql = " delete from gr_requirement_attachments " +
				" where requirement_id  in " +
				"	(select id from gr_requirements where project_id = ? )";				
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.execute();

			
			// lets delete the requirements 
			sql = "delete from gr_requirements " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
		
			// lets delete the role privs
			sql = "delete from gr_reports " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the role privs
			sql = "delete from gr_role_privs " +
				" where folder_id in (select id from gr_folders where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets delete the word templates 
			sql = "delete from gr_word_templates " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets delete the folders
			sql = "delete from gr_folders " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets delete the invitations 
			sql = "delete from gr_invitations " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			
			// lets delete the user_roles 
			sql = "delete from gr_user_roles " +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets delete the roles 
			sql = "delete from gr_roles" +
				" where project_id = " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();
			
			// lets delete the requirements_seq
			sql = "delete from gr_requirements_seq" +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
						
			// lets delete the rt_attributes
			sql = "delete from gr_rt_attributes " +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the rt_baselines
			sql = "delete from gr_rt_baselines " +
				" where requirement_type_id in " +
				" (select id from gr_requirement_types where project_id= " + projectId  + ")";
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
									
			// lets delete the requirement_types
			sql = "delete from gr_requirement_types  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the project_log
			sql = "delete from gr_project_log " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the gr-search
			sql = "delete from gr_search " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// we are deliberately not deleteing messages here.
			/*
			// lets delete the gr_messages
			sql = "delete from gr_messages " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			*/
			
			
			// lets delete the release requirements
			sql = "delete from gr_release_requirements " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the release_metrics
			sql = "delete from gr_release_metrics  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the project_metrics
			sql = "delete from gr_project_metrics " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the user_metrics
			sql = "delete from gr_user_metrics " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the baseline metrics 
			sql = "delete from gr_baseline_metrics  " +
				" where project_id= " + projectId ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the folder metrics
			sql = "delete from gr_folder_metrics " +
				" where project_id= " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
			// lets delete the requirement_types
			sql = "delete from gr_projects " +
				" where id  = " + projectId  ;
			prepStmt = con.prepareStatement(sql);
			prepStmt.execute();
			prepStmt.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
		}
	} 


}
