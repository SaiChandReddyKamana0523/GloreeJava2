package com.gloree.actions;

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to import an excel file to either create requiremetns / update reqs.
//  This servlet does the actual creation / updation of requirements. 
// Note : a similarly named servlet 'ImportFromExcelAction' just stores the file on disk.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import com.gloree.beans.*;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.RequirementUtil;
import com.gloree.utils.SecurityUtil;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*; //Excel POI stuff.

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

public class ImportFromExcelProcessAction extends HttpServlet {

	public ImportFromExcelProcessAction() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");
		// /////////////////////////////SECURITY//////////////////////////////
		// Security Note:
		// user has to be logged in by the time he is here.
		// And he needs to be an Member
		// of this project. Note : Admins are members too.
		// /////////////////////////////SECURITY//////////////////////////////

		// see if the user is logged in. If he is not, the method below will
		// redirect him to the log in page.
		if (!(SecurityUtil.authenticationPassed(request, response))){
			return;
		}

		// now check if this users should be in this project.
		HttpSession session = request.getSession(true);
		SecurityProfile securityProfile = (SecurityProfile) session
				.getAttribute("securityProfile");
		Project project = (Project) session.getAttribute("project");
		if (!(securityProfile.getRoles().contains("MemberInProject"
				+ project.getProjectId()))) {
			// User is NOT a member of this project. so do nothing and return.
			return;
		}

		User user = securityProfile.getUser();
		// /////////////////////////////SECURITY//////////////////////////////

