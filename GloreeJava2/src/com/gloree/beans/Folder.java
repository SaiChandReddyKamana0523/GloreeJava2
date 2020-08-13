package com.gloree.beans;

//GloreeJava2
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.InitialContext;

import com.gloree.utils.FolderUtil;
import com.gloree.utils.ProjectUtil;

public class Folder {

	private int folderId;
	private int projectId;
	private String folderName;
	private String folderDescription;
	private int parentFolderId;
	private int folderLevel;
	private int folderOrder;
	private String folderPath;
	private int requirementTypeId;
	private String requirementTypeName;
	private String createdBy;
	// private Date createdDt;
	private String lastModifiedBy;
	// private Date lastModifiedDt;

	// The following method is called when the Folders core values are known
	// and the system is only interested in creating a bean with those values.
	public Folder(int folderId, int projectId, String folderName, String folderDescription, int parentFolderId,
			int folderLevel, int folderOrder, String folderPath, int requirementTypeId, String requirementTypeName,
			String createdBy, String lastModifiedBy) {
		this.folderId = folderId;
		this.projectId = projectId;
		this.folderName = folderName;
		this.folderDescription = folderDescription;
		this.parentFolderId = parentFolderId;
		this.folderLevel = folderLevel;
		this.folderOrder = folderOrder;
		this.folderPath = folderPath;
		this.requirementTypeId = requirementTypeId;
		this.requirementTypeName = requirementTypeName;
		this.createdBy = createdBy;
		// this.createdDt = createdDt;
		this.lastModifiedBy = lastModifiedBy;
		// this.lastModifiedDt = lastModifiedDt;
	}

