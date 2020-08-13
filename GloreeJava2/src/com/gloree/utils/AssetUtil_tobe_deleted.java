package com.gloree.utils;

//AssetJava2




import com.gloree.beans.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AssetUtil_tobe_deleted {
		

	public static void setAssetJavaCustomizations(Requirement requirement, HttpServletRequest request , User user) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			// if the object is a program, make a sub folder in allocations for this program
			if (requirement.getRequirementFullTag().startsWith("PGM-")){
				/*
				// NOTE : YUI has issues in explorer if the name  has ' or ". so replacing
				// them with ^.
				// Same with folderDescription.
				String folderName = requirement.getRequirementName();
				if (folderName.length() > 50 ){
					folderName = folderName.substring(1,50);
				}
				folderName += " ("+ requirement.getRequirementFullTag() + ")";
				folderName = folderName.replace('\'', '^');
				folderName = folderName.replace('"', '^');
				folderName = folderName.replace("::", "--");
				
				String folderDescription = requirement.getRequirementDescription();
				folderDescription = folderDescription.replace('\'', '^');
				folderDescription = folderDescription.replace('"', '^');
				folderDescription = folderDescription.replace("::", "--");
				folderDescription = folderDescription.replace('\n', ' ');
				folderDescription = folderDescription.replace('\r', ' ');
				


				// for all sub folders (ie non root level folders), we default the 
				// folderOrder to 0. Since we use the sorting by folder_order, folder_name,
				// we should be Ok
				RequirementType aLCReqType = new RequirementType(requirement.getProjectId(), "ALC", user.getEmailId());
				
					
					
				Folder folder = new Folder( aLCReqType.getRootFolderId(), requirement.getProjectId() , folderName, 
						folderDescription, 0, requirement.getRequirementOwner() , "mySQL");
				
				// Once the folder is created, the project structure has changed and the project object in memory is no longer
				// valid.So, we need to create a new one and replace the one in the session memory.
				Project project = new Project(requirement.getProjectId(), "mySQL");
				HttpSession session = request.getSession(true);
				
				session.setAttribute("project", project);
				
				// Same with the security privs. we need to reset them in the session, so that this user
				// can work on these newly created folders. 
				
				SecurityProfile securityProfile = new SecurityProfile(user.getUserId(),"mySQL");
				session.setAttribute("securityProfile", securityProfile);
				*/

			}
			if (requirement.getRequirementFullTag().startsWith("PGM-")){
				// lets make an entry in ra_programs
				String sql = " insert into ra_programs( "
						+ "	project_id ,tc_id, full_tag , name,	"
						+ " start_dt , end_dt ,	owner ) "
						+ " values ("
						+ " ?,?,?,?,"
						+ " STR_TO_DATE(?, '%m/%d/%Y'), STR_TO_DATE(?, '%m/%d/%Y') ,?) ";
				
				 prepStmt = con.prepareStatement(sql);
				
				prepStmt.setInt(1, requirement.getProjectId() );
				prepStmt.setInt(2, requirement.getRequirementId());
				prepStmt.setString(3, requirement.getRequirementFullTag());
				prepStmt.setString(4, requirement.getRequirementName());
				
				// lets get start date and end date attribute value
				String startDate = requirement.getAttributeValue("Start Date");
				System.out.println("srt start date is " + startDate);
				prepStmt.setString(5, startDate);
				String endDate = requirement.getAttributeValue("End Date");
				System.out.println("srt enddate is " + endDate);
				prepStmt.setString(6, endDate);
				
				prepStmt.setString(7, requirement.getRequirementOwner() );
				
				prepStmt.execute();
				prepStmt.close();
			}

			if (requirement.getRequirementFullTag().startsWith("RES-")){
				// lets make an entry in ra_resources
				String sql = " insert into ra_resources( "
						+ "	project_id ,tc_id, full_tag , name,	"
						+ " type, cost_per_day, unit_of_currency, owner ) "
						+ " values ("
						+ " ?,?,?,?,"
						+ " ?,?,?,?) ";
				
				 prepStmt = con.prepareStatement(sql);
				
				prepStmt.setInt(1, requirement.getProjectId() );
				prepStmt.setInt(2, requirement.getRequirementId());
				prepStmt.setString(3, requirement.getRequirementFullTag());
				prepStmt.setString(4, requirement.getRequirementName());
				
				// lets get start date and end date attribute value
				String type = requirement.getAttributeValue("Type");
				System.out.println("srt type is " + type);
				prepStmt.setString(5, type);
				
				int costPerDay = 0;
				try {
					costPerDay = Integer.parseInt(requirement.getAttributeValue("Cost Per Day"));
				}
				catch (Exception e){
					e.printStackTrace();
				}
					System.out.println("srt enddate is " + costPerDay);
				prepStmt.setInt(6, costPerDay);
				
				String unitOfCurrency = requirement.getAttributeValue("Unit of Currency");
				System.out.println("srt unit of currency is " + unitOfCurrency);
				prepStmt.setString(7, unitOfCurrency);
				
				prepStmt.setString(8, requirement.getRequirementOwner() );
				
				prepStmt.execute();
			}

			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return;
	}






	// make an ra_alllocation and ra_allocation_timeline entry
	public static void makeAllocationEntry(Requirement program, Requirement resource, Requirement allocation ) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the required values
			int programId = 0;
			String sql = " select id from ra_programs where tc_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, program.getRequirementId());
			rs = prepStmt.executeQuery();
			while (rs.next()){
				programId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			
			
			
			int resourceId  = 0;
			sql = " select id from ra_resources where tc_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, resource.getRequirementId());
			rs = prepStmt.executeQuery();
			while (rs.next()){
				resourceId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			
			

			String startDt = allocation.getAttributeValue("Start Date");
			String endDt = allocation.getAttributeValue("End Date");
			String unitOfCurrency = allocation.getAttributeValue("Unit of Currency");
			

			String allocatableOnSaturdays  = allocation.getAttributeValue("Allocatable on Saturdays");
			String allocatableOnSundays  = allocation.getAttributeValue("Allocatable on Sundays");
			int costPerDay = 0;
			try {
				costPerDay = Integer.parseInt(allocation.getAttributeValue("Cost Per Day"));
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			
			
			
			int percentAllocated = 0;
			try {
				percentAllocated  = Integer.parseInt(allocation.getAttributeValue("Percent of Resource Allocated"));
			}
			catch (Exception e){
				e.printStackTrace();
			}
			
			// if the object is a program, make a sub folder in allocations for this program
			// lets make an entry in ra_allocations
			sql = " insert into ra_allocations( "
					+ "	project_id ,tc_id, full_tag , name,	"
					+ " program_name, program_id, program_tc_id, program_full_tag , "
					+ " resource_name, resource_id, resource_tc_id, resource_full_tag, "
					+ " start_dt, end_dt,  cost_per_day, unit_of_currency, "
					+ " allocatable_On_Saturdays, allocatable_On_Sundays, "
					+ " percent_allocated, owner ) "
					+ " values ("
					+ " ?,?,?,?,"
					+ " ?,?,?,?, "
					+ " ?,?,?,?,"
					+ " STR_TO_DATE(?, '%m/%d/%Y'), STR_TO_DATE(?, '%m/%d/%Y') ,?,?,"
					+ "?,? , "
					+ " ?,?) ";
			
			prepStmt = con.prepareStatement(sql);
			
			// allocation details
			prepStmt.setInt(1, allocation.getProjectId() );
			prepStmt.setInt(2, allocation.getRequirementId());
			prepStmt.setString(3, allocation.getRequirementFullTag());
			prepStmt.setString(4, allocation.getRequirementName());
			
			
			// program details
			prepStmt.setString(5, program.getRequirementName());
			prepStmt.setInt(6, programId );
			prepStmt.setInt(7, program.getRequirementId());
			prepStmt.setString(8, program.getRequirementFullTag());
			
			// resource details
			prepStmt.setString(9, resource.getRequirementName());
			prepStmt.setInt(10, resourceId );
			prepStmt.setInt(11, resource.getRequirementId());
			prepStmt.setString(12, resource.getRequirementFullTag());
			
			
			prepStmt.setString(13, startDt);
			prepStmt.setString(14 , endDt );
			prepStmt.setInt(15, costPerDay) ;
			prepStmt.setString(16, unitOfCurrency);
			
			prepStmt.setString(17, allocatableOnSaturdays);
			prepStmt.setString(18, allocatableOnSundays);
			
			
			prepStmt.setInt(19, percentAllocated) ;
			prepStmt.setString(20, allocation.getRequirementOwner());
			
			
			prepStmt.execute();
			prepStmt.close();	
			
			// lets get the alllocation id from the ra_allocation table for the latest insert
			int allocationId  = 0;
			sql = " select id from ra_allocations where tc_id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, allocation.getRequirementId());
			rs = prepStmt.executeQuery();
			while (rs.next()){
				allocationId = rs.getInt("id");
			}
			prepStmt.close();
			rs.close();
			
			
			
			// lets make an ra_alllocations_timeline entry
			//
			
			// for every date between start date and end date, make this entry
			sql = "select  date_format(allocation_date, '%a') 'dayOfWeek' , "
					+ " date_format(allocation_date, '%m/%d/%Y')  'allocation_date' from calendar c "
				+	" where c.allocation_date BETWEEN STR_TO_DATE(?,'%m/%d/%Y') and STR_TO_DATE(?,'%m/%d/%Y')";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, startDt);
			prepStmt.setString(2, endDt);
			rs = prepStmt.executeQuery();

			String sql2 = " insert into ra_allocation_timeline( "
					+ "	project_id  , "
					+ " program_id, program_tc_id, program_full_tag , "
					+ " resource_id, resource_tc_id, resource_full_tag, "
					+ " allocation_id, allocation_tc_id, allocation_full_tag, "
					+ " allocation_dt, percent_allocated, allocation_day_cost , "
					+ " unit_of_currency, owner ) "
					+ " values ("
					+ " ?,"
					+ " ?,?,?,"
					+ " ?,?,?,"
					+ " ?,?,?,"
					+ " STR_TO_DATE(?, '%m/%d/%Y'),?,?, "
					+ " ?,?) ";
			
			PreparedStatement prepStmt2 = con.prepareStatement(sql2);
			
			while (rs.next()){
				
				String dayOfWeek = rs.getString("dayOfWeek");
				if (allocatableOnSaturdays.equals("No")){
					// not allocatbale on saturdays
					if (dayOfWeek.equals("Sat")){
						// skipping this day as we are not allowed to allocate Saturdays
						System.out.println("srt skipping today as it's a " + dayOfWeek + "  and our allocatable onSaturday flag is " +allocatableOnSaturdays );
						continue;
					}
				}
				
				if (allocatableOnSundays.equals("No")){
					// not allocatbale on sundays
					if (dayOfWeek.equals("Sun")){
						// skipping this day as we are not allowed to allocate Sundays
						System.out.println("srt skipping today as it's a " + dayOfWeek + "  and our allocatable on Sunday flag is " + allocatableOnSundays );
						continue;
					}
				}
				
				
				String allocationDate = rs.getString("allocation_date");
				
				// lets set the params for prepStmt2
				prepStmt2.setInt(1, allocation.getProjectId());
			
				prepStmt2.setInt(2, programId );
				prepStmt2.setInt(3, program.getRequirementId());
				prepStmt2.setString(4, program.getRequirementFullTag());
				
				prepStmt2.setInt(5, resourceId );
				prepStmt2.setInt(6, resource.getRequirementId());
				prepStmt2.setString(7, resource.getRequirementFullTag());
					
				prepStmt2.setInt(8, allocationId );
				prepStmt2.setInt(9, allocation.getRequirementId());
				prepStmt2.setString(10, allocation.getRequirementFullTag());

				
				prepStmt2.setString(11, allocationDate);
				prepStmt2.setInt(12, percentAllocated) ;
				prepStmt2.setInt(13, costPerDay) ;

				
				prepStmt2.setString(14, unitOfCurrency);
				prepStmt2.setString(15, allocation.getRequirementOwner());
					
				
				prepStmt2.execute();
				
			}
			prepStmt2.close();
			prepStmt.close();
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return;
	}

	// make an ra_alllocation and ra_allocation_timeline entry
	public static ArrayList getDatesInMonth(String thisMonth) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList datesInMonth = new ArrayList();
		
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the required values
			
			String sql = " select allocation_date, date_format(allocation_date, '%a') 'dayOfWeek',  date_format(allocation_date, '%d') 'dayOfMonth'   "
					+ " from calendar where date_format(allocation_date, '%m/%Y') = ?  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, thisMonth);
			rs = prepStmt.executeQuery();
			String allocationDate = "";
			while (rs.next()){
				allocationDate  = rs.getString("allocation_date");
				String dayOfWeek = rs.getString("dayOfWeek");
				String dayOfMonth = rs.getString("dayOfMonth");
				
				datesInMonth.add(allocationDate + ":#:" + dayOfWeek + ":#:" + dayOfMonth);
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return datesInMonth;
	}



	


	// make an ra_alllocation and ra_allocation_timeline entry
	public static HashMap getResourceTimeline(int projectId, String resourceFullTag) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		HashMap resourceTimelines = new HashMap();
		
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the required values
			
			String sql = " select project_id, resource_full_tag  , allocation_dt, sum(percent_allocated) 'percentAllocated' "
					+ " from ra_allocation_timeline "
					+ " where  project_id =  ? "
					+ " and resource_full_tag = ? "
					+ " group by project_id, resource_full_tag, allocation_dt  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, resourceFullTag);
			
			rs = prepStmt.executeQuery();
			String allocationDate = "";
			int percentAllocated = 0;
			while (rs.next()){
				allocationDate  = rs.getString("allocation_dt");
				percentAllocated = rs.getInt("percentAllocated");
				
				ResourceTimeline rTimeline = new ResourceTimeline (projectId ,  resourceFullTag, allocationDate, percentAllocated);
				resourceTimelines.put( resourceFullTag + ":#:" + allocationDate ,rTimeline);
				
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return resourceTimelines;
	}

	
	public static HashMap getAllocationTimeline(int projectId, String allocationFullTag) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		HashMap resourceTimelines = new HashMap();
		
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the required values
			
			String sql = " select  rat.project_id, rat.resource_full_tag, rat.allocation_full_tag, ra.name,   rat.allocation_dt, sum(rat.percent_allocated) 'percentAllocated' "
					+ " from ra_allocation_timeline rat, ra_allocations ra  "
					+ " where  rat.project_id =  ? "
					+ " and rat.allocation_id = ra.id "
					+ " and rat.allocation_full_tag = ? "
					+ " group by rat.project_id, rat.resource_full_tag, rat.allocation_full_tag, ra.name, rat.allocation_dt  ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, allocationFullTag);
			
			rs = prepStmt.executeQuery();
			String allocationDate = "";
			int percentAllocated = 0;
			String resourceFullTag = "";
			String allocationName = "";
			
			while (rs.next()){
				allocationDate  = rs.getString("allocation_dt");
				percentAllocated = rs.getInt("percentAllocated");
				resourceFullTag = rs.getString("resource_full_tag");
				allocationName = rs.getString("name");

				AllocationTimeline allocationTimeline = new AllocationTimeline (projectId ,  resourceFullTag, allocationFullTag, allocationName, allocationDate, percentAllocated);
				resourceTimelines.put( allocationFullTag + ":#:"  +  allocationDate ,allocationTimeline);
				
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return resourceTimelines;
	}	
	
	// make an ra_alllocation and ra_allocation_timeline entry
	public static ArrayList getResourceAllocations(int projectId, String resourceFullTag) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList allocationTags = new ArrayList();
		
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the required values
			
			
			
			
			String sql = " select distinct project_id, allocation_full_tag, name,  program_name,  "
					+ " from ra_allocations "
					+ " where  project_id =  ? "
					+ " and resource_full_tag = ? ";
			
					
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, resourceFullTag);
			
			rs = prepStmt.executeQuery();
			String allocationFullTag = "";
			
			while (rs.next()){
				allocationFullTag  = rs.getString("allocation_full_tag");
				
				allocationTags.add( allocationFullTag);
				
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return allocationTags;
	}


	public static ArrayList getResourceAllocationTags(int projectId, String resourceFullTag) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		ArrayList allocationTags = new ArrayList();
		
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the required values
			
			String sql = " select distinct rat.allocation_full_tag, ra.name "
					+ " from ra_allocation_timeline rat, ra_allocations ra "
					+ " where  rat.project_id =  ? "
					+ " and rat.resource_full_tag = ? "
					+ " and rat.allocation_id = ra.id ";
			
					
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, resourceFullTag);
			
			rs = prepStmt.executeQuery();
			String allocationFullTag = "";
			String allocationName = "";
			
			while (rs.next()){
				allocationFullTag  = rs.getString("allocation_full_tag");
				allocationName = rs.getString("name");
				
				allocationTags.add( allocationFullTag + ":#:" + allocationName);
				
			}
			prepStmt.close();
			rs.close();
			
			
			
			
			
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (prepStmt !=null) { 
				try {prepStmt.close();} catch (Exception e) {}
			} 
			if (rs != null) { 
				try {rs.close();} catch (Exception e) {}
			} 
			if (con != null) {
				try {con.close();} catch (Exception e) {}
				con = null;
			}
		}
		return allocationTags;
	}

}