		String action = request.getParameter("action");
		if (action.equals("createNewRequirements")) {
			// lets get the excel file, parse it per the mapping info and then
			// create the requrements
			// first lets validate the excel file.
			boolean validFile = false;
			validFile = validateCreateNewRequirementsExcelFile(request,
					session, user, project, securityProfile);

			if (validFile) {
				createNewRequirementsFromExcel(request, session, user, project,
						securityProfile, databaseType);
				// forward request to be displayed.

				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/jsp/Excel/createNewRequirementsFromExcelResults.jsp");
				dispatcher.forward(request, response);
			} else {
				// there is something wrong with the input file. So , lets print
				// the error messages.
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/jsp/Excel/createNewRequirementsFromExcelInvalidFile.jsp");
				dispatcher.forward(request, response);
			}

		} else if (action.equals("updateExistingRequirements")) {
			// lets get the excel file, parse it per the mapping info and then
			// update the requrements
			// first lets validate the excel file.
			boolean validFile = false;
			validFile = validateUpdateExistingRequirementsExcelFile(request,
					session, user, project, securityProfile, databaseType);


			if (validFile) {

				updateExistingRequirementsFromExcel(request, session, user,
						project, securityProfile, databaseType);
				// forward request to be displayed.

				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/jsp/Excel/updateExistingRequirementsFromExcelResults.jsp");
				dispatcher.forward(request, response);
			} else {
				// there is something wrong with the input file. So , lets print
				// the error messages.
				RequestDispatcher dispatcher = request
						.getRequestDispatcher("/jsp/Excel/updateExistingRequirementsFromExcelInvalidFile.jsp");
				dispatcher.forward(request, response);
			}
		}
	}

	// this method reads the excel file, parses it, and checks to see if the
	// reqs are valid.
	// returns a boolean value based on Reqs Valid or not.
	// returns true is valid and false if invalid.
	private boolean validateCreateNewRequirementsExcelFile(
			HttpServletRequest request, HttpSession session, User user,
			Project project, SecurityProfile securityProfile) {

		boolean validFile = true;
		// we are taking a conscious decision to not check for security while
		// creating requirements.
		// this is controlled by 'Import from Excel' button being grayed out for
		// this folders
		// if the user does not have 'CreateRequirements' privs on tihs folder.

		ArrayList errorRows = new ArrayList();
		String excelFilePath = (String) session.getAttribute("excelFilePath");

		try {
			Folder folder = new Folder(Integer.parseInt(request
					.getParameter("folderId")));

			// lets get the column number where we can find the requirement
			// attributes.
			int nameColumn = Integer.parseInt(request
					.getParameter("nameColumn"));
			int descriptionColumn = Integer.parseInt(request
					.getParameter("descriptionColumn"));
			int priorityColumn = Integer.parseInt(request
					.getParameter("priorityColumn"));
			int ownerColumn = Integer.parseInt(request
					.getParameter("ownerColumn"));
			int pctCompleteColumn = Integer.parseInt(request
					.getParameter("pctCompleteColumn"));
			int externalURLColumn = Integer.parseInt(request
					.getParameter("externalURLColumn"));
			int folderPathColumn = Integer.parseInt(request
					.getParameter("folderPathColumn"));
			int processSelectedColumn = Integer.parseInt(request
					.getParameter("processSelectedColumn"));
			int parentChildColumn = Integer.parseInt(request.getParameter("parentChildColumn"));

			
			int traceToColumn = -1;
			if (request.getParameter("traceToColumn") != null) {
				traceToColumn = Integer.parseInt(request
						.getParameter("traceToColumn"));
			}
			int traceFromColumn = -1;
			if (request.getParameter("traceFromColumn") != null) {
				traceFromColumn = Integer.parseInt(request
						.getParameter("traceFromColumn"));
			}

			int testingStatusColumn = -1;
			if (request.getParameter("testingStatusColumn") != null) {
				testingStatusColumn = Integer.parseInt(request
						.getParameter("testingStatusColumn"));
			}

			// lets get the custom attribute Ids.
			String[] attributeIds = {};

			String attributeIdString = request
					.getParameter("attributeIdString");

			try {
				// remove the last # from attributeIdString.
				if (attributeIdString.contains("::")) {
					attributeIdString = (String) attributeIdString.subSequence(
							0, attributeIdString.lastIndexOf("::"));
					attributeIds = attributeIdString.split("::");
				}
			} catch (Exception e) {
				// do nothing.
			}

			// lets open the Excel File and start reading values...
			InputStream myxls = new FileInputStream(excelFilePath);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0); // first sheet

			// We will look for up to 20000 rows per excel page.
			// Also since row 1 (that is i==0) will be the header row, we will
			// start from i =1 .
			for (int i = 1; i < 20000; i++) {

				// we will use inputRow to figure out if this were an empty row
				// .
				String dataInputRow = "";

				// cathces any error messages.
				String errorMessage = "";
				boolean validRow = true;
				String requirementName = "";
				String requirementDescription = "";
				String requirementPriority = "";
				String requirementOwner = "";
				int requirementPctComplete = 0;
				String requirementExternalURL = "";
				String requirementFolderPath = "";
				String processSelected = "";
				int parentChild = 0;
				String requirementTraceTo = "";
				String requirementTraceFrom = "";

				String requirementTestingStatus = "";

				// lets get the row.
				HSSFRow row = sheet.getRow(i);

				if (row == null) {
					continue;
				}
				if (processSelectedColumn > -1) {
					// Since the user chose this option, we process only those rows 
					// with a 'yes' value in this column.
					try {
						HSSFCell processSelectedCell = row.getCell(processSelectedColumn);
						processSelected = processSelectedCell.getStringCellValue();
					} catch (Exception e) {
						// do nothing
					}
					if (!(processSelected.trim().toLowerCase().equals("yes"))){
						// we don't need to process this row.
						continue;
					}
				}
				// requirement Name
				try {
					HSSFCell requirementNameCell = row.getCell(nameColumn);
					requirementName = requirementNameCell.getStringCellValue();
					dataInputRow += requirementName;
					if (requirementName.equals("")) {
						validRow = false;
						errorMessage += " 		Requirement Name is empty <br>";

					}
				} catch (Exception e) {
					// if we run into any exception, set error message.
					validRow = false;
					errorMessage += " 		Requirement Name is empty <br>";
				}

				// requirement Description
				try {
					HSSFCell requirementDescriptionCell = row
							.getCell(descriptionColumn);
					requirementDescription = requirementDescriptionCell
							.getStringCellValue();
					if (requirementDescription.equals("")) {
						validRow = false;
						errorMessage += " 		Requirement Description is empty <br>";
					}
					dataInputRow += requirementDescription;
				} catch (Exception e) {
					// if we run into any exception, set error message.
					validRow = false;
					errorMessage += " 		Requirement Description is empty<br>";
				}

				// to prevent processing a lot of empty rows, we check to see if
				// req name and description are emmpty. If they are , then we
				// skip processing this row.

				if ((requirementName.equals(""))
						&& (requirementDescription.equals(""))) {
					continue;
				}

				// requirement Priority
				// do the validation only if the user chose to provide this
				// column of data
				if (priorityColumn > -1) {
					try {

						HSSFCell requirementPriorityCell = row.getCell(priorityColumn);
						requirementPriority = requirementPriorityCell.getStringCellValue();
						
						// lets ensure that priority is in valid list of values.
						if ((requirementPriority == null) || (requirementPriority.equals(""))) {
							validRow = false;
							errorMessage += " 		Requirement Priority is a required field.<br>";
							
						}
						else if ((requirementPriority != null)
								&& (!((requirementPriority.trim().toUpperCase().contains("HIGH"))
										|| (requirementPriority.trim().toUpperCase().contains("MEDIUM"))
										|| (requirementPriority.trim().toUpperCase().contains("LOW"))))) {
							validRow = false;
							errorMessage += " 		Requirement Priority is not in High or Medium or Low.<br>";
						}
						
						dataInputRow += requirementPriority;						
					} catch (Exception e) {

						validRow = false;
						errorMessage += " 		Requirement Priority is a required field.<br>";
					}
				}

				// requirement Owner
				// do the validation only if the user chose to provide this
				// column of data
				if (ownerColumn > -1) {
					try {
						HSSFCell requirementOwnerCell = row.getCell(ownerColumn);
						requirementOwner = requirementOwnerCell.getStringCellValue();
						if ((requirementOwner == null) || (requirementOwner.equals(""))) {
							validRow = false;
							errorMessage += " 		Requirement Owner is a required field.<br>";
							
						}
						else if (!(ProjectUtil.isValidUserInProject(requirementOwner, project))) {
							// if the requirementOwner is not a member of this
							// project,
							// default the owner field to the person importing the
							// requirement.

							validRow = false;
							errorMessage += " 		Requirement Owner is not a member of this project.<br>";

						}
						dataInputRow += requirementOwner;

					} catch (Exception e) {
						validRow = false;
						errorMessage += " 		Requirement Owner is a required field.<br>";

					}
				}

				// requirement PctComplete
				// default this to 0, and try to compute the value.
				// do the validation only if the user chose to provide this
				// column of data
				if (pctCompleteColumn > -1) {

					try {
						HSSFCell requirementPctCompleteCell = row.getCell(pctCompleteColumn);
						
						if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
							requirementPctComplete = (int) requirementPctCompleteCell.getNumericCellValue();
						}
						if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_STRING ) {
							requirementPctComplete = Integer.parseInt(requirementPctCompleteCell.getStringCellValue());
						
						}
						
						if (requirementPctComplete > 100) {
							validRow = false;
							errorMessage += " 		Percent Complete > 100 .<br>";

						}
						if (requirementPctComplete < 0) {
							validRow = false;
							errorMessage += " 		Percent Complete < 0  .<br>";

						}
					} catch (Exception e) {
						// if requirementPctComplete does not have a valid
						// value, we
						// can use the default 0
						// so no error condition.
						validRow = false;
						errorMessage += " 		Percent Complete is not a number.<br>";

					}
				}

				
				// requirement Folder Path
				// do the validation only if the user chose to provide this
				// column of data
				if (folderPathColumn > -1) {
					try {
						HSSFCell folderPathCell = row.getCell(folderPathColumn);
						requirementFolderPath = folderPathCell.getStringCellValue().trim();
						if ((folderPathCell == null) || (folderPathCell.equals(""))) {
							validRow = false;
							errorMessage += " 		Folder Path is required<br>";
							
						}
						else if (!(ProjectUtil.isValidFolderPathForRequirementType(requirementFolderPath, folder.getRequirementTypeId()))) {
							// if the requirementOwner is not a member of this
							// project,
							// default the owner field to the person importing the
							// requirement.

							validRow = false;
							errorMessage += " 		Not a valid Folder Path for this Requirement Type.<br>";

						}
						else {
							Folder rowFolder = new Folder(requirementFolderPath, project.getProjectId());
							if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
									+ rowFolder.getFolderId()))){
								validRow = false;
								errorMessage += " 		You do not have Create Requirements permissions on this folder.<br>";
							}
						}
						dataInputRow += requirementFolderPath;

					} catch (Exception e) {
						validRow = false;
						errorMessage += " 		Not a valid folder path.<br>";

					}
				}
				if (parentChildColumn > -1) {

					try {
						HSSFCell parentChildCell = row.getCell(parentChildColumn);
						if (parentChildCell != null) {
							if (parentChildCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
								parentChild = (int) parentChildCell.getNumericCellValue();
							}
							if (parentChildCell.getCellType() == HSSFCell.CELL_TYPE_STRING ) {
								parentChild= Integer.parseInt(parentChildCell.getStringCellValue());
							}
						}
					} catch (Exception e) {
						// if requirementPctComplete does not have a valid
						// value, we
						// can use the default 0
						// so no error condition.
						validRow = false;
						errorMessage += " 		Parent Child Value is not a number.<br>";
					}
				}

				
				
				
				// lets validate the traceto and trace from values.
				// requirement traceTo
				if (traceToColumn != -1) {
					try {
						HSSFCell requirementTraceToCell = row
								.getCell(traceToColumn);
						requirementTraceTo = requirementTraceToCell
								.getStringCellValue();
					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						requirementTraceTo = "";
					}

					// if a non empty field is given, lets make sure that req
					// ids exist.
					if (!(requirementTraceTo.equals(""))) {
						// if all req tags are valid , you get back an empty
						// string. else
						// you get back a list of tags that don't exist in the
						// system.
						String status = RequirementUtil.validateRequirementTags(requirementTraceTo,project.getProjectId());
						if (!(status.equals(""))) {
							validRow = false;
							errorMessage += " 		Following Requirement Tags do not exist in the system (Either deleted or purged)."
									+ " Trace To Column.<br>" + status;
						}
					}
				}

				// requirement traceFrom
				if (traceFromColumn != -1) {
					try {
						HSSFCell requirementTraceFromCell = row
								.getCell(traceFromColumn);
						requirementTraceFrom = requirementTraceFromCell
								.getStringCellValue();
					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						requirementTraceFrom = "";
					}

					// if a non empty field is given, lets make sure that req
					// ids exist.
					if (!(requirementTraceFrom.equals(""))) {
						// if all req tags are valid , you get back an empty
						// string. else
						// you get back a list of tags that don't exist in the
						// system.
						String status = RequirementUtil
								.validateRequirementTags(requirementTraceFrom,
										project.getProjectId());
						if (!(status.equals(""))) {
							validRow = false;
							errorMessage += " 		Following Requirement Tags do not exist in the system (Either deleted or purged)."
									+ " Trace From Column.<br>" + status;
						}
					}
				}

				// requirement testingStatus
				if (testingStatusColumn != -1) {
					try {
						HSSFCell requirementTestingStatusCell = row.getCell(testingStatusColumn);
						requirementTestingStatus = requirementTestingStatusCell.getStringCellValue();
					} catch (Exception e) {
						// testing status can be empty. So no error condition.
						requirementTraceFrom = "";
					}

					// if a non empty field is given, lets make sure its valid
					if (!(requirementTestingStatus.equals(""))){
						if ((!
								(
								(requirementTestingStatus.trim().toUpperCase().contains("PENDING"))
								|| 
								(requirementTestingStatus.trim().toUpperCase().contains("PASS"))
									|| 
								(requirementTestingStatus.trim().toUpperCase().contains("FAIL"))
								)
							)) {
							// non empty testing status and testing status is not one of Pending, Pass, Fail.
							validRow = false;
							errorMessage += " 		Requirement Testing Status is not in Pending or Pass or Fail.<br>";
						}
					}
					
				}
				
				// lets validate the custom attribute values.

				for (int j = 0; j < attributeIds.length; j++) {
					int attributeId = Integer.parseInt(attributeIds[j]);
					RTAttribute rTAttribute = new RTAttribute(attributeId);
					String attributeValue = "";
					int attributeIdColumn = -1;

					try {

						String attributeIdColumnString = request.getParameter(Integer.toString(attributeId));
						attributeIdColumn = Integer.parseInt(attributeIdColumnString);
					} catch (Exception e) {
						// if we run into any exception, we don't need to worry.
						attributeValue = "";
					}

					// do the validation only if the user chose to provide this
					// column of data
					if (attributeIdColumn > -1) {
						HSSFCell attributeCell = row.getCell(attributeIdColumn);
						if (rTAttribute.getAttributeType().equals("Date")){
							try {
								// if this is  a date type attribute, lets get the value
								if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
									 attributeValue = attributeCell.getDateCellValue().toString();
								}
								else{
									// there are situations where the user sent in data in mm/dd/yyyy format
									// but it was sent as a text string.
									attributeValue = attributeCell.getStringCellValue().trim();
									// lets try to convert this to a date just make sure its valid
									// we convert this to a date and back to string
									DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
									attributeValue = formatter.format(formatter.parse(attributeValue));
									
								}
							} catch (Exception e) {
								e.printStackTrace();
								attributeValue = "";
							}
							// if this is a required field and we didn't get a valid date, then we error out.
							if ((rTAttribute.getAttributeRequired() == 1) && (attributeValue.equals("")) ) {
								validRow = false;
								int errorColumn = attributeIdColumn + 1;
								errorMessage += "Custom attribute "+ rTAttribute.getAttributeName() +" should be a valid Excel date. See Column #  "
										+ errorColumn + "<br>";
								continue;
							}
						}
						
						
						// if this is not a date type attrib lets process it now.
						if (!(rTAttribute.getAttributeType().equals("Date"))){
							try {
								if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
									Double attributeValueDouble = new Double(attributeCell.getNumericCellValue());
									attributeValue = Integer.toString(attributeValueDouble.intValue());
								}
								else {
									attributeValue = attributeCell.getStringCellValue().trim();
								}
							} catch (Exception e) {
								// if we run into any exception, we don't need to
								// worry.
								attributeValue = "";
							}
							// if this attribute is mandatory lets make sure the input value is not empty 
							if ((rTAttribute.getAttributeRequired() == 1) && (attributeValue.equals(""))){
								validRow = false;
								int errorColumn = attributeIdColumn + 1;
								errorMessage += "Empty value is not permitted in mandatory custom attribute "+ rTAttribute.getAttributeName() +". See Column #  "
										+ errorColumn + "<br>";
								continue;
							}
							
						}
						
						
						
						
						
						
						
						
						if (!attributeValue.equals("")) {
							
							
							// lets see if this attribute value is permitted for
							// this attribute. this tests for drop down value matches
							boolean permitted = ProjectUtil.isPermittedValueInAttribute(attributeId, attributeValue);
							// if permitted , lets update the req attribute to
							// this
							// value.
							// else log it.
							if (!(permitted)) {
								validRow = false;
								int errorColumn = attributeIdColumn + 1;
								errorMessage += attributeValue
										+ " is not a permitted value in custom attribute "+ rTAttribute.getAttributeName() +". See Column #  "
										+ errorColumn + "<br>";
								continue;
							}
						}
					}
				}

				int rowNum = i + 1;
				if ((!(errorMessage.equals("")))
						&& (!(dataInputRow.equals("")))) {

					// this means we have an error row and this input row from
					// excel
					// was not a blank row. we need to do this because
					// we want process up to 20000 rows.
					// NOTE : since we want to display the excel row num in the
					// results page,
					// we add that to the error message.
					// we have to be careful to differentiate entire empty rows
					// from
					// rows with missing values. imagine an excel file with 10
					// rows of data
					// and 20000 empty rows. we don't want to display the empty
					// rows as
					// error data.
					String inputString = rowNum + ":##:" + "Name : "
							+ requirementName + ";  " + "Priority : "
							+ requirementPriority + ";  " + "Owner : "
							+ requirementOwner + ";  " + "Pct Complete : "
							+ requirementPctComplete + ";  "
							+ "External URL : " + requirementExternalURL
							+ ";  " + "Description : " + requirementDescription
							+ "; :##:" + errorMessage;
					errorRows.add(inputString);
				}
				// for empty rows, we won't even get here.
				// for data rows, if there is something wrong with it
				// we set the file to invalid.
				if (!validRow) {
					validFile = false;
				}
			}
			// close the input stream.
			myxls.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Now we need to add the createdRequirements and errorRows to the
		// request.
		request.setAttribute("errorRows", errorRows);

		// if invalid file, then we need to drop the uploaded file, so that the
		// user can
		// make changes and re upload.
		if (!(validFile)) {
			// Clean up. i.e drop files, folders etc...
			// we need to delete the file and the folder it contains. 
			// Note : we keep only 1 file in each folder. so this is safe.
			File file = new File(excelFilePath);
			if (file != null){
				File dir = file.getParentFile();
				// lets drop the file.
				file.delete();
				
				if (dir != null) {
					dir.delete();
				}
			}

		}
		return validFile;
	}

	// this method reads the excel file, parses it, creates requirements
	// it then removes the session variables & file from disk.
	// it also stores the response on the request variables, so that forwarded
	// JSP can display it.

	// Note all the validation is done prior to coming here, so the file should
	// be clean.
	private void createNewRequirementsFromExcel(HttpServletRequest request,
			HttpSession session, User user, Project project,
			SecurityProfile securityProfile, String databaseType) {

		// we are taking a conscious decision to not check for security while
		// creating requirements.
		// this is controlled by 'Import from Excel' button being grayed out for
		// this folders
		// if the user does not have 'CreateRequirements' privs on tihs folder.

		ArrayList createdRequirements = new ArrayList();
		ArrayList alertRows = new ArrayList();

		String excelFilePath = (String) session.getAttribute("excelFilePath");

		try {

			Folder folder = new Folder(Integer.parseInt(request
					.getParameter("folderId")));

			int requirementTypeId = folder.getRequirementTypeId();
			int folderId = folder.getFolderId();
			int projectId = project.getProjectId();

			// lets get the column number where we can find the requirement
			// attributes.
			int nameColumn = Integer.parseInt(request
					.getParameter("nameColumn"));
			int descriptionColumn = Integer.parseInt(request
					.getParameter("descriptionColumn"));
			int priorityColumn = Integer.parseInt(request
					.getParameter("priorityColumn"));
			int ownerColumn = Integer.parseInt(request
					.getParameter("ownerColumn"));
			int pctCompleteColumn = Integer.parseInt(request
					.getParameter("pctCompleteColumn"));
			int externalURLColumn = Integer.parseInt(request
					.getParameter("externalURLColumn"));
			int folderPathColumn = Integer.parseInt(request
					.getParameter("folderPathColumn"));
			int processSelectedColumn = Integer.parseInt(request
					.getParameter("processSelectedColumn"));
			
			int existingParentColumn = Integer.parseInt(request.getParameter("existingParentColumn"));
			int parentChildColumn = Integer.parseInt(request.getParameter("parentChildColumn"));

			
			
			int traceToColumn = -1;
			if (request.getParameter("traceToColumn") != null) {
				traceToColumn = Integer.parseInt(request
						.getParameter("traceToColumn"));
			}
			int traceFromColumn = -1;
			if (request.getParameter("traceFromColumn") != null) {
				traceFromColumn = Integer.parseInt(request
						.getParameter("traceFromColumn"));
			}

			int testingStatusColumn = -1;
			if (request.getParameter("testingStatusColumn") != null) {
				testingStatusColumn = Integer.parseInt(request
						.getParameter("testingStatusColumn"));
			}
			
			// lets get the custom attribute Ids.
			String[] attributeIds = {};

			String attributeIdString = request
					.getParameter("attributeIdString");

			try {
				// remove the last # from attributeIdString.
				if (attributeIdString.contains("::")) {
					attributeIdString = (String) attributeIdString.subSequence(
							0, attributeIdString.lastIndexOf("::"));
					attributeIds = attributeIdString.split("::");
				}
			} catch (Exception e) {
				// do nothing.
			}

			// lets open the Excel File and start reading values...
			InputStream myxls = new FileInputStream(excelFilePath);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0); // first sheet

			// we create a hashmap called parent map that tracks the 
			// row number of the input file to the requirement tag of the created row
			// as we create new requirements that need to be children of an earlier inputed row
			// we can look up in he map to figure out the requirement full tag.
			HashMap parentMap = new HashMap();
			// We will look for up to 5,000 rows per excel page.
			// Also since row 1 (that is i==0) will be the header row, we will
			// start from i =1 .
			for (int i = 1; i < 20000; i++) {

				int rowNum = i + 1;

				String requirementName = "";
				String requirementDescription = "";

				String requirementPriority = "";
				String requirementOwner = "";
				int requirementPctComplete = 0;
				String requirementExternalURL = "";
				String processSelected = "";
				
				String existingParent = "";
				int parentChild = 0;

				String requirementTraceTo = "";
				String requirementTraceFrom = "";

				String requirementTestingStatus = "";

				// lets get the row.
				HSSFRow row = sheet.getRow(i);
				if (row == null){
					continue;
				}
				if (processSelectedColumn > -1) {
					// Since the user chose this option, we process only those rows 
					// with a 'yes' value in this column.
	
					// process selected row
					try {
						HSSFCell processSelectedCell = row.getCell(processSelectedColumn);
						processSelected = processSelectedCell.getStringCellValue();
					} catch (Exception e) {
						// do nothing
					}
					if (!(processSelected.trim().toLowerCase().equals("yes"))){
						// we don't need to process this row.
						continue;
					}
				}
				// requirement Name
				HSSFCell requirementNameCell = row.getCell(nameColumn);
				if (requirementNameCell != null) {
					requirementName = requirementNameCell.getStringCellValue();
				}
				
				if (requirementName.length() > 3999) {
					requirementName = requirementName.substring(0, 3990) + "...";
				}

				// requirement Description
				HSSFCell requirementDescriptionCell = row
						.getCell(descriptionColumn);
				if (requirementDescriptionCell != null) {
					requirementDescription = requirementDescriptionCell
						.getStringCellValue();
					if ((requirementDescription != null) && (requirementDescription.contains("\n"))){
						requirementDescription = requirementDescription.replace("\n", "<br>");
					}
				}
				// to prevent processing a lot of empty rows, we check to see if
				// req name and description are emmpty. If they are , then we
				// skip processing this row.

				if ((requirementName.equals("")) && (requirementDescription.equals(""))) {
					continue;
				}

				// requirement Priority
				// get the value from the excel only if the user gave us a
				// column to pick from.
				if (priorityColumn > -1) {
					HSSFCell requirementPriorityCell = row
							.getCell(priorityColumn);
					requirementPriority = requirementPriorityCell
							.getStringCellValue();
					if ((requirementPriority.trim().toUpperCase().equals("HIGH"))) {
						requirementPriority = "High";
					}
					if ((requirementPriority.trim().toUpperCase().equals("MEDIUM"))) {
						requirementPriority = "Medium";
					}

					if ((requirementPriority.trim().toUpperCase().equals("LOW"))) {
						requirementPriority = "Low";
					}
				} else {
					requirementPriority = "Medium";
				}

				// requirement Owner
				// get the value from the excel only if the user gave us a
				// column to pick from.
				if (ownerColumn > -1) {
					HSSFCell requirementOwnerCell = row.getCell(ownerColumn);
					requirementOwner = requirementOwnerCell
							.getStringCellValue();
				} else {
					requirementOwner = user.getEmailId();
				}

				// requirement PctComplete
				// get the value from the excel only if the user gave us a
				// column to pick from.
				if (pctCompleteColumn > -1) {
					HSSFCell requirementPctCompleteCell = row.getCell(pctCompleteColumn);
					if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
						requirementPctComplete = (int) requirementPctCompleteCell.getNumericCellValue();
					}
					if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_STRING ) {
						requirementPctComplete = Integer.parseInt(requirementPctCompleteCell.getStringCellValue());
					
					}
					
				} else {
					requirementPctComplete = 0;
				}

				// we are not validaing external url and trace to and from
				// objects, in the validatio file.
				// so lets validate them here.
				// requirement ExternalURL
				try {
					HSSFCell requirementExternalURLCell = row
							.getCell(externalURLColumn);
					requirementExternalURL = requirementExternalURLCell
							.getStringCellValue();
					if (requirementExternalURL.length() > 999) {
						requirementExternalURL = requirementExternalURL
								.substring(0, 998);
					}
				} catch (Exception e) {
					// external URL can be empty. So no error condition.
					requirementExternalURL = "";
				}
				
				// requirement Folder Path
				// do the validation only if the user chose to provide this
				// column of data
				int rowFolderId = folderId;
				if (folderPathColumn > -1) {
					try {
						HSSFCell folderPathCell = row.getCell(folderPathColumn);
						String requirementFolderPath = folderPathCell.getStringCellValue().trim();
						Folder rowFolder = new Folder(requirementFolderPath, project.getProjectId());
						rowFolderId = rowFolder.getFolderId();
					} catch (Exception e) {
						e.printStackTrace();
						rowFolderId = folderId;
					}
				}

			
				if (existingParentColumn > -1) {
					System.out.println("srt existingParentColumn is " + existingParentColumn);
					try {
						HSSFCell existingParentCell = row.getCell(existingParentColumn);
						if (existingParentCell != null) {
							if (existingParentCell.getCellType() == HSSFCell.CELL_TYPE_STRING ) {
								existingParent= existingParentCell.getStringCellValue();
							}
							System.out.println("srt existingParent is " + existingParent );
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				
				if (parentChildColumn > -1) {
					try {
						HSSFCell parentChildCell = row.getCell(parentChildColumn);
						if (parentChildCell != null) {
							if (parentChildCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
								parentChild = (int) parentChildCell.getNumericCellValue();
							}
							if (parentChildCell.getCellType() == HSSFCell.CELL_TYPE_STRING ) {
								parentChild= Integer.parseInt(parentChildCell.getStringCellValue());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				
				// requirement traceTo
				if (traceToColumn != -1) {
					try {
						HSSFCell requirementTraceToCell = row.getCell(traceToColumn);
						requirementTraceTo = requirementTraceToCell.getStringCellValue();
					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						requirementTraceTo = "";
					}
				}
				// requirement traceFrom
				if (traceFromColumn != -1) {
					try {
						HSSFCell requirementTraceFromCell = row.getCell(traceFromColumn);
						requirementTraceFrom = requirementTraceFromCell.getStringCellValue();
					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						requirementTraceFrom = "";
					}
				}


				// requirement testingStatus
				if (testingStatusColumn != -1) {
					try {
						HSSFCell requirementTestingStatusCell = row.getCell(testingStatusColumn);
						
						requirementTestingStatus = requirementTestingStatusCell.getStringCellValue();
						if ((requirementTestingStatus.trim().toUpperCase().equals("PENDING"))) {
							requirementTestingStatus = "Pending";
						}
						if ((requirementTestingStatus.trim().toUpperCase().equals("PASS"))) {
							requirementTestingStatus = "Pass";
						}

						if ((requirementTestingStatus.trim().toUpperCase().equals("FAIL"))) {
							requirementTestingStatus = "Fail";
						}

					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						requirementTestingStatus = "Pending";
					}
				}

				// since the minimum fields are available. so lets create the
				// requirement.

				// lets set the parentFullTag based on input in the file
				String parentFullTag = "";
				if (parentChild > 0) {
					if (parentMap.get(new Integer(parentChild)) != null){
						parentFullTag = (String) parentMap.get(new Integer(parentChild));
					}
				}
		
				
				// lets see if we were sent in a parent full tag. if found, then we will make this the parent full tag. 
				if ((existingParent != null ) && !(existingParent.equals(""))){
					// lets validate that the existing parent full tag , actually exists in this project.
					Requirement existingParentObject = new Requirement(existingParent.trim(), projectId, databaseType);
					parentFullTag = existingParentObject.getRequirementFullTag();
					System.out.println("srt found existing parent full tag "+ parentFullTag );
				}
		
				String requirementLockedBy = "";
				Requirement requirement = new Requirement(parentFullTag,requirementTypeId,
						rowFolderId, projectId, requirementName,
						requirementDescription, requirementPriority,
						requirementOwner, requirementLockedBy, requirementPctComplete,
						requirementExternalURL, user.getEmailId(),databaseType);

					
					
					
					
				// lets make the entry in the parentMap hasmap. will be used
				// by the parent child
				parentMap.put(new Integer(rowNum), requirement.getRequirementFullTag());
				// lets set the testing status.
				// if testingstatus has not been set, lets default it to Pending.
				if ((requirementTestingStatus == null) || (requirementTestingStatus.equals(""))){
					requirementTestingStatus = "Pending";
				}
				requirement.setTestingStatus(requirementTestingStatus, user.getEmailId(),databaseType);
				// Now that the requirement is created, lets create the custom
				// attributes.

				boolean customError = false;
				boolean customAttributesUpdated = false;
				for (int j = 0; j < attributeIds.length; j++) {
					int attributeId = Integer.parseInt(attributeIds[j]);
					RTAttribute rTAttribute = new RTAttribute(attributeId);
					String attributeValue = "";
					int attributeIdColumn = 0;
					try {

						String attributeIdColumnString = request.getParameter(Integer.toString(attributeId));
						attributeIdColumn = Integer.parseInt(attributeIdColumnString);

						// get the attribute value from the excelfile
						// this could be a numeric cell or a string cell.
						HSSFCell attributeCell = row.getCell(attributeIdColumn);
						
						if (rTAttribute.getAttributeType().equals("Date")){
							try {
								// if this is  a date type attribute, lets get the value
								if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
									DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
									 attributeValue = formatter.format(attributeCell.getDateCellValue());
								}
								else{
									// there are situations where the user sent in data in mm/dd/yyyy format
									// but it was sent as a text string.
									attributeValue = attributeCell.getStringCellValue().trim();
									// lets try to convert this to a date just make sure its valid
									// we convert this to a date and back to string
									DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
									attributeValue = formatter.format(formatter.parse(attributeValue));
									
								}
							} catch (Exception e) {
								attributeValue = "";
							}
						}
						
						
						// if this is not a date type attrib lets process it now.
						if (!(rTAttribute.getAttributeType().equals("Date"))){
							try {
								if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
									Double attributeValueDouble = new Double(attributeCell.getNumericCellValue());
									attributeValue = Integer.toString(attributeValueDouble.intValue());
								}
								else {
									attributeValue = attributeCell.getStringCellValue().trim();
								}
							} catch (Exception e) {
								// if we run into any exception, we don't need to
								// worry.
								attributeValue = "";
							}
						}
						
					} catch (Exception e) {
						// if we run into any exception, we don't need to worry.
						attributeValue = "";
					}
					if (!attributeValue.equals("")) {
						// if we are here, then it means that this is a valid
						// value for this attribute
						// the validate routine ensures it.
						RequirementUtil.updateRequirementAttribute(requirement.getRequirementId(), attributeId,
							attributeValue, user.getEmailId(),databaseType);
						customAttributesUpdated = true;
					}
				}

				// Now that we have updated a bunch of user defined attributes,
				// we need to
				// update the UDA for this requirement.
				if (customAttributesUpdated) {
					RequirementUtil.setUserDefinedAttributes(requirement.getRequirementId(), user.getEmailId(),databaseType);
				}

				// lets updates the newly created Reqs tracebility . These
				// values may also
				// have been sent as part of the imported excel file.
				if ((requirementTraceTo != null)
						|| (requirementTraceFrom != null)) {
					
					String status = RequirementUtil.createTraces(project, requirement
							.getRequirementId(), requirementTraceTo,
							requirementTraceFrom, project.getProjectId(), securityProfile,  databaseType);
					if (!(status.equals(""))) {
						// the create traces method returned a non empty string.
						// means that there were some circular traces that were
						// not created.
						String inputString = rowNum + ":##:" + "Name : "
								+ requirementName + ";  " + "Priority : "
								+ requirementPriority + ";  " + "Owner : "
								+ requirementOwner + ";  " + "Pct Complete : "
								+ requirementPctComplete + ";  "
								+ "External URL : " + requirementExternalURL
								+ ";  " + "Description : "
								+ requirementDescription + "; :##:" + status;
						alertRows.add(inputString);
					}
				}

				// Since the requirement has undergone changes (i.e custom
				// attribs are created
				// lets refresh.

				requirement = new Requirement(requirement.getRequirementId(),databaseType);
				createdRequirements.add(requirement);
			}

			// close the input stream.
			myxls.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Now we need to add the createdRequirements and errorRows to the
		// request.
		request.setAttribute("createdRequirements", createdRequirements);
		request.setAttribute("alertRows", alertRows);

		// Clean up. i.e drop files, folders etc...
		// we need to delete the file and the folder it contains. 
		// Note : we keep only 1 file in each folder. so this is safe.
		File file = new File(excelFilePath);
		if (file != null){
			File dir = file.getParentFile();
			// lets drop the file.
			file.delete();
			
			if (dir != null) {
				dir.delete();
			}
		}
	}

	private boolean validateUpdateExistingRequirementsExcelFile(
			HttpServletRequest request, HttpSession session, User user,
			Project project, SecurityProfile securityProfile, String databaseType) {

		// if the user got till here, it means that he has Create privs on these
		// requirements.
		// to ensure that they don't update requiremetns without update privs
		// and to close a
		// security hole i.e user has update privs on folderA, brings in an
		// excel file with
		// rows in another folderB (on which he deoes not have security privs)
		// and tries
		// to update, we are locking down security at an update row level.

		boolean validFile = true;
		ArrayList errorRows = new ArrayList();

		String excelFilePath = (String) session.getAttribute("excelFilePath");

		try {

			int projectId = project.getProjectId();

			// lets get the column number where we can find the requirement
			// attributes.
			int idColumn = Integer.parseInt(request.getParameter("idColumn"));

			// a value of -1 indicates that these columnsnumbers are not
			// provided.
			int nameColumn = -1;
			int descriptionColumn = -1;
			int priorityColumn = -1;
			int ownerColumn = -1;
			int pctCompleteColumn = -1;
			int externalURLColumn = -1;
			int folderPathColumn = -1;
			int processSelectedColumn = -1;
			int commentByColumn = -1;
			
			
			// not a big chunk of these attributes can be null. i.e. users
			// decided not to
			// update these values.
			if (request.getParameter("nameColumn") != null) {
				nameColumn = Integer.parseInt(request.getParameter("nameColumn"));
			}
			if (request.getParameter("descriptionColumn") != null) {
				descriptionColumn = Integer.parseInt(request.getParameter("descriptionColumn"));
			}
			if (request.getParameter("priorityColumn") != null) {
				priorityColumn = Integer.parseInt(request.getParameter("priorityColumn"));
			}
			if (request.getParameter("ownerColumn") != null) {
				ownerColumn = Integer.parseInt(request.getParameter("ownerColumn"));
			}
			if (request.getParameter("pctCompleteColumn") != null) {
				pctCompleteColumn = Integer.parseInt(request.getParameter("pctCompleteColumn"));
			}
			if (request.getParameter("externalURLColumn") != null) {
				externalURLColumn = Integer.parseInt(request.getParameter("externalURLColumn"));
			}
			if (request.getParameter("folderPathColumn") != null) {
				folderPathColumn = Integer.parseInt(request.getParameter("folderPathColumn"));
			}
			if (request.getParameter("processSelectedColumn") != null) {
				processSelectedColumn = Integer.parseInt(request.getParameter("processSelectedColumn"));
			}
			if (request.getParameter("commentByColumn") != null) {
				commentByColumn = Integer.parseInt(request.getParameter("commentByColumn"));
			}

			
			
			
			int traceToColumn = -1;
			if (request.getParameter("traceToColumn") != null) {
				traceToColumn = Integer.parseInt(request.getParameter("traceToColumn"));
			}
			int traceFromColumn = -1;
			if (request.getParameter("traceFromColumn") != null) {
				traceFromColumn = Integer.parseInt(request.getParameter("traceFromColumn"));
			}
			
			int testingStatusColumn = -1;
			if (request.getParameter("testingStatusColumn") != null) {
				testingStatusColumn = Integer.parseInt(request.getParameter("testingStatusColumn"));
			}
			// lets get the custom attribute Ids.
			String[] attributeIds = {};
			String attributeIdString = request
					.getParameter("attributeIdString");
			try {
				// remove the last # from attributeIdString.
				if (attributeIdString.contains("::")) {
					attributeIdString = (String) attributeIdString.subSequence(
							0, attributeIdString.lastIndexOf("::"));

					attributeIds = attributeIdString.split("::");
				}
			} catch (Exception e) {
				// do nothing.
			}

			// lets open the Excel File and start reading values...
			InputStream myxls = new FileInputStream(excelFilePath);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0); // first sheet

			// We will look for up to 20000 rows per excel page.
			// Also since row 1 (that is i==0) will be the header row, we will
			// start from i =1 .
			for (int i = 1; i < 20000; i++) {

				Requirement requirement = null;
				int rowNum = i + 1;
				boolean validRow = true;
				String inputData = "";
				String errorMessage = "";
				String requirementTag = "";

				String newRequirementName = "";
				String newRequirementDescription = "";
				String newRequirementPriority = "";
				String newRequirementOwner = "";
				int newRequirementPctComplete = -1;
				String newRequirementExternalURL = "";
				String newRequirementFolderPath = "";
				String processSelected = "";
				String newRequirementCommentBy = "";
				
				

				String newRequirementTraceTo = "";
				String newRequirementTraceFrom = "";

				String newRequirementTestingStatus = "";

				// lets get the row.
				HSSFRow row = sheet.getRow(i);

				if (row == null) {
					continue;
				}
				if (processSelectedColumn > -1) {
					// Since the user chose this option, we process only those rows 
					// with a 'yes' value in this column.
					try {
						HSSFCell processSelectedCell = row.getCell(processSelectedColumn);
						processSelected = processSelectedCell.getStringCellValue();
					} catch (Exception e) {
						// do nothing
					}
					if (!(processSelected.trim().toLowerCase().equals("yes"))){
						// we don't need to process this row.
						continue;
					}
				}
				// our first action is to get the requirementID, and create a
				// requirement object.
				// requirement id
				try {
					HSSFCell requirementTagCell = row.getCell(idColumn);
					requirementTag = requirementTagCell.getStringCellValue();
				} catch (Exception e) {
					// if we run into any exception, set error message.
				}

				inputData += requirementTag;
				if (requirementTag.equals("")) {
					validRow = false;
					errorMessage += " 		Requirement Tag can not be empty.<br>";
				} else {
					// lets get the requirementId.
					int requirementId = RequirementUtil.getRequirementId(projectId, requirementTag);
					requirement = new Requirement(requirementId, databaseType);
					
					if (requirementId == 0) {
						// some junk was sent it ,and we couldn't create a req object with this value.
						validRow = false;
						errorMessage += " 		Could not locate this requirement. Please change the Requirement Tag.<br>";
					}
					else {
						// lets see if this user is permitted to update this
						// requirement.
						if (!(securityProfile.getPrivileges().contains("updateRequirementsInFolder"
							+ requirement.getFolderId()))) {
							// User is does not have update privs on this row. So
							// lets
							// skip this row.
							validRow = false;
							errorMessage += " 		You do not have permissions to update this requirement.<br>";
						}
					}
				}

				if (nameColumn != -1) {
					// i.e nameColumn has been selected to be updated.
					// requirement Name
					try {

						HSSFCell requirementNameCell = row.getCell(nameColumn);
						newRequirementName = requirementNameCell.getStringCellValue();
					} catch (Exception e) {
					}

					inputData += newRequirementName;
					if (newRequirementName.equals("")) {
						validRow = false;
						errorMessage += " 		Requirement Name can not be empty.<br>";
					}
				}

				if (descriptionColumn != -1) {
					// i.e nameColumn has been selected to be updated.
					// requirement Description
					try {
						HSSFCell requirementDescriptionCell = row.getCell(descriptionColumn);
						newRequirementDescription = requirementDescriptionCell.getStringCellValue();

					} catch (Exception e) {
					}

					inputData += newRequirementDescription;
					if (newRequirementDescription.equals("")) {
						validRow = false;
						errorMessage += " 		Requirement Description can not be empty.<br>";
					}
				}

				// do the validation only if the user chose to provide this
				// column of data
				if (priorityColumn != -1) {
					// i.e priorityColumn has been selected to be updated.
					// requirement Priority
					try {
						HSSFCell requirementPriorityCell = row.getCell(priorityColumn);
						newRequirementPriority = requirementPriorityCell.getStringCellValue();
						// lets ensure that priority is in valid list of values.
						if ((newRequirementPriority == null) || (newRequirementPriority.equals(""))) {
							validRow = false;
							errorMessage += " 		Requirement Priority is a required field.<br>";
							
						}						
						else if ((newRequirementPriority != null)
								&& (!((newRequirementPriority.trim().toUpperCase().contains("HIGH"))
										|| (newRequirementPriority.trim().toUpperCase().contains("MEDIUM")) 
										|| (newRequirementPriority.trim().toUpperCase().contains("LOW"))))) {
							validRow = false;
							errorMessage += " 		Requirement Priority is not in High or Medium or Low.<br>";
						}
					} catch (Exception e) {
						validRow = false;
						errorMessage += " 		Requirement Priority is a required field.<br>";
					}
				}

				if (ownerColumn != -1) {
					// i.e ownerColumn has been selected to be updated.
					// requirement Owner
					try {
						HSSFCell requirementOwnerCell = row.getCell(ownerColumn);
						newRequirementOwner = requirementOwnerCell.getStringCellValue();

						if ((newRequirementOwner == null) || (newRequirementOwner.equals(""))) {
							validRow = false;
							errorMessage += " 		Requirement Owner is a required field.<br>";							
						}
						else if (!(ProjectUtil.isValidUserInProject(newRequirementOwner, project))) {
							// if the newRequirementOwner is not a member of this
							// project,
							// default the owner field to the person importing the
							// requirement.
							
							validRow = false;
							errorMessage += " 		Requirement Owner is not a member of this project.<br>";
						}
					} catch (Exception e) {
						validRow = false;
						errorMessage += " 		Requirement Owner is a required field.<br>";
					}
					inputData += newRequirementOwner;
				}
				
				if (commentByColumn != -1) {
					// i.e commentByColumn has been selected to be updated.
					// requirement commentBy
					try {
						HSSFCell requirementcommentByCell = row.getCell(commentByColumn);
						newRequirementCommentBy = requirementcommentByCell.getStringCellValue();

						if ((newRequirementCommentBy == null) || (newRequirementCommentBy.equals(""))) {
							validRow = false;
							errorMessage += " 		Requirement commentBy is a required field.<br>";							
						}
						else if (!(ProjectUtil.isValidUserInProject(newRequirementCommentBy, project))) {
							// if the newRequirementcommentBy is not a member of this
							// project,
							// default the commentBy field to the person importing the
							// requirement.
							
							validRow = false;
							errorMessage += " 		Requirement commentBy is not a member of this project.<br>";
						}
					} catch (Exception e) {
						validRow = false;
						errorMessage += " 		Requirement commentBy is a required field.<br>";
					}
					inputData += newRequirementCommentBy;
				}

				// requirement PctComplete
				if (pctCompleteColumn != -1) {
					// i.e pctCompleteColumn has been selected to be updated.
					try {
						HSSFCell requirementPctCompleteCell = row.getCell(pctCompleteColumn);
						if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
							newRequirementPctComplete = (int) requirementPctCompleteCell.getNumericCellValue();
						}
						if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_STRING ) {
							newRequirementPctComplete = Integer.parseInt(requirementPctCompleteCell.getStringCellValue());
						
						}
						
						
						if (newRequirementPctComplete > 100) {
							validRow = false;
							errorMessage += " 		Percent Complete > 100 .<br>";
						}
						if (newRequirementPctComplete < 0) {
							validRow = false;
							errorMessage += " 		Percent Complete < 0  .<br>";
						}
					} catch (Exception e) {
						validRow = false;
						errorMessage += " 		Percent Complete is not a number.<br>";
					}

					if (newRequirementPctComplete != -1) {
						inputData += newRequirementPctComplete;
					}

				}

				// requirement Folder Path
				// do the validation only if the user chose to provide this
				// column of data
				if (folderPathColumn > -1) {
					try {
						HSSFCell folderPathCell = row.getCell(folderPathColumn);
						newRequirementFolderPath = folderPathCell.getStringCellValue().trim();
						if ((folderPathCell == null) || (folderPathCell.equals(""))) {
							validRow = false;
							errorMessage += " 		Folder Path is required<br>";
							
						}
						else {
							Folder rowFolder = new Folder(newRequirementFolderPath, project.getProjectId());
							if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
									+ rowFolder.getFolderId()))){
								validRow = false;
								errorMessage += " 		You do not have Create Requirements permissions on this folder.<br>";
							}
						}
						
						if (requirement != null){
							if (!(ProjectUtil.isValidFolderPathForRequirementType(newRequirementFolderPath, requirement.getRequirementTypeId()))) {
						
							// if the requirementOwner is not a member of this
							// project,
							// default the owner field to the person importing the
							// requirement.

							validRow = false;
							errorMessage += " 		Not a valid Folder Path for this Requirement Type.<br>";
							}
						}
					
						
						inputData += newRequirementFolderPath;

					} catch (Exception e) {
						validRow = false;
						errorMessage += " 		Not a valid folder path.<br>";

					}
				}
				
				// lets validate the traceto and trace from values.
				// requirement traceTo
				if (traceToColumn != -1) {
					try {
						HSSFCell requirementTraceToCell = row
								.getCell(traceToColumn);
						newRequirementTraceTo = requirementTraceToCell
								.getStringCellValue();
					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						newRequirementTraceTo = "";
					}

					inputData += newRequirementTraceTo;

					// if a non empty field is given, lets make sure that req
					// ids exist.
					if (!(newRequirementTraceTo.equals(""))) {
						// if all req tags are valid , you get back an empty
						// string. else
						// you get back a list of tags that don't exist in the
						// system.
						String status = RequirementUtil
								.validateRequirementTags(newRequirementTraceTo,
										project.getProjectId());
						if (!(status.equals(""))) {
							validRow = false;
							errorMessage += " 		Following Requirement Tags do not exist in the system (Either deleted or purged)."
									+ " Trace To Column." + status + "<br>";
						}
					}
				}

				// requirement traceFrom
				if (traceFromColumn != -1) {
					try {
						HSSFCell requirementTraceFromCell = row
								.getCell(traceFromColumn);
						newRequirementTraceFrom = requirementTraceFromCell
								.getStringCellValue();
					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						newRequirementTraceFrom = "";
					}

					inputData += newRequirementTraceTo;
					// if a non empty field is given, lets make sure that req
					// ids exist.
					if (!(newRequirementTraceFrom.equals(""))) {
						// if all req tags are valid , you get back an empty
						// string. else
						// you get back a list of tags that don't exist in the
						// system.
						String status = RequirementUtil
								.validateRequirementTags(
										newRequirementTraceFrom, project
												.getProjectId());
						if (!(status.equals(""))) {
							validRow = false;
							errorMessage += " 		Following Requirement Tags do not exist in the system (Either deleted or purged)."
									+ " Trace From Column." + status + "<br>";
						}
					}
				}

				// do the validation only if the user chose to provide this
				// column of data
				if (testingStatusColumn != -1) {
					// i.e testingStatusColumn has been selected to be updated.
					try {
						HSSFCell requirementTestingStatusCell = row.getCell(testingStatusColumn);
						newRequirementTestingStatus = requirementTestingStatusCell.getStringCellValue();
						// lets ensure that testing status is in valid list of values.
						if (
								(
										(newRequirementTestingStatus != null) 
										&& 
										(!newRequirementTestingStatus.equals(""))
								)
								&& 
								(!(
										(newRequirementTestingStatus.toUpperCase().trim().contains("PENDING"))
										|| 
										(newRequirementTestingStatus.toUpperCase().trim().contains("PASS")) 
										|| 
										(newRequirementTestingStatus.toUpperCase().trim().contains("FAIL"))
								))
							)
						{
							// if testingstatus is non empty, it must be one of the three values.
							validRow = false;
							errorMessage += " 		Requirement Testing Status is not in Pending or Pass or Fail.<br>";
						}
					} catch (Exception e) {
						newRequirementTestingStatus = "";
					}
				}

				// lets validate the custom attribute values.

				for (int j = 0; j < attributeIds.length; j++) {
					int attributeId = Integer.parseInt(attributeIds[j]);
					RTAttribute rTAttribute = new RTAttribute(attributeId);
					String attributeValue = "";
					int attributeIdColumn = -1;

					try {

						String attributeIdColumnString = request.getParameter(Integer.toString(attributeId));
						attributeIdColumn = Integer.parseInt(attributeIdColumnString);
					} catch (Exception e) {
						// if we run into any exception, we don't need to worry.
						attributeValue = "";
					}

					// do the validation only if the user chose to provide this
					// column of data
					if (attributeIdColumn > -1) {
						HSSFCell attributeCell = row.getCell(attributeIdColumn);
						
						if (rTAttribute.getAttributeType().equals("Date")){
							try {
								// if this is  a date type attribute, lets get the value
								if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
									 attributeValue = attributeCell.getDateCellValue().toString();
								}
								else{
									// there are situations where the user sent in data in mm/dd/yyyy format
									// but it was sent as a text string.
									attributeValue = attributeCell.getStringCellValue().trim();
									// lets try to convert this to a date just make sure its valid
									// we convert this to a date and back to string
									DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
									attributeValue = formatter.format(formatter.parse(attributeValue));
									
								}
							} catch (Exception e) {
								e.printStackTrace();
								attributeValue = "";
								
							}
							// if this is a required field and we didn't get a valid date, then we error out.
							if ((rTAttribute.getAttributeRequired() == 1) && (attributeValue.equals("")) ) {
								validRow = false;
								int errorColumn = attributeIdColumn + 1;
								errorMessage += "Custom attribute "+ rTAttribute.getAttributeName() +" should be a valid Excel date. See Column #  "
										+ errorColumn + "<br>";
								continue;
							}
						}
						
						
						// if this is not a date type attrib lets process it now.
						if (!(rTAttribute.getAttributeType().equals("Date"))){
							try {
								if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
									Double attributeValueDouble = new Double(attributeCell.getNumericCellValue());
									attributeValue = Integer.toString(attributeValueDouble.intValue());
								}
								else {
									attributeValue = attributeCell.getStringCellValue().trim();
								}
							} catch (Exception e) {
								// if we run into any exception, we don't need to
								// worry.
								attributeValue = "";
							}
							// if this attribute is mandatory lets make sure the input value is not empty 
							if ((rTAttribute.getAttributeRequired() == 1) && (attributeValue.equals(""))){
								validRow = false;
								int errorColumn = attributeIdColumn + 1;
								errorMessage += "Empty value is not permitted in mandatory custom attribute "+ rTAttribute.getAttributeName() +". See Column #  "
										+ errorColumn + "<br>";
								continue;
							}
							
						}
						
						
						inputData += attributeValue;

						if (!attributeValue.equals("")) {
							// lets see if this attribute value is permitted for
							// this attribute.
							// lets see if this attribute value is permitted for
							// this attribute.
							boolean permitted = ProjectUtil.isPermittedValueInAttribute(attributeId, attributeValue);

							// if permitted , lets update the req attribute to
							// this
							// value.
							// else log it.
							if (!(permitted)) {
								validRow = false;
								int errorColumn = attributeIdColumn + 1;
								errorMessage += attributeValue
										+ " is not a permitted value for " + rTAttribute.getAttributeName() 
										+ " See Column #  "
										+ errorColumn + "<br>";
								continue;
							}
						}
						
					}
				}

				if ((!(errorMessage.equals(""))) && (!(inputData.equals("")))) {

					// this means we have an error row and this input row from
					// excel
					// was not a blank row. we need to do this because
					// we want process up to 20000 rows.
					// NOTE : since we want to display the excel row num in the
					// results page,
					// we add that to the error message.
					// we have to be careful to differentiate entire empty rows
					// from
					// rows with missing values. imagine an excel file with 10
					// rows of data
					// and 20000 empty rows. we don't want to display the empty
					// rows as
					// error data.
					String inputString = rowNum + ":##:" + "Name : "
							+ newRequirementName + ";  " + "Priority : "
							+ newRequirementPriority + ";  " + "Owner : "
							+ newRequirementOwner + ";  " + "Pct Complete : "
							+ newRequirementPctComplete + ";  "
							+ "External URL : " + newRequirementExternalURL
							+ ";  " + "Description : "
							+ newRequirementDescription + "; :##:"
							+ errorMessage;
					errorRows.add(inputString);
				}
				// for data rows, if there is something wrong with it
				// we set the file to invalid.
				if ((!(inputData.equals(""))) && (!validRow)) {
					// if it's a non empty row and the data is invalid, then we set the
					// file to false.
					validFile = false;
					
				}
			}
			// close the input stream.
			myxls.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Now we need to add the createdRequirements and errorRows to the
		// request.
		request.setAttribute("errorRows", errorRows);

		// if invalid file, then we need to drop the uploaded file, so that the
		// user can
		// make changes and re upload.
		if (!(validFile)) {
			// Clean up. i.e drop files, folders etc...
			// we need to delete the file and the folder it contains. 
			// Note : we keep only 1 file in each folder. so this is safe.
			File file = new File(excelFilePath);
			if (file != null){
				File dir = file.getParentFile();
				// lets drop the file.
				file.delete();
				
				if (dir != null) {
					dir.delete();
				}
			}
		}
		return validFile;
	}

	// this method reads the excel file, parses it, updates requirements when
	// data row is valid
	// and when data row is not correct, creates an error log.
	// it then removes the session variables & file from disk.
	// it also stores the response on the request variables, so that forwarded
	// JSP can display it.
	private void updateExistingRequirementsFromExcel(
			HttpServletRequest request, HttpSession session, User user,
			Project project, SecurityProfile securityProfile, String databaseType) {

		// if the user got till here, it means that he has Create privs on these
		// requirements.
		// the validate routine took care of that.
		// to ensure that they don't update requiremetns without update privs
		// and to close a
		// security hole i.e user has update privs on folderA, brings in an
		// excel file with
		// rows in another folderB (on which he deoes not have security privs)
		// and tries
		// to update, we are locking down security at an update row level.

		ArrayList updatedRequirements = new ArrayList();
		ArrayList errorRows = new ArrayList();

		String excelFilePath = (String) session.getAttribute("excelFilePath");

		try {
			int projectId = project.getProjectId();

			// lets get the column number where we can find the requirement
			// attributes.
			int idColumn = Integer.parseInt(request.getParameter("idColumn"));

			// a value of -1 indicates that these columnsnumbers are not
			// provided.
			int nameColumn = -1;
			int descriptionColumn = -1;
			int priorityColumn = -1;
			int ownerColumn = -1;
			int pctCompleteColumn = -1;
			int externalURLColumn = -1;
			int folderPathColumn = -1;
			int existingParentColumn = -1;
			int processSelectedColumn = -1;

			// not a big chunk of these attributes can be null. i.e. users
			// decided not to
			// update these values.
			if (request.getParameter("nameColumn") != null) {
				nameColumn = Integer.parseInt(request
						.getParameter("nameColumn"));
			}
			if (request.getParameter("descriptionColumn") != null) {
				descriptionColumn = Integer.parseInt(request
						.getParameter("descriptionColumn"));
			}
			if (request.getParameter("priorityColumn") != null) {
				priorityColumn = Integer.parseInt(request
						.getParameter("priorityColumn"));
			}
			if (request.getParameter("ownerColumn") != null) {
				ownerColumn = Integer.parseInt(request
						.getParameter("ownerColumn"));
			}
			if (request.getParameter("pctCompleteColumn") != null) {
				pctCompleteColumn = Integer.parseInt(request
						.getParameter("pctCompleteColumn"));
			}
			if (request.getParameter("externalURLColumn") != null) {
				externalURLColumn = Integer.parseInt(request
						.getParameter("externalURLColumn"));
			}
			if (request.getParameter("folderPathColumn") != null) {
				folderPathColumn = Integer.parseInt(request.getParameter("folderPathColumn"));
			}
			
			if (request.getParameter("existingParentColumn") != null) {
				existingParentColumn = Integer.parseInt(request.getParameter("existingParentColumn"));
			}
			
			if (request.getParameter("processSelectedColumn") != null) {
				processSelectedColumn = Integer.parseInt(request.getParameter("processSelectedColumn"));
			}

			
			int traceToColumn = -1;
			if (request.getParameter("traceToColumn") != null) {
				traceToColumn = Integer.parseInt(request
						.getParameter("traceToColumn"));
			}
			int traceFromColumn = -1;
			if (request.getParameter("traceFromColumn") != null) {
				traceFromColumn = Integer.parseInt(request
						.getParameter("traceFromColumn"));
			}

			int testingStatusColumn = -1;
			if (request.getParameter("testingStatusColumn") != null) {
				testingStatusColumn = Integer.parseInt(request.getParameter("testingStatusColumn"));
			}
			
			int commentColumn = -1;
			if (request.getParameter("commentColumn") != null) {
				commentColumn = Integer.parseInt(request.getParameter("commentColumn"));
			}
			
			int commentByColumn = -1;
			if (request.getParameter("commentByColumn") != null) {
				commentByColumn = Integer.parseInt(request.getParameter("commentByColumn"));
			}

			// lets open the Excel File and start reading values...
			InputStream myxls = new FileInputStream(excelFilePath);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0); // first sheet

			// We will look for up to 20000 rows per excel page.
			// Also since row 1 (that is i==0) will be the header row, we will
			// start from i =1 .
			for (int i = 1; i < 20000; i++) {

				int rowNum = i + 1;
				String errorMessage = "";
				String inputData = "";
					
				String requirementTag = "";

				String newRequirementName = "";
				String newRequirementDescription = "";
				String newRequirementPriority = "";
				String newRequirementOwner = "";
				int newRequirementPctComplete = -1;
				String newRequirementExternalURL = "";
				String newRequirementFolderPath = "";
				
				String existingParent = "";
				
				String processSelected = "";
				
				String requirementTraceTo = "";
				String requirementTraceFrom = "";

				String requirementTestingStatus = "";

				// lets get the row.
				HSSFRow row = sheet.getRow(i);

				if (row == null) {
					continue;
				}
				if (processSelectedColumn > -1) {
					// Since the user chose this option, we process only those rows 
					// with a 'yes' value in this column.
	
					// process selected row
					try {
						HSSFCell processSelectedCell = row.getCell(processSelectedColumn);
						processSelected = processSelectedCell.getStringCellValue();
					} catch (Exception e) {
						// do nothing
					}
					if (!(processSelected.trim().toLowerCase().equals("yes"))){
						// we don't need to process this row.
						continue;
					}
				}
				// our first action is to get the requirementID, and create a
				// requirement object.
				// requirement id
				HSSFCell requirementTagCell = row.getCell(idColumn);
				if (requirementTagCell != null) {
					requirementTag = requirementTagCell.getStringCellValue();
				}
				if ((requirementTag == null) || (requirementTag.equals(""))) {
					continue;
				}
				
				// lets get the requirementId. we will use this through out.
				int requirementId = RequirementUtil.getRequirementId(projectId,requirementTag);

			

				Requirement requirement = new Requirement(requirementId, databaseType);
				
				// lets make sure that this row is not locked. If it is locked by some one other than this current
				// user, then we will need to skip it.
				if (
					(!(requirement.getRequirementLockedBy().equals("")))
						&&
					(!(requirement.getRequirementLockedBy().equals(user.getEmailId())))
				){
						String inputString = rowNum + ":##:" + "Name : "
								+ newRequirementName + ";  " + "Priority : "
								+ newRequirementPriority + ";  " + "Owner : "
								+ newRequirementOwner + ";  " + "Pct Complete : "
								+ newRequirementPctComplete + ";  "
								+ "External URL : " + newRequirementExternalURL
								+ ";  " + "Description : "
								+ newRequirementDescription + "; :##:" + " The Requirement "+ requirement.getRequirementFullTag() 
								+" is locked by " + requirement.getRequirementLockedBy() + " and can not be modified by you";
						errorRows.add(inputString);
						continue;
				}
				
				
				
				
				
				

				// now lets go through all the cells and see if we need to
				// update the
				// requirement or not.
				boolean updateRequirement = false;

				if (nameColumn != -1) {
					// i.e nameColumn has been selected to be updated.
					updateRequirement = true;
					// requirement Name
					HSSFCell requirementNameCell = row.getCell(nameColumn);
					newRequirementName = requirementNameCell.getStringCellValue();
					// What are we? Fucking Stupid??? truncating the name to 98 chars. Hah!!! 
					// we have removed the restriction that the name can only be 99 chars
					//if (newRequirementName.length() > 99) {
					//	newRequirementName = newRequirementName.substring(0, 98);
					//	errorMessage += "Truncating the Requirment Name to 100 characters.<br>";
					//}
				}
				else {
					newRequirementName = requirement.getRequirementName();
				}

				if (descriptionColumn != -1) {
					// i.e nameColumn has been selected to be updated.
					updateRequirement = true;
					// requirement Description
					HSSFCell requirementDescriptionCell = row.getCell(descriptionColumn);
					newRequirementDescription = requirementDescriptionCell.getStringCellValue();
					if ((newRequirementDescription != null) && (newRequirementDescription.contains("\n"))){
						newRequirementDescription = newRequirementDescription.replace("\n", "<br>");
					}

				}
				else {
					newRequirementDescription = requirement.getRequirementDescription();
				}


				if (priorityColumn != -1) {
					// i.e nameColumn has been selected to be updated.
					updateRequirement = true;
					// requirement Priority
					HSSFCell requirementPriorityCell = row.getCell(priorityColumn);
					newRequirementPriority = requirementPriorityCell.getStringCellValue();
					// lets ensure that priority is in valid list of values.

					// catching the scenario where there are extra white
					// spaces after high, medium, low
					if ((newRequirementPriority.trim().toUpperCase().equals("HIGH"))) {
						newRequirementPriority = "High";
					}
					if ((newRequirementPriority.trim().toUpperCase().equals("MEDIUM"))) {
						newRequirementPriority = "Medium";
					}

					if ((newRequirementPriority.trim().toUpperCase().equals("LOW"))) {
						newRequirementPriority = "Low";
					}
				}
				else {
					newRequirementPriority = requirement.getRequirementPriority();
				}

				if (ownerColumn != -1) {
					// i.e nameColumn has been selected to be updated.
					updateRequirement = true;

					// requirement Owner
					HSSFCell requirementOwnerCell = row.getCell(ownerColumn);
					newRequirementOwner = requirementOwnerCell.getStringCellValue();
				}
				else {
					newRequirementOwner = requirement.getRequirementOwner();
				}

				// requirement PctComplete
				// default this to 0, and try to compute the value.
				if (pctCompleteColumn != -1) {				
					try {
						// i.e nameColumn has been selected to be updated.
						updateRequirement = true;
						// requirement PctComplete
						// default this to 0, and try to compute the value.

						HSSFCell requirementPctCompleteCell = row.getCell(pctCompleteColumn);
						if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
							newRequirementPctComplete = (int) requirementPctCompleteCell.getNumericCellValue();
						}
						if (requirementPctCompleteCell.getCellType() == HSSFCell.CELL_TYPE_STRING ) {
							newRequirementPctComplete = Integer.parseInt(requirementPctCompleteCell.getStringCellValue());
						
						}
					} catch (Exception e) {
						e.printStackTrace();
						newRequirementPctComplete = requirement.getRequirementPctComplete();
					}
				}
				else {
					newRequirementPctComplete = requirement.getRequirementPctComplete();
				}

				
				
				// since we are not validating the external URL field in the validate
				// routine, we are doing it here.
				if (externalURLColumn != -1) {
					// i.e nameColumn has been selected to be updated.
					updateRequirement = true;
					// requirement ExternalURL
					try {
						HSSFCell requirementExternalURLCell = row.getCell(externalURLColumn);
						newRequirementExternalURL = requirementExternalURLCell.getStringCellValue();
					} catch (Exception e) {
						// external URL can be empty. So no error condition.
						newRequirementExternalURL = "";
					}
				}
				else {
					newRequirementExternalURL = requirement.getRequirementExternalUrl();
				}
				// requirement Folder Path
				// do the validation only if the user chose to provide this
				// column of data
				int rowFolderId = requirement.getFolderId();
				if (folderPathColumn > -1) {
					// i.e folderPath has been selected to be updated.
					updateRequirement = true;
					try {
						HSSFCell folderPathCell = row.getCell(folderPathColumn);
						String requirementFolderPath = folderPathCell.getStringCellValue().trim();
						Folder rowFolder = new Folder(requirementFolderPath, project.getProjectId());
						rowFolderId = rowFolder.getFolderId();
					} catch (Exception e) {
						e.printStackTrace();
						rowFolderId = requirement.getFolderId();
					}
				}


				// lets see if the user chose to move this under a different parent
				if (existingParentColumn > -1) {
					// i.e folderPath has been selected to be updated.
					updateRequirement = true;
					try {
						HSSFCell existingParentCell = row.getCell(existingParentColumn);
						existingParent = existingParentCell.getStringCellValue().trim();
						// lets move this requirement under a new full tag.
						if ((existingParent != null ) && !(existingParent.equals(""))){
							// lets validate that the existing parent full tag , actually exists in this project.
							Requirement existingParentObject = new Requirement(existingParent.trim(), projectId, databaseType);
							String newParentFullTag = existingParentObject.getRequirementFullTag();
							
							String currentParentFullTag = requirement.getParentFullTag();
							
							// lets move this requirement under the new parent. 
							if (
									(!(newParentFullTag.equals(currentParentFullTag)))
									&&
									(!(newParentFullTag.equals(requirement.getRequirementFullTag())))
								)
								// only if the new parent tag is not same as old one and new parent tag is not the requirement's current tag			
									
							{
								String childrensFuture = "takeChildrenAlong";
								requirement = RequirementUtil.assignToNewParent(requirementId,requirement.getParentFullTag(), newParentFullTag,childrensFuture, user.getEmailId(),  databaseType); 

							}
							
							
						}
						
						
					} catch (Exception e) {
						e.printStackTrace();
						
					}
				}

				
				if (updateRequirement) {
					// This means that at least 1 requirement attribute has
					// changed value.
					// so ,lets update the requirement.

					// Note , to update the requirement, we just need to re
					// create the
					// requirement bean, , but this time giving the requirement
					// id , along
					// with old values for unchanged stuff, and the new value
					// for the stuff
					// you want to change.

					requirement = new Requirement(requirementId,
							newRequirementName, newRequirementDescription,
							newRequirementPriority, newRequirementOwner,
							newRequirementPctComplete,
							newRequirementExternalURL, user.getEmailId(),
							request, databaseType);
					if (rowFolderId != requirement.getFolderId()){
						RequirementUtil.moveRequirementToAnotherFolder(requirement, rowFolderId, user.getEmailId(),  databaseType);
					}
				}

				// Now that the requirement is updated, lets update the custom
				// attributes.

				// lets get the custom attribute Ids.
				String[] attributeIds = {};
				String attributeIdString = request.getParameter("attributeIdString");
				try {
					// remove the last # from attributeIdString.
					if (attributeIdString.contains("::")) {
						attributeIdString = (String) attributeIdString.subSequence(0, attributeIdString.lastIndexOf("::"));
						attributeIds = attributeIdString.split("::");
					}
				} catch (Exception e) {
					e.printStackTrace();
					// do nothing.
				}
				
				
				boolean customAttributesUpdated = false;

				// lets determine which all attributes this user has permission to update , based on his roles
			  	String  updateAttributes = securityProfile.getUpdateAttributesForFolder(requirement.getFolderId());
				
				// lets loop through all the attributes in the req type, and see if a mapped column is given.
				// if it is , then we will process it. otherwise skip it.
				for (int j = 0; j < attributeIds.length; j++) {
					int attributeId = Integer.parseInt(attributeIds[j]);
					RTAttribute rTAttribute = new RTAttribute(attributeId);
					String attributeValue = "";
					int attributeIdColumn = 0;
					try {

						String attributeIdColumnString = request.getParameter(attributeIds[j]);
						if (attributeIdColumnString == null) {
							continue;
						}
						attributeIdColumn = Integer.parseInt(attributeIdColumnString);
						// get the attribute value from the excelfile
						HSSFCell attributeCell = row.getCell(attributeIdColumn);
				
						if ((attributeCell == null) || (attributeCell.equals(""))){
							attributeValue = "";
						}
						else {
							if (rTAttribute.getAttributeType().equals("Date")){
								try {
									// if this is  a date type attribute, lets get the value
									if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
										DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
										 attributeValue = formatter.format(attributeCell.getDateCellValue());
									}
									else{
										// there are situations where the user sent in data in mm/dd/yyyy format
										// but it was sent as a text string.
										attributeValue = attributeCell.getStringCellValue().trim();
										// lets try to convert this to a date just make sure its valid
										// we convert this to a date and back to string
										DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
										attributeValue = formatter.format(formatter.parse(attributeValue));
										
									}
								} catch (Exception e) {
									attributeValue = "";
								}
							}
							
							
							// if this is not a date type attrib lets process it now.
							if (!(rTAttribute.getAttributeType().equals("Date"))){
								try {
									if (attributeCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
										Double attributeValueDouble = new Double(attributeCell.getNumericCellValue());
										attributeValue = Integer.toString(attributeValueDouble.intValue());
									}
									else {
										attributeValue = attributeCell.getStringCellValue().trim();
									}
								} catch (Exception e) {
									// if we run into any exception, we don't need to
									// worry.
									attributeValue = "";
								}
							}
						}
						
						
						if (attributeValue == null) {
							attributeValue = "";
						}
						
						
						
						// lets get the old Attribute Value object, and compare its value with the new value.
						// only if the value has changed, do we need to update the system and trigger
						// version, traceability and workflow.
						RAttributeValue oldAttributeValue = new RAttributeValue(requirementId, attributeId, databaseType);
						if (!(oldAttributeValue.getAttributeEnteredValue().trim().equals(attributeValue.trim()))) {
							
						
							// If the user does not have updateAttributes permission on this attribute in this folder, then we need
							// add an error message and skip updating this attribute.
					    	if 	(!(updateAttributes.contains(":#:"+ rTAttribute.getAttributeName() + ":#:"))){
					    		// SINCE THE USER DOES NOT HAVE  UPDATE PERMISSIONS ON THIS ATTRIBUTE ON THIS REQ type in this folder, 
					    		// lets skip this attribute for updating.
					    		String inputString = rowNum + ":##:" + "Name : "
										+ newRequirementName + ";  " + "Priority : "
										+ newRequirementPriority + ";  " + "Owner : "
										+ newRequirementOwner + ";  " + "Pct Complete : "
										+ newRequirementPctComplete + ";  "
										+ "External URL : " + newRequirementExternalURL
										+ ";  " + "Description : "
										+ newRequirementDescription + "; :##:" + " You do not have permissions to update " 
										+ " attribute '" + rTAttribute.getAttributeName() + "' of this Requirement ";
								errorRows.add(inputString);
								continue;
					    	}
				    		
					    	// see if the appendReplace+attributeId was sent in. If it's append, then call the update function
					    	// If its replace, then call the replace function
					    	String appendReplace = "replace";
					    	try{
					    		appendReplace = request.getParameter("appendReplace" + attributeId );
					    		System.out.println("srt appendReplace for " + attributeId + "attributeId is " + appendReplace);
					    	}
					    	catch (Exception appendReplaceException){
					    		appendReplaceException.printStackTrace();
					    	}
					    	if (appendReplace == null){
					    		appendReplace = "replace";
					    	}
					    	
					    	String newAttributeValue = "";
					    	if (appendReplace.equals("replace")){
					    		System.out.println("srt replace " + attributeId);
					    		newAttributeValue = attributeValue.trim();
					    	}
					    	else {
					    		System.out.println("srt append " + attributeId);
					    		// lets write the append function here
					    		if (rTAttribute.getAttributeType().equals("Drop Down Multiple")){
					    			newAttributeValue = oldAttributeValue.getAttributeEnteredValue().trim() + "," + attributeValue.trim();
					    			
					    			try{
						    			// we could end up with values like ,a,b,c (when existing is null) or with a,b,c,a,b,a if the user tries multiple appends
						    			// Since this is a multiple dropdown it can be messy. So, lets try to clean up
						    			String[] values = newAttributeValue.split(",");
						    			HashMap<String,String> attribHashMap = new HashMap<String,String>();
						    			for (String v: values){
						    				attribHashMap.put(v,v);
						    			}
						    			newAttributeValue = "";
						    			for (String v : attribHashMap.keySet()){
						    				if (!(v.equals(""))){
						    					newAttributeValue = newAttributeValue + v + ",";
						    				}
						    			}
						    			// remove the last, 
						    			if (newAttributeValue.contains(",")) {
						    				newAttributeValue = (String) newAttributeValue.subSequence(0, newAttributeValue.lastIndexOf(","));
										}
					    			}
					    			catch(Exception makeAttribValuesStringUniqueException){
					    				// nothing
					    			}
					    			
					    		}
					    		
					    		if (rTAttribute.getAttributeType().equals("Text Box")){
					    			newAttributeValue = oldAttributeValue.getAttributeEnteredValue().trim() + " " + attributeValue.trim();
					    		}
					    		
					    		if (rTAttribute.getAttributeType().equals("URL")){
					    			newAttributeValue = oldAttributeValue.getAttributeEnteredValue().trim() + "\n" + attributeValue.trim();
					    		}
					    		System.out.println("srt attributeType is "  + rTAttribute.getAttributeType() + " and newAttributeValue is " + newAttributeValue );
					    	}
					    	
					    	RequirementUtil.updateRequirementAttribute(requirementId, attributeId, newAttributeValue,user.getEmailId(), databaseType);
							customAttributesUpdated = true;
							if (oldAttributeValue.getAttributeImpactsVersion() == 1) {
								RequirementUtil.updateVersion(requirementId, request,  databaseType);
								// at this point, lets create an entry in the gr_requirement_version table
								RequirementUtil.createRequirementVersion(requirementId);
							}
							if (oldAttributeValue.getAttributeImpactsTraceability() == 1) {
								RAttributeValue rAV = new RAttributeValue (oldAttributeValue.getAttributeValueId(), databaseType);
								String traceDefinition = rAV.getAttributeName() + ":" + rAV.getAttributeEnteredValue() + "  ";
								RequirementUtil.updateTraceability(traceDefinition, requirementId, request, user.getEmailId(),  databaseType);
							}
							if (oldAttributeValue.getAttributeImpactsApprovalWorkflow() == 1) {
								RequirementUtil.updateApprovalWorkflow(requirementId, request);
							}
						}
						
						
					} catch (Exception e) {
						// if we run into any exception, we don't need to worry.
						e.printStackTrace();
					}
				}


				// Now that we have updated a bunch of user defined attributes,
				// we need to
				// update the UDA for this requirement.
				if (customAttributesUpdated) {
					RequirementUtil.setUserDefinedAttributes(requirementId,user.getEmailId(), databaseType);
				}

				
				// lets updates the newly created Reqs tracebility . These
				// values may also
				// have been sent as part of the imported excel file.
				
				// requirement traceTo
				if (traceToColumn != -1) {

					HSSFCell requirementTraceToCell = row.getCell(traceToColumn);
					if (requirementTraceToCell != null) {
						requirementTraceTo = requirementTraceToCell.getStringCellValue();
					}

				}

				// requirement traceFrom
				if (traceFromColumn != -1) {
					HSSFCell requirementTraceFromCell = row.getCell(traceFromColumn);
					if (requirementTraceFromCell != null) {
						requirementTraceFrom = requirementTraceFromCell.getStringCellValue();
					}
				}
				
				if ((requirementTraceTo != null) || (requirementTraceFrom != null)) {
					String status = RequirementUtil.createTraces(project, requirement.getRequirementId(), requirementTraceTo,
						requirementTraceFrom, project.getProjectId(), securityProfile,  databaseType);
					if (!(status.equals(""))) {
						// the create traces method returned a non empty string.
						// means that there were some circular traces that were
						// not created.
						status = " Error while creating Traces : " + status;
						String inputString = rowNum + ":##:" + "Name : "
								+ newRequirementName + ";  " + "Priority : "
								+ newRequirementPriority + ";  " + "Owner : "
								+ newRequirementOwner + ";  " + "Pct Complete : "
								+ newRequirementPctComplete + ";  "
								+ "External URL : " + newRequirementExternalURL
								+ ";  " + "Description : "
								+ newRequirementDescription + "; :##:" + status;
						errorRows.add(inputString);
					}

				}

				// requirement traceFrom
				if (testingStatusColumn != -1) {
					HSSFCell requirementTestingStatusCell = row.getCell(testingStatusColumn);
					if (requirementTestingStatusCell != null) {
						requirementTestingStatus = requirementTestingStatusCell.getStringCellValue();
						if ((requirementTestingStatus != null) && (!requirementTestingStatus.equals(""))
								&& (((requirementTestingStatus.trim().toUpperCase().contains("PENDING"))
										|| (requirementTestingStatus.trim().toUpperCase().contains("PASS")) 
										|| (requirementTestingStatus.trim().toUpperCase().contains("FAIL"))))) {

							// we update testing status, only if its not empty.
							if ((requirementTestingStatus.trim().toUpperCase().equals("PENDING"))) {
								requirementTestingStatus = "Pending";
							}
							if ((requirementTestingStatus.trim().toUpperCase().equals("PASS"))) {
								requirementTestingStatus = "Pass";
							}

							if ((requirementTestingStatus.trim().toUpperCase().equals("FAIL"))) {
								requirementTestingStatus = "Fail";
							}
							requirement.setTestingStatus(requirementTestingStatus, user.getEmailId(), databaseType);
						}
						
					}
				}
				
				if (commentColumn != -1) {
					HSSFCell commentCell = row.getCell(commentColumn);
					if (commentCell != null) {
						String comment = commentCell.getStringCellValue();
						//if the user is an admin and a comment by cell exists, we try to calculate the comment by
						String commenterEmailId = user.getEmailId();
						try {
							if (securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId())){
								if (commentByColumn != -1) {
									HSSFCell commentByCell = row.getCell(commentByColumn);
									if (commentByCell != null) {
										String commentBy = commentByCell.getStringCellValue();
										User commenter = new User(commentBy, databaseType);
										commenterEmailId = commenter.getEmailId();
									}
								}
							}
						}
						catch(Exception commentByxception){
							// do nothing
						}
						if ((comment != null) && (!(comment.equals("")))){
							RequirementUtil.createComment(requirementId,commenterEmailId,comment, request, databaseType);
						}
						
					}
				}

				// Since the requirement has undergone changes (i.e custom
				// attribs are created
				// lets refresh.
				requirement = new Requirement(requirementId, databaseType);
				updatedRequirements.add(requirement);

			}
			// close the input stream.
			myxls.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Now we need to add the createdRequirements and errorRows to the
		// request.
		request.setAttribute("updatedRequirements", updatedRequirements);
		request.setAttribute("errorRows", errorRows);

		// Clean up. i.e drop files, folders etc...
		// we need to delete the file and the folder it contains. 
		// Note : we keep only 1 file in each folder. so this is safe.
		File file = new File(excelFilePath);
		if (file != null){
			File dir = file.getParentFile();
			// lets drop the file.
			file.delete();
			
			if (dir != null) {
				dir.delete();
			}
		}
	}
}
