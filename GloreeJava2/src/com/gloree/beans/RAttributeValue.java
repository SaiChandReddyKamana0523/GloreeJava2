package com.gloree.beans;

//GloreeJava2

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;

public class RAttributeValue {

	private int attributeValueId;
	private int attributeId;
	private int systemAttribute;
	private int requirementTypeId;
	private int requirementId;
	private String attributeName;
	private String attributeDescription;
	private String attributeType;
	private String attributeDropDownOptions;
	private int attributeRequired;
	private String attributeDefaultValue;
	private String attributeSortOrder;
	
	private String attributeEnteredValue;
	
	private int attributeImpactsVersion  ; 
	private int attributeImpactsTraceability ;
	private int attributeImpactsApprovalWorkflow ;
	
	// This bean represents a requirement's attibutes and thieir selected values. 
	
	// The following method is called when the Attribute core values are already known and the system is only
	// interested in creating a bean. .
	
	public 	RAttributeValue(int attributeValueId, int attributeId, int systemAttribute, int requirementTypeId, int requirementId, String attributeName, String attributeDescription,
			String attributeType, String attributeDropDownOptions, int attributeRequired, String attributeDefaultValue,     
			String attributeSortOrder, String attributeEnteredValue,
			int attributeImpactsVersion, int attributeImpactsTraceability, int attributeImpactsApprovalWorkflow ){
		
		this.attributeValueId = attributeValueId;
		this.attributeId = attributeId;
		this.systemAttribute = systemAttribute;
		this.requirementTypeId = requirementTypeId;
		this.requirementId = requirementId;
		this.attributeName = attributeName;
		this.attributeDescription = attributeDescription;
		this.attributeType = attributeType;
		this.attributeDropDownOptions = attributeDropDownOptions;
		this.attributeRequired = attributeRequired;
		this.attributeDefaultValue = attributeDefaultValue;
		this.attributeSortOrder = attributeSortOrder;
		this.attributeEnteredValue = attributeEnteredValue;
		
		this.attributeImpactsVersion  = attributeImpactsVersion; 
		this.attributeImpactsTraceability = attributeImpactsTraceability;
		this.attributeImpactsApprovalWorkflow = attributeImpactsApprovalWorkflow;		
		
	}
		
