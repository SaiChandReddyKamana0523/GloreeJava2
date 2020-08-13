package com.gloree.beans;

//GloreeJava2

import com.gloree.utils.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.InitialContext;

public class RTAttribute {

	
	private int attributeId;
	private int parentAttributeId;
	private int systemAttribute;
	private int requirementTypeId;
	private String attributeName;
	private String attributeType;
	private int attributeRequired;
	private int attributeDefaultDisplay;
	private String attributeDefaultValue;
	private String attributeDropDownOptions;
	private String attributeDescription;
	private String attributeSortOrder;
	private int attributeImpactsVersion ;
	private int attributeImpactsTraceability;
	private int attributeImpactsApprovalWorkflow;	
	private String createdBy;
	//private Date createdDt;
	private String lastModifiedBy;
	//private Date lastModifiedDt;
	
	
	
	// The following method is called when the Attribute core values are already known and the system is only
	// interested in creating a bean. . 
	public RTAttribute (int attributeId, int parentAttributeId, int systemAttribute, int requirementTypeId, String attributeName,  
			String attributeType, int attributeRequired, String attributeDefaultValue, 
			String attributeDropDownOptions, String attributeDescription,
			String attributeSortOrder,
			int attributeImpactsVersion, int attributeImpactsTraceability, int attributeImpactsApprovalWorkflow,
			String createdBy, String lastModifiedBy){
		
		this.attributeId = attributeId;
		this.parentAttributeId = parentAttributeId;
		this.systemAttribute = systemAttribute;
		this.requirementTypeId = requirementTypeId;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.attributeRequired = attributeRequired;
		this.attributeDefaultValue = attributeDefaultValue;
		this.attributeDropDownOptions = attributeDropDownOptions;
		this.attributeDescription = attributeDescription;
		this.attributeSortOrder = attributeSortOrder;
		
		this.attributeImpactsVersion  = attributeImpactsVersion; 
		this.attributeImpactsTraceability = attributeImpactsTraceability;
		this.attributeImpactsApprovalWorkflow = attributeImpactsApprovalWorkflow;
		
		
		this.createdBy = createdBy;
		//this.createdDt = rs.getDate("created_dt");
		this.lastModifiedBy = lastModifiedBy;
		//this.lastModifiedDt = rs.getDate("last_modified_by");
	}
	
	
	// the following method is used when the system knows only the attributeId and wants this bean
	// to go and get details from the db to create the bean.
	public RTAttribute (int rTAttributeId) {

		java.sql.Connection con =  null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			String sql = "select id, parent_attribute_id, system_attribute, requirement_type_id, name, description," +
				" type , " +
				" options , required , default_display, default_value , sort_order, " +
				" impacts_version, impacts_traceability, impacts_approval_workflow, " +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_attributes " +
				" where id = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTAttributeId);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.attributeId = rs.getInt("id");
				this.parentAttributeId = rs.getInt("parent_attribute_id");
				this.systemAttribute = rs.getInt("system_attribute");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.attributeName = rs.getString("name");
				this.attributeDescription = rs.getString ("description");
				this.attributeType = rs.getString("type");
				this.attributeDropDownOptions = rs.getString("options");
				this.attributeRequired = rs.getInt("required"); 
				this.attributeDefaultDisplay = rs.getInt("default_display");
				this.attributeDefaultValue = rs.getString("default_value");
				this.attributeSortOrder = rs.getString("sort_order");
				
				this.attributeImpactsVersion  = rs.getInt("impacts_version"); 
				this.attributeImpactsTraceability = rs.getInt("impacts_traceability");
				this.attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");

				
				this.createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
			}
			prepStmt.close();
			rs.close();
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

