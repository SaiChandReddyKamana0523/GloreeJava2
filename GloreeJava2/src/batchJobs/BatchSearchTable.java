package batchJobs;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

/*
 * This is a cron job used to create the gr_search table and its full_text index. * 
 */
public class BatchSearchTable{

	public static void main(String[] args)  {
		java.sql.Connection con = null;
		
		try {

    		// this program expects some input parameters. if they are empty, then it can not run
    		// eg : userName, passWord
    		String databaseType = "";
    		String userName = "";
    		String password = "";
    		
	   		 for (int i = 0; i < args.length; i++){
		         String inputParam = args[i];
		         if (inputParam.contains("databaseType:")){
		        	 databaseType = inputParam.replace("databaseType:", "");
		         }
		         if (inputParam.contains("userName:")){
		        	 userName = inputParam.replace("userName:", "");
		         }
		         if (inputParam.contains("password:")){
		        	 password = inputParam.replace("password:", "");
		         }
		     }    	
	   		String correctSyntax = "BatchSearchTable databaseType:mySQL userName:dbuserId password:dbPassword \n or \n "+
			 "BatchSearchTable databaseType:oracle userName:dbuserId password:dbPassword " ;
	   		
			 if (databaseType.equals("")){
				 System.out.println("Error : databaseType is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
				 return;
			 }
			 
			 if (userName.equals("")){
				 System.out.println("Error : userName is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
				 return;
			 }
			 if (password.equals("")){
				 System.out.println("Error : password is a required parameter to run this job.\n\n The correct syntax to run this job is \n\n" + correctSyntax);
				 return;
			 }
			
			// get a db connection.
			// options for this are mySQL , oracle
			try {
				if (databaseType.equals("mySQL")){
			       String url = "jdbc:mysql://localhost/gloree";
			       Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			       con = DriverManager.getConnection (url, userName, password);
			       System.out.println ("Database connection established");
				}
				else{
			       String url = "jdbc:oracle:thin:@( DESCRIPTION= (ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=1521)) (CONNECT_DATA=(SERVICE_NAME=XE)) )";
			       Class.forName ("oracle.jdbc.driver.OracleDriver").newInstance ();
			       con = DriverManager.getConnection (url, userName, password);
			       System.out.println ("Database connection established");
				}
		    }
		    catch (Exception e) {
		    		e.printStackTrace();
		        	System.err.println ("Cannot connect to database server");
		    }
		    


		    // lets drop the gr_search table.
		    String sql = "drop table gr_search";
		    PreparedStatement prepStmt = con.prepareStatement(sql);
		    try {
		    	prepStmt.execute();
		    }
		    catch (Exception e) {
		    		// do nothing. probably happened because gr_search doesnt' exist.
		    }
		    prepStmt.close();
		    System.out.println("Dropped the gr_search table @ " + Calendar.getInstance().getTime());
		    
		    // lets create the gr_search table.
		    if (databaseType.equals("mySQL")){
		    	sql = " create table gr_search ( " + 
		    	" project_id int not null , " + 
		    	" object_id int not null not null, " +
		    	" object_type varchar(100) not null, " +
		    	" object_text varchar(20000) not null , " +
		    	" foreign key (project_id) references GR_PROJECTS(id), " +
		    	" index(object_id), " +
		    	" index(object_type) " +
		    	" ) ENGINE = MyISAM ";
			}
			else{
				sql = " create table gr_search ( " + 
		    	" project_id int not null , " + 
		    	" object_id int not null , " +
		    	" object_type varchar(100), " +
		    	" object_text clob" +
		    	") ";
			}
		     
		    prepStmt = con.prepareStatement(sql);
		    prepStmt.execute();
		    prepStmt.close();
		    
		    System.out.println("Created the gr_search table @ " + Calendar.getInstance().getTime());
		    
		    // lets populate the gr_search table.
		    if (databaseType.equals("mySQL")){
		    	sql = " insert into gr_search (project_id, object_id, object_type, object_text) " + 
		    	" 		select r.project_id, r.id, 'Requirement', " +
		    	" 			concat(r.full_tag, ' ',  r.name, ' ', r.description, ' '," +
		    	"		    r.owner, ' ',   r.status, ' ', r.priority,' ', " +
		    	"	 		ifnull(r.external_url,' '), ' ', r.user_defined_attributes, ' ',  r.created_by, ' ', " +
		    	"			r.last_modified_by, ' ', " +
		    	"			ifnull(c.comment_note,' '), ' ',  ifnull(c.commenter_email_id,' ')) " +
		    	"		from gr_requirements r left join " +
		    	"			(select requirement_id, group_concat(distinct comment_note separator ' ') 'comment_note', " +
		    	"				group_concat(distinct commenter_email_id separator ' ') 'commenter_email_id' " +
		    	"			from gr_requirement_comments group by requirement_id) c " +
		    	"		on  (r.id = c.requirement_id) ";
			}
			else{
				sql = " insert into gr_search (project_id, object_id, object_type, object_text) " + 
		    	" 		select r.project_id, r.id, 'Requirement', " +
		    	" 			r.full_tag|| ' '||  r.name|| ' '|| r.description|| ' '||" +
		    	"		    r.owner|| ' '||   r.status|| ' '|| r.priority||' '|| " +
		    	"	 		nvl(r.external_url, ' ')|| ' '|| r.user_defined_attributes|| ' '||  r.created_by|| ' '|| " +
		    	"			r.last_modified_by " +
		    	"		from gr_requirements r ";
			}
		     
		    prepStmt = con.prepareStatement(sql);
		   prepStmt.execute();
		    prepStmt.close();
		    System.out.println("Insered rows into the gr_search table @ " + Calendar.getInstance().getTime());
		    
		    
		    if (databaseType.equals("oracle")){
		    	// since Oracle is a pain in the butt stone age RDBMS that should be *&^$#$$ shot on site,
		    	// we have to do stupid shit like this.
		    	
		    	sql = "create index gr_search_index1 on gr_search(project_id)";
		    	prepStmt = con.prepareStatement(sql);
			    prepStmt.execute();
			    System.out.println("Created ProjectId index on  gr_search table @ " + Calendar.getInstance().getTime());
			    
			    sql = "create index gr_search_index2 on gr_search(object_id)";
		    	prepStmt = con.prepareStatement(sql);
			    prepStmt.execute();
			    System.out.println("Created object_id index on  gr_search table @ " + Calendar.getInstance().getTime());
			    
			    
			    
			    
		    	// lets loop through comments and add them to the search text.
		    	sql = "select comment_note ||' '|| commenter_email_id \"comment\" , requirement_id  from gr_requirement_comments ";
		    	 prepStmt = con.prepareStatement(sql);
				 ResultSet rs =    prepStmt.executeQuery();
				 while(rs.next()){
					 int requirementId = rs.getInt("requirement_id");
					 String comment = rs.getString("comment");
					
					 String sql2 = " update gr_search" +
					 	" set object_text = to_char(object_text) || ' ' || ?   " +
					 	" where object_type = 'Requirement' " +
					 	" and object_id = ?";
					 PreparedStatement  prepStmt2 = con.prepareStatement(sql2);
					 prepStmt2.setString(1,comment );
					 prepStmt2.setInt(2,requirementId );
					 prepStmt2.execute();
					 prepStmt2.close();
					 
				 }
				 
				 System.out.println("Added comments  gr_search table @ " + Calendar.getInstance().getTime());
				    
		    	
			}
		    // lets create the fulltext index
		    if (databaseType.equals("mySQL")){
		    	sql = " create fulltext index gr_search_fulltext_index on gr_search (object_text)"; 
			}
			else{
				sql = " CREATE INDEX gr_search_fulltext_index ON gr_search (object_text) INDEXTYPE IS CTXSYS.CONTEXT"; 
			}
		     
		    
		    prepStmt = con.prepareStatement(sql);
		    prepStmt.execute();
		    prepStmt.close();
		    System.out.println("Created the fulltext index on gr_search table @ " + Calendar.getInstance().getTime());
		    
		    
		    prepStmt.close();
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
