package com.gloree.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;

import com.gloree.utils.ProjectUtil;

// this is a wrapper class of Requirement Type.
public class SharedRequirementType {

	private int sRTId;
	private String sRRTBaselineIds ;
	private String sRPublishStatus = "";
	private int sRShareComments ;
	private String sRDomainAdministrators = "";
	private int sRMandatoryNotification ;
	private String sRInstructions = "";
	private RequirementType requirementType ;
	
	
	public SharedRequirementType (int sRTId, String sRRTBaselineIds , String sRPublishStatus , int sRShareComments ,
		String sRDomainAdministrators, int sRMandatoryNotification , String sRInstructions,
		RequirementType requirementType ) {
		this.sRTId = sRTId;
		this.sRRTBaselineIds = sRRTBaselineIds;
		this.sRPublishStatus = sRPublishStatus;
		this.sRShareComments = sRShareComments;
		this.sRDomainAdministrators = sRDomainAdministrators;
		this.sRMandatoryNotification = sRMandatoryNotification;
		this.sRInstructions = sRInstructions;
		this.requirementType = requirementType;
	}
	
	public SharedRequirementType (int sharedRequirementTypeId) {

		java.sql.Connection con = null;

		try {
			
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			this.requirementType = new RequirementType(sharedRequirementTypeId);
			this.sRTId = sharedRequirementTypeId;
			
			String sql = "select sr_rt_baseline_ids, sr_publish_status, sr_share_comments," +
				" sr_domain_administrators, sr_mandatory_notification, sr_instructions " +
				" from gr_requirement_types rt " +
				" where rt.id  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, sharedRequirementTypeId);
			ResultSet rs = prepStmt.executeQuery();


			if (rs.next()){
				this.sRRTBaselineIds = rs.getString("sr_rt_baseline_ids");
				this.sRPublishStatus = rs.getString("sr_publish_status");
				this.sRShareComments = rs.getInt("sr_share_comments");
				this.sRDomainAdministrators = rs.getString("sr_domain_administrators");
				this.sRMandatoryNotification = rs.getInt("sr_mandatory_notification");
				this.sRInstructions = rs.getString("sr_instructions");
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
	
	public SharedRequirementType (int sharedRequirementTypeId, 
			String sRPublishStatus, String sRRTBaselineIds, int sRShareComments, 
			String sRDomainAdministrators, int sRMandatoryNotification, String sRInstructions) {

		java.sql.Connection con = null;

		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource =(javax.sql.DataSource)context.lookup ("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			this.requirementType = new RequirementType(sharedRequirementTypeId);
			this.sRTId = sharedRequirementTypeId;
			this.sRRTBaselineIds = sRRTBaselineIds;
			this.sRPublishStatus = sRPublishStatus;
			this.sRShareComments = sRShareComments;
			this.sRDomainAdministrators = sRDomainAdministrators;
			this.sRMandatoryNotification = sRMandatoryNotification;
			this.sRInstructions = sRInstructions;
			
			String sql = "update gr_requirement_types " +
				" set sr_rt_baseline_ids = ?, " +
				" sr_publish_status = ? ," +
				" sr_share_comments = ? , " +
				" sr_domain_administrators = ? , " +
				" sr_mandatory_notification = ?  , " +
				" sr_instructions = ? " +
				" where id  = ? ";
			
			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, sRRTBaselineIds);
			prepStmt.setString(2, sRPublishStatus);
			prepStmt.setInt(3, sRShareComments);
			prepStmt.setString(4, sRDomainAdministrators);
			
			prepStmt.setInt(5, sRMandatoryNotification);
			prepStmt.setString(6, sRInstructions);
			prepStmt.setInt(7, sharedRequirementTypeId);

			prepStmt.execute();
			prepStmt.close();
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

	public int getSRTId(){
		return this.sRTId;
	}
	public String getSRRTBaselineIDs (){
		if (this.sRRTBaselineIds == null){
			this.sRRTBaselineIds = "";
		}
		return this.sRRTBaselineIds;
	}
	
	public String getSRPublishStatus() {
		if (this.sRPublishStatus == null){
			this.sRPublishStatus = "";
		}
		return this.sRPublishStatus;
	}
	
	public int getSRShareComments(){
		return this.sRShareComments;
	}
	
	public String getSRDomainAdministrators(){
		if (this.sRDomainAdministrators == null){
			this.sRDomainAdministrators = "";
		}
		return this.sRDomainAdministrators;
	}
	
	public int getSRMandatoryNotification(){
		return this.sRMandatoryNotification;
	}
	
	public String getSRInstructions(){
		if (this.sRInstructions == null){
			this.sRInstructions = "";
		}
		return this.sRInstructions;
	}

	public RequirementType getRequirementType(){
		return this.requirementType;
	}
	
	public ArrayList getAllSharedAttributesInRequirementType(){
		ArrayList attributes = ProjectUtil.getAllAttributes(this.sRTId);
		ArrayList sRTAttributes = new ArrayList();
		Iterator a = attributes.iterator();
		while (a.hasNext()){
			RTAttribute rTAttribute = (RTAttribute) a.next();
			SharedRequirementTypeAttribute sRTAttribute = new SharedRequirementTypeAttribute(rTAttribute.getAttributeId());
			sRTAttributes.add(sRTAttribute);
		}
		
		return sRTAttributes;
	}

}