	// the following method is used when the system knows only requirement type id 
	// and the attribute name. and was us 
	// to go and get details from the db to create the bean.
	public RTAttribute (int requirementTypeId, String attributeName) {

		java.sql.Connection con =  null;
		try {

			System.out.println("srt in getRTAttrinute for reqTypeId " + requirementTypeId + " attribute Name " + attributeName);
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// Now we get the data from the database and populate the bean.
			String sql = "select id, parent_attribute_id, system_attribute, requirement_type_id, name, description," +
				" type , " +
				" options , required , default_value , default_display, sort_order, " +
				" impacts_version, impacts_traceability, impacts_approval_workflow, " +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_attributes " +
				" where requirement_type_id = ? " +
				" and lower(name)  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setString(2, attributeName.toLowerCase());
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.attributeId = rs.getInt("id");
				this.parentAttributeId = rs.getInt("parent_attribute_id");
				this.systemAttribute = rs.getInt("system_attribute");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.attributeName = rs.getString("name");
				this.attributeDescription = rs.getString ("description");
				this.attributeType = rs.getString("type");
				this.attributeDropDownOptions = rs.getString("options");
				this.attributeRequired = rs.getInt("required"); 
				this.attributeDefaultDisplay = rs.getInt("default_display");
				this.attributeDefaultValue = rs.getString("default_value");
				this.attributeSortOrder = rs.getString("sort_order");
				
				this.attributeImpactsVersion  = rs.getInt("impacts_version"); 
				this.attributeImpactsTraceability = rs.getInt("impacts_traceability");
				this.attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");

				
				this.createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
			}
			prepStmt.close();
			rs.close();
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

	
	
	// the following method is used when the system knows only the information required to create a bean in the db
	// the code will do the following :
	// 1. create the attribute
	// 2. set the bean attributes.
	
	public RTAttribute (int projectId, int parentAttributeId, int systemAttribute,  int requirementTypeId, 
			String attributeName ,
			String  attributeType , String  attributeSortOrder,
			int attributeRequired, String attributeDefaultValue,
			String attributeDropDownOptions, String attributeDescription, 
			int attributeImpactsVersion, int attributeImpactsTraceability, int attributeImpactsApprovalWorkflow,
			String createdByEmailId, String	 databaseType) {
		java.sql.Connection con =  null;
		try {


			// set this using the setter
			int attributeDefaultDisplay = 0;
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			if (attributeDefaultValue == null){
				attributeDefaultValue = "";
			}
			
			// Now insert the row in the database.
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = "insert into gr_rt_attributes (parent_attribute_id, system_attribute, requirement_type_id,name, description, type , " +
					" options , required ,  default_value , sort_order, " +
					" impacts_version, impacts_traceability, impacts_approval_workflow, " +
					" created_by, created_dt, " +
					" last_modified_by , last_modified_dt) " +
					" values (?, ?, ?, ?, ?, ?," +
					" ?, ?, ? , ?, " +
					" ?, ?, ?," +
					" ?, now()," +
					" ?, now())";
			}
			else {
				sql = "insert into gr_rt_attributes (parent_attribute_id, system_attribute, requirement_type_id,name, description, type , " +
				" options , required ,  default_value , sort_order, " +
				" impacts_version, impacts_traceability, impacts_approval_workflow, " +
				" created_by, created_dt, " +
				" last_modified_by , last_modified_dt) " +
				" values (?,?, ?, ?, ?, ?," +
				" ?, ?, ? , ?," +
				" ?, ?, ?," +
				" ?, sysdate," +
				" ?, sysdate)";
			}
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, parentAttributeId);
			prepStmt.setInt(2, systemAttribute);
			prepStmt.setInt(3, requirementTypeId);
			prepStmt.setString(4, attributeName);
			prepStmt.setString(5, attributeDescription);
			prepStmt.setString(6, attributeType);
		
			prepStmt.setString(7, attributeDropDownOptions);
			prepStmt.setInt(8, attributeRequired);
			prepStmt.setString(9, attributeDefaultValue);
			prepStmt.setString(10, attributeSortOrder);
			
			prepStmt.setInt(11, attributeImpactsVersion);
			prepStmt.setInt(12, attributeImpactsTraceability);
			prepStmt.setInt(13, attributeImpactsApprovalWorkflow);
			
			prepStmt.setString(14, createdByEmailId);
			
			prepStmt.setString(15, createdByEmailId);
			
			prepStmt.execute();
			
			// at this point the attribute is created in the db.

			// get the id of the attribute we have just created. We are going to use this for creating attribute 
			// values for existing reqs of this req type.
			
			// Now we get the data from the database and populate the bean.
			
			
			if (databaseType.equals("mySQL")){
				sql = "select id, parent_attribute_id, system_attribute, requirement_type_id, name, description, type , " +
				" options , required , default_display, ifnull(default_value, '') \"default_value\" , sort_order, " +
				" impacts_version, impacts_traceability, impacts_approval_workflow," +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_attributes " +
				" where requirement_type_id = ? " + 
				" and name = ? ";
			}
			else {
				sql = "select id, parent_attribute_id, system_attribute, requirement_type_id, name, description, type , " +
				" options , required , default_display, nvl(default_value, '') \"default_value\" , sort_order, " +
				" impacts_version, impacts_traceability, impacts_approval_workflow," +
				" created_by, created_dt, last_modified_by , last_modified_dt " + 
				" from gr_rt_attributes " +
				" where requirement_type_id = ? " + 
				" and name = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.setString(2, attributeName);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				this.attributeId = rs.getInt("id");
				this.parentAttributeId = rs.getInt("parent_attribute_id");
				this.systemAttribute = rs.getInt("system_attribute");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.attributeName = rs.getString("name");
				this.attributeDescription = rs.getString ("description");
				this.attributeType = rs.getString("type");
				this.attributeDropDownOptions = rs.getString("options");
				this.attributeRequired = rs.getInt("required");
				this.attributeDefaultDisplay = rs.getInt("default_display");
				this.attributeDefaultValue = rs.getString("default_value");
				if (this.attributeDefaultValue == null){
					this.attributeDefaultValue = "";
				}
				
				this.attributeSortOrder = rs.getString("sort_order");

				this.attributeImpactsVersion  = rs.getInt("impacts_version"); 
				this.attributeImpactsTraceability = rs.getInt("impacts_traceability");
				this.attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");
				
				
				this.createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
			}
	
			
			// Since we have created a new attribute for a requirement type, we need to find all requirements of that type, and insert 
			// an attribute value for them.
			if (databaseType.equals("mySQL")){
				sql = " insert into gr_r_attribute_values (requirement_id, attribute_id,value,created_by,created_dt,last_modified_by, last_modified_dt )" +
					" select r.id, '" +  this.attributeId + "','" + this.attributeDefaultValue + "','system',now(), 'system', now() " + 
					" from gr_requirements r " + 
					" where r.requirement_type_id = ? ";
			}
			else {
				sql = " insert into gr_r_attribute_values (requirement_id, attribute_id,value,created_by,created_dt,last_modified_by, last_modified_dt )" +
				" select r.id, '" +  this.attributeId + "','" + this.attributeDefaultValue + "','system', sysdate, 'system', sysdate " + 
				" from gr_requirements r " + 
				" where r.requirement_type_id = ? ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementTypeId);
			prepStmt.execute();


			//We also need to set the userDefinedAttribute values for all the already existing reqs of this type.
			
			RequirementUtil.setUserDefinedAttributesForAllRequirementsInRT(requirementTypeId,  databaseType);

			// lets create a log entry in the project log.
			ProjectUtil.createProjectLog(projectId, attributeName, "Create", 
				"Created Attribute ", createdByEmailId,  databaseType);
			
			prepStmt.close();
			rs.close();
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
	

	// the following method is used when the system needs to update it in the db
	// and then create the beam.
	// NOTE : if rTAttributeId exists in the constructor call, then we must be calling to update. Else , we must be calling 
	// to create it.
	// 1. update the attribute in the database
	// 1.2 we cache a requirement attribute values in the gr_requirements table. So we will need to 
	// replace the cache of attribute name with the new name. 
	// 2. create the bean objects. 
	
	// we should consider giving this a different name, as it is not a constructor.
	public RTAttribute (int rTAttributeId, int parentAttributeId,  int systemAttribute, 
			int requirementTypeId, String attributeName ,String  attributeType , String  attributeSortOrder, 
			int attributeRequired, String attributeDropDownOptions, String attributeDescription,
			int attributeImpactsVersion, int attributeImpactsTraceability, int attributeImpactsApprovalWorkflow, String databaseType) {

		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// lets get the old RTAttribute example.
			RTAttribute oldRTAttribute = new RTAttribute(rTAttributeId);
			
			String sql = "";
			// Now insert the row in the database.
			if (databaseType.equals("mySQL")){
				sql = "update gr_rt_attributes " +
					" set parent_attribute_id = ? , " +
					" system_attribute = ? , " +
					" name = ? ," +
					" description = ? , " +
					" type  =  ? , " +
					" options = ?  , " +
					" required = ? , " +
					" default_value = ? , " +
					" sort_order = ? , " +
					" impacts_version = ? , " + 
					" impacts_traceability = ? ," +
					" impacts_approval_workflow = ? ," +
					" created_by = 'system' , created_dt = now() , last_modified_by = 'system' , last_modified_dt = now() " +
					" where id = ? " ;
			}
			else {
				sql = "update gr_rt_attributes " +
				" set parent_attribute_id = ?, " +
				" system_attribute = ? , " +
				" name = ? ," +
				" description = ? , " +
				" type  =  ? , " +
				" options = ?  , " +
				" required = ? , " +
				" default_value = ? , " +
				" sort_order = ? , " +
				" impacts_version = ? , " + 
				" impacts_traceability = ? ," +
				" impacts_approval_workflow = ? ," +
				" created_by = 'system' , created_dt = sysdate , last_modified_by = 'system' , last_modified_dt = sysdate " +
				" where id = ? " ;
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, parentAttributeId);
			prepStmt.setInt(2,systemAttribute);
			prepStmt.setString(3, attributeName);
			prepStmt.setString(4, attributeDescription);
			prepStmt.setString(5, attributeType);
			prepStmt.setString(6, attributeDropDownOptions);
			prepStmt.setInt(7, attributeRequired);
			
			prepStmt.setString(8, attributeDefaultValue);
			prepStmt.setString(9, attributeSortOrder);
			
			prepStmt.setInt(10, attributeImpactsVersion);
			prepStmt.setInt(11, attributeImpactsTraceability);
			prepStmt.setInt(12, attributeImpactsApprovalWorkflow);
			
			
			prepStmt.setInt(13, rTAttributeId);
			prepStmt.execute();
			prepStmt.close();
			
			// since the RT attribute label is changing, lets locate all Requirements of this type
			// and change any occurence of the old label to new one in the user_defined_attributes field.
			sql = " update gr_requirements " +
				" set user_defined_attributes = replace(user_defined_attributes,?,?)" +
				" where requirement_type_id = ? ";
			prepStmt = con.prepareStatement(sql);
			
			// i.e in all gr_requiremetns' user_defined_attributes field, replace attributeName with new one.
			prepStmt.setString(1, oldRTAttribute.attributeName);
			prepStmt.setString(2, attributeName);
			prepStmt.setInt(3, requirementTypeId);
			prepStmt.execute();
			prepStmt.close();
			
			// Since the user's may have had some user defined reports on custom attributes,
			// to be good samaritans, we are regreshing the report definition also for old reports,
			// i.e where ever this label appears, rename it in all the reports on this req type.
			sql = " update gr_reports" +
				" set report_definition = replace(report_definition,?,?)" +
				" where folder_id in (select id from gr_folders where requirement_type_id = ?)  ";
			prepStmt = con.prepareStatement(sql);
			
			// i.e in all gr_requiremetns' user_defined_attributes field, replace attributeName with new one.
			prepStmt.setString(1, oldRTAttribute.attributeName);
			prepStmt.setString(2, attributeName);
			prepStmt.setInt(3, requirementTypeId);
			prepStmt.execute();
			prepStmt.close();

			
			
			// at this point the attribute is updated in the db.
			
			// Now we get the data from the database and populate the bean.
			

			sql = "select id, parent_attribute_id, system_attribute, requirement_type_id, name, description, type , " +
			" options , required , default_value , sort_order, " +
			" impacts_version, impacts_traceability, impacts_approval_workflow," +
			" created_by, created_dt, last_modified_by , last_modified_dt " + 
			" from gr_rt_attributes " +
			" where id = ? ";
			
			
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, rTAttributeId);
			ResultSet rs = prepStmt.executeQuery();
	
			if (rs.next()){
				
				this.attributeId = rs.getInt("id");
				this.parentAttributeId = rs.getInt("parent_attribute_id");
				this.systemAttribute = rs.getInt("system_attribute");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.attributeName = rs.getString("name");
				this.attributeDescription = rs.getString ("description");
				this.attributeType = rs.getString("type");
				this.attributeDropDownOptions = rs.getString("options");
				this.attributeRequired = rs.getInt("required"); 
				this.attributeDefaultValue = rs.getString("default_value");
				this.attributeSortOrder = rs.getString("sort_order");
				
				this.attributeImpactsVersion  = rs.getInt("impacts_version"); 
				this.attributeImpactsTraceability = rs.getInt("impacts_traceability");
				this.attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");

				
				this.createdBy = rs.getString("created_by");
				//this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by") ;
				//this.lastModifiedDt = rs.getDate("last_modified_by");
			}
	
			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		
	}
	
	
	public int getAttributeId(){
		return this.attributeId;
	}
	
	public int getParentAttributeId(){
		return this.parentAttributeId;
	}
	
	public int getSystemAttribute(){
		return this.systemAttribute;
	}
	
	public int getRequirementTypeId () {
		return this.requirementTypeId;
	}
	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getAttributeType(){
		if (this.attributeType == null){
			this.attributeType = "";
		}

		return this.attributeType;
	}
	
	public int getAttributeRequired(){
		return this.attributeRequired;
	}
	
	public int getAttributeDefaultDisplay(){
		int attributeDefaultDisplay = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select default_display from gr_rt_attributes where id = ?   ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.attributeId);
			
			ResultSet rs  = prepStmt.executeQuery();
			while (rs.next()){
				attributeDefaultDisplay = rs.getInt("default_display");
			}
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return attributeDefaultDisplay ;
	}
	

