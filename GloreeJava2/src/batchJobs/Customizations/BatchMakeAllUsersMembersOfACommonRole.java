package batchJobs.Customizations;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;



public class BatchMakeAllUsersMembersOfACommonRole {

	public static void main(String[] args) {
		java.sql.Connection con = null;

		// the use case for this program
		// There are a lot of users that get added to many projects over time
		// However, we want to make sure that they get a set of common permission in each project (eg : read permissions)
		// So, any time a project is created, we ask the project admin to ensure that a common role is defined and its permissions are set correctly
		// Then once in a while, we can run this job with this common roles as the target
		// then all the users are assigned to this common role (if they are not already a member of this role)
		
		
		
		// this program expects some input parameters. if they are empty, then it can not run
		// eg : userName, passWord
		String userName = "";
		String password = "";
		String debug = "yes";
		String commonRoleName = "";
		
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
	         if (inputParam.contains("commonRoleName:")){
	        	 commonRoleName = inputParam.replace("commonRoleName:", "");
	         }
	         
	         
	     }    	
   		
   		
   		String correctSyntax = "DieboldBatchReadAccessToAll useName:dbuserId password:dbPassword commonRoleName:ReadOnlyGuest  ";
   		
   		
  		 
		 if (userName.equals("")){
			 System.out.println("Error : userName is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 if (password.equals("")){
			 System.out.println("Error : password is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		
		 if (commonRoleName.equals("")){
			 System.out.println("Error : commonRoleName is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
			 return;
		 }
		 

		
		try {

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
				// lets get all the project's that have the commonRole in them.
				String sql = "select r.name 'role_name', r.id 'role_id', p.id 'project_id', p.name 'project_name' " +
					" from gr_roles r, gr_projects p " +
					" where r.name = ?  " + 
					" and r.project_id = p.id";
					
				PreparedStatement prepStmt = con.prepareStatement(sql);
				prepStmt.setString(1, commonRoleName);
				ResultSet rs = prepStmt.executeQuery();
				
				while (rs.next()){
					int projectId = rs.getInt("project_id");
					int roleId = rs.getInt("role_id");
					String projectName = rs.getString("project_name");
					String roleName = rs.getString("role_name");
					
					if (debug.equals("yes")) {
						System.out.println(" Starting work on Project Id:" + projectId + " Name : " +  projectName   );
						System.out.println(" role Id :" + roleId + " Name : " + roleName);
						
						// lets try to insert all the users in the site who are not in this project role 
						// in to this role.
						
						sql = "insert into gr_user_roles (user_id, project_id, role_id) " + 
								" select id, ?, ? " + 
								" from gr_users " +
								" where id not in ( " +
								" select user_id from gr_user_roles " +
								" where project_id = ? " +
								" and role_id = ?)";
						
						PreparedStatement prepStmt2 = con.prepareStatement(sql);
						prepStmt2.setInt(1, projectId);
						prepStmt2.setInt(2, roleId);
						prepStmt2.setInt(3, projectId);
						prepStmt2.setInt(4,roleId);
						
						prepStmt2.execute();
						
					}
					
				}
				
				prepStmt.close();
				rs.close();

			}
			catch (Exception e){
				e.printStackTrace();
			}
		    

		    con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}


}
