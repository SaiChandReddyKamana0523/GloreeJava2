package com.gloree.beans;

//GloreeJava2
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;

import com.gloree.utils.FolderUtil;
import com.gloree.utils.ProjectUtil;

public class Sprint {

	private int sprintId;
	private int projectId;
	private String sprintName;
	private String sprintDescription;
	private String scrumMaster;
	private String sprintStartDt;
	private String sprintEndDt;
	
	// The following method is called when the Sprint values are known 
	// and the system is only interested in creating a bean with those values. 
	public Sprint ( int sprintId , int projectId, String sprintName , String sprintDescription,String scrumMaster,String sprintStartDt,String sprintEndDt){
		this.sprintId = sprintId;
		this.projectId = projectId;
		this.sprintName = sprintName;
		this.sprintDescription = sprintDescription;
		this.scrumMaster = scrumMaster;
		this.sprintStartDt = sprintStartDt;
		this.sprintEndDt = sprintEndDt;
	}

	// the following method is used when the system knows only the sprintId and wants this bean
	// to go and get details i.e. sub folders and requirements.
	public Sprint (int sprintId, String databaseType) {

		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "";
			
			if (databaseType.equals("mySQL")){
				sql = "select id, project_id, name, description, scrum_master, " +
					" date_format(start_dt, '%m/%d/%Y')  \"start_dt\" , " +
					" date_format(end_dt, '%m/%d/%Y')  \"end_dt\"  " +
					" from gr_sprints " + 
					" where id  = ?  ";
			}
			else {
				sql = "select id, project_id, name, description, scrum_master, " +
				" to_char(start_dt, 'MM/DD/YYYY') \"start_dt\" , " +
				" to_char(end_dt, 'MM/DD/YYYY') \"start_dt\"  " +
				" from gr_sprints " + 
				" where id  = ?  ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sprintId);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				this.sprintId = rs.getInt("id");
				this.projectId = rs.getInt("project_id");
				this.sprintName = rs.getString("name");
				this.sprintDescription = rs.getString("description");
				this.scrumMaster = rs.getString("scrum_master");
				this.sprintStartDt = rs.getString("start_dt");
				this.sprintEndDt = rs.getString("end_dt");
			}
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	
	// the following method is used when the system knows only the information required to create a bean in the db
	// the code will do the following :
	
	// 1. create the sprint in the db
	// 2. For every req type in the project, update the 'Agile Sprint' attribute to have this new sprint added to the drop down.
	// 3. create the sprint object
	//
	public Sprint (Project project, String sprintName , String sprintDescription,String scrumMaster,String sprintStartDt,String sprintEndDt,
			String databaseType) {
		java.sql.Connection con = null;
		try {
			//  we can not have , in the sprint name, because each sprint is a value 'agile sprint' drop down, which is comma separated.
			if ((sprintName != null) && (!(sprintName.equals("")))){
				sprintName = sprintName.replace(",", " ");
			}
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			String sql = "";
			
			
			// Now insert the row in the database. This creates the sprint in the system.
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_sprints (project_id, name, description, scrum_master,start_dt, end_dt) " +
					" values (?, ?, ?, ?, STR_TO_DATE(?,'%m/%d/%Y'), STR_TO_DATE(?,'%m/%d/%Y'))";
			
			}
			else {
				sql = "insert into gr_sprints (id, project_id, name, description, scrum_master,start_dt, end_dt) " +
				" values (gr_sprints_s.nextval, ?, ?, ?, ?, TO_DATE(?,'MM/DD/YYYY'), TO_DATE(?,'MM/DD/YYYY'))";
			
			}
			

			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.setString(2, sprintName);
			prepStmt.setString(3, sprintDescription);
			prepStmt.setString(4, scrumMaster);
			prepStmt.setString(5, sprintStartDt);
			prepStmt.setString(6, sprintEndDt);
			
			prepStmt.execute();
		
			// to create the sprint first lets get the id of the sprint.
			sql = "select id" +
				" from gr_sprints " +
				" where  project_id = ? " +
				" and name = ? " +
				" and description = ? " +
				" and scrum_master = ? ";
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId() );
			prepStmt.setString(2, sprintName);
			prepStmt.setString(3, sprintDescription);
			prepStmt.setString(4, scrumMaster);
			
			ResultSet rs = prepStmt.executeQuery();
			int newSprintId = 0;
			
			if (rs.next()){
				newSprintId = rs.getInt("id");
			}
			
			// for all the req types in this project, we need to make sure that we add the value of the new sprint to the
			// existin Agile sprint drop down options. 
			ArrayList requirementTypes = project.getMyRequirementTypes();
			Iterator rT = requirementTypes.iterator();
			while (rT.hasNext() ){
				RequirementType requirementType = (RequirementType) rT.next();
				if (requirementType.getRequirementTypeEnableAgileScrum() == 1){
					requirementType.refreshAgileScrumSprints(databaseType);
				}
			}
			
			// lets re-create the whole bean along with the calculated values. 
			
			if (databaseType.equals("mySQL")){
				sql = "select id, project_id, name, description, scrum_master, " +
					" date_format(start_dt, '%m/%d/%Y')  \"start_dt\" , " +
					" date_format(end_dt, '%m/%d/%Y')  \"end_dt\"  " +
					" from gr_sprints " + 
					" where id  = ?  ";
			}
			else {
				sql = "select id, project_id, name, description, scrum_master, " +
				" to_char(start_dt, 'MM/DD/YYYY') \"start_dt\" , " +
				" to_char(end_dt, 'MM/DD/YYYY') \"end_dt\"  " +
				" from gr_sprints " + 
				" where id  = ?  ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, newSprintId);
			rs = prepStmt.executeQuery();
			
			if (rs.next()){
				this.sprintId = rs.getInt("id");
				this.projectId = rs.getInt("project_id");
				this.sprintName = rs.getString("name");
				this.sprintDescription = rs.getString("description");
				this.scrumMaster = rs.getString("scrum_master");
				this.sprintStartDt = rs.getString("start_dt");
				this.sprintEndDt = rs.getString("end_dt");
			}
		
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}

	// the following method is used when the system wants to update an existing sprint.
	
	
	public Sprint (Sprint oldSprint, Project project, String sprintName , String sprintDescription,String scrumMaster,String sprintStartDt,String sprintEndDt,
			String databaseType) {
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			String sql = "";
			
			String oldSprintName = oldSprint.getSprintName();
			String newSprintName = sprintName;
			//  we can not have , in the sprint name, because each sprint is a value 'agile sprint' drop down, which is comma separated.
			if ((newSprintName != null) && (!(newSprintName.equals("")))){
				newSprintName = newSprintName.replace(",", " ");
			}
			
			// Now insert the row in the database. This creates the sprint in the system.
			if (databaseType.equals("mySQL")){
				sql = "update gr_sprints  " +
					" set name = ? , " +
					" description = ? ," +
					" scrum_master = ? , " +
					" start_dt = STR_TO_DATE(?,'%m/%d/%Y') , " +
					" end_dt = STR_TO_DATE(?,'%m/%d/%Y') " +
					" where id = ?  ";
			
			}
			else {
				sql = "update gr_sprints  " +
				" set name = ? , " +
				" description = ? ," +
				" scrum_master = ? , " +
				" start_dt = TO_DATE(?,'MM/DD/YYYY') , " +
				" end_dt = TO_DATE(?,'MM/DD/YYYY') " +
				" where id = ?  ";
				
			}
			

			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			
			
			prepStmt.setString(1, sprintName);
			prepStmt.setString(2, sprintDescription);
			prepStmt.setString(3, scrumMaster);
			prepStmt.setString(4, sprintStartDt);
			prepStmt.setString(5, sprintEndDt);
			
			prepStmt.setInt(6, oldSprint.getSprintId());
			prepStmt.execute();
		
			
			this.sprintName = sprintName;
			this.sprintDescription = sprintDescription;
			this.scrumMaster = scrumMaster;
			this.sprintStartDt = sprintStartDt;
			this.sprintEndDt = sprintEndDt;
		
			prepStmt.close();
			con.close();
			
			// if the sprint name has been changed, then for every req type in this 
			// project, we need to update the 'Agile Sprint' drop down value.
			if (!(newSprintName.equals(oldSprintName))){
				ArrayList requirementTypes = project.getMyRequirementTypes();
				Iterator rT = requirementTypes.iterator();
				while (rT.hasNext() ){
					RequirementType requirementType = (RequirementType) rT.next();
					RTAttribute agileSprintRTAttribute = new RTAttribute(requirementType.getRequirementTypeId(), "Agile Sprint");
					String oldDropDownOptions = agileSprintRTAttribute.getAttributeDropDownOptions();
					String newDropDownOptions =  oldDropDownOptions.replace(oldSprintName, newSprintName);
					
					// lets update the req type with the new option.
					RTAttribute rTAttribute = new RTAttribute(agileSprintRTAttribute.getAttributeId(),
						agileSprintRTAttribute.getParentAttributeId(),
						agileSprintRTAttribute.getSystemAttribute(), 
						agileSprintRTAttribute.getRequirementTypeId(),
						agileSprintRTAttribute.getAttributeName() , 
						agileSprintRTAttribute.getAttributeType() , 
						agileSprintRTAttribute.getAttributeSortOrder(), 
						agileSprintRTAttribute.getAttributeRequired(), 
						newDropDownOptions,
						agileSprintRTAttribute.getAttributeDescription(),
						agileSprintRTAttribute.getAttributeImpactsVersion(), 
						agileSprintRTAttribute.getAttributeImpactsTraceability(), 
						agileSprintRTAttribute.getAttributeImpactsApprovalWorkflow(), 
						databaseType);
					
					
					 
				}
			}
			
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	
	
	
	public int getSprintId(){
		return this.sprintId;
	}
	
	public int getProjectId(){
		return this.projectId;
	}
	
	public String getSprintName(){
		return this.sprintName;
	}
	
	public String getSprintDescription () {
		return this.sprintDescription;
	}
	
	public String getScrumMaster() {
		return this.scrumMaster;
	}
	
	public String getSprintStartDt() {
		return this.sprintStartDt;
	}
	public String getSprintEndDt() {
		return this.sprintEndDt;
	}
	
	// this method sets All the requirement in the sprint that are 100% completed to 'Completed' and 
	// all the 'Completed' requirements to 100% complete
	public void refreshCompleted (User user, String databaseType) {
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			// lets set all Agile Sprint Status = Completed requiremetns that are not 100% complete to 100% complete.
			String sprintClause = "Agile Sprint:#:" + this.sprintName;
			String taskStatusCompletedClause = "Agile Task Status:#:Completed";
			


			String sql = " update gr_requirements" +
					" set  pct_complete = 100" +
					" where project_id = ? " +
					" and pct_complete <> 100 " +
					" and user_defined_attributes like '%"+ sprintClause +"%'" +
					" and user_defined_attributes like '%"+ taskStatusCompletedClause +"%'";
						
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.projectId);
			prepStmt.execute();
		
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	public void Destroy (String databaseType) {
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			String sql = "delete from gr_sprints where id = ? ";
						
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.sprintId);
			prepStmt.execute();
		
			
			// for all the req types in this project, we need to make sure that we add the value of the new sprint to the
			// existin Agile sprint drop down options. 
			Project project  = new Project (projectId, databaseType);
			ArrayList requirementTypes = project.getMyRequirementTypes();
			Iterator rT = requirementTypes.iterator();
			while (rT.hasNext() ){
				RequirementType requirementType = (RequirementType) rT.next();
				if (requirementType.getRequirementTypeEnableApproval() == 1){
					requirementType.refreshAgileScrumSprints(databaseType);
				}
			}
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}

	
	public ArrayList getTaskOwners() {
		ArrayList userList = new ArrayList();
		java.sql.Connection con = null;
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			// lets set all Agile Sprint Status = Completed requiremetns that are not 100% complete to 100% complete.
			String sprintClause = "Agile Sprint:#:" + this.sprintName;
			


			String sql = " select distinct owner from gr_requirements" +
					" where project_id = ? " +
					" and user_defined_attributes like '%"+ sprintClause +"%'" ;
						
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.projectId);
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next() ){
				userList.add(rs.getString("owner"));
			}
			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return (userList);
	}
	
	
	public void addScrumNotes(int userId, String scrumNotes, String databaseType) {
		java.sql.Connection con = null;
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql =  " insert into gr_sprint_log (user_id, sprint_id, log_dt, log_notes) values (?,?,now(), ?)";
			}
			else {
				sql =  " insert into gr_sprint_log (user_id, sprint_id, log_dt, log_notes) values (?,?, sysdate , ?);";
			}

			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, userId);
			prepStmt.setInt(2, this.sprintId);
			prepStmt.setString(3, scrumNotes);
			
			prepStmt.execute();
			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return ;
	}		
	
	
	
	public ArrayList getScrumNotes() {
		ArrayList scrumNotes = new ArrayList();
		java.sql.Connection con = null;
		
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			// lets set all Agile Sprint Status = Completed requiremetns that are not 100% complete to 100% complete.
			


			String sql = " select u.first_name, u.last_name, sl.log_dt, sl.log_notes " +
					" from gr_users u, gr_sprint_log sl " +
					" where sl.user_id = u.id " +
					" and sl.sprint_id = ? " +
					" order by sl.log_dt desc" ;
						
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.sprintId);
			ResultSet rs = prepStmt.executeQuery();
			
			String data = "";
			while (rs.next() ){
				String firstName = rs.getString("first_name");
				String lastName = rs.getString("last_name");
				String logDt = rs.getString("log_dt");
				String scrumNote  = rs.getString("log_notes");
				
				data = firstName + ":##:" + lastName + ":##:" + logDt + ":##:"  + scrumNote; 
				scrumNotes.add(data);
				
			}
			
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}    finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
		return (scrumNotes);
	}
		
	
}