	public void setAttributeDefaultDisplay(int attributeDefaultDisplay){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "update  gr_rt_attributes set default_display = ? where id = ?   ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, attributeDefaultDisplay);
			prepStmt.setInt(2, this.attributeId);
			
			prepStmt.execute();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}


	public void setAttributeDefaultValue(String attributeDefaultValue){
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "update  gr_rt_attributes set default_value = ? where id = ?   ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setString(1, attributeDefaultValue);
			prepStmt.setInt(2, this.attributeId);
			
			prepStmt.execute();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
	}

	
	public String getAttributeDefaultValue(){
		if (this.attributeDefaultValue == null ){
			this.attributeDefaultValue= "";
		}
		return this.attributeDefaultValue;
	}
	
	public String getAttributeDropDownOptions(){
		if (this.attributeDropDownOptions == null){
			this.attributeDropDownOptions = "";
		}
		return this.attributeDropDownOptions;
	}
	
	
	public String getAttributeDescription () {
		return this.attributeDescription;
	}
	
	public String getAttributeSortOrder(){
		return this.attributeSortOrder;
	}

	
	public int getAttributeImpactsVersion(){
		return this.attributeImpactsVersion;
	}
		
	public int getAttributeImpactsTraceability(){
		return this.attributeImpactsTraceability;
	}
		
	public int getAttributeImpactsApprovalWorkflow(){
		return this.attributeImpactsApprovalWorkflow;
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
	

	// called by the editFolder part of FolderAction.
	public ArrayList getChildAttributesIds(){
		ArrayList childAttributeIds = new ArrayList();
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select id from gr_rt_attributes where parent_attribute_id = ?   ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.attributeId);
			
			ResultSet rs  = prepStmt.executeQuery();
			while (rs.next()){
				Integer childAttribute = new Integer(rs.getInt("id"));
				childAttributeIds.add(childAttribute);
			}
			
			prepStmt.close();
			con.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}   finally {
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return childAttributeIds;
		
	}

}
