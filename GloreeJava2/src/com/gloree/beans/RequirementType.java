package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;

import com.gloree.utils.FolderUtil;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.RoleUtil;

//GloreeJava2


// This class is used to store an object of Requirement Type.
// TODO : enhance this to take a requirementtypeid and create the object.
// TODO : Also get all the requirements of this type and give them out as an array list.

public class RequirementType implements java.io.Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int requirementTypeId;
	private int projectId;
	private String requirementTypeShortName;
	private String requirementTypeName;
	private String requirementTypeDescription;
	private int requirementTypeDisplaySequence;
	private int requirementTypeEnableApproval;
	private int requirementTypeEnableAgileScrum;
	private int requirementTypeCanBeDangling;
	private int requirementTypeCanBeOrphan;
	private String requirementTypeCanNotTraceTo;
	private String createdBy;
	//private Date createdDt;
	private String lastModifiedBy;
	//private Date lastModifiedDt;
	
	// The following method is called when the project core values are known and the system is only
	// interested in them. 
	public RequirementType (int requirementTypeId, int projectId, String requirementTypeShortName,
			String requirementTypeName, 
			String requirementTypeDescription, int requirementTypeDisplaySequence, 
			int requirementTypeEnableApproval,int requirementTypeEnableAgileScrum,
			int requirementTypeCanBeDangling, int requirementTypeCanBeOrphan, String requirementTypeCanNotTraceTo,
			String createdBy, String lastModifiedBy){
		this.requirementTypeId = requirementTypeId;
		this.projectId = projectId;
		this.requirementTypeShortName = requirementTypeShortName;
		this.requirementTypeName = requirementTypeName;
		this.requirementTypeDescription = requirementTypeDescription;
		this.requirementTypeDisplaySequence = requirementTypeDisplaySequence;
		this.requirementTypeEnableApproval = requirementTypeEnableApproval;
		this.requirementTypeEnableAgileScrum = requirementTypeEnableAgileScrum;
		this.requirementTypeCanBeDangling = requirementTypeCanBeDangling;
		this.requirementTypeCanBeOrphan = requirementTypeCanBeOrphan;
		this.requirementTypeCanNotTraceTo = requirementTypeCanNotTraceTo;
		this.createdBy = createdBy;
		//this.createdDt = createdDt;
		this.lastModifiedBy = lastModifiedBy;
		//this.lastModifiedDt = lastModifiedDt;
	}
	
	
	// the following method is used when the system knows only the requirementTypeId and wants this bean
	// to go and get details i.e. attributes etc..
	public RequirementType (int requirementTypeId) {
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select id, project_id,short_name,name, description, display_sequence, " +
			" enable_approval, enable_agile_scrum, can_be_dangling, can_be_orphan, can_not_trace_to, created_by," +
			" last_modified_by " + 
			" from gr_requirement_types " + 
			" where id = ?";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				
				this.requirementTypeId = rs.getInt("id");
				this.projectId = rs.getShort("project_id");
				this.requirementTypeShortName = rs.getString("short_name");
				this.requirementTypeName = rs.getString("name");
				this.requirementTypeDescription = rs.getString("description");
				this.requirementTypeDisplaySequence = rs.getInt("display_sequence");
				this.requirementTypeEnableApproval = rs.getInt("enable_approval");
				this.requirementTypeEnableAgileScrum = rs.getInt("enable_agile_scrum");
				this.requirementTypeCanBeDangling = rs.getInt("can_be_dangling");
				this.requirementTypeCanBeOrphan = rs.getInt("can_be_orphan");
				this.requirementTypeCanNotTraceTo = rs.getString("can_not_trace_to");
				this.createdBy = rs.getString("created_by");
				//this.createdDt = createdDt;
				this.lastModifiedBy = rs.getString("last_modified_by");;
			}
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		if (con != null) {
			try {con.close();} catch (Exception e) {}
			con = null;
		}
	}

		
	}
	

	
	// the following method is used when the system knows only the requirementTypeName and projectId
	// and wants this bean
	// to go and get details i.e. attributes etc..
	public RequirementType (int projectId, String requirementTypeName) {
		java.sql.Connection con = null;
		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select id, project_id,short_name,name, description, display_sequence, " +
			" enable_approval, enable_agile_scrum, can_not_trace_to, created_by," +
			" last_modified_by " + 
			" from gr_requirement_types " + 
			" where project_id = ? " +
			" and lower(name) = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, requirementTypeName.trim().toLowerCase());
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				
				this.requirementTypeId = rs.getInt("id");
				this.projectId = rs.getShort("project_id");
				this.requirementTypeShortName = rs.getString("short_name");
				this.requirementTypeName = rs.getString("name");
				this.requirementTypeDescription = rs.getString("description");
				this.requirementTypeDisplaySequence = rs.getInt("display_sequence");
				this.requirementTypeEnableApproval = rs.getInt("enable_approval");
				this.requirementTypeEnableAgileScrum = rs.getInt("enable_agile_scrum");
				this.requirementTypeCanNotTraceTo = rs.getString("can_not_trace_to");
				this.createdBy = rs.getString("created_by");
				//this.createdDt = createdDt;
				this.lastModifiedBy = rs.getString("last_modified_by");;
			}
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	

	public RequirementType (int projectId, String requirementTypeShortName, String userEmailId) {
		java.sql.Connection con = null;
		try {
			
			// we don't really need the userEmailId, but we are asking for it to make the signature unique.
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "select id, project_id,short_name,name, description, display_sequence, " +
			" enable_approval, enable_agile_scrum, can_not_trace_to, created_by," +
			" last_modified_by " + 
			" from gr_requirement_types " + 
			" where project_id = ? " +
			" and lower(short_name) = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, requirementTypeShortName.trim().toLowerCase());
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				
				this.requirementTypeId = rs.getInt("id");
				this.projectId = rs.getShort("project_id");
				this.requirementTypeShortName = rs.getString("short_name");
				this.requirementTypeName = rs.getString("name");
				this.requirementTypeDescription = rs.getString("description");
				this.requirementTypeDisplaySequence = rs.getInt("display_sequence");
				this.requirementTypeEnableApproval = rs.getInt("enable_approval");
				this.requirementTypeEnableAgileScrum = rs.getInt("enable_agile_scrum");
				this.requirementTypeCanNotTraceTo = rs.getString("can_not_trace_to");
				this.createdBy = rs.getString("created_by");
				//this.createdDt = createdDt;
				this.lastModifiedBy = rs.getString("last_modified_by");;
			}
			
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	
	
	

	// the following method is used when the system knows only the information required to create a bean in the db
	// the code will do the following :
	// 1. create the requirement type bean in the db
	// 1.1 sets the rolePrivs . i.e give everything to admin
	// 1.2 create Canned Reports.
	// 2. set the bean attributes.
	
	public RequirementType (int projectId, String projectName, String requirementTypeShortName, 
		String requirementTypeName,String requirementTypeDescription,
		int requirementTypeDisplaySequence, int requirementTypeEnableApproval, int requirementTypeEnableAgileScrum,
		int requirementTypeCanBeDangling, int requirementTypeCanBeOrphan, 
		String requirementTypeCanNotTraceTo, String createdByEmailId, String databaseType) {
		
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Call procedure to create the requirement type. This returns the requirement type id. use it to create the default folder.	
			requirementTypeId = ProjectUtil.createARequirementType(projectId, 
				requirementTypeShortName, requirementTypeName, requirementTypeDescription,
				requirementTypeDisplaySequence, requirementTypeEnableApproval, requirementTypeEnableAgileScrum,
				requirementTypeCanBeDangling,
				requirementTypeCanBeOrphan, requirementTypeCanNotTraceTo,  createdByEmailId,  databaseType);
			
			// Now call the routine to make the corresponding Folder.
			int folderId = ProjectUtil.createAFolder (projectId,  
				requirementTypeId, requirementTypeName,0, createdByEmailId,  databaseType);
					
			
			// any time a new requirement type is created, lets by default create a new attribute for notifying the stake holders.
			int parentAttributeId = 0 ;
			int systemAttribute = 0;
			String attributeName = "Keep Me Informed";
			String attributeType = "Text Box";
			String attributeSortOrder = "a";
			int attributeRequired = 0;
			String attributeDefaultValue = "";
			String attributeDropDownOptions = "";
			String attributeDescription = "Comma separated list of email addresses that will be notified of any changes to objects";
			int attributeImpactsVersion = 0;
			int attributeImpactsTraceability = 0;
			int attributeImpactsApprovalWorkflow = 0;
			RTAttribute rTAttribute = new RTAttribute(projectId,parentAttributeId, systemAttribute, requirementTypeId, attributeName , 
				attributeType , attributeSortOrder,	attributeRequired, attributeDefaultValue, 
				attributeDropDownOptions, attributeDescription,
				attributeImpactsVersion, attributeImpactsTraceability, attributeImpactsApprovalWorkflow,
				createdByEmailId, databaseType);
			
			
			// NOTE : this is critical. The call to updateRolePriv table will ensure
			// that a row is created in the rolepriv table for this newly created folder
			// for all roles in this project.
			RoleUtil.updateRolePrivTable(projectId);

			

			// get adminRoleId.
			int adminRoleId = RoleUtil.getAdminRoleId(projectId);
			// Now grant all role privileges on this folder to the administrator role.
			RoleUtil.setPrivileges( adminRoleId, folderId, "createRequirement");
			RoleUtil.setPrivileges( adminRoleId, folderId, "readRequirement");
			RoleUtil.setPrivileges( adminRoleId, folderId, "updateRequirement");
			RoleUtil.setPrivileges( adminRoleId, folderId, "deleteRequirement");
			RoleUtil.setPrivileges( adminRoleId, folderId, "traceRequirement");
			RoleUtil.setPrivileges( adminRoleId, folderId, "approveRequirement");
			
			// we need to give read privs to ALL roles to this folder.
			RoleUtil.setReadPrivileges(folderId);

			// lets ensure that the newly created attribute 'keep me notified' is editable by the admin
			RoleUtil.setUpdateAttributes(adminRoleId, folderId, ":#:Keep Me Informed:#:");
			
			// method to create a bunch of canned reports for every folder.
			FolderUtil.createCannedReports(projectId, folderId, requirementTypeName, createdByEmailId, databaseType);
			
			/*
			 * 
			 * we don't need this logic any more, as the user can set this req type to be Agile enabled in the core info page.
			// if this project has 'Agile Scrum Enabled' then we need to create the scrum attributes for this req type.
			Project project = new Project(projectId, databaseType);
			if (project.getEnableAgileScrum() == 1){
				// this project is AgileScrum enabled . Lets create the scrum attributes.
				ProjectUtil.setUpAgileScrumAttributesInReqType(projectId, requirementTypeId, createdByEmailId,databaseType);
			}
			*/
			
			// now lets populate the bean
			String sql = "select id, project_id,short_name,name, description, display_sequence," +
			" enable_approval, enable_agile_scrum, " +
			" can_be_dangling, can_be_orphan, can_not_trace_to, created_by, last_modified_by " + 
			" from gr_requirement_types " + 
			" where project_id = ?  and short_name = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, requirementTypeShortName);
			ResultSet rs = prepStmt.executeQuery();
			
			if (rs.next()){
				
				this.requirementTypeId = rs.getInt("id");
				this.projectId = rs.getShort("project_id");
				this.requirementTypeShortName = rs.getString("short_name");
				this.requirementTypeName = rs.getString("name");
				this.requirementTypeDescription = rs.getString("description");
				this.requirementTypeDisplaySequence = rs.getInt("display_sequence");
				this.requirementTypeEnableApproval =  rs.getInt("enable_approval");
				this.requirementTypeEnableAgileScrum = rs.getInt("enable_agile_scrum");
				this.requirementTypeCanBeDangling = rs.getInt("can_be_dangling");
				this.requirementTypeCanBeOrphan = rs.getInt("can_be_orphan");
				this.requirementTypeCanNotTraceTo = rs.getString("can_not_trace_to");
				this.createdBy = rs.getString("created_by");
				//this.createdDt = createdDt;
				this.lastModifiedBy = rs.getString("last_modified_by");;
			}
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}		
	}

	public int getRequirementTypeId(){
		return this.requirementTypeId;
	}
	
	public int getProjectId(){
		return this.projectId;
	}
	
	public String getRequirementTypeShortName(){
		return this.requirementTypeShortName;
	}
	
	public String getRequirementTypeName(){
		return this.requirementTypeName;
	}
	
	
	public String getRequirementTypeDescription () {
		return this.requirementTypeDescription;
	}

	public int getRequirementTypeDisplaySequence () {
		return this.requirementTypeDisplaySequence;
	}
	
	public int getRequirementTypeEnableApproval () {
		return this.requirementTypeEnableApproval ;
	}

	public int getRequirementTypeEnableAgileScrum() {
		return this.requirementTypeEnableAgileScrum;
	}
	public int getRequirementTypeCanBeDangling () {
		return this.requirementTypeCanBeDangling ;
	}

	public int getRequirementTypeCanBeOrphan() {
		return this.requirementTypeCanBeOrphan ;
	}

	public String getRequirementTypeCanNotTraceTo(){
		if (this.requirementTypeCanNotTraceTo == null ){
			this.requirementTypeCanNotTraceTo = "";
		}
		return this.requirementTypeCanNotTraceTo;
	}
	
	public String getCreatedBy () {
		return this.createdBy;
	}
	
	//public Date getCreatedDt () {
	//	return this.createdDt;
	//}
	
	public String getLastModifiedBy () {
		return this.lastModifiedBy;
	}
	
	//public Date getLastModifiedDt () {
	//	return this.lastModifiedDt;
	//}
	
	// returns the id of the rootFolder for this req type.
	public int getRootFolderId() {
		int rootFolderId = 0 ;
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = "select id from gr_folders " + 
				" where requirement_type_id = ?  " + 
				" and folder_level = 1"; 
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementTypeId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				rootFolderId = rs.getInt("id"); 
			}
			
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return rootFolderId;
	}
	
	public int getRequirementTypeEnableVotes() {
		int enableVotes = 0 ;
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = "select enable_votes from gr_requirement_types " + 
				" where id = ?  " ; 
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementTypeId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				enableVotes = rs.getInt("enable_votes"); 
			}
			
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return enableVotes;
		
	}
	
	public void setRequirementTypeEnableVotes( int enableVotes) {
		System.out.println("srt called to setRequirementTypeEnableVotes =  " + enableVotes + " for " + this.requirementTypeName);
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = "update gr_requirement_types " + 
				" set enable_votes = ?  " + 
				" where id = ?  " ; 
	
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, enableVotes );
			prepStmt.setInt(2, this.requirementTypeId );
			prepStmt.execute();
	
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return ;
		
	}
	
	public void setNotifyOnOwnerChange( int notifyOnOwnerChange) {
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = "update gr_requirement_types " + 
				" set notify_on_owner_change = ?  " + 
				" where id = ?  " ; 
	
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, notifyOnOwnerChange );
			prepStmt.setInt(2, this.requirementTypeId );
			prepStmt.execute();
	
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return ;
		
	}
	
	
	public int getNotifyOnOwnerChange() {
		int notifyOnOwnerChange = 0 ;
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = "select notify_on_owner_change from gr_requirement_types " + 
				" where id = ?  " ; 
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementTypeId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				notifyOnOwnerChange = rs.getInt("notify_on_owner_change"); 
			}
			
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return notifyOnOwnerChange;
		
	}
	
	public void setNotifyOnApprovalChange( int notifyOnApprovalChange) {
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = "update gr_requirement_types " + 
				" set notify_on_approval_change = ?  " + 
				" where id = ?  " ; 
	
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, notifyOnApprovalChange );
			prepStmt.setInt(2, this.requirementTypeId );
			prepStmt.execute();
	
			prepStmt.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return ;
		
	}
	
	
	public int getNotifyOnApprovalChange() {
		int notifyOnApprovalChange = 0 ;
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = "select notify_on_approval_change from gr_requirement_types " + 
				" where id = ?  " ; 
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementTypeId );
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()){
				notifyOnApprovalChange = rs.getInt("notify_on_approval_change"); 
			}
			
			prepStmt.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return notifyOnApprovalChange;
		
	}
	public void setRequirementTypeEnableAgileScrum() {
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = " update gr_requirement_types set enable_agile_scrum = 1  " + 
				" where id = ?  " ;
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementTypeId );
			prepStmt.execute();
			
			prepStmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
	}
	
	
	public void setRequirementTypeEnableAgileScrumToDisabled() {
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = " update gr_requirement_types set enable_agile_scrum = 0  " + 
				" where id = ?  " ;
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementTypeId );
			prepStmt.execute();
			
			prepStmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
	}
	
	public ArrayList getAllRequirementsInRequirementType(String databaseType){
		ArrayList requirements = ProjectUtil.getAllRequirementsInRT(this.requirementTypeId, "all", databaseType);
		return requirements;
	}
	
	public ArrayList getRequirementsInRequirementTypeOrderByFullTag(String databaseType, String filter){
		ArrayList requirements = ProjectUtil.getAllRequirementsInRTOrderByFullTag(this.requirementTypeId, filter, databaseType);
		return requirements;
	}
	
	
	public ArrayList<RTAttribute> getAllAttributesInRequirementType(){
		ArrayList<RTAttribute> attributes = ProjectUtil.getAllAttributes(this.requirementTypeId);
		return attributes;
	}
	
	public ArrayList getAllBaselines(){
		ArrayList baselines = ProjectUtil.getAllBaselines(requirementTypeId);
		return baselines;
	}
	
	// this refreshes the list of drop down options in the requirement type for the attribute 'Agile Sprint'.
	public void refreshAgileScrumSprints(String databaseType) {
		try {
			if (this.requirementTypeEnableAgileScrum == 0 ){
				return;
			}
			
			System.out.println("srt about to refresh AGile scrum sprints for " + this.getRequirementTypeName());
			// since the project structure may have changed (is sprints dropped etc... since the object was created)
			// lets get a fresh project object.
			Project project = new Project(this.projectId, databaseType);
			ArrayList sprints = project.getProjectSprints(databaseType);
			Iterator s = sprints.iterator();
			String AgileSprintsString = "";
			while (s.hasNext()){
				Sprint sprint = (Sprint) s.next();
				String sprintName = sprint.getSprintName();
				sprintName = sprintName.replace(",", " ");
				AgileSprintsString += sprint.getSprintName() + ",";
			}
			// lets drop the last ,
			if (AgileSprintsString.contains(",")){
				AgileSprintsString = (String) AgileSprintsString.subSequence(0,AgileSprintsString.lastIndexOf(","));
			}
			

			System.out.println("srt setting Agile Sprint new value to " + AgileSprintsString);
			
			// now we need to replace the 'Agile Sprint' attribute drop down options with AgielSprintsString.
			RTAttribute agileSprintRTAttribute = new RTAttribute(this.requirementTypeId , "Agile Sprint");
			
			
			// lets update the req type with the new option.
			RTAttribute rTAttribute = new RTAttribute(
					agileSprintRTAttribute.getAttributeId(),
					agileSprintRTAttribute.getParentAttributeId(),
					agileSprintRTAttribute.getSystemAttribute(), 
					agileSprintRTAttribute.getRequirementTypeId(),
					agileSprintRTAttribute.getAttributeName() , 
					agileSprintRTAttribute.getAttributeType() , 
					agileSprintRTAttribute.getAttributeSortOrder(), 
					agileSprintRTAttribute.getAttributeRequired(), 
					AgileSprintsString,
					agileSprintRTAttribute.getAttributeDescription(),
					agileSprintRTAttribute.getAttributeImpactsVersion(), 
					agileSprintRTAttribute.getAttributeImpactsTraceability(), 
					agileSprintRTAttribute.getAttributeImpactsApprovalWorkflow(), 
					databaseType);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	public String getRemindApproversOn() {
		String remindApproversOn = "";
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = " select remind_approvers_on " +
				" from  gr_requirement_types   " + 
				" where id = ?  " ;
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.requirementTypeId );
			ResultSet rs = prepStmt.executeQuery();
			
			while (rs.next()){
				remindApproversOn = rs.getString("remind_approvers_on");
			}
			rs.close();
			prepStmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
		return (remindApproversOn);
	}
		
	
	public void setRemindApproversOn(String remindApproversOn) {
		java.sql.Connection con  = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();			
			// lets get the UDA values and put them in a string.
			String sql = " update  gr_requirement_types   " +
					" set remind_approvers_on = ? " + 
				" where id = ?  " ;
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, remindApproversOn);
			prepStmt.setInt(2, this.requirementTypeId );
			prepStmt.execute();
			prepStmt.close();
			
		} catch (Exception e) {
			e.printStackTrace();
				
		}  finally {
			if (con != null) {
				try { con.close(); } catch (Exception e) { ; }
				con = null;
			}
		}		
	}
		
	
	
}
