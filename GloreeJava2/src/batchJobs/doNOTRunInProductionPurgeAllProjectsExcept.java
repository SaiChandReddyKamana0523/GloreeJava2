package batchJobs;


import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Scanner;

/*
 * 
 * It finds all users who do not have a sample project and clones a sample project for them
 * 
 */
public class doNOTRunInProductionPurgeAllProjectsExcept{

	public static void main(String[] args) {
		java.sql.Connection con = null;

		
		Scanner scanner = new Scanner(System.in);
	    System.out.print("Do not run this in production. it will delete all NON TELSTRA data . Type YESIKNOW to proceed \n\n");
	    String stupid1 = scanner.next();
	    System.out.println("Your response  is: " + stupid1);
	    
	    if (!(stupid1.equals("YESIKNOW"))){

		    System.out.println("You are NOT stupid. exiting: " );
	    	return;
	    }

	    
		// this program expects some input parameters. if they are empty, then it can not run
		// eg : userName, passWord
		String userName = "Gloree";
		String password = "SpecialGloree1";
		
   		
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
		    	try {
			    	
			    	String sql = "";
					PreparedStatement prepStmt ;
					 
					
					// lets delete the req baselines
					sql = "select id, short_name, name, owner from gr_projects "
							+ " where (created_by  not like '%telstra%' and created_by not like 'nathan@tracecloud.com')";
					prepStmt = con.prepareStatement(sql);
					ResultSet rs = prepStmt.executeQuery();
					while (rs.next()){
						String name = rs.getString("name");
						int id = rs.getInt("id");
						String short_name = rs.getString("short_name");
						String owner = rs.getString("owner");
						if  (id != 0){
							// lets try to purge
					    	//System.out.println("Should I  delete projectId (yes/no) " + projectId);
					    	//String input = System.console().readLine();
					    	//if (input.trim().equals("yes")){
					    		
					    	    System.out.print("Should I delete project  YES/NO : "  + " \n " + 
					    	     "\n id --> " + id +
					    	     "\n short_name --> " + short_name + 
					    	     "\n name --> " + name + 
					    	     "\n shortwner_name --> " + short_name + 
					    	     "\n\n");
					    	    String deleteProject = scanner.next();
					    	    System.out.println("Your response to deleteProject  is: " + deleteProject);
					    	    
					    	    if ((deleteProject.equals("YES"))){

					    		    System.out.println("I am going to delete project " );

						    		purgeProject(con, id);
					    	    }
					    	
							
						}
					}
					prepStmt.close();
					
					
					
			    	
			    	
		    	}
		    	catch (Exception e){
		    		e.printStackTrace();
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