	// this constructor is called when only the RAttributeId is know and we have to create the bean from the values
	// inthe database.
	public 	RAttributeValue(int attributeValueId, String databaseType ){
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// now the bean has been updated lets populate the bean
			String sql = "";
	
			if (databaseType.equals("mySQL")){
				sql = " select av.id \"attribute_value_id\", a.id \"attribute_id\", a.system_attribute , " +
				" a.requirement_type_id, av.requirement_id,a.name, a.description, a.type, a.options, " +
				" a.required, a.default_value, a.sort_order,  ifnull(av.value, '') \"value\", " +
				" a.impacts_version, a.impacts_traceability, a.impacts_approval_workflow " +
				" from gr_rt_attributes a, gr_r_attribute_values av " + 
				" where  av.id= ? " + 
				" and av.attribute_id = a.id " + 
				" order by a.sort_order ";
			}
			else {
				sql = " select av.id \"attribute_value_id\", a.id \"attribute_id\", a.system_attribute , " +
				" a.requirement_type_id, av.requirement_id,a.name, a.description, a.type, a.options, " +
				" a.required, a.default_value, a.sort_order, ifnull(av.value, '') \"value\", " +
				" a.impacts_version, a.impacts_traceability, a.impacts_approval_workflow " +
				" from gr_rt_attributes a, gr_r_attribute_values av " + 
				" where  av.id= ? " + 
				" and av.attribute_id = a.id " + 
				" order by a.sort_order ";
			}
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, attributeValueId);
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()) {
				this.attributeValueId  = rs.getInt("attribute_value_id");
				this.attributeId = rs.getInt("attribute_id");
				this.systemAttribute = rs.getInt("system_attribute");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.requirementId = rs.getInt("requirement_id");
				this.attributeName = rs.getString("name");
				this.attributeDescription = rs.getString("description");
				this.attributeType = rs.getString("type");
				this.attributeDropDownOptions = rs.getString("options");
				this.attributeRequired = rs.getInt("required");
				this.attributeDefaultValue = rs.getString("default_value");
				this.attributeSortOrder = rs.getString("sort_order");
				this.attributeEnteredValue = rs.getString("value");
	
				this.attributeImpactsVersion  = rs.getInt("impacts_version"); 
				this.attributeImpactsTraceability = rs.getInt("impacts_traceability");
				this.attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");
				
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
		
	// this constructor is called when only the requirementId adn attribute id are known and we have to create the bean from the values
	// inthe database.
	// NOTE : this is the 'AttributeId' as in the Requirement Types's Attribute, which is different
	// from Requirement'sAttributeValue Id.
	public 	RAttributeValue(int requirementId  , int attributeId,  String databaseType){
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			// now the bean has been updated lets populate the bean
			String sql = "";
			if (databaseType.equals("mySQL")){
				sql = " select av.id \"attribute_value_id\", a.id \"attribute_id\", a.system_attribute  , " +
				" a.requirement_type_id, av.requirement_id,a.name, a.description, a.type, a.options, " +
				" a.required, a.default_value, a.sort_order,  ifnull(av.value, '') \"value\", " +
				" a.impacts_version, a.impacts_traceability, a.impacts_approval_workflow " +
				" from gr_rt_attributes a, gr_r_attribute_values av " + 
				" where  av.requirement_id= ? " +
				" and av.attribute_id = ? " + 
				" and av.attribute_id = a.id " + 
				" order by a.sort_order ";
			}
			else {
				sql = " select av.id \"attribute_value_id\", a.id \"attribute_id\", a.system_attribute  , " +
				" a.requirement_type_id, av.requirement_id,a.name, a.description, a.type, a.options, " +
				" a.required, a.default_value, a.sort_order,  nvl(av.value, '') \"value\", " +
				" a.impacts_version, a.impacts_traceability, a.impacts_approval_workflow " +
				" from gr_rt_attributes a, gr_r_attribute_values av " + 
				" where  av.requirement_id= ? " +
				" and av.attribute_id = ? " + 
				" and av.attribute_id = a.id " + 
				" order by a.sort_order ";
			}
	
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, requirementId);
			prepStmt.setInt(2, attributeId);
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()) {
				this.attributeValueId  = rs.getInt("attribute_value_id");
				this.attributeId = rs.getInt("attribute_id");
				this.systemAttribute = rs.getInt("system_attribute");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.requirementId = rs.getInt("requirement_id");
				this.attributeName = rs.getString("name");
				this.attributeDescription = rs.getString("description");
				this.attributeType = rs.getString("type");
				this.attributeDropDownOptions = rs.getString("options");
				this.attributeRequired = rs.getInt("required");
				this.attributeDefaultValue = rs.getString("default_value");
				this.attributeSortOrder = rs.getString("sort_order");
				this.attributeEnteredValue = rs.getString("value");
	
				this.attributeImpactsVersion  = rs.getInt("impacts_version"); 
				this.attributeImpactsTraceability = rs.getInt("impacts_traceability");
				this.attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");
				
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
			
	
	// called when the attributeId (from req type), requirementId and attributeValue are known.
	// We then update the attributevalue in the database.
	public 	RAttributeValue(int attributeValueId, String attributeValue, String databaseType , String actorEmailId ) {
		
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			
			String sql = "update gr_r_attribute_values set value = ? where id = ? ";
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, attributeValue);
			prepStmt.setInt(2, attributeValueId);
			
			
			
			prepStmt.execute();
			prepStmt.close();
			
			// now the bean has been updated lets populate the bean
			
	
			if (databaseType.equals("mySQL")){
				sql = " select av.id \"attribute_value_id\", a.id \"attribute_id\", a.system_attribute, " +
				" a.requirement_type_id, av.requirement_id,a.name, a.description, a.type, a.options, " +
				" a.required, a.default_value, a.sort_order, ifnull(av.value, '') \"value\", " +
				" a.impacts_version, a.impacts_traceability, a.impacts_approval_workflow " +
				" from gr_rt_attributes a, gr_r_attribute_values av " + 
				" where  av.id= ? " + 
				" and av.attribute_id = a.id " + 
				" order by a.sort_order ";
			}
			else {
				sql = " select av.id \"attribute_value_id\", a.id \"attribute_id\", a.system_attribute, " +
				" a.requirement_type_id, av.requirement_id,a.name, a.description, a.type, a.options, " +
				" a.required, a.default_value, a.sort_order, nvl(av.value, '') \"value\", " +
				" a.impacts_version, a.impacts_traceability, a.impacts_approval_workflow " +
				" from gr_rt_attributes a, gr_r_attribute_values av " + 
				" where  av.id= ? " + 
				" and av.attribute_id = a.id " + 
				" order by a.sort_order ";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, attributeValueId);
			ResultSet rs = prepStmt.executeQuery();
	
			while (rs.next()) {
				this.attributeValueId  = rs.getInt("attribute_value_id");
				this.attributeId = rs.getInt("attribute_id");
				this.systemAttribute = rs.getInt("system_attribute");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.requirementId = rs.getInt("requirement_id");
				this.attributeName = rs.getString("name");
				this.attributeDescription = rs.getString("description");
				this.attributeType = rs.getString("type");
				this.attributeDropDownOptions = rs.getString("options");
				this.attributeRequired = rs.getInt("required");
				this.attributeDefaultValue = rs.getString("default_value");
				this.attributeSortOrder = rs.getString("sort_order");
				this.attributeEnteredValue = rs.getString("value");
	
				this.attributeImpactsVersion  = rs.getInt("impacts_version"); 
				this.attributeImpactsTraceability = rs.getInt("impacts_traceability");
				this.attributeImpactsApprovalWorkflow = rs.getInt("impacts_approval_workflow");
				
			}
			
			rs.close();
			prepStmt.close();
			// for this req, lets update last modified date.
			
			
			sql = "update gr_requirements set last_modified_by =  ? , last_modified_dt = now()  where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, actorEmailId);
			prepStmt.setInt(2, this.requirementId);
			prepStmt.execute();
			prepStmt.close();
			
			System.out.println(" srt refreshed attribute object " + attributeValueId + " to " +  attributeValue);
			
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

	

	public int getAttributeValueId(){
		return this.attributeValueId;
	}
	
	public int getAttributeId(){
		return this.attributeId;
	}
	
	public int getSystemAttribute(){
		return this.systemAttribute;
	}
	
	public int getRequirementTypeId () {
		return this.requirementTypeId;
	}

	public int getRequirementId () {
		return this.requirementId;
	}

	
	public String getAttributeName(){
		return this.attributeName;
	}
	
	public String getAttributeDescription () {
		return this.attributeDescription;
	}
	
	
	public String getAttributeType(){
		return this.attributeType;
	}
	
	public String getAttributeDropDownOptions(){
		return this.attributeDropDownOptions;
	}
	
	public int getAttributeRequired(){
		return this.attributeRequired;
	}
	
	public String getAttributeDefaultValue(){
		return this.attributeDefaultValue;
	}
	
	
	public String getAttributeSortOrder(){
		return this.attributeSortOrder;
	}
	
	public String getAttributeEnteredValue () {
		if (this.attributeEnteredValue == null){
			this.attributeEnteredValue = "";
		}
		return this.attributeEnteredValue;
	}
	
	public int getAttributeImpactsVersion () {
		return this.attributeImpactsVersion;
	}

	public int getAttributeImpactsTraceability () {
		return this.attributeImpactsTraceability;
	}

	public int getAttributeImpactsApprovalWorkflow () {
		return this.attributeImpactsApprovalWorkflow;
	}

	
}