	// the following method is used when the system knows only the folderId and
	// wants this bean
	// to go and get details i.e. sub folders and requirements.
	public Folder(int folderId) {

		java.sql.Connection con = null;

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "select f.id, f.project_id, f.name, f.description, f.parent_folder_id,"
					+ " f.folder_level, f.folder_order, f.folder_path, rt.name \"requirement_type_name\", "
					+ " rt.id \"requirement_type_id\", f.created_by, f.created_dt, f.last_modified_by , "
					+ " f.last_modified_dt " 
					+ " from gr_folders f, gr_requirement_types rt "
					+ " where f.requirement_type_id = rt.id " 
					+ " and f.id = ? ";

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, folderId);
			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				this.folderId = rs.getInt("id");
				this.projectId = rs.getInt("project_id");
				this.folderName = rs.getString("name");
				this.folderDescription = rs.getString("description");
				this.parentFolderId = rs.getInt("parent_folder_id");
				this.folderLevel = rs.getInt("folder_level");
				this.folderOrder = rs.getInt("folder_order");
				this.folderPath = rs.getString("folder_path");
				this.requirementTypeName = rs.getString("requirement_type_name");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.createdBy = rs.getString("created_by");
				// this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by");
				// this.lastModifiedDt = rs.getDate("last_modified_by");
			}

			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

	}

	// the following method is used when the system knows only the folder path
	// and project id and wants this bean
	// to go and get details i.e. sub folders and requirements.
	public Folder(String folderPath, int projectId) {

		java.sql.Connection con = null;

		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			System.out.println("srt trying to find a folder with this path " + folderPath);
			String sql = "select f.id, f.project_id, f.name, f.description, f.parent_folder_id,"
					+ " f.folder_level, f.folder_order, f.folder_path, rt.name \"requirement_type_name\", "
					+ " rt.id \"requirement_type_id\", f.created_by, f.created_dt, f.last_modified_by , "
					+ " f.last_modified_dt " + " from gr_folders f, gr_requirement_types rt "
					+ " where f.requirement_type_id = rt.id " + " and lower(f.folder_path) = ? "
					+ " and f.project_id = ? ";

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, folderPath.trim().toLowerCase());
			prepStmt.setInt(2, projectId);
			ResultSet rs = prepStmt.executeQuery();

			System.out.println("srt trying to find a folder with this path after trimming etc.."
					+ folderPath.trim().toLowerCase() + " sql is " + sql);
			if (rs.next()) {
				this.folderId = rs.getInt("id");
				this.projectId = rs.getInt("project_id");
				this.folderName = rs.getString("name");
				this.folderDescription = rs.getString("description");
				this.parentFolderId = rs.getInt("parent_folder_id");
				this.folderLevel = rs.getInt("folder_level");
				this.folderOrder = rs.getInt("folder_order");
				this.folderPath = rs.getString("folder_path");
				this.requirementTypeName = rs.getString("requirement_type_name");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.createdBy = rs.getString("created_by");
				// this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by");
				// this.lastModifiedDt = rs.getDate("last_modified_by");

				System.out.println("srt found folder " + folderPath);
			}

			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

	}

	// the following method is used when the system knows only the information
	// required to create a bean in the db
	// the code will do the following :
	// 1. get the parentFolder level and try to calculate this folder's level
	// 1. create the folder in the db
	// 3. get the folder id of the last created folder
	// 4. set the core attributes of this folder.
	// 5. create the canned reports for this folder.
	// we don't need to set the sub folder / requirements values of this folder
	// as this just created.
	// TODO if a folder of the same name exists, to recognize it and give an
	// error message more gracefully

	public Folder(int parentFolderId, int projectId, String folderName, String folderDescription, int folderOrder,
			String createdByEmailId, String databaseType) {
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// get the folder_level of the parent folder and add 1 to it. So
			// that we know
			// that this is a sub folder of the parent.
			// also get the requirement type id and folder path from the parent
			// folder id
			int folderLevel = 1;
			int requirementTypeId = 0;
			String folderPath = "";

			String sql = "SELECT folder_level, folder_path, requirement_type_id" + "  from gr_folders where id = ?";

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, parentFolderId);

			ResultSet rs = prepStmt.executeQuery();

			if (rs.next()) {
				folderLevel = rs.getInt("folder_level");
				folderPath = rs.getString("folder_path");
				requirementTypeId = rs.getInt("requirement_type_id");
			}
			// this folder is a child of the parent. so bump level by 1.
			folderLevel += 1;
			// also, we need to add this folder's name to the parent folders
			// path, to get the path for this folder.
			// i.e proejctName :: parent folder name :: this folders name is the
			// current path.
			folderPath = folderPath + "/" + folderName;

			// Now insert the row in the database. This creates the folder in
			// the system.
			if (databaseType.equals("mySQL")) {
				sql = "insert into gr_folders (project_id, name, description, parent_folder_id, folder_level ,"
						+ " folder_order, "
						+ " folder_path, requirement_type_id, created_by, created_dt, last_modified_by , last_modified_dt) "
						+ " values (?, ?, ?, ?, ?, ?,  ?, ?, ? , now(), ? , now())";
			} else {
				sql = "insert into gr_folders (project_id, name, description, parent_folder_id, folder_level ,"
						+ " folder_order, "
						+ " folder_path, requirement_type_id, created_by, created_dt, last_modified_by , last_modified_dt) "
						+ " values (?, ?, ?, ?, ?, ?,  ?, ?, ? , sysdate, ? , sysdate)";

			}
			prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, projectId);
			prepStmt.setString(2, folderName);
			prepStmt.setString(3, folderDescription);
			prepStmt.setInt(4, parentFolderId);
			prepStmt.setInt(5, folderLevel);
			prepStmt.setInt(6, folderOrder);
			prepStmt.setString(7, folderPath);
			prepStmt.setInt(8, requirementTypeId);
			prepStmt.setString(9, createdByEmailId);
			prepStmt.setString(10, createdByEmailId);

			prepStmt.execute();

			ProjectUtil.createProjectLog(projectId, folderName, "Create", "Created a Folder", createdByEmailId,
					databaseType);

			// We search for the folder with this name. For this folder it
			// should not have
			// any sub folders and requirements. So , we can just get the core
			// folder info
			// (and not the sub folder / requirement info)
			// and set the bean attributes and call it day.
			//
			// Since we have a constraint at db level that says , folder names
			// and parent
			// folder id combinations are unique, we should be safe getting
			// a unique value set.

			// first lets get the id of the folder.
			sql = "select id" + " from gr_folders " + " where  name = ? and parent_folder_id = ?";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, folderName);
			prepStmt.setInt(2, parentFolderId);
			rs = prepStmt.executeQuery();
			int newFolderId = 0;

			if (rs.next()) {
				newFolderId = rs.getInt("id");
			}
			// We also need to set the role privileges for this folder.
			// one way to do this is grand father the role privs of the
			// parent folder to the child folder.
			// the following sql gets the roleprivs of the parent folder
			// and sets them as role privs of the child folder.
			sql = "insert into gr_role_privs ("
					+ " role_id, folder_id, create_requirement, read_requirement, update_requirement,"
					+ " delete_requirement, trace_requirement ," + " approve_requirement, update_attributes) "
					+ " select role_id, ? , create_requirement, read_requirement, update_requirement,"
					+ " delete_requirement, trace_requirement," + " approve_requirement, update_attributes  "
					+ " from gr_role_privs " + " where folder_id = ?";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, newFolderId);
			prepStmt.setInt(2, parentFolderId);
			prepStmt.execute();

			// Now lets create the canned reports..
			FolderUtil.createCannedReports(projectId, newFolderId, folderName, createdByEmailId, databaseType);

			// Now that we have the newly created folder id, lets populate the
			// bean.
			sql = "select f.id, f.project_id, f.name, f.description, f.parent_folder_id,"
					+ " f.folder_level, f.folder_order, f.folder_path, rt.name \"requirement_type_name\", "
					+ " rt.id \"requirement_type_id\", f.created_by, f.created_dt, f.last_modified_by,"
					+ " f.last_modified_dt " + " from gr_folders f, gr_requirement_types rt "
					+ " where f.requirement_type_id = rt.id " + " and f.id = ? ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, newFolderId);
			rs = prepStmt.executeQuery();

			if (rs.next()) {
				this.folderId = rs.getInt("id");
				this.projectId = rs.getInt("project_id");
				this.folderName = rs.getString("name");
				this.folderDescription = rs.getString("description");
				this.parentFolderId = rs.getInt("parent_folder_id");
				this.folderLevel = rs.getInt("folder_level");
				this.folderOrder = rs.getInt("folder_order");
				this.folderPath = rs.getString("folder_path");
				this.requirementTypeName = rs.getString("requirement_type_name");
				this.requirementTypeId = rs.getInt("requirement_type_id");
				this.createdBy = rs.getString("created_by");
				// this.createdDt = rs.getDate("created_dt");
				this.lastModifiedBy = rs.getString("last_modified_by");
				// this.lastModifiedDt = rs.getDate("last_modified_by");

			}

			prepStmt.close();
			rs.close();
			con.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

	}

	// called by the editFolder part of FolderAction.
	public void setNameAndDescription(int folderId, String newFolderName, String newFolderDescription) {
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String oldFolderName = this.folderName;
			String oldFolderPath = this.folderPath;
			// replace the oldFolderPaths' oldFolderName with newFolderName.
			String newFolderPath = oldFolderPath.replace(oldFolderName, newFolderName);

			// Now insert the row in the database. This creates the folder in
			// the system.
			String sql = "update gr_folders set name = ? , description = ? where id = ? ";

			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setString(1, newFolderName);
			prepStmt.setString(2, newFolderDescription);
			prepStmt.setInt(3, folderId);

			prepStmt.execute();

			// since the name of the folder has changed, we need to modify the
			// path
			// of this folder, and that of its sub folders and that of any
			// requirements
			// in this release.
			// so , lets go one step at a time.
			// For all folders in Req Type that this folder belongs to, replace
			// any occurance of oldFolderPath
			// with newFolderPath in the 'folder_path' column.
			// This takes care of scenarios like Rel/test/car and rel/test/bus
			// where you rename test to vehicle.
			// so we will rename the test folder path to re/vehicle and the bus
			// and car folders to correct patht too.

			sql = "update gr_folders set folder_path = replace(folder_path, ?, ? )" + " where requirement_type_id = ? ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setString(1, oldFolderPath);
			prepStmt.setString(2, newFolderPath);
			prepStmt.setInt(3, this.requirementTypeId);
			prepStmt.execute();

			// set the bean attributes.
			this.folderName = newFolderName;
			this.folderDescription = newFolderDescription;

			prepStmt.close();
			con.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

	}

	public int getFolderId() {
		return this.folderId;
	}

	public int getProjectId() {
		return this.projectId;
	}

	public String getFolderName() {
		return this.folderName;
	}

	public String getFolderDescription() {
		return this.folderDescription;
	}

	public int getParentFolderId() {
		return this.parentFolderId;
	}

	public int getFolderLevel() {
		return this.folderLevel;
	}

	public int getFolderOrder() {
		return this.folderOrder;
	}

	public String getFolderPath() {
		return this.folderPath;
	}

	public int getRequirementTypeId() {
		return this.requirementTypeId;
	}

	public String getRequirementTypeName() {
		return this.requirementTypeName;
	}

	public String getRequirementTypeShortName() {

		String requirementTypeShortName = "";
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";

			sql = " select  rt.short_name " + " from gr_folders f , gr_requirement_types rt "
					+ " where f.requirement_type_id = rt.id " + " and f.id = ? ";

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.folderId);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				requirementTypeShortName = rs.getString("short_name");
			}
			prepStmt.close();

			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					;
				}
				con = null;
			}
		}
		return (requirementTypeShortName);
	}

	public int getIsFolderEnabledForApproval() {

		int folderEnabledForApproval = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "	select rt.enable_approval " + " from gr_requirement_types rt, gr_folders f "
					+ " where f.requirement_type_id = rt.id  " + " and  f.id = ?";

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.folderId);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				folderEnabledForApproval = rs.getInt("enable_approval");
			}
			prepStmt.close();

			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					;
				}
				con = null;
			}
		}
		return (folderEnabledForApproval);
	}

	public boolean canBeReportedOrphan() {
		boolean canBeReportedOrphan = FolderUtil.canBeReportedOrphan(this.folderId);
		return (canBeReportedOrphan);
	}

	public boolean canBeReportedDangling() {
		boolean canBeReportedDangling = FolderUtil.canBeReportedDangling(this.folderId);
		return (canBeReportedDangling);
	}

	public int getCountOfRequirements() {

		int countOfRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";

			sql = " select  count(*) countOfRequirements " + " from gr_requirements r " + " where r.folder_id = ?  "
					+ " and r.deleted = 0 ";

			PreparedStatement prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, this.folderId);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				countOfRequirements = rs.getInt("countOfRequirements");
			}
			prepStmt.close();

			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					;
				}
				con = null;
			}
		}
		return (countOfRequirements);
	}

	public int getCumulativeCountOfRequirements() {

		int countOfRequirements = 0;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";

			sql = " select  count(*) countOfRequirements " + " from gr_requirements r , gr_folders f "
					+ " where 	r.folder_id = f.id " + " and f.folder_path like '" + this.folderPath + "%' "
					+ " and f.project_id = ? " + " and r.deleted = 0 ";

			PreparedStatement prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, this.projectId);

			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				countOfRequirements = rs.getInt("countOfRequirements");
			}
			prepStmt.close();

			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					;
				}
				con = null;
			}
		}
		return (countOfRequirements);
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	// public Date getCreatedDt () {
	// return this.createdDt;
	// }

	public String getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	// public Date getLastModifiedDt () {
	// return this.lastModifiedDt;
	// }

	// returns the concatenated list of deleted and active requirements in this
	// folder.
	public ArrayList getDeletedAndActiveRequirements(int projectId, String databaseType) {
		// 0 flag for deleted = 0 i.e not deleted

		ArrayList deletedRequirements = FolderUtil.getRequirementsInFolder(projectId, this.folderId, 0, databaseType);
		ArrayList allRequirements = FolderUtil.getRequirementsInFolder(projectId, this.folderId, 1, databaseType);

		allRequirements.addAll(deletedRequirements);
		return allRequirements;
	}

	public ArrayList getMyRequirements(int projectId, String databaseType) {
		// 0 flag for deleted = 0 i.e not deleted
		int deletedFlag = 0;
		ArrayList myRequirements = FolderUtil.getRequirementsInFolder(projectId, this.folderId, deletedFlag,
				databaseType);
		return myRequirements;
	}

	public ArrayList<Requirement> getMyRequirementsSorted(int projectId, String databaseType, String sortBy, HashMap<String, String> folderFilters) {
		// 0 flag for deleted = 0 i.e not deleted
		int deletedFlag = 0;
		ArrayList<Requirement> myRequirements = FolderUtil.getRequirementsInFolderSorted(projectId, this.folderId, deletedFlag, databaseType, sortBy, 
				folderFilters);
		return myRequirements;
	}

	public ArrayList getMyDeletedRequirements(int projectId, String databaseType) {
		// 1 flag for deleted = 1 i.e deleted
		int deletedFlag = 1;
		ArrayList myRequirements = FolderUtil.getRequirementsInFolder(projectId, this.folderId, deletedFlag,
				databaseType);
		return myRequirements;
	}

	public ArrayList getMyReports(int projectId) {
		ArrayList reports = FolderUtil.getReportsInFolder(projectId, this.folderId);
		return reports;
	}

	public ArrayList getMyWordTemplates(int projectId, String databaseType) {
		ArrayList wordTemplates = FolderUtil.getWordTemplatesInFolder(projectId, this.folderId, databaseType);
		return wordTemplates;
	}

	public ArrayList getCreateRequirementRoles(int projectId) {
		ArrayList createRequirementRoles = FolderUtil.getPrivilegedRolesForFolder(projectId, this.folderId,
				"createRequirement");
		return createRequirementRoles;
	}

	public ArrayList getReadRequirementRoles(int projectId) {
		ArrayList readRequirementRoles = FolderUtil.getPrivilegedRolesForFolder(projectId, this.folderId,
				"readRequirement");
		return readRequirementRoles;
	}

	public ArrayList getUpdateRequirementRoles(int projectId) {
		ArrayList updateRequirementRoles = FolderUtil.getPrivilegedRolesForFolder(projectId, this.folderId,
				"updateRequirement");
		return updateRequirementRoles;
	}

	public ArrayList getDeleteRequirementRoles(int projectId) {
		ArrayList deleteRequirementRoles = FolderUtil.getPrivilegedRolesForFolder(projectId, this.folderId,
				"deleteRequirement");
		return deleteRequirementRoles;
	}

	public ArrayList getTraceToRequirementRoles(int projectId) {
		ArrayList tracetoRequirementRoles = FolderUtil.getPrivilegedRolesForFolder(projectId, this.folderId,
				"traceRequirement");
		return tracetoRequirementRoles;
	}

	public ArrayList getTraceFromRequirementRoles(int projectId) {
		ArrayList tracefromRequirementRoles = FolderUtil.getPrivilegedRolesForFolder(projectId, this.folderId,
				"traceRequirement");
		return tracefromRequirementRoles;
	}

	public ArrayList<Role> getApproveRequirementRoles(int projectId) {
		ArrayList<Role> approveRequirementRoles = FolderUtil.getPrivilegedRolesForFolder(projectId, this.folderId,
				"approveRequirement");
		return approveRequirementRoles;
	}

	public ArrayList<Role> getStackedApprovalRolesForFolder() {
		ArrayList<Role> approveRequirementRoles = FolderUtil.getStackedApprovalRolesForFolder(this.folderId);
		return approveRequirementRoles;
	}

	public ArrayList<String> getDefaultDisplayAttributes() {
		ArrayList<String> defaultDisplayAttributes = FolderUtil.getDefaultDisplayAttributesForFolder(this.folderId);
		return defaultDisplayAttributes;
	}

	public int getFolderMetric_NoOfCompletedRequirements() {
		return (FolderUtil.getFolderMetric_NoOfCompletedRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfTestPendingRequirements() {
		return (FolderUtil.getFolderMetric_NoOfTestPendingRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfTestPassRequirements() {
		return (FolderUtil.getFolderMetric_NoOfTestPassRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfDanglingRequirements() {
		return (FolderUtil.getFolderMetric_NoOfDanglingRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfOrphanRequirements() {
		return (FolderUtil.getFolderMetric_NoOfOrphanRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfSuspectUpstreamRequirements() {
		return (FolderUtil.getFolderMetric_NoOfSuspectUpstreamRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfSuspectDownstreamRequirements() {
		return (FolderUtil.getFolderMetric_NoOfSuspectDownstreamRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfApprovalPendingRequirements() {
		return (FolderUtil.getFolderMetric_NoOfApprovalPendingRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfApprovedRequirements() {
		return (FolderUtil.getFolderMetric_NoOfApprovedRequirements(this.folderId));
	}

	public int getFolderMetric_NoOfRejectedRequirements() {
		return (FolderUtil.getFolderMetric_NoOfRejectedRequirements(this.folderId));
	}

}
