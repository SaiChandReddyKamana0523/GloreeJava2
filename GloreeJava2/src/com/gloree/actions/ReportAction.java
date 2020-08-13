package com.gloree.actions;

// PDF Lowagie iText stuff

// MS Word Lowagie iText for RTF

/////////////////////////////////////////////Purpose ///////////////////////////////////////////
//
//	This servlet is used to build a report and export its contents to word, excel and PDF.
//
///////////////////////////////////////////Purpose ///////////////////////////////////////////

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
// Excel POI stuff.
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddressList;

import com.aspose.words.CellVerticalAlignment;
import com.aspose.words.ControlChar;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.FieldStart;
import com.aspose.words.FieldType;
import com.aspose.words.LineStyle;
import com.aspose.words.Node;
import com.aspose.words.NodeList;
import com.aspose.words.Orientation;
import com.aspose.words.SaveFormat;
import com.aspose.words.Shape;
import com.aspose.words.Underline;
import com.gloree.beans.Comment;
import com.gloree.beans.Folder;
import com.gloree.beans.GlobalRequirement;
import com.gloree.beans.MessagePacket;
import com.gloree.beans.Project;
import com.gloree.beans.RTAttribute;
import com.gloree.beans.RTBaseline;
import com.gloree.beans.Report;
import com.gloree.beans.Requirement;
import com.gloree.beans.RequirementAttachment;
import com.gloree.beans.RequirementType;
import com.gloree.beans.RequirementVersion;
import com.gloree.beans.SecurityProfile;
import com.gloree.beans.TDCSDocument;
import com.gloree.beans.TraceTreeRow;
import com.gloree.beans.User;
import com.gloree.utils.EmailUtil;
import com.gloree.utils.FolderUtil;
import com.gloree.utils.ProjectUtil;
import com.gloree.utils.ReportUtil;
import com.gloree.utils.RequirementUtil;
import com.gloree.utils.SecurityUtil;
import com.gloree.utils.TDCSUtil;
import com.lowagie.text.Anchor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.html.*;
//import com.gloree.actions.Hyperlink;


public class ReportAction extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ReportAction() {
        super();
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	doPost (request,response);
    }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String databaseType = this.getServletContext().getInitParameter("databaseType");

		///////////////////////////////SECURITY//////////////////////////////
		// Security  Note:
		// user has to be logged in by the time he is here. 
		// And he needs to be an Member
		// of this project. Note : Admins are members too.
		///////////////////////////////SECURITY//////////////////////////////
		
		// see if the user is logged in. If he is not, the method below will 
		// redirect him to the log in page.
		if (!(SecurityUtil.authenticationPassed(request, response))){
			return;
		}
		
		// now check if this users should be in this project.
		HttpSession session = request.getSession(true);
		SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
		Project project = (Project) session.getAttribute("project");
		if (!(securityProfile.getRoles().contains("MemberInProject" + project.getProjectId()))){
			//User is NOT a member of this project. so do nothing and return.
			return;
		}
		
		User user = securityProfile.getUser();
		///////////////////////////////SECURITY//////////////////////////////

		String action = request.getParameter("action");

		if ( action.equals("exportTracePanel")){
				////////////////////////////////////////SECURITY//////////////////////////
				//
				// This gets picked up from session memory , so we should be OK here.
				// 
				////////////////////////////////////////SECURITY//////////////////////////
				// lets pick up the report object from session memory and print it out.
				exportTracePanelToExcel( request, response, project, securityProfile.getUser());
				return;
		}
		if ( 
				(action.equals("runReport") )
				|| 
				(action.equals("saveReport"))
				|| 
				(action.contains("updateReport"))
				
			){

			////////////////////////////////////////SECURITY//////////////////////////
			//
			// We ensure that the project Id is used as a filter in the ProjectUtil's
			// saveReport, Run List Report and Run TraceTree Report 
			// routine. This project id comes from the user's session, hence the user is 
			// logged in and is a member of this project. 
			//
			////////////////////////////////////////SECURITY//////////////////////////

			
		    Date date = new Date();
		    System.out.println("srt in report 1 " + date.toString());
			String reportType = request.getParameter("reportType");
			// If this report was called from a saved report, then reportIdString has a value
			//  otherwise empty.

			
			// lets get the search params.
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			Folder folder = new Folder (folderId);
			String danglingSearch = request.getParameter("danglingSearch");
			String orphanSearch = request.getParameter("orphanSearch");
			String completedSearch = request.getParameter("completedSearch");
			String incompleteSearch = request.getParameter("incompleteSearch");
			String suspectUpStreamSearch = request.getParameter("suspectUpStreamSearch");
			String suspectDownStreamSearch = request.getParameter("suspectDownStreamSearch");
			String lockedSearch = request.getParameter("lockedSearch");
			String includeSubFoldersSearch = request.getParameter("includeSubFoldersSearch");
			
			
			
			int inRTBaselineSearch = Integer.parseInt(request.getParameter("inRTBaselineSearch"));
			int changedAfterRTBaselineSearch = Integer.parseInt(request.getParameter("changedAfterRTBaselineSearch"));

			//since testingStatusSearch came from a multi select list, it has an extra ,.
			// we need to drop it.
			String testingStatusSearch = request.getParameter("testingStatusSearch");
			if ((testingStatusSearch != null ) && (testingStatusSearch.contains(","))) {
				testingStatusSearch = (String) testingStatusSearch.subSequence(0,testingStatusSearch.lastIndexOf(","));
			}
			
			String nameSearch = request.getParameter("nameSearch");
			String descriptionSearch = request.getParameter("descriptionSearch");
			String ownerSearch = request.getParameter("ownerSearch");
			String externalURLSearch = request.getParameter("externalURLSearch");
			String approvedBySearch = request.getParameter("approvedBySearch");
			String rejectedBySearch = request.getParameter("rejectedBySearch");
			String pendingBySearch = request.getParameter("pendingBySearch");
			String traceToSearch = request.getParameter("traceToSearch");
			String traceFromSearch = request.getParameter("traceFromSearch");
			

			// since statusSearch came from a multi select list, it has an extra ,.
			// we need to drop it.
			String statusSearch = request.getParameter("statusSearch");
			if ((statusSearch != null ) && (statusSearch.contains(","))) {
				statusSearch = (String) statusSearch.subSequence(0,statusSearch.lastIndexOf(","));
			}

			// since prioritySearch came from a multi select list, it has an extra ,.
			// we need to drop it.
			String prioritySearch = request.getParameter("prioritySearch");
			if ((prioritySearch != null ) && (prioritySearch.contains(","))) {
				prioritySearch = (String) prioritySearch.subSequence(0,prioritySearch.lastIndexOf(","));
			}
			
			//  pctCompleteSearch came from a text box
			String pctCompleteSearch = request.getParameter("pctCompleteSearch");

			int traceTreeDepth = Integer.parseInt(request.getParameter("traceTreeDepth"));
			
			String displayRequirementType = request.getParameter("displayRequirementType");
			
			String standardDisplay = request.getParameter("standardDisplay");
			String customAttributesDisplay = request.getParameter("customAttributesDisplay");

			String sortBy = request.getParameter("sortBy");
			String sortByType = request.getParameter("sortByType");
			
			int rowsPerPage = 100;
			try {
				rowsPerPage = Integer.parseInt(request.getParameter("rowsPerPage"));
			}
			catch (Exception e){
				e.printStackTrace();
				rowsPerPage = 100;
			}
			
			// Now lets handle the custom attributes.
			// Note , by the time we are done with this block of code, 
			// customAttributeSearch will have 
			//avalue1:--:avalu2sel1:##:avalu2sel1:##:avalu2sel2:--:avalue3
			String customAttributeSearch ="";
			String attributeIdString = request.getParameter("attributeIdString");
			// attributeIdString has a string of attribute Ids and values in the following format.
			// id#value##id#value. eg : attributeIdString=2#DropDown##4#URL##3#Date##1#Text##
			// we need to get a list of attribute ids and get the request.getparameter values for these.
			// a typical URL looks like this : 
			//url is /GloreeJava2/servlet/RequirementAction?action=createAttributes&requirementId=1&attributeIdString=2#DropDown##4#URL
			// ##3#Date##1#Text##&2=good%20to%20have&4=external&3=datepromised&1=cost
			

			String [] attributeStrings = attributeIdString.split("##");
			String reportDefinitionCustomAttributes = "";
			for (int i=0; i<attributeStrings.length; i++ ){
				
				String [] attribute = attributeStrings[i].split("#");
				// Note : id here is the id of the attribute in requirment type. 
				// we will be using it to create an attribute value.
				String id = attribute[0];
				if (id != null){
					String aValue = request.getParameter(id);	
					reportDefinitionCustomAttributes += id + ":--:" + aValue + ":###:";

					// now lets build the custom search string, which should be in the form of
					// label:#:value
					if ((aValue != null) && (!aValue.equals(""))) {
						// id is typically in the format customA38 or customA39 where 38 adn 39 are
						// the custom Attribute Ids. so, we can drop off the customA to get the attribute id.
						int rTAttributeId = Integer.parseInt(id.replace("customA", ""));
						RTAttribute rTAttribute = new RTAttribute(rTAttributeId);
						
						customAttributeSearch += rTAttribute.getAttributeName() + ":#:" +  aValue + ":--:";
					}

				}
			}
			// drop the last :--:
			if (customAttributeSearch.contains(":--:")){
				customAttributeSearch = (String) customAttributeSearch.subSequence(0,customAttributeSearch.lastIndexOf(":--:"));
			}
			
			// lets build the report definition string. it can be used in multiple places.
			String reportDefinition = "";
			
			reportDefinition += "projectId" + ":--:" + project.getProjectId()+ ":###:";
			reportDefinition += "folderId" + ":--:" + folderId + ":###:";
			reportDefinition += "active" + ":--:" + "active" + ":###:";
			reportDefinition += "danglingSearch" + ":--:" + danglingSearch + ":###:";
			reportDefinition += "orphanSearch" + ":--:" + orphanSearch + ":###:";
			reportDefinition += "completedSearch" + ":--:" + completedSearch + ":###:";
			reportDefinition += "incompleteSearch" + ":--:" + incompleteSearch + ":###:";
			reportDefinition += "suspectUpStreamSearch" + ":--:" + suspectUpStreamSearch + ":###:";
			reportDefinition += "suspectDownStreamSearch" + ":--:" + suspectDownStreamSearch + ":###:";
			reportDefinition += "lockedSearch" + ":--:" + lockedSearch + ":###:";
			reportDefinition += "includeSubFoldersSearch" + ":--:" + includeSubFoldersSearch + ":###:";
			
			reportDefinition += "inRTBaselineSearch" + ":--:" + inRTBaselineSearch + ":###:";
			reportDefinition += "changedAfterRTBaselineSearch" + ":--:" + changedAfterRTBaselineSearch + ":###:";
			reportDefinition += "testingStatusSearch" + ":--:" + testingStatusSearch + ":###:";
			
			reportDefinition += "nameSearch" + ":--:" + nameSearch + ":###:";
			reportDefinition += "descriptionSearch" + ":--:" + descriptionSearch + ":###:";
			reportDefinition += "ownerSearch" + ":--:" + ownerSearch + ":###:";
			reportDefinition += "externalURLSearch" + ":--:" + externalURLSearch + ":###:";
			reportDefinition += "approvedBySearch" + ":--:" + approvedBySearch + ":###:";
			reportDefinition += "rejectedBySearch" + ":--:" + rejectedBySearch + ":###:";
			reportDefinition += "pendingBySearch" + ":--:" + pendingBySearch + ":###:";
			reportDefinition += "traceToSearch" + ":--:" + traceToSearch + ":###:";
			reportDefinition += "traceFromSearch" + ":--:" + traceFromSearch + ":###:";
			reportDefinition += "statusSearch" + ":--:" + statusSearch + ":###:";
			reportDefinition += "prioritySearch" + ":--:" + prioritySearch + ":###:";
			reportDefinition += "pctCompleteSearch" + ":--:" + pctCompleteSearch + ":###:";
			
			
			reportDefinition += "displayRequirementType" + ":--:" + displayRequirementType + ":###:";
			reportDefinition += "standardDisplay" + ":--:" + standardDisplay + ":###:";
			reportDefinition += "customAttributesDisplay" + ":--:" + customAttributesDisplay + ":###:";
			
			reportDefinition += reportDefinitionCustomAttributes;
			
			reportDefinition += "sortBy" + ":--:" + sortBy + ":###:";
			reportDefinition += "sortByType" + ":--:" + sortByType + ":###:";
			
			reportDefinition += "rowsPerPage" + ":--:" + rowsPerPage + ":###:";
			String inRelease = request.getParameter("inRelease");
			if ((inRelease != null) && !(inRelease.equals(""))){
				reportDefinition += "inRelease" + ":--:" + inRelease + ":###:";
			}
			
			if (action.equals("saveReport")) {
				// if the action is save Report, then save the report and return.
				// for saveReport we have a few extra params like report Name and report Desciption.
				// we also build the report definition based on the params we have received.
				//i.e report name and report description.
				String reportVisibility = request.getParameter("reportVisibility");
				String reportName = request.getParameter("reportName");
				String reportDescription = request.getParameter("reportDescription");
				ReportUtil.saveReport(project.getProjectId(), folderId,  reportVisibility, reportName, 
						reportDescription, reportType , traceTreeDepth, reportDefinition, user.getEmailId(),   databaseType);
				
				PrintWriter out = response.getWriter();
				
				out.println("<div style='float:right'> ");
				out.println("<a href='so#' onclick='document.getElementById(\"saveReportResultDiv\").style.display = \"none\";'> Close </a>");
				out.println("</div>");
				out.println("This report has been successfully saved to the system.");
				out.println("<br> You can locate it in the folder : " + folder.getFolderPath());
				
				
				return;
			}
			if (action.contains("updateReport")) {
				String[] uR = action.split(":#:");
				int reportId = Integer.parseInt(uR[1]);

				ReportUtil.updateReport(reportId, reportDefinition);
				
				PrintWriter out = response.getWriter();
				
				out.println("<div style='float:right'> ");
				out.println("<a href='so#' onclick='document.getElementById(\"saveReportResultDiv\").style.display = \"none\";'> Close </a>");
				out.println("</div>");
				out.println("This report has been successfully updated");
				
				
				return;
			}
			
			// lets handle the list report display differently than the traceTree report display.
			
			if (reportType.equals("list")){
				

			    date = new Date(); System.out.println("srt in report 2 " + date.toString());
			    
				ArrayList listReport = ReportUtil.runListReport(securityProfile, 
					project.getProjectId(), folderId, "active",
					danglingSearch,orphanSearch,completedSearch,incompleteSearch,
					suspectUpStreamSearch, suspectDownStreamSearch,  lockedSearch, includeSubFoldersSearch,
					inRTBaselineSearch,changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch,
					approvedBySearch, rejectedBySearch, pendingBySearch , 
					traceToSearch,
					traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch, 
					customAttributeSearch, sortBy, sortByType, inRelease, databaseType);
				
				
				// any time you are asked to report TraceTree data, see if there is a resultsetFilter condition
				// if so , remove any rows that dont' match filter condition

				// if custom attribute filter string is sent in
				// and this requirement does not meet the filter criteria
				// then remove this req from this list and display
				String filterCondition = request.getParameter("filterCondition");
				if (filterCondition == null){filterCondition = "";}
				
				
				ArrayList <Requirement> postFiltered = new ArrayList<Requirement>();  
				if (filterCondition.length() > 0 ){
					// iterate through all the objects in the report and remove any that don't fit the filter condition
					
					Iterator i = listReport.iterator();
					while (i.hasNext()){

						Requirement r = (Requirement) i.next();	
				   		if (ReportUtil.matchFilterConditionMultiAttribute(r, filterCondition)){
							postFiltered.add(r);
							
						}
				   		
					}
				}
				else {
					// since no filter condition exists, lets assign postFilter to point to original traceTreeReport
					postFiltered = listReport;
				}
				//NEEL is SUPER COOL WOOT WOOT I LUVV U DADDDDD!!!
				ArrayList <Requirement> postFilteredSorted = new ArrayList<Requirement>();  
				postFilteredSorted = postFiltered;
				try{
					if (sortBy.startsWith("CustomAttribute")){
						String customAttribute = sortBy.replace("CustomAttribute", "");
						customAttribute = customAttribute.replace(":#:", "");
						// if the custom attribute is of type number, then we have to sort the arraylist manually
						RTAttribute attribute = new RTAttribute(folder.getRequirementTypeId(),customAttribute);
						if (attribute.getAttributeType().equals("Number")){
							// this is an attribute of type number, and our alphanumeric sorting doesn't work.
							// So we will have to manually sort. 
							postFilteredSorted = FolderUtil.sortRequirementsInArrayNumerically(postFiltered,attribute,sortByType);
						}
					
						
					}
				}
				catch (Exception d){
					d.printStackTrace();
				}
				
				// Once the report is created, lets store the report object in session scope ,
				// the benefit is , if we want to convert this to Excel , Word, PDF, we won't need to
				// re run the query.
				// NOTE : we are using folderId appended to listReport to uniquely identify a report
				// while not perfect, its the only solution, as reportId may not exist for new reports.
				session.setAttribute("listReportForFolder" + folderId , postFilteredSorted);
				session.setAttribute("listReportDefinitionForFolder" + folderId , reportDefinition);
				session.setAttribute("listReportStandardDisplay" + folderId , standardDisplay);
				session.setAttribute("listReportCustomAttributesDisplay" + folderId , customAttributesDisplay);
				session.setAttribute("listReportRowsPerPage", new Integer(rowsPerPage));
				
				//we will try forwarding to displayListReport_data page.
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Report/displayListReport_data.jsp");
				dispatcher.forward(request, response);
				return;
			}
			if (reportType.equals("traceTree")){
				//this is trace tree. so we handle it here.
				int  maxRowsInTraceTree = Integer.parseInt(this.getServletContext().getInitParameter("maxRowsInTraceTree"));
				ArrayList traceTreeReport = ReportUtil.runTraceTreeReport(securityProfile, project.getProjectId(), folderId,
					"active", traceTreeDepth,
					danglingSearch,orphanSearch,completedSearch,incompleteSearch,
					suspectUpStreamSearch, suspectDownStreamSearch,  lockedSearch, includeSubFoldersSearch, 
					inRTBaselineSearch,changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch,
					approvedBySearch, rejectedBySearch, pendingBySearch ,
					traceToSearch,
					traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch, 
					customAttributeSearch, databaseType, maxRowsInTraceTree,request, displayRequirementType );
		
				
				// any time you are asked to report TraceTree data, see if there is a resultsetFilter condition
				// if so , remove any rows that dont' match filter condition

				// if custom attribute filter string is sent in
				// and this requirement does not meet the filter criteria
				// then remove this req from this list and display
				String filterCondition = request.getParameter("filterCondition");
				if (filterCondition == null){filterCondition = "";}
				
				
				
				
				ArrayList <TraceTreeRow> postFiltered = new ArrayList<TraceTreeRow>();  
				if (filterCondition.length() > 0 ){
					// iterate through all the objects in the report and remove any that don't fit the filter condition
					
					Iterator i = traceTreeReport.iterator();
					while (i.hasNext()){
				   		TraceTreeRow tTR = (TraceTreeRow) i.next();
						Requirement r = tTR.getRequirement();	
				   		if (ReportUtil.matchFilterConditionMultiAttribute(r, filterCondition)){
							postFiltered.add(tTR);
						}
				   		
					}
				}
				else {
					// since no filter condition exists, lets assign postFilter to point to original traceTreeReport
					postFiltered = traceTreeReport;
				}
				
				
				
				// Once the report is created, lets store the report object in request scope ,
				// the benefit is , if we want to convert this to Excel , Word, PDF, we won't need to
				// re run the query.
				// NOTE : we are using folderId appended to listReport to uniquely identify a report
				// while not perfect, its the only solution, as reportId may not exist for new reports.
				session.setAttribute("displayRequirementType" + folderId, displayRequirementType);
				
				
				session.setAttribute("traceTreeReportForFolder" + folderId, postFiltered);
				session.setAttribute("traceTreeReportDefinitionForFolder" + folderId, reportDefinition);
				session.setAttribute("traceTreeReportDepthForFolder" + folderId, Integer.toString(traceTreeDepth));
				session.setAttribute("traceTreeReportStandardDisplay" + folderId , standardDisplay);
				session.setAttribute("traceTreeReportRowsPerPage", new Integer(rowsPerPage));
							
				//we will try forwarding to displayListReport_data page.
				RequestDispatcher dispatcher =	request.getRequestDispatcher("/jsp/Report/displayTraceTreeReport_data.jsp");
				dispatcher.forward(request, response);
				return;
			}
	
		}
		else if (action.equals("bulkEditForUserDashboard")) {
			
			// this is a cousin of BulkEdit and is used for hte UserDashboard BulkEdit operations.
			////////////////////////////////////////SECURITY//////////////////////////
			//NELELELE IS EPIC
			// In the ReportUtil.BulkEdit, we check to make sure that every 
			// every input requirement's projectId is the same as the projectId
			// that the user has logged into.
			//
			////////////////////////////////////////SECURITY//////////////////////////

			// lets get the input params.
			
			String targetArtifact = request.getParameter("targetArtifact");
			String targetValue = request.getParameter("targetValue");
			String targetRequirements = request.getParameter("targetRequirements");
			// neel is sooooooooo cololool lol dad m coment
			// the targetRequiremetns are reqIds separated by :##:
			// drop the last :##:
			if (targetRequirements.contains(":##:")){
				targetRequirements = (String) targetRequirements.subSequence(0,targetRequirements.lastIndexOf(":##:"));
			}
			
			String bulkEditResponse = "";
		
			

			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			
			bulkEditResponse = ReportUtil.bulkEdit(project, targetArtifact,targetValue, 
					targetRequirements, securityProfile, project.getProjectId(), request, databaseType,
					mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword);
			
			
			
			
		
			
			// we are having difficulty showing the bulkedit response, as 
			// its getting over written when we auto refresh the user dashboard report
			// so we are using a clever (messy???) work around
			// we store the bulkEditresponse in a session variable and disply it
			// if it exists. 
			
			
			if (!(bulkEditResponse.equals(""))){
				String bulkEditResponseString  =  "<div id='userAlert' class='alert alert-success'> ";
				bulkEditResponseString += "<div style='float: right;'> ";
				bulkEditResponseString += "<a href='#' " + 
					" onclick='document.getElementById(\"bulkEditActionResponse\").style.display = \"none\";'> " +
					"Close " +
					"</a>";
				bulkEditResponseString += "</div> <span class='normalText'>";
				bulkEditResponseString += bulkEditResponse;
				bulkEditResponseString += "</span></div>";
				
				session.setAttribute("bulkEditResponseString", bulkEditResponseString);
			}
			
			return;
		}		
		else if (action.equals("bulkEdit")) {
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// In the ReportUtil.BulkEdit, we check to make sure that every 
			// every input requirement's projectId is the same as the projectId
			// that the user has logged into.
			//
			////////////////////////////////////////SECURITY//////////////////////////

			// lets get the input params.
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			
			String targetArtifact = request.getParameter("targetArtifact");
			String targetValue = request.getParameter("targetValue");
			System.out.println("srt targetValue1 is " + targetValue);

			String targetRequirements = request.getParameter("targetRequirements");
			
			// if the user has asked to change the owner of a req and the owner is 
			// not in this project, lets stop them cold here.
			if (targetArtifact.equals("owner")){
				if (!(ProjectUtil.isValidUserInProject(targetValue, project))){
					PrintWriter out = response.getWriter();
					// the user is trying to set the owner as someone who is not in this project.
					out.println("<div id='userAlert' class='alert alert-success'> ");
					out.println("<div style='float: right;'> ");
					out.println("<a href='#' " + 
						" onclick='document.getElementById(\"bulkEditActionResponse\").style.display = \"none\";'> " +
						"Close " +
						"</a>");
					out.println("</div> <span class='normalText'>");
					out.println(targetValue + " is not a valid user in the project. No Requirements have been updated");
					out.println("</span></div>");
					return;
				}
			}
			// the targetRequiremetns are reqIds separated by :##:
			// drop the last :##:
			if (targetRequirements.contains(":##:")){
				targetRequirements = (String) targetRequirements.subSequence(0,targetRequirements.lastIndexOf(":##:"));
			}
			
			String bulkEditResponse = "";
			// Note : for custom attributes, the target artifact comes as 
			// customAttribute:##:Attributeid , where attributeid is a number.

			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			
			bulkEditResponse = ReportUtil.bulkEdit(project, targetArtifact,targetValue, 
					targetRequirements, securityProfile, project.getProjectId(), request, databaseType,
					mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword); 
			if (targetArtifact.equals("owner")){
				// lets email the new owner that he is the owner of a bunch of new requirements
				// we are ignoring the notifyOnOwnerChang as this is a single email. No spam risk
				String to = targetValue;
				String cc = "";
				String subject = "You have been made the Owner of some new object in TraceCloud" ;
				String message = 
				"<html><body>Hello, <br><br>" + 
				user.getFirstName() + " " + user.getLastName() + " has made you the Owner of these object : <br><br> <table>" +
				bulkEditResponse + 
				" </table><br><br>From now on the metrics about this object will start showing up in your dashboard. <br><br>" +
				" Best Regards <br><br>" + 
				" TraceCloud Administrator </body></html>";
				
				// lets send the email out to the toEmailId;
				ArrayList toArrayList = new ArrayList();
				if (to != null){
					to = to.trim();
					if (!to.equals("")){
						if (to.contains(",")){
							String [] toEmails = to.split(",");
							for (int i=0; i < toEmails.length; i++ ){
								toArrayList.add(toEmails[i]);
							}
						}
						else {
							toArrayList.add(to);
						}
					}
				}
				
				ArrayList ccArrayList = new ArrayList();
				if (cc != null){
					cc = cc.trim();
					if (!cc.equals("")){
						if (cc.contains(",")){
							String [] ccEmails = cc.split(",");
							for (int i=0; i < ccEmails.length; i++ ){
								ccArrayList.add(ccEmails[i]);
							}
						}
						else {
							ccArrayList.add(cc);
						}
					}
				}
				MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, "");
				 mailHost = this.getServletContext().getInitParameter("mailHost");
				transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
				smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
				smtpPort = this.getServletContext().getInitParameter("smtpPort");
				smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
				emailUserId = this.getServletContext().getInitParameter("emailUserId");
				emailPassword = this.getServletContext().getInitParameter("emailPassword");
								
				EmailUtil.email(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword); 
				
				// lets empty out bulkEditResponse, so it doesn't get displayed on screen
				bulkEditResponse = "";
				
			}
			
			PrintWriter out = response.getWriter();
			
			if (!(bulkEditResponse.equals(""))){
				out.println("<div id='userAlert' class='alert alert-success'> ");
				out.println("<div style='float: right;'> ");
				out.println("<a href='#' " + 
					" onclick='document.getElementById(\"bulkEditActionResponse\").style.display = \"none\";'> " +
					"Close " +
					"</a>");
				out.println("</div> <span class='normalText'>");
				out.println(bulkEditResponse);
				out.println("</span></div>");
			}
			
			return;
		}
		else if (action.equals("displayExistingReport")) {
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			int reportId = Integer.parseInt(request.getParameter("reportId"));
			Report report = new Report(reportId);
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// We ensure that the report's projectId matches the project Id
			// that the user has logged into. 
			//
			////////////////////////////////////////SECURITY//////////////////////////

			if (report.getProectId() != project.getProjectId()){
				return;
			}
			if (report.getReportType().equals("list")) {
				String url="/jsp/Report/displayListReport.jsp?folderId=" + folderId;
				url += "&reportId=" +reportId;
				RequestDispatcher dispatcher =	request.getRequestDispatcher(url);
				dispatcher.forward(request, response);
				return;
			}
			if (report.getReportType().equals("traceTree")) {
				String url="/jsp/Report/displayTraceTreeReport.jsp?folderId=" + folderId;
				url += "&reportId=" +reportId;
				RequestDispatcher dispatcher =	request.getRequestDispatcher(url);
				dispatcher.forward(request, response);
				return;
			}
			
		}
		else if (action.equals("displayDynamicReport")) {
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			Folder folder = new Folder(folderId);
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// We ensure that the project Id of the folder that has been 
			// given as a parameter, matches the project that the user has logged into.
			//
			////////////////////////////////////////SECURITY//////////////////////////
			if (folder.getProjectId() != project.getProjectId()){
				return;
			}
			
			String reportDefinition = request.getParameter("reportDefinition");
			String reportType = request.getParameter("reportType");
			
			if (reportType.equals("list")) {
				String url="/jsp/Report/displayListReport.jsp?folderId=" + folderId;
				url += "&reportDefinition=" + reportDefinition ;
				RequestDispatcher dispatcher =	request.getRequestDispatcher(url);
				dispatcher.forward(request, response);
				return;
			}
			if (reportType.equals("traceTree")) {
				String url="/jsp/Report/displayTraceTreeReport.jsp?folderId=" + folderId;
				url += "&reportDefinition=" + reportDefinition ;
				RequestDispatcher dispatcher =	request.getRequestDispatcher(url);
				dispatcher.forward(request, response);
				return;
			}
			
			
		}
		else if (action.equals("deleteReport")){
			// get the reportId param, call the deleteReport method and display the results.
			int reportId = Integer.parseInt(request.getParameter("reportId"));
			Report report = new Report(reportId);
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// We ensure that the report's projectId matches the project Id
			// that the user has logged into. 
			//
			////////////////////////////////////////SECURITY//////////////////////////

			if (report.getProectId() != project.getProjectId()){
				return;
			}

			ReportUtil.deleteReport(reportId);
			PrintWriter out = response.getWriter();
			out.println("<div id='userAlert' class='alert alert-success'> ");
			out.println("<br>Your Report has been successfully deleted.<br>");
			out.println("</div>");
			return;
		}

		else if (action.equals("exportListReportToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////
			// lets pick up the report object from session memory and print it out.
			exportListReportToExcel( request, response, project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportSearchReportToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////
			// lets pick up the report object from session memory and print it out.
			
			System.out.println("srt ckn 1");
			int targetRequirementTypeId  = 0;
			int targetFolderId = 0;
			
			try {
				targetRequirementTypeId = Integer.parseInt(request.getParameter("targetRequirementTypeId"));
			}
			catch (Exception e){}
			
			try {
				targetFolderId = Integer.parseInt(request.getParameter("targetFolderId"));
			}
			catch (Exception e){}
			String searchString = request.getParameter("searchString");
			String searchProjects = "";
		            
			exportSearchReportToExcel( request, response,user,  project ,"HTML", databaseType,
					securityProfile, searchString, searchProjects, targetRequirementTypeId, targetFolderId);
			return;
		}
		else if (action.equals("exportListReportToWord")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////
			
			
			// lets pick up the report object from session memory and print it out.
			exportListReportToWord( request, response, project, securityProfile.getUser(), "HTML", databaseType);
			return;
		}
		else if (action.equals("exportListReportToPDF")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////
			
			
			// lets pick up the report object from session memory and print it out.
			exportListReportToPDF( request, response, project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportTraceTreeReportToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			
			// lets pick up the report object from session memory and print it out.
			exportTraceTreeReportToExcel( request, response, project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportTraceTreeReportToWord2")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			String templateFilePath = this.getServletContext().getInitParameter("templateFilePath");
			// lets pick up the report object from session memory and print it out.
			

			System.out.println("insde exportTTRW2 option");
				

			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			
			String fileName = exportTraceTreeReportToWord2( request, response, project, securityProfile.getUser(), 
					securityProfile,  databaseType, templateFilePath, rootDataDirectory);
			
			System.out.println("fileOutput is " + fileName);
			
			
			fileName.replace(" ", "_");
			fileName.replace("-", "_");

			FileInputStream inputStream= new FileInputStream(fileName);
			
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setContentType("application/msword");
    		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    		
	        ServletOutputStream out = response.getOutputStream();
	        byte buf[]=new byte[1024];
	        int len;
	        while((len=inputStream.read(buf))>0)
	        	out.write(buf,0,len);
	        
	        out.close();
	        inputStream.close();
			
	        
			return;
		}
			
		/*
		else if (action.equals("exportTraceTreeReportToWord")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			
			// lets pick up the report object from session memory and print it out.
			exportTraceTreeReportToWord( request, response, project, securityProfile.getUser(),"HTML");
			return;
		}
		*/
		else if (action.equals("exportTraceTreeReportToPDF")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			// lets pick up the report object from session memory and print it out.
			exportTraceTreeReportToPDF( request, response, project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportProjectMetricsReportToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			
			// lets pick up the report object from session memory and print it out.
			exportMetricsReportToExcel(request, response,"projectReport", project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportReleaseMetricsReportToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			// lets pick up the report object from session memory and print it out.
			exportMetricsReportToExcel(request, response,"releaseReport", project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportBaselineMetricsReportToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			// lets pick up the report object from session memory and print it out.
			exportMetricsReportToExcel(request, response,"baselineReport", project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportUserMetricsReportToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			// lets pick up the report object from session memory and print it out.
			exportMetricsReportToExcel(request, response,"userReport", project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}
		else if (action.equals("exportProjectDashboardDataTableToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			// lets pick up the report object from session memory and print it out.
			exportProjectDashboardDataTableToExcel(request, response,"projectDashboardDataTable", project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}		
		else if (action.equals("exportReleaseDashboardDataTableToExcel")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////

			// lets pick up the report object from session memory and print it out.
			exportReleaseDashboardDataTableToExcel(request, response,"releaseDashboardDataTable", project, securityProfile.getUser(),"HTML", databaseType);
			return;
		}		
		else if (action.equals("emailReportAsAttachment")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////
			
			String reportType = request.getParameter("reportType");
			String to = request.getParameter("to");
			String cc = request.getParameter("cc");
			String attachmentType = request.getParameter("attachmentType");
			String subject = request.getParameter("subject");
			String message = request.getParameter("message");
			
			// users may enter email ids separated by space or semicolon. lets 
			// make them all comma separated
			if (to.trim().contains(" ")){
				to = to.replace(' ', ',');
			}
			if (to.trim().contains(";")){
				to = to.replace(';', ',');
			}
			if (cc.trim().contains(" ")){
				cc = cc.replace(' ', ',');
			}
			if (cc.trim().contains(";")){
				cc = cc.replace(';', ',');
			}
			
			String filename = "";
			// lets pick up the report object from session memory and print it out.
			if (reportType.equals("list")){

				if (attachmentType.equals("excel")){
					filename = exportListReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("excelVersionComments")){
					filename = exportListReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("word")){
					filename = exportListReportToWord( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("pdf")){
					filename = exportListReportToPDF( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
			}
			if (reportType.equals("traceTree")){
				if (attachmentType.equals("excel")){
					filename = exportTraceTreeReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("excelVersionComments")){
					filename = exportTraceTreeReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("word")){
					//filename = exportTraceTreeReportToWord2( request, response, project, securityProfile.getUser(),"file");



					String templateFilePath = this.getServletContext().getInitParameter("templateFilePath");
					String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
					filename = exportTraceTreeReportToWord2( request, response, project, securityProfile.getUser(), 
							securityProfile,  databaseType, templateFilePath, rootDataDirectory);
					
				}
				if (attachmentType.equals("pdf")){
					filename = exportTraceTreeReportToPDF( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
			}

			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			if (to != null){
				to = to.trim();
				if (!to.equals("")){
					if (to.contains(",")){
						String [] toEmails = to.split(",");
						for (int i=0; i < toEmails.length; i++ ){
							toArrayList.add(toEmails[i]);
						}
					}
					else {
						toArrayList.add(to);
					}
				}
			}
			
			ArrayList ccArrayList = new ArrayList();
			if (cc != null){
				cc = cc.trim();
				if (!cc.equals("")){
					if (cc.contains(",")){
						String [] ccEmails = cc.split(",");
						for (int i=0; i < ccEmails.length; i++ ){
							ccArrayList.add(ccEmails[i]);
						}
					}
					else {
						ccArrayList.add(cc);
					}
				}
			}
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, filename);
			
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			
			EmailUtil.emailWithAttachment(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );

			
			// now lets remove the temp file.
			File file = new File(filename);
			if (file != null){
				// lets drop the file.
				file.delete();
			}
			
		    PrintWriter out = response.getWriter();
		    out.println("<span class='normalText'> An email with attachment has been sent out." +
		    		" <br><a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> </span>");
		    out.close();

			return;
		}		
		else if (action.equals("emailMetricsExcelAsAttachment")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////
			
			String to = request.getParameter("to");
			String cc = request.getParameter("cc");
			String subject = request.getParameter("subject");
			String message = request.getParameter("message");
			
			// users may enter email ids separated by space or semicolon. lets 
			// make them all comma separated
			if (to.trim().contains(" ")){
				to = to.replace(' ', ',');
			}
			if (to.trim().contains(";")){
				to = to.replace(';', ',');
			}
			if (cc.trim().contains(" ")){
				cc = cc.replace(' ', ',');
			}
			if (cc.trim().contains(";")){
				cc = cc.replace(';', ',');
			}
			
			String dataType = request.getParameter("dataType");
			
			String filename = "";
			// lets pick up the report object from session memory and print it out.
			if (dataType.equals("projectRequirements")){
				filename =  exportMetricsReportToExcel(request, response,"projectReport", project, securityProfile.getUser(),"file", databaseType);
			}
			if (dataType.equals("baselineRequirements")){
				filename =  exportMetricsReportToExcel(request, response,"baselineReport", project, securityProfile.getUser(),"file", databaseType);
			}
			if (dataType.equals("folderRequirements")){
				// NOTE : this is not a bug. put here deliberately. Since the output format of the project requriements
				// and folder requirements are the same we can leverage them. 
				filename =  exportMetricsReportToExcel(request, response,"projectReport", project, securityProfile.getUser(),"file", databaseType);
			}
			if (dataType.equals("releaseRequirements")){
				filename =  exportMetricsReportToExcel(request, response,"releaseReport", project, securityProfile.getUser(),"file", databaseType);
			}
			if (dataType.equals("userRequirements")){
				filename =  exportMetricsReportToExcel(request, response,"userReport", project, securityProfile.getUser(),"file", databaseType);
			}
			
			
			
			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			if (to != null){
				to = to.trim();
				if (!to.equals("")){
					if (to.contains(",")){
						String [] toEmails = to.split(",");
						for (int i=0; i < toEmails.length; i++ ){
							toArrayList.add(toEmails[i]);
						}
					}
					else {
						toArrayList.add(to);
					}
				}
			}
			
			ArrayList ccArrayList = new ArrayList();
			if (cc != null){
				cc = cc.trim();
				if (!cc.equals("")){
					if (cc.contains(",")){
						String [] ccEmails = cc.split(",");
						for (int i=0; i < ccEmails.length; i++ ){
							ccArrayList.add(ccEmails[i]);
						}
					}
					else {
						ccArrayList.add(cc);
					}
				}
			}
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, filename);
			
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
			EmailUtil.emailWithAttachment(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );

			
			// now lets remove the temp file.
			File file = new File(filename);
			if (file != null){
				// lets drop the file.
				file.delete();
			}
			
		    PrintWriter out = response.getWriter();
		    out.println("<span class='normalText'> An email with attachment has been sent out." +
		    		" <br><a href='#' onClick='document.getElementById(\"emailExcelDiv\").style.display=\"none\"'>Close</a> </span>");
		    out.close();

			return;
		}	
		else if (
				(action.equals("exportProjectTrendData"))
				||
				(action.equals("exportReleaseTrendData"))
				||
				(action.equals("exportBaselineTrendData"))
				||
				(action.equals("exportFolderTrendData"))
				){

			int releaseId = 0;
			if (action.equals("exportReleaseTrendData")){
				releaseId = Integer.parseInt(request.getParameter("releaseId"));
			}
			
			int rTBaselineId = 0;
			if (action.equals("exportBaselineTrendData")){
				rTBaselineId  = Integer.parseInt(request.getParameter("rTBaselineId"));
			}
			
			int folderId = 0;
			if (action.equals("exportFolderTrendData")){
				folderId  = Integer.parseInt(request.getParameter("folderId"));
			}

			if (action.equals("exportReleaseTrendData")){
				exportReleaseTrends(request, response, project, securityProfile.getUser(),action, releaseId,rTBaselineId,
						folderId, databaseType);
			}
			else {
				exportProjectTrends(request, response, project, securityProfile.getUser(),action, releaseId,rTBaselineId,
					folderId, databaseType);
			}
			 
			 
			/*
			String to = user.getEmailId();
			String subject = "";
			
			if (action.equals("exportProjectTrendData")){
				subject = "Project Trend Data for : " + project.getProjectName();
			}
			if (action.equals("exportReleaseTrendData")){	
				subject = "Release Trend Data for : " + project.getProjectName();
			}
			
			if (action.equals("exportBaselineTrendData")){
				subject = "Baseline Trend Data for : " + project.getProjectName();
			}
			if (action.equals("exportFolderTrendData")){
				subject = "Folder Trend Data for : " + project.getProjectName();
			}
			
			
			String message = "Hi " + user.getFirstName() + " "  + user.getLastName();
			message += "\nHere comes the Data Trends file that you requested.";
			message += "\n\nRegards";
			message += "\nTraceCloud Admin";
			
			String filename = "";
			
			int releaseId = 0;
			if (action.equals("exportReleaseTrendData")){
				releaseId = Integer.parseInt(request.getParameter("releaseId"));
			}
			
			int rTBaselineId = 0;
			if (action.equals("exportBaselineTrendData")){
				rTBaselineId  = Integer.parseInt(request.getParameter("rTBaselineId"));
			}
			
			int folderId = 0;
			if (action.equals("exportFolderTrendData")){
				folderId  = Integer.parseInt(request.getParameter("folderId"));
			}
						
			
			filename =  exportProjectTrends(request, response, project, securityProfile.getUser(),action, releaseId,rTBaselineId,
					folderId, databaseType);
			
			
			
			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			if (to != null){
				to = to.trim();
				if (!to.equals("")){
					if (to.contains(",")){
						String [] toEmails = to.split(",");
						for (int i=0; i < toEmails.length; i++ ){
							toArrayList.add(toEmails[i]);
						}
					}
					else {
						toArrayList.add(to);
					}
				}
			}
			
			ArrayList ccArrayList = new ArrayList();
			
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, filename);
			
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
				
			EmailUtil.emailWithAttachment(mP , mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );

			
			// now lets remove the temp file.
			File file = new File(filename);
			if (file != null){
				// lets drop the file.
				file.delete();
			}
			
		    PrintWriter out = response.getWriter();
		    out.println("<div><span class='normalText'> An email with attachment has been sent to " + user.getEmailId() + " .  " + 
		    		" <br><div style='float:right'><a href='#' onClick='document.getElementById(\"emailTrendDataDiv\").style.display=\"none\"'>Close</a></div> </span></div>");
		    out.close();

			*/
			return;
		}			
		else if (action.equals("saveTDCSDocument")){
			////////////////////////////////////////SECURITY//////////////////////////
			//
			// This gets picked up from session memory , so we should be OK here.
			// 
			////////////////////////////////////////SECURITY//////////////////////////
			int folderId = Integer.parseInt(request.getParameter("folderId"));
			Folder folder = new Folder (folderId);
			
			
			
			String reportType = request.getParameter("reportType");
			String tDCSAction = request.getParameter("tDCSAction");
			String tDCSDocumentTag = "";
			String title = "";
			// we will have tDCSDocument only if the user is trying to update an existing version.
			TDCSDocument tDCSDocument = null;
			if (tDCSAction.equals("new")){
				title = request.getParameter("title");
				// if the action is to create a new document, then the user has to have
				// write permissions on this folder
				if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" 
						+ folderId))){
					PrintWriter out = response.getWriter();
				    out.println("<span class='normalText'> You do not have permissions to save TDCS documents in this Folder" +
				    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> </span>");
				    out.close();

					return;
				}
			}
			if (tDCSAction.equals("existing")){
				
				title = request.getParameter("title");
				String tDCSDocumentFullTag = request.getParameter("tDCSDocumentFullTag");
				tDCSDocument = new TDCSDocument(tDCSDocumentFullTag, project.getProjectId(), databaseType);
				if (tDCSDocument.getProjectId() != project.getProjectId()){
					PrintWriter out = response.getWriter();
				    out.println("<span class='normalText'> This document does not exist in this project. Please check your document Id." +
				    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> </span>");
				    out.close();
				    return;
				}
				else if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + tDCSDocument.getFolderId() ))){
					PrintWriter out = response.getWriter();
				    out.println("<span class='normalText'> You do not have Create permissions in the Folder where the Document resides. Please work with your administrator to " +
							" get Create Permissions or choose another document Id." +
				    		" &nbsp;&nbsp;<a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> </span>");
				    out.close();
				    return;
				}
				else if (tDCSDocument.getCurrentVersionDocumentStatus().equals("locked")){
					// this is a locked document. lets get the user to get the document unlocked.
					if (tDCSDocument.getCurrentVersionDocumentStatusBy().equals(securityProfile.getUser().getEmailId() )){ 
						// this means that the lock was put in place by this user.		
						PrintWriter out = response.getWriter();
					    out.println("<span class='normalText'>This document has been Locked by you on " +
					    	tDCSDocument.getCurrentVersionLastModifiedDt() + 
							" Please Unlock it at <a href='#' onClick='displayTDCSHome()'> " + 
							" <img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> "  + 
							" prior to adding a new version to it. </span>");
					    out.close();
					    return;
					} 
					else {
						PrintWriter out = response.getWriter();
					    out.println("<span class='normalText'>This document has been Locked by " +
					    	tDCSDocument.getCurrentVersionDocumentStatusBy() + " on " +
					    	tDCSDocument.getCurrentVersionLastModifiedDt() + 
							"  Please work with this person or the project administrators to " + 
							" Unlock it at <a href='#' onClick='displayTDCSHome()'> " + 
							" <img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> "  + 
							" prior to adding a new version to it. </span>");
					    out.close();
					    return;
					}
				}
			}

			
			String attachmentType = request.getParameter("attachmentType");
			String description = request.getParameter("description");

			String filepath = "";
			// lets pick up the report object from session memory , make an excel file out of it and them move it to to TDCS folder.
			if (reportType.equals("list")){
				if (attachmentType.equals("excel")){
					filepath = exportListReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("excelVersionComments")){
					filepath = exportListReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("word")){
					filepath = exportListReportToWord( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("pdf")){
					filepath = exportListReportToPDF( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
			}
			if (reportType.equals("traceTree")){
				if (attachmentType.equals("excel")){
					filepath = exportTraceTreeReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("excelVersionComments")){
					filepath = exportTraceTreeReportToExcel( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
				if (attachmentType.equals("word")){
					filepath = exportTraceTreeReportToWord( request, response, project, securityProfile.getUser(),"file");
				}
				if (attachmentType.equals("pdf")){
					filepath = exportTraceTreeReportToPDF( request, response, project, securityProfile.getUser(),"file", databaseType);
				}
			}


			// lets move this file to a permanent location.
			
			// first let make sure that the E://TraceCloud/ProjectId/TDCS/unique directory exists.
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			Calendar cal = Calendar.getInstance();
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
			}
			

			// if rootDataDirectory/TraceCloud/ProjectId does not exist, lets create it.
			File projectRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId());
			if (!(projectRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId()).mkdir();
			}
			
			// lets create a TDCS folder in the project.
			File projectAttachmentRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator + "TDCS");
			if (!(projectAttachmentRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator + "TDCS").mkdir();
			}
			
			// lets create a unique directory within the ProjectRoot to store
			// the file.
			String targetDirName; 
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy-hhmm-ss");
			String today =  sdf.format(cal.getTime());
		    targetDirName= rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "TDCS" + File.separator + user.getUserId() + "-" + today ;	    
		    new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  project.getProjectId() + File.separator +  "TDCS" + File.separator + user.getUserId() + "-" + today).mkdir();

		    // now that we have the target directory, lets copy the file over.
		    // File (or directory) to be moved 
		    File file = new File(filepath); 
		    // Destination directory 
		    File dir = new File(targetDirName); 
		    // Move file to new directory 
		    boolean success = file.renameTo(new File(dir, file.getName())); 
		    
			// if the user requested that we add this to an existing doc, lets add it
			// if the user requested that we create a new doc, lets do that.
		    // since attachmenttype excelVersionComments are really excel files, lets change it here.
		    if (attachmentType.equals("excelVersionComments")){
		    	attachmentType = "excel";
		    }
			String targetFilePath = targetDirName + File.separator + file.getName();
			if (tDCSAction.equals("new")){
				String uniqueTDCSFullTag = this.getServletContext().getInitParameter("uniqueTDCSFullTag");
				tDCSDocument = TDCSUtil.createNewTDCSDocument(uniqueTDCSFullTag, project.getProjectId() , folderId,title,
				description,attachmentType,targetFilePath, user, databaseType);
				
				PrintWriter out = response.getWriter();
			    out.println("<div style='float:right'>" +
			    		" 		<a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> " +
			    		"	</div>" +
			    		"<span class='normalText'> A new document <b>"+ tDCSDocument.getFullTag() + "</b>" + 
			    		"  has been added to the TraceCloud Document Control System. You can access it at  " +
			    		" 	<a href='#' onClick='displayTDCSHome()'><img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> " +
			    		" &nbsp;&nbsp;</span>");
			    out.close();
				return;	
			}
			else if (tDCSAction.equals("existing")){
				TDCSUtil.updateExistingTDCSDocument(tDCSDocument, project.getProjectId() , folderId,
				description,attachmentType,targetFilePath, user, databaseType);
				
				// since we have added a new version, lets refresh the tDCSDocument object.
				tDCSDocument = new TDCSDocument(tDCSDocument.getDocumentId(), databaseType);
				PrintWriter out = response.getWriter();
			    out.println("<div style='float:right'>" +
			    		" 		<a href='#' onClick='document.getElementById(\"emailAttachmentDiv\").style.display=\"none\"'>Close</a> " +
			    		"	</div>" +
			    		"<span class='normalText'> A new version <b>"+ tDCSDocument.getCurrentVersionNumber()+
			    		"</b> has been added to the document <b>"+ tDCSDocument.getFullTag() + "</b>" + 
			    		"  in the TraceCloud Document Control System. You can access it at  " +
			    		" 	<a href='#' onClick='displayTDCSHome()'><img src='/GloreeJava2/images/database_refresh16.png'> TDCS</a> " +
			    		" &nbsp;&nbsp;</span>");
			    out.close();
				return;
			}
		}		
	}
	
	// This method uses the Apache POI module to print out XLS files.
    private String exportListReportToExcel (HttpServletRequest request,
            HttpServletResponse response, Project project, User user, String exportType, String databaseType) 
    		throws ServletException, IOException {
    	String filename = "";
    	
		// Get the session. It should have the last View Report in memory.
    	HttpSession session = request.getSession(true);
    	int folderId = Integer.parseInt(request.getParameter("folderId"));
    	Folder folder = new Folder(folderId);
    	RequirementType requirementType = new RequirementType(folder.getRequirementTypeId());
        ArrayList reportArrayList = (ArrayList) session.getAttribute("listReportForFolder" + folderId);
        String reportIdString = (String) session.getAttribute("listReportIdStringForFolder" + folderId);
        String reportDefinition = (String) session.getAttribute("listReportDefinitionForFolder" + folderId);
        String standardDisplay = (String) session.getAttribute("listReportStandardDisplay" + folderId );
        String customAttributesDisplay = (String) session.getAttribute("listReportCustomAttributesDisplay" + folderId );
        if (customAttributesDisplay == null ) customAttributesDisplay = "";
		String [] customAttributesToDisplay = customAttributesDisplay.split(",");
		
        try {

        	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();

        	response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" Report " + today + ".xls";
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Expires", "0");
    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    			response.setHeader("Pragma", "public");
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    pendingStyle.setWrapText(true);
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    approvedStyle.setWrapText(true);
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    rejectedStyle.setWrapText(true);
    	    

    	    // lets pick the cell styles for dangling / orphan / dangling and orphan
    		HSSFCellStyle danglingStyle = wb.createCellStyle();
    		danglingStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
    	    danglingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    danglingStyle.setWrapText(true);
    	    
    		HSSFCellStyle orphanStyle = wb.createCellStyle();
    		orphanStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
    	    orphanStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    orphanStyle.setWrapText(true);
    	    
    		HSSFCellStyle danglingAndOrphanStyle = wb.createCellStyle();
    		danglingAndOrphanStyle.setFillForegroundColor(HSSFColor.RED.index);
    	    danglingAndOrphanStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    danglingAndOrphanStyle.setWrapText(true);
    	    
    	    // lets pick the cell styles for pending / pass / fail
    		HSSFCellStyle testPendingStyle = wb.createCellStyle();
    		testPendingStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
    		testPendingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testPendingStyle.setWrapText(true);
    	    
    		HSSFCellStyle testPassStyle = wb.createCellStyle();
    		testPassStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
    		testPassStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testPassStyle.setWrapText(true);
    	    
    		HSSFCellStyle testFailStyle = wb.createCellStyle();
    		testFailStyle.setFillForegroundColor(HSSFColor.RED.index);
    		testFailStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testFailStyle.setWrapText(true);
    	    
    	    
    		HSSFCellStyle dateStyle = wb.createCellStyle();
    		short dateFormat = createHelper.createDataFormat().getFormat("dd-MMM-yyyy");
    		dateStyle.setDataFormat(dateFormat);
    		
    		
    	    
    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    
    	    
    	    // lets build all the sheets in this file.
    	    HSSFSheet infoSheet  = wb.createSheet("Report Info");
    	    HSSFSheet reportSheet = wb.createSheet("List Report");
	    	String includeRevisionHistory = request.getParameter("includeRevisionHistory");
    		HSSFSheet revisionHistorySheet = null;
    	    HSSFSheet commentsSheet = null;

	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		revisionHistorySheet = wb.createSheet("Requirement Version History");
	    	    commentsSheet = wb.createSheet("Requirement Comments");
	    	}
	    	HSSFSheet dataValidationSheet = wb.createSheet("Data Validation");

	    	
	    	/////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    // lets start on the 5th Row.
    	    int startRow = 5; 
    		HSSFRow row     = infoSheet.createRow((short)startRow++);



    		row     = infoSheet.createRow((short)startRow++);
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Excel Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Excel Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		startRow += 2;
    		
    	
    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    		if ((reportIdString != null) && !(reportIdString == "")) {
    			int reportId = Integer.parseInt(reportIdString);
    			Report report = new Report(reportId);
    			
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Id"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (Integer.toString(report.getReportId())));
    			
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Created By"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getCreatedByEmailId() ));

        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report URL"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
        		cellB.setCellValue(new HSSFRichTextString ( reportURLString ));
			    
        		
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Name"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportName()));
        		
        		
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Description"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportDescription() ));

    		}
    		
    		if ((reportDefinition != null) && !(reportDefinition == "")) {
    			if (reportDefinition.contains(":###")){
    				row     = infoSheet.createRow((short)startRow++);
    				row     = infoSheet.createRow((short)startRow++);
            		cellA = row.createCell(2);
            		cellA.setCellValue(new HSSFRichTextString ("Report Definition"));
            		cellA.setCellStyle(headerStyle);
            		row     = infoSheet.createRow((short)startRow++);
            		
    				String[] elements = reportDefinition.split(":###:");
    				for (String e : elements){
    					
    					try{
    					String[] eDetails = e.split(":--:");
    					
    					String filterName = eDetails[0];
    					String filterDetails = eDetails[1];
    					
    					if (
    						(filterName.equals("projectId"))
    							||
    						(filterName.equals("active"))
    						||
    						(filterName.equals("displayRequirementType"))
    						||
    						(filterName.contains("customA"))
    						||
    						(filterName.contains("sort"))
    						||
    						(filterName.contains("rowsPerPage"))
    						||
    						(filterName.contains("standardDisplay"))
    						)
    						{
    						continue;
    					}
    					if (filterName.equals("folderId")){
    						filterName = "Folder";
    						filterDetails = folder.getFolderPath();
    					}
    					if (filterName.equals("danglingSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Dangling";
    						filterDetails = "True";
    					}
    					if (filterName.equals("orphanSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Orphan";
    						filterDetails = "True";
    					}
    					if (filterName.equals("completedSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
							filterName = "Completed";
    						filterDetails = "True";
    					}
    					if (filterName.equals("incompleteSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "InComplete";
    						filterDetails = "True";
    					}
    					if (filterName.equals("includeSubFoldersSearch")){
    						if (filterDetails.equals("no")){
    							continue;
    						}
    						filterName = "Include Sub Folders";
    						filterDetails = "True";
    						
    						
    					}
    					if (filterName.equals("suspectUpStreamSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Suspects UpStream";
    						filterDetails = "True";
    					}
    					if (filterName.equals("suspectDownStreamSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Suspects DownStream";
    						filterDetails = "True";
    					}
    					if (filterName.equals("lockedSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Locked";
    						filterDetails = "True";
    					}
    					
    					if (filterName.equals("nameSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Name";
    					}
    					if (filterName.equals("descriptionSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Description";
    					}
    					if (filterName.equals("ownerSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Owner";
    					}
    					
    					if (filterName.equals("inRelease")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						Requirement requirement = new Requirement(Integer.parseInt(filterDetails),"mySQL");
    						filterName = "In Relase ";
    						filterDetails = requirement.getRequirementFullTag() + ":" + requirement.getRequirementName() ;
    					}
    					
    					if (filterName.equals("inRTBaselineSearch")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						RTBaseline rTBaseline = new RTBaseline(Integer.parseInt(filterDetails));
    						
    						filterName = "In Baseline ";
    						filterDetails = rTBaseline.getBaselineName() ;
    					}
    					
    					if (filterName.equals("changedAfterRTBaselineSearch")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						RTBaseline rTBaseline = new RTBaseline(Integer.parseInt(filterDetails));
    						filterName = "Changed After Baseline ";
    						filterDetails = rTBaseline.getBaselineName() ;
    					}
    					
    					row     = infoSheet.createRow((short)startRow++);
    	        		cellA = row.createCell(3);
    	        		cellA.setCellValue(new HSSFRichTextString (filterName));
    	        		cellA.setCellStyle(headerStyle);
    	        		row.createCell(4).setCellStyle(headerStyle);
    	        		cellB = row.createCell(5);
    	        		cellB.setCellValue(new HSSFRichTextString (filterDetails)); 
    					}
    					catch (Exception ex){
    						ex.printStackTrace();
    					}
    				}
    				
    				// custom attributes


					row     = infoSheet.createRow((short)startRow++);
					row     = infoSheet.createRow((short)startRow++);
	        		cellA = row.createCell(3);
	        		cellA.setCellValue("Custom Attribute Filters ");
	        		cellA.setCellStyle(headerStyle);
	        		row.createCell(4).setCellStyle(headerStyle);
	        		cellB = row.createCell(5);
	        		cellB.setCellValue(" ");
					row     = infoSheet.createRow((short)startRow++);
	        		
    				for (String e : elements){
    					
    					try{
    					String[] eDetails = e.split(":--:");
    					
    					String filterName = eDetails[0];
    					String filterDetails = eDetails[1];
    					
    					
    					if (!(filterName.contains("customA"))){
    						continue;
    					}
    				
    					
						filterName = filterName.replace("customA", "");
						RTAttribute rTAttribute = new RTAttribute(Integer.parseInt(filterName));
						filterName = rTAttribute.getAttributeName();
						filterDetails = filterDetails.replace(":##:", ", ");
					
    					row     = infoSheet.createRow((short)startRow++);
    	        		cellA = row.createCell(3);
    	        		cellA.setCellValue(new HSSFRichTextString (filterName));
    	        		cellA.setCellStyle(headerStyle);
    	        		row.createCell(4).setCellStyle(headerStyle);
    	        		cellB = row.createCell(5);
    	        		cellB.setCellValue(new HSSFRichTextString (filterDetails)); 
    					}
    					catch (Exception ex){
    						ex.printStackTrace();
    					}
    				}
        		  
    			}
    		}
    		
    		// project Info.
    		startRow += 4;
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		
    			
    	    // lets build the list report.    	    


            
            Iterator i = reportArrayList.iterator();
            
            int j = 0;
            
            // since we will need to uda (to print the datavalidation) outside the while loop
            // lets define it outside
            String [] attribs = null;
	    	while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
	    		String uda = r.getUserDefinedAttributes();
				attribs = uda.split(":##:");
				HashMap attribsHashMap = ProjectUtil.getHashMapUDA(r.getUserDefinedAttributes());
				
	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		row     = reportSheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Tag          "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		// lets add a comment to the tag describing the color coding.
	        		HSSFPatriarch patr = reportSheet.createDrawingPatriarch();
	        		HSSFRichTextString textString = createHelper.createRichTextString("Legend \n\n" +
	        				"Orphan (No UpStream Requirements) : Background Color Yellow\n" +
	        				"Dangling (No DownStream Requirements) : Background Color Orange\n" +
	        				"Orphan AND Dangling (No UpStream or DownStream Requirements) : Background Color Red\n\n" );
	        		HSSFClientAnchor anchor = createHelper.createClientAnchor();
	        		anchor.setAnchor((short)1, 1, 1,  1, (short)5,5, 5, 5);
	        		HSSFComment comment1 = patr.createComment( anchor );
	        		comment1.setString(textString);
	        		cell.setCellComment(comment1);
	        		
	        	    
	        	    
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                          "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Version"));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		if (standardDisplay.contains("description")) {
				        cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Description (Without formatting Do not use for upload)                                                                                                        "));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("comments")) {
				        cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Comments                                                                                                        "));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		
	        		if (standardDisplay.contains("owner")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("testingStatus")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing Status                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        	}

	        		
	        		if (standardDisplay.contains("status")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval Status                "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		
	        		if (standardDisplay.contains("priority")) {
	            	    // lets add some data validation to this reportSheet.
	        			CellRangeAddressList addressList = new CellRangeAddressList(0, 10000, cellNum+1, cellNum+1);
	        			DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(new String[]{"High", "Medium", "Low"});
	        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			reportSheet.addValidationData(dataValidation);


		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Priority          "));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		if (standardDisplay.contains("percentComplete")) {
	        			// lets add some data validation to this reportSheet.
	        			CellRangeAddressList addressList = new CellRangeAddressList(0, 10000, cellNum+1, cellNum+1);
	        			DVConstraint dvConstraint =  DVConstraint.createNumericConstraint(
	        				    DVConstraint.ValidationType.INTEGER,
	        				    DVConstraint.OperatorType.BETWEEN, "0", "100");
	        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			dataValidation.setErrorStyle(HSSFDataValidation.ErrorStyle.STOP);
	        			dataValidation.createErrorBox("Box Title", "Please ensure that you enter a number between 0 and 100");

	        			reportSheet.addValidationData(dataValidation);
	        			
	        			
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approved Dt                            "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		if (standardDisplay.contains("lockedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Locked By                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("pendingBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Pending Approval By                "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("approvedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approved By                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("rejectedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Rejected By                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("traceTo")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace To                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace To - With Details                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		
		        		
		        		
	        		}
	        		
	        		if (standardDisplay.contains("traceFrom")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace From                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace From -  With Details                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		
	        		}
	        		
	        		if (standardDisplay.contains("externalURL")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		if (standardDisplay.contains("folderPath")) {
	            	    // lets add some data validation to this reportSheet.
	        			CellRangeAddressList addressList = new CellRangeAddressList(0, 10000, cellNum+1, cellNum+1);
	        			HSSFName namedRange = wb.createName();
	        			namedRange.setNameName("folderPathDataValidation");
	        			namedRange.setRefersToFormula("'Data Validation'!$A$2:$A$"+ (project.getMyFolders().size() +1)  );
	        			DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint("folderPathDataValidation");
        			  
	        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			reportSheet.addValidationData(dataValidation);

		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Folder Path                                             "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("baselines")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Baselines                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		if (standardDisplay.contains("createdBy")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Created By                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("createdDate")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		
	        		if (standardDisplay.contains("lastModifiedBy")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Last Modified By                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		
	        		if (standardDisplay.contains("lastModifiedDate")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Last Modified Date                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}

	        		if (standardDisplay.contains("attachments")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Attachments                         "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		}
	        		
	        		// now to print the custom labels.
	        		int dataValidationCount = (project.getMyFolders().size()) + 3;
	        		
	        		// for each of te customAttribsDisplay, lets print the cell values

					for (int k=0; k<customAttributesToDisplay.length; k++) {
						String thisAttribLabel = customAttributesToDisplay[k];
						// lets get the attribute object.
						// occasionally attrib[0] can be empty. For example if this req type does not have
						// custom attribs defined and the first req has uda = ""
						if ((thisAttribLabel == null) || (thisAttribLabel.trim().equals(""))){
							continue;
						}
						RTAttribute rTAttribute = new RTAttribute(folder.getRequirementTypeId(), thisAttribLabel);
						if (rTAttribute.getAttributeType().equals("Drop Down")){
							// NOTE	 : VERY IMPORTANT : WE ARE STORING ALL THE CUSTOM ATTRIBUTES VALUES IN 
							// A COLUMN IN A TAB CALLED DATA VALIDATION. SINCE WE DON'T KNOW HOW MANY CUSTOM
							// ATTRIBS THERE ARE AND HOW MANY VALUES THEY CAN HOLD, WE WARE FORCED TO USE
							// EXCEL CELLS TO DO THE VALIDATION
							// TO MAKE THE LONG STORY SHORT, MAKE SURE THAT YOU USE THE SAME LOGIC TO POPULATE
							// TEH DATA VALIDATION PAGE WITH VALUES AS YOU ARE USING TO READ THOSE VALUES BACK
							// FOR SETTING THE VALIDATION. OTHERWISE YOU ARE SCREWED.......
							// since this is a drop down, the drop down options will have a value.
							int countOfAttributeOptions = 0;
							String attributeOptionsString = rTAttribute.getAttributeDropDownOptions();
							if (attributeOptionsString != null){
								String [] attributeOptions = attributeOptionsString.split(",");
								countOfAttributeOptions = attributeOptions.length;
							}
							
							// now we know how many options are there , lets set the beginning and ending set
							// for the data validation for this attribute type.
							int validationStart = dataValidationCount;
							int validationEnd = dataValidationCount + countOfAttributeOptions;
							dataValidationCount += countOfAttributeOptions + 3;
							// this is a drop down type attribute.
							// lets add some data validation to this reportSheet.
		        			CellRangeAddressList addressList = new CellRangeAddressList(0, 10000, cellNum+1, cellNum+1);
		        			HSSFName namedRange = wb.createName();
		        			String rangeName = rTAttribute.getAttributeName() + "DataValidation";
		        			rangeName = rangeName.trim().replace(" ", "_");
		        			rangeName = rangeName.trim().replace("-", "_");
		        			rangeName = rangeName.replace("?", "");
		        			
		        			rangeName = rangeName.replace("(", "");
		        			rangeName = rangeName.replace(")", "");
		        			
		        			
		        			namedRange.setNameName(rangeName);
		        			namedRange.setRefersToFormula("'Data Validation'!$A$"+ validationStart +":$A$"+ validationEnd  );
		        			DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(rangeName);
						
		        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
		        			dataValidation.setSuppressDropDownArrow(false);
		        			reportSheet.addValidationData(dataValidation);
						}
						if (customAttributesDisplay.contains(thisAttribLabel) ) {
							// lets display the custom attribute based on the display field value
							
							String attributeName = thisAttribLabel;
							
							if (attributeName.contains("?")){
								attributeName = attributeName.replace("?", " ");
							}
							cell = row.createCell(++cellNum); 
			        		cell.setCellValue(new HSSFRichTextString (attributeName + "             "));
    		        		cell.setCellStyle(headerStyle);
			        		reportSheet.autoSizeColumn(column++);
						}
					}
							
	    		}

	    		// print the data rows now.
	    		row     = reportSheet.createRow(j);
			    
	    		// Create a cell and put a value in it.
	    		// we have decided to not make cell0 a hyperlink to source because Excel 97-03 was having trouble
	    		// saving these files.
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

	    		int cellNum = 0;
			  
			    HSSFCell cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
			    String requirementTraceTo = r.getRequirementTraceTo();
			    String requirementTraceFrom = r.getRequirementTraceFrom();
			    
			    if (requirementType.getRequirementTypeCanBeOrphan() == 1){
				    if ((requirementTraceTo == null)	||  (requirementTraceTo.trim().equals(""))){
				    	// req type can be orphan and the req is orphan
				    	cell.setCellStyle(orphanStyle);
				    }
			    }
			    if (requirementType.getRequirementTypeCanBeDangling() == 1) {
				    if ((requirementTraceFrom == null)	||  (requirementTraceFrom.trim().equals(""))){
				    	// req type can be dangling and the req is dangling
				    	cell.setCellStyle(danglingStyle);
				    }		
			    }
			    if ((requirementType.getRequirementTypeCanBeDangling() == 1)
			    		&&
			    	(requirementType.getRequirementTypeCanBeOrphan() == 1)
			    		){
				    if (
				    		((requirementTraceTo == null)	||  (requirementTraceTo.equals("")))
				    		&&
				    		((requirementTraceFrom == null)	||  (requirementTraceFrom.equals("")))
				    		){
				    	// req type can be dang and orph and req is dang and orphan
				    	cell.setCellStyle(danglingAndOrphanStyle);
				    }
			    }
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (url));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(r.getVersion())));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementName()));
			    cell.setCellStyle(wrappedStyle);
			    
			    if (standardDisplay.contains("description")) {
					cell = row.createCell(++cellNum);
				    cell.setCellValue(new HSSFRichTextString (r.getRequirementDescriptionNoHTMLWithJSoup() ));
				    cell.setCellStyle(wrappedStyle);
			    }
			    
			    if (standardDisplay.contains("comments")) {
					cell = row.createCell(++cellNum);
				    cell.setCellValue(new HSSFRichTextString (r.getRequirementCommentsString(databaseType)));
				    cell.setCellStyle(wrappedStyle);
			    }
			    
			    if (standardDisplay.contains("owner")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementOwner()));
			    	cell.setCellStyle(wrappedStyle);
			    }


			    if (standardDisplay.contains("testingStatus")) {
			    	HSSFCell testingStatusCell = row.createCell(++cellNum);
	        	    testingStatusCell.setCellValue(new HSSFRichTextString (r.getTestingStatus() ));
			    	
	        	    
			    	if (r.getTestingStatus().equals("Pending")){
			    		testingStatusCell.setCellStyle(testPendingStyle);
    	        	    
    	        	}
        			if (r.getTestingStatus().equals("Pass")){
        				testingStatusCell.setCellStyle(testPassStyle);
    	        	   
		        	}
        			if (r.getTestingStatus().equals("Fail")){
        				testingStatusCell.setCellStyle(testFailStyle);
    	        	   
		        	}
        			
        			
        		}
			    
			    
        		if (standardDisplay.contains("status")) {			    
			 		// lets see if this requirement is in a folder that is enabled for approval work flow
			 		String folderIdApprovalCheck = "#" + r.getFolderId() + "#";
			 		if (!(foldersEnabledForApprovalWorkFlow.contains(folderIdApprovalCheck))){
	        			HSSFCell statusCell = row.createCell(++cellNum);
		        	    statusCell.setCellValue(new HSSFRichTextString ("Not Applicable"));
		        	    // lets set the status Cell color based on its value
		        	    HSSFCellStyle statusStyle = wb.createCellStyle();
		        		statusStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					    statusCell.setCellStyle(statusStyle);
			 		}
			 		else {
	
	        			HSSFCell statusCell = row.createCell(++cellNum);
		        	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
		
		        	    // lets set the status Cell color based on its value
		        	    HSSFCellStyle statusStyle = wb.createCellStyle();
					    if (r.getApprovalStatus().equals("Draft")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("Approved")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("Rejected")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    
					    
					    statusCell.setCellStyle(statusStyle);
			 		}
        		}
        		if (standardDisplay.contains("priority")) {
        			cell = row.createCell(++cellNum);
        			cell.setCellValue(new HSSFRichTextString (r.getRequirementPriority()));
        			cell.setCellStyle(wrappedStyle);
    			    
        		}
        		if (standardDisplay.contains("percentComplete")) {
        			cell = row.createCell(++cellNum);
        			cell.setCellValue(new HSSFRichTextString (r.getRequirementPctComplete() + "" ));
        			cell.setCellStyle(wrappedStyle);
    			    
        		}
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getApprovedByAllDt() ));
			    cell.setCellStyle(wrappedStyle);
			    
        		if (standardDisplay.contains("lockedBy")) {
        			cell = row.createCell(++cellNum);
        			cell.setCellValue(new HSSFRichTextString (r.getRequirementLockedBy()));
        			cell.setCellStyle(pendingStyle);
    			    
        		}
			    
			    
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}
			    	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }
			    
			    if (standardDisplay.contains("pendingBy")) {
			    	HSSFCell  pendingCell = row.createCell(++cellNum);
			    	pendingCell.setCellValue(new HSSFRichTextString (pendingApprovers ));
			    	pendingCell.setCellStyle(pendingStyle);
			    }
			    
			    if (standardDisplay.contains("approvedBy")) {
			    	HSSFCell  approvedCell = row.createCell(++cellNum);
			    	approvedCell.setCellValue(new HSSFRichTextString (approvedApprovers ));
			    	approvedCell.setCellStyle(approvedStyle);
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
			    	HSSFCell  rejectedCell = row.createCell(++cellNum);
			    	rejectedCell.setCellValue(new HSSFRichTextString (rejectedApprovers ));
			    	rejectedCell.setCellStyle(rejectedStyle);
			    }
	
			    if (standardDisplay.contains("traceTo")) {
			    	
			    	String[] traces = r.getRequirementTraceTo().split(",");
		    		String coloredTraceTo = "";
		    		for (int l=0;l<traces.length;l++){
		    			try {
							// if you can get the requirement object, then print more details. If you hit exception print what you have
							String tempReqName = RequirementUtil.getRequirementName(traces[l].replace("(s)",""), project.getProjectId());
							if (
									(!(tempReqName.contains("null")))
									&&
									(tempReqName.length() > 0)
								)
									{
								coloredTraceTo +=  traces[l] + " :"+ tempReqName +"\n\n ";
							}
			    		}
						catch (Exception e){
							// if you run into exception, do the simple way. 
							e.printStackTrace();
							coloredTraceTo +=  traces[l] +" \n ";
						}
		    		}
		    		
		    		String traceTo = r.getRequirementTraceTo();
		    		if (traceTo.contains("(s")){
		    			traceTo = traceTo.replace("(s)", "");
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (traceTo));
			    	// since these can get very long we aren't wrapping them around.
			    	cell.setCellStyle(wrappedStyle);
			    	
			    	
			    			// since an excel cell can not hold more than 30,000 chars  then we only show the req names. 
		    		
			    	if (coloredTraceTo.length() > 10000){
		    			coloredTraceTo = traceTo;
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (coloredTraceTo ));
			    	// since these can get very long we aren't wrapping them around.
			    	cell.setCellStyle(wrappedStyle);
			    	
			    	
			    }
			    if (standardDisplay.contains("traceFrom")) {
			    	String[] traces = r.getRequirementTraceFrom().split(",");
		    		String coloredTraceFrom = "";
		    		Requirement traceFromReq = null;
		    		for (int l=0;l<traces.length;l++){
		    			try {
		    				
							// if you can get the requirement object, then print more details. If you hit exception print what you have
							
							String tempReqName = RequirementUtil.getRequirementName(traces[l].replace("(s)",""), project.getProjectId());
							if (
									(!(tempReqName.contains("null")))
									&&
									(tempReqName.length() > 0)
								){
								coloredTraceFrom +=  traces[l] + " :"+ tempReqName + "\n\n ";
							}
							
			    		}
						catch (Exception e){
							// if you run into exception, do the simple way. 
							e.printStackTrace();
							coloredTraceFrom +=  traces[l] +" \n ";
						}
		    		}
		    		
		    		
		    		String traceFrom = r.getRequirementTraceFrom();
		    		if (traceFrom.contains("(s")){
		    			traceFrom = traceFrom.replace("(s)", "");
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (traceFrom));
			    	// since these can get pretty long, we aren't wrapping them around
			    	cell.setCellStyle(wrappedStyle);
			    	
			    	

			    	// since an excel cell can not hold more than 30,000 chars  then we only show the req names. 
		    		
			    	if (coloredTraceFrom.length() > 10000){
		    			coloredTraceFrom = traceFrom;
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (coloredTraceFrom ));
			    	// since these can get pretty long, we aren't wrapping them around
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    
			    if (standardDisplay.contains("externalURL")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementExternalUrl() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("folderPath")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getFolderPath() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("baselines")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementBaselineString(databaseType) ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }

			    
			    if (standardDisplay.contains("createdBy")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getCreatedBy() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }

			    if (standardDisplay.contains("createdDate")) {
			    	
			    	
			    	
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getCreatedDt() ));
			    	cell.setCellStyle(dateStyle);
				    
			    }


			    if (standardDisplay.contains("lastModifiedBy")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getLastModifiedBy() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("lastModifiedDate")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getLastModifiedDt() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    if (standardDisplay.contains("attachments")) {
			    	String attachmentString = "";
					ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							
							String attachmentURL = ProjectUtil.getRequirementAttachmentURL(request, r.getRequirementId(), attachment.getRequirementAttachmentId()); 
							attachmentString += "URL to file : " + attachmentURL + "\n";
							attachmentString += "Title :  " + attachment.getTitle()  + " \n";
						}															
					} 
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (attachmentString ));
			    	cell.setCellStyle(wrappedStyle);
				    
			    }
			    
			    // now to print the custom values.
			    for (int k=0; k<customAttributesToDisplay.length; k++) {
					String thisAttribLabel = customAttributesToDisplay[k];
					String attribValue = (String) attribsHashMap.get(thisAttribLabel.trim());
					
					cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString (attribValue));
	        		cell.setCellStyle(wrappedStyle);
				
			    }
				
			}
	    	

	    	
	    	
	    	
	    	
	    	
	    	
	    	// lets see if the user  has chosen to print out the Revision History.
	    	// if yes, we will need to add another tab to the excel file and include revision history of every
	    	// requirement in this output.
	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		// lets add a new revisionHistorySheet to the excel output.
	    	    // for the first row, print the header and user defined columns headers. etc..

	    	    int rowNum = 0;
        		row     = revisionHistorySheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		int cellNum = 0;
        		int column = 0;
        		
        		HSSFCell cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approvers                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approval Status "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Priority"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        	
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace To                           "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace From                         "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created By                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Custom Attributes                            "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
	    	    i = reportArrayList.iterator();
	            
		    	while ( i.hasNext() ) {
		    		Requirement r = (Requirement) i.next();


		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement versions.
		    		ArrayList requirementVersions = r.getRequirementVersions( databaseType);
		    		Iterator m = requirementVersions.iterator();
		    		
		    		while (m.hasNext()) {
		    			RequirementVersion v = (RequirementVersion) m.next();
		    			// for each req version, lets print a row.
			    		// print the data rows now.
			    		row     = revisionHistorySheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionName()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionDescriptionNoHTML()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionApprovers() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionStatus() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionPriority() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionOwner() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersionPctComplete())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionExternalURL() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceTo() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceFrom() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedBy()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedDt()));
					    cell.setCellStyle(wrappedStyle);
					    
					
					    String uDA = v.getVersionUserDefinedAttributes();
					    String formattedUDA = "";
					    if ((uDA != null) && (uDA.contains(":##:"))) {
					    	String [] uDAs = uDA.split(":##:") ;
					    	
					    	for (int u=0; u<uDAs.length; u++) {
					    		formattedUDA += uDAs[u] + "\n";
					    	}
					    }
					    cell = row.createCell(++cellNum);
			    		cell.setCellValue(new HSSFRichTextString (formattedUDA));
			    		cell.setCellStyle(wrappedStyle);
			    		
					    
		    		}
		    	}
		    	

		    	
		    	
	    		// for the first row, print the header and user defined columns headers. etc..

	    	    rowNum = 0;
        		row     = commentsSheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		cellNum = 0;
        		column = 0;
        		
        		cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Commenter                                    "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Comment                                                                                                           "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		        

        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Date                              "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        			    
	    	    
	    	    i = reportArrayList.iterator();
	            
		    	while ( i.hasNext() ) {
		    		Requirement r = (Requirement) i.next();


		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement comments.
		    		ArrayList requirementComments = r.getRequirementComments( databaseType);
		    		Iterator m = requirementComments.iterator();
		    		
		    		while (m.hasNext()) {
		    			Comment c = (Comment) m.next();
		    			// for each req comment, lets print a row.
			    		// print the data rows now.
			    		row     = commentsSheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(c.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommenterEmailId()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getComment_note()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommentDate()));
					    cell.setCellStyle(wrappedStyle);
					    
		    		}
		    	}
	    	}

	    	
    	    // lets build a sheet to hold data validation rows.
	    	int rowNum = 0;
    			
			ArrayList folders = project.getMyFolders();
			Iterator f = folders.iterator();
			
			// folder paths
			row     = dataValidationSheet.createRow((short)(rowNum++));	
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString ("Valid Folder Paths                                                             "));
    		cell.setCellStyle(headerStyle);
    		dataValidationSheet.autoSizeColumn(0);

			while (f.hasNext()){
				Folder tempFolder = (Folder) f.next();
				row     = dataValidationSheet.createRow((short)(rowNum++));	
				cell = row.createCell(0);
				cell.setCellValue(new HSSFRichTextString (tempFolder.getFolderPath()));
			}
	    	
    		// now to print the custom labels.
    		int dataValidationCount = (project.getMyFolders().size()) + 2;
			for (int k=0; k<attribs.length; k++) {
				String[] attrib = attribs[k].split(":#:");
				// lets get the attribute object.
				// occasionally attrib[0] can be empty. For example if this req type does not have
				// custom attribs defined and the first req has uda = ""
				if ((attrib[0] == null) || (attrib[0].trim().equals(""))){
					continue;
				}
				RTAttribute rTAttribute = new RTAttribute(folder.getRequirementTypeId(), attrib[0].trim());
				
				
				if (customAttributesDisplay.contains(attrib[0]) ) {
					// lets display the custom attribute based on the display field value
					if (rTAttribute.getAttributeType().equals("Drop Down")){
						// NOTE	 : VERY IMPORTANT : WE ARE STORING ALL THE CUSTOM ATTRIBUTES VALUES IN 
						// A COLUMN IN A TAB CALLED DATA VALIDATION. SINCE WE DON'T KNOW HOW MANY CUSTOM
						// ATTRIBS THERE ARE AND HOW MANY VALUES THEY CAN HOLD, WE WARE FORCED TO USE
						// EXCEL CELLS TO DO THE VALIDATION
						// TO MAKE THE LONG STORY SHORT, MAKE SURE THAT YOU USE THE SAME LOGIC TO POPULATE
						// TEH DATA VALIDATION PAGE WITH VALUES AS YOU ARE USING TO READ THOSE VALUES BACK
						// FOR SETTING THE VALIDATION. OTHERWISE YOU ARE SCREWED.......
						// since this is a drop down, the drop down options will have a value.
						String attributeOptionsString = rTAttribute.getAttributeDropDownOptions();
						String [] attributeOptions = null;
						if (attributeOptionsString != null){
							attributeOptions = attributeOptionsString.split(",");
						}
						
						// now we know how many options are there , lets set the beginning and ending set
						// for the data validation for this attribute type.
				
						row     = dataValidationSheet.createRow((short)(dataValidationCount -1));	
						cell = row.createCell(0);
						cell.setCellValue(new HSSFRichTextString ( rTAttribute.getAttributeName()));
			    		cell.setCellStyle(headerStyle);
			    		dataValidationSheet.autoSizeColumn(0);

			    		int parentAttributeId = rTAttribute.getParentAttributeId();
						for (int m=0 ; m<attributeOptions.length; m++){
							String optionName = attributeOptions[m];
							if (parentAttributeId > 0){
								// this is a child attribute and the attribute values are like Porsche:911,Porsche:Panamera,Porsche:Carrera
								// where Porsche is the parent attribute value and 911,Panamera and Carrera are the potential child values.
								// so we need to strip out the first portion .
								if ((optionName != null) && (optionName.contains(":"))){
									String [] oN = optionName.split(":");
									optionName = oN[1];
								}
							}
							row     = dataValidationSheet.createRow((short)(dataValidationCount++));	
							cell = row.createCell(0);
							cell.setCellValue(new HSSFRichTextString (optionName));
						}
						dataValidationCount += 3;
					}
				}
			}

					
					
            // Write the output
	    	if (exportType.equals("HTML")){
	    		OutputStream out = response.getOutputStream();
	            wb.write(out);
	            out.close();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
	    		}

	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
	    		}

	    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
	    	}
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    
    private String exportSearchReportToExcel (HttpServletRequest request,
            HttpServletResponse response, User user, Project project,String exportType, 
            String databaseType, SecurityProfile securityProfile, String searchString, String searchProjects, 
            int targetRequirementTypeId, int targetFolderId) 
    		throws ServletException, IOException {
    	String filename = "";
    	
    	
    	System.out.println("srt ckn 2");
		ArrayList requirements = new ArrayList();
		// if this was a 'save request', lets save the report and then display the report saved message. 
		if ((searchString != null) && !(searchString.equals(""))) {
			requirements = ReportUtil.getglobalSearchReport(securityProfile, searchProjects, searchString,
					securityProfile.getUser(), databaseType, project.getProjectId(), targetRequirementTypeId, targetFolderId);

		}
		
		
		// Get the session. It should have the last View Report in memory.
    	
		
        try {

        	System.out.println("srt ckn 3");
        	response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" Report " + today + ".xls";
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Expires", "0");
    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    			response.setHeader("Pragma", "public");
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    pendingStyle.setWrapText(true);
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    approvedStyle.setWrapText(true);
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    rejectedStyle.setWrapText(true);
    	    

    	    // lets pick the cell styles for dangling / orphan / dangling and orphan
    		HSSFCellStyle danglingStyle = wb.createCellStyle();
    		danglingStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
    	    danglingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    danglingStyle.setWrapText(true);
    	    
    		HSSFCellStyle orphanStyle = wb.createCellStyle();
    		orphanStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
    	    orphanStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    orphanStyle.setWrapText(true);
    	    
    		HSSFCellStyle danglingAndOrphanStyle = wb.createCellStyle();
    		danglingAndOrphanStyle.setFillForegroundColor(HSSFColor.RED.index);
    	    danglingAndOrphanStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    danglingAndOrphanStyle.setWrapText(true);
    	    
    	    // lets pick the cell styles for pending / pass / fail
    		HSSFCellStyle testPendingStyle = wb.createCellStyle();
    		testPendingStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
    		testPendingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testPendingStyle.setWrapText(true);
    	    
    		HSSFCellStyle testPassStyle = wb.createCellStyle();
    		testPassStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
    		testPassStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testPassStyle.setWrapText(true);
    	    
    		HSSFCellStyle testFailStyle = wb.createCellStyle();
    		testFailStyle.setFillForegroundColor(HSSFColor.RED.index);
    		testFailStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testFailStyle.setWrapText(true);
    	    
    	    
    		HSSFCellStyle dateStyle = wb.createCellStyle();
    		short dateFormat = createHelper.createDataFormat().getFormat("dd-MMM-yyyy");
    		dateStyle.setDataFormat(dateFormat);
    		
    		
    	    
    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    
    	    
    	    // lets build all the sheets in this file.
    	    HSSFSheet infoSheet  = wb.createSheet("Report Info");
    	    HSSFSheet reportSheet = wb.createSheet("Search Report");
	    	String includeRevisionHistory = "yes";
    		HSSFSheet revisionHistorySheet = null;
    	    HSSFSheet commentsSheet = null;

	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		revisionHistorySheet = wb.createSheet("Requirement Version History");
	    	    commentsSheet = wb.createSheet("Requirement Comments");
	    	}
	    	HSSFSheet dataValidationSheet = wb.createSheet("Data Validation");

	    	
	    	/////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    // lets start on the 5th Row.
    	    int startRow = 5; 
    		HSSFRow row     = infoSheet.createRow((short)startRow++);



    		row     = infoSheet.createRow((short)startRow++);
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Excel Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Excel Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		startRow += 2;
    		
    	
    		
    		
    		// project Info.
    		startRow += 4;
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		
    			
    	    // lets build the list report.    	    


            
            Iterator i = requirements.iterator();
            
            int j = 0;
            
            // since we will need to uda (to print the datavalidation) outside the while loop
            // lets define it outside
            String [] attribs = null;
	    	while ( i.hasNext() ) {
	    		GlobalRequirement gr = (GlobalRequirement) i.next();
				Requirement r = gr.getRequirement();
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))) {
					r.redact();
				}
				
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
	    		String uda = r.getUserDefinedAttributes();
				attribs = uda.split(":##:");
				HashMap attribsHashMap = ProjectUtil.getHashMapUDA(r.getUserDefinedAttributes());
				
	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		row     = reportSheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Tag          "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		// lets add a comment to the tag describing the color coding.
	        		HSSFPatriarch patr = reportSheet.createDrawingPatriarch();
	        		HSSFRichTextString textString = createHelper.createRichTextString("Legend \n\n" +
	        				"Orphan (No UpStream Requirements) : Background Color Yellow\n" +
	        				"Dangling (No DownStream Requirements) : Background Color Orange\n" +
	        				"Orphan AND Dangling (No UpStream or DownStream Requirements) : Background Color Red\n\n" );
	        		HSSFClientAnchor anchor = createHelper.createClientAnchor();
	        		anchor.setAnchor((short)1, 1, 1,  1, (short)5,5, 5, 5);
	        		HSSFComment comment1 = patr.createComment( anchor );
	        		comment1.setString(textString);
	        		cell.setCellComment(comment1);
	        		
	        	    
	        	    
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                          "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Version"));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		
				        cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Description (Without formatting Do not use for upload)                                                                                                        "));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Comments                                                                                                        "));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing Status                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        	
	        		
	            	    // lets add some data validation to this reportSheet.
	        			CellRangeAddressList addressList = new CellRangeAddressList(0, 10000, cellNum+1, cellNum+1);
	        			DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(new String[]{"High", "Medium", "Low"});
	        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			reportSheet.addValidationData(dataValidation);


		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Priority          "));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
	        			// lets add some data validation to this reportSheet.
	        			addressList = new CellRangeAddressList(0, 10000, cellNum+1, cellNum+1);
	        			dvConstraint =  DVConstraint.createNumericConstraint(
	        				    DVConstraint.ValidationType.INTEGER,
	        				    DVConstraint.OperatorType.BETWEEN, "0", "100");
	        			dataValidation = new HSSFDataValidation(addressList, dvConstraint);
	        			dataValidation.setSuppressDropDownArrow(false);
	        			dataValidation.setErrorStyle(HSSFDataValidation.ErrorStyle.STOP);
	        			dataValidation.createErrorBox("Box Title", "Please ensure that you enter a number between 0 and 100");

	        			reportSheet.addValidationData(dataValidation);
	        			
	        			
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approved Dt                            "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(column++);
	        		
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Locked By                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Pending Approval By                "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approved By                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Rejected By                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace To                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace To - With Details                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		
		        		
		        		
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace From                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace From -  With Details                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
		        		
		        		
		        		
	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                        "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
	            	    

		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Folder Path                                             "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        			        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Baselines                          "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        			        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Created By                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        			        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Last Modified By                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        			        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Last Modified Date                              "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Attachments                         "));
    	        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(column++);
	        		
	        		// now to print the custom labels.
	        		int dataValidationCount = (project.getMyFolders().size()) + 3;
	        		
	        		// for each of te customAttribsDisplay, lets print the cell values

	        		/*
					for (int k=0; k<customAttributesToDisplay.length; k++) {
						String thisAttribLabel = customAttributesToDisplay[k];
						// lets get the attribute object.
						// occasionally attrib[0] can be empty. For example if this req type does not have
						// custom attribs defined and the first req has uda = ""
						if ((thisAttribLabel == null) || (thisAttribLabel.trim().equals(""))){
							continue;
						}
						RTAttribute rTAttribute = new RTAttribute(folder.getRequirementTypeId(), thisAttribLabel);
						if (rTAttribute.getAttributeType().equals("Drop Down")){
							// NOTE	 : VERY IMPORTANT : WE ARE STORING ALL THE CUSTOM ATTRIBUTES VALUES IN 
							// A COLUMN IN A TAB CALLED DATA VALIDATION. SINCE WE DON'T KNOW HOW MANY CUSTOM
							// ATTRIBS THERE ARE AND HOW MANY VALUES THEY CAN HOLD, WE WARE FORCED TO USE
							// EXCEL CELLS TO DO THE VALIDATION
							// TO MAKE THE LONG STORY SHORT, MAKE SURE THAT YOU USE THE SAME LOGIC TO POPULATE
							// TEH DATA VALIDATION PAGE WITH VALUES AS YOU ARE USING TO READ THOSE VALUES BACK
							// FOR SETTING THE VALIDATION. OTHERWISE YOU ARE SCREWED.......
							// since this is a drop down, the drop down options will have a value.
							int countOfAttributeOptions = 0;
							String attributeOptionsString = rTAttribute.getAttributeDropDownOptions();
							if (attributeOptionsString != null){
								String [] attributeOptions = attributeOptionsString.split(",");
								countOfAttributeOptions = attributeOptions.length;
							}
							
							// now we know how many options are there , lets set the beginning and ending set
							// for the data validation for this attribute type.
							int validationStart = dataValidationCount;
							int validationEnd = dataValidationCount + countOfAttributeOptions;
							dataValidationCount += countOfAttributeOptions + 3;
							// this is a drop down type attribute.
							// lets add some data validation to this reportSheet.
		        			CellRangeAddressList addressList = new CellRangeAddressList(0, 10000, cellNum+1, cellNum+1);
		        			HSSFName namedRange = wb.createName();
		        			String rangeName = rTAttribute.getAttributeName() + "DataValidation";
		        			rangeName = rangeName.trim().replace(" ", "_");
		        			rangeName = rangeName.trim().replace("-", "_");
		        			rangeName = rangeName.replace("?", "");
		        			
		        			rangeName = rangeName.replace("(", "");
		        			rangeName = rangeName.replace(")", "");
		        			
		        			
		        			namedRange.setNameName(rangeName);
		        			namedRange.setRefersToFormula("'Data Validation'!$A$"+ validationStart +":$A$"+ validationEnd  );
		        			DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(rangeName);
						
		        			HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
		        			dataValidation.setSuppressDropDownArrow(false);
		        			reportSheet.addValidationData(dataValidation);
						}
						if (customAttributesDisplay.contains(thisAttribLabel) ) {
							// lets display the custom attribute based on the display field value
							
							String attributeName = thisAttribLabel;
							
							if (attributeName.contains("?")){
								attributeName = attributeName.replace("?", " ");
							}
							cell = row.createCell(++cellNum); 
			        		cell.setCellValue(new HSSFRichTextString (attributeName + "             "));
    		        		cell.setCellStyle(headerStyle);
			        		reportSheet.autoSizeColumn(column++);
						}
					}
					*/
							
	    		}

	    		// print the data rows now.
	    		row     = reportSheet.createRow(j);
			    
	    		// Create a cell and put a value in it.
	    		// we have decided to not make cell0 a hyperlink to source because Excel 97-03 was having trouble
	    		// saving these files.
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

	    		int cellNum = 0;
			  
			    HSSFCell cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
			    String requirementTraceTo = r.getRequirementTraceTo();
			    String requirementTraceFrom = r.getRequirementTraceFrom();
			    
			   
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (url));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(r.getVersion())));
			    cell.setCellStyle(wrappedStyle);
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementName()));
			    cell.setCellStyle(wrappedStyle);
			    
			   
				cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementDescriptionNoHTMLWithJSoup() ));
			    cell.setCellStyle(wrappedStyle);
		    
				cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementCommentsString(databaseType)));
			    cell.setCellStyle(wrappedStyle);
		   
		    	cell = row.createCell(++cellNum);
		    	cell.setCellValue(new HSSFRichTextString (r.getRequirementOwner()));
		    	cell.setCellStyle(wrappedStyle);
		   
		    	HSSFCell testingStatusCell = row.createCell(++cellNum);
        	    testingStatusCell.setCellValue(new HSSFRichTextString (r.getTestingStatus() ));
		    	
        	    
		    	if (r.getTestingStatus().equals("Pending")){
		    		testingStatusCell.setCellStyle(testPendingStyle);
	        	   
	        	}
    			if (r.getTestingStatus().equals("Pass")){
    				testingStatusCell.setCellStyle(testPassStyle);
	        	    
	        	}
    			if (r.getTestingStatus().equals("Fail")){
    				testingStatusCell.setCellStyle(testFailStyle);
	        	   
	        	}
    			
    			
    		 		
			 		
    			cell = row.createCell(++cellNum);
    			cell.setCellValue(new HSSFRichTextString (r.getRequirementPriority()));
    			cell.setCellStyle(wrappedStyle);
			    
    		
    			cell = row.createCell(++cellNum);
    			cell.setCellValue(new HSSFRichTextString (r.getRequirementPctComplete() + "" ));
    			cell.setCellStyle(wrappedStyle);
			    
    		
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getApprovedByAllDt() ));
			    cell.setCellStyle(wrappedStyle);
			    
    		
    			cell = row.createCell(++cellNum);
    			cell.setCellValue(new HSSFRichTextString (r.getRequirementLockedBy()));
    			cell.setCellStyle(pendingStyle);
			    
    		
	    
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}
			    	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }
			    
			    	HSSFCell  pendingCell = row.createCell(++cellNum);
			    	pendingCell.setCellValue(new HSSFRichTextString (pendingApprovers ));
			    	pendingCell.setCellStyle(pendingStyle);
			    	
			    	HSSFCell  approvedCell = row.createCell(++cellNum);
			    	approvedCell.setCellValue(new HSSFRichTextString (approvedApprovers ));
			    	approvedCell.setCellStyle(approvedStyle);
			   
			    	HSSFCell  rejectedCell = row.createCell(++cellNum);
			    	rejectedCell.setCellValue(new HSSFRichTextString (rejectedApprovers ));
			    	rejectedCell.setCellStyle(rejectedStyle);
			    	
			    	String[] traces = r.getRequirementTraceTo().split(",");
		    		String coloredTraceTo = "";
		    		for (int l=0;l<traces.length;l++){
		    			try {
							// if you can get the requirement object, then print more details. If you hit exception print what you have
							String tempReqName = RequirementUtil.getRequirementName(traces[l].replace("(s)",""), project.getProjectId());
							if (
									(!(tempReqName.contains("null")))
									&&
									(tempReqName.length() > 0)
								)
									{
								coloredTraceTo +=  traces[l] + " :"+ tempReqName +"\n\n ";
							}
			    		}
						catch (Exception e){
							// if you run into exception, do the simple way. 
							e.printStackTrace();
							coloredTraceTo +=  traces[l] +" \n ";
						}
		    		}
		    		
		    		String traceTo = r.getRequirementTraceTo();
		    		if (traceTo.contains("(s")){
		    			traceTo = traceTo.replace("(s)", "");
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (traceTo));
			    	// since these can get very long we aren't wrapping them around.
			    	cell.setCellStyle(wrappedStyle);
			    	
			    	
			    			// since an excel cell can not hold more than 30,000 chars  then we only show the req names. 
		    		
			    	if (coloredTraceTo.length() > 10000){
		    			coloredTraceTo = traceTo;
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (coloredTraceTo ));
			    	// since these can get very long we aren't wrapping them around.
			    	cell.setCellStyle(wrappedStyle);
			    	
			    	
			   
			    	traces = r.getRequirementTraceFrom().split(",");
		    		String coloredTraceFrom = "";
		    		Requirement traceFromReq = null;
		    		for (int l=0;l<traces.length;l++){
		    			try {
		    				
							// if you can get the requirement object, then print more details. If you hit exception print what you have
							
							String tempReqName = RequirementUtil.getRequirementName(traces[l].replace("(s)",""), project.getProjectId());
							if (
									(!(tempReqName.contains("null")))
									&&
									(tempReqName.length() > 0)
								){
								coloredTraceFrom +=  traces[l] + " :"+ tempReqName + "\n\n ";
							}
							
			    		}
						catch (Exception e){
							// if you run into exception, do the simple way. 
							e.printStackTrace();
							coloredTraceFrom +=  traces[l] +" \n ";
						}
		    		}
		    		
		    		
		    		String traceFrom = r.getRequirementTraceFrom();
		    		if (traceFrom.contains("(s")){
		    			traceFrom = traceFrom.replace("(s)", "");
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (traceFrom));
			    	// since these can get pretty long, we aren't wrapping them around
			    	cell.setCellStyle(wrappedStyle);
			    	
			    	

			    	// since an excel cell can not hold more than 30,000 chars  then we only show the req names. 
		    		
			    	if (coloredTraceFrom.length() > 10000){
		    			coloredTraceFrom = traceFrom;
		    		}
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (coloredTraceFrom ));
			    	// since these can get pretty long, we aren't wrapping them around
			    	cell.setCellStyle(wrappedStyle);
				    
			   
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementExternalUrl() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			   
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getFolderPath() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			  
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementBaselineString(databaseType) ));
			    	cell.setCellStyle(wrappedStyle);
				    
			  
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getCreatedBy() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			   
			    	
			    	
			    	
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getCreatedDt() ));
			    	cell.setCellStyle(dateStyle);
				    
			   
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getLastModifiedBy() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			   
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getLastModifiedDt() ));
			    	cell.setCellStyle(wrappedStyle);
				    
			   
			    	String attachmentString = "";
					ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							
							String attachmentURL = ProjectUtil.getRequirementAttachmentURL(request, r.getRequirementId(), attachment.getRequirementAttachmentId()); 
							attachmentString += "URL to file : " + attachmentURL + "\n";
							attachmentString += "Title :  " + attachment.getTitle()  + " \n";
						}															
					} 
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (attachmentString ));
			    	cell.setCellStyle(wrappedStyle);
				    
			   /* 
			    // now to print the custom values.
			    for (int k=0; k<customAttributesToDisplay.length; k++) {
					String thisAttribLabel = customAttributesToDisplay[k];
					String attribValue = (String) attribsHashMap.get(thisAttribLabel.trim());
					
					cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString (attribValue));
	        		cell.setCellStyle(wrappedStyle);
				
			    }
				*/
			}
	    	

	    	
	    	
	    	
	    	
	    	
	    	
	    	// lets see if the user  has chosen to print out the Revision History.
	    	// if yes, we will need to add another tab to the excel file and include revision history of every
	    	// requirement in this output.
	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		// lets add a new revisionHistorySheet to the excel output.
	    	    // for the first row, print the header and user defined columns headers. etc..

	    	    int rowNum = 0;
        		row     = revisionHistorySheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		int cellNum = 0;
        		int column = 0;
        		
        		HSSFCell cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approvers                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approval Status "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Priority"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        	
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace To                           "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace From                         "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created By                        "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Custom Attributes                            "));
        		cell.setCellStyle(headerStyle);
        		revisionHistorySheet.autoSizeColumn(column++);
    		
	    	    i = requirements.iterator();
	            
		    	while ( i.hasNext() ) {
		    		GlobalRequirement gr = (GlobalRequirement) i.next();
					Requirement r = gr.getRequirement();
					if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))) {
						r.redact();
					}


		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement versions.
		    		ArrayList requirementVersions = r.getRequirementVersions( databaseType);
		    		Iterator m = requirementVersions.iterator();
		    		
		    		while (m.hasNext()) {
		    			RequirementVersion v = (RequirementVersion) m.next();
		    			// for each req version, lets print a row.
			    		// print the data rows now.
			    		row     = revisionHistorySheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionName()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionDescriptionNoHTML()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionApprovers() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionStatus() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionPriority() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionOwner() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersionPctComplete())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionExternalURL() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceTo() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceFrom() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedBy()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedDt()));
					    cell.setCellStyle(wrappedStyle);
					    
					
					    String uDA = v.getVersionUserDefinedAttributes();
					    String formattedUDA = "";
					    if ((uDA != null) && (uDA.contains(":##:"))) {
					    	String [] uDAs = uDA.split(":##:") ;
					    	
					    	for (int u=0; u<uDAs.length; u++) {
					    		formattedUDA += uDAs[u] + "\n";
					    	}
					    }
					    cell = row.createCell(++cellNum);
			    		cell.setCellValue(new HSSFRichTextString (formattedUDA));
			    		cell.setCellStyle(wrappedStyle);
			    		
					    
		    		}
		    	}
		    	

		    	
		    	
	    		// for the first row, print the header and user defined columns headers. etc..

	    	    rowNum = 0;
        		row     = commentsSheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		cellNum = 0;
        		column = 0;
        		
        		cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Commenter                                    "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Comment                                                                                                           "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        		        

        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Date                              "));
        		cell.setCellStyle(headerStyle);
        		commentsSheet.autoSizeColumn(column++);
        			    
	    	    
	    	    i = requirements.iterator();
	            
		    	while ( i.hasNext() ) {
		    		GlobalRequirement gr = (GlobalRequirement) i.next();
					Requirement r = gr.getRequirement();
					if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + r.getFolderId()))) {
						r.redact();
					}

		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement comments.
		    		ArrayList requirementComments = r.getRequirementComments( databaseType);
		    		Iterator m = requirementComments.iterator();
		    		
		    		while (m.hasNext()) {
		    			Comment c = (Comment) m.next();
		    			// for each req comment, lets print a row.
			    		// print the data rows now.
			    		row     = commentsSheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(c.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommenterEmailId()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getComment_note()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommentDate()));
					    cell.setCellStyle(wrappedStyle);
					    
		    		}
		    	}
	    	}

	    	
    	    

	    	System.out.println("srt ckn 4 . export type is " + exportType);
					
					
            // Write the output
	    	if (exportType.equals("HTML")){
	    		OutputStream out = response.getOutputStream();
	            wb.write(out);
	            out.close();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
	    		}

	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
	    		}

	    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
	    	}
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }

    private String exportTracePanelToExcel (HttpServletRequest request,
            HttpServletResponse response, Project project, User user) 
    		throws ServletException, IOException {
    	String filename = "";
    	
		// Get the session. It should have the last View Report in memory.
    	HttpSession session = request.getSession(true);
    	
        ArrayList fromRequirements = (ArrayList) session.getAttribute("tracePanelFromRequirements" );
        ArrayList toRequirements = (ArrayList) session.getAttribute("tracePanelToRequirements" );
        
        
        try {

        	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();

    		// create a file name and set it to it.
        	Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" TracePanel Report " + today + ".xls";
    		filename.replace(' ', '_');
    		
		
    		
    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    	    HSSFCellStyle headerStyle2 = wb.createCellStyle();
    		headerStyle2.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
    	    headerStyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    // lets pick the cell styles for dangling / orphan / dangling and orphan
    		HSSFCellStyle suspectStyle = wb.createCellStyle();
    		suspectStyle.setFillForegroundColor(HSSFColor.RED.index);
    	    suspectStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    		HSSFCellStyle clearStyle = wb.createCellStyle();
    		clearStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
    	    clearStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    	    
    	    
    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    
    	    
    	    // lets build all the sheets in this file.
    	    HSSFSheet infoSheet  = wb.createSheet("Report Info");
    	    HSSFSheet reportSheet = wb.createSheet("Trace Panel Report");
	    	

	    	/////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    // lets start on the 5th Row.
    	    int startRow = 5; 
    		HSSFRow row     = infoSheet.createRow((short)startRow++);



    		row     = infoSheet.createRow((short)startRow++);
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Trace Panel Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Trace Panel Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		startRow += 2;
    		
    	
    		// if this is a saved report, then lets print the report info.
    		// project Info.
    		startRow += 4;
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		
    			
    	    // lets build the list report.    	    


            
            Iterator i = fromRequirements.iterator();
            
            int j = 0;
            
            // since we will need to uda (to print the datavalidation) outside the while loop
            // lets define it outside
            String [] attribs = null;
	    	while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();
	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		row     = reportSheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("      "));
	        		cell.setCellStyle(headerStyle2);
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("         		               "));
	        		cell.setCellStyle(headerStyle2);
	        		reportSheet.autoSizeColumn(cellNum);

					// lets print each of the to folder columns
	        		Iterator toI = toRequirements.iterator();
	        		while (toI.hasNext()){
	        			Requirement toR = (Requirement) toI.next();
		        		cell = row.createCell(++cellNum);
		        		cell.setCellValue(new HSSFRichTextString (toR.getRequirementFullTag()));
		        		cell.setCellStyle(headerStyle2);
		        		reportSheet.autoSizeColumn(cellNum);
	        		}
	        		
	        		
	        		
	        		
		    		// lets print another row with the to req names
		    		row     = reportSheet.createRow(j++);
	        		cellNum = 0;
	        		
	        		cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("      "));
	        		cell.setCellStyle(headerStyle2);
	        		reportSheet.autoSizeColumn(cellNum);
	        		
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("         		               "));
	        		cell.setCellStyle(headerStyle);
	        		reportSheet.autoSizeColumn(cellNum);

					// lets print each of the to folder columns
	        		toI = toRequirements.iterator();
	        		while (toI.hasNext()){
	        			Requirement toR = (Requirement) toI.next();
		        		cell = row.createCell(++cellNum);
		        		cell.setCellValue(new HSSFRichTextString (toR.getRequirementName()));
		        		cell.setCellStyle(headerStyle);
		        		reportSheet.autoSizeColumn(cellNum);
	        		}
	    		}


	    		
	    		
	    		// print the data rows now.
	    		row     = reportSheet.createRow(j);
			    
	    		// Create a cell and put a value in it.
	    		// we have decided to not make cell0 a hyperlink to source because Excel 97-03 was having trouble
	    		// saving these files.

	    		int cellNum = 0;
			  
			    HSSFCell cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
			    cell.setCellStyle(headerStyle2);
        		reportSheet.autoSizeColumn(cellNum);
			    
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementName()));
			    cell.setCellStyle(headerStyle);
        		reportSheet.autoSizeColumn(cellNum);
			    
			    String traceTo = "," + r.getRequirementTraceTo() + ",";
			    // lets print each of the to folder columns
        		Iterator toI = toRequirements.iterator();
        		while (toI.hasNext()){
        			Requirement toR = (Requirement) toI.next();
	        		cell = row.createCell(++cellNum);
	        		System.out.println("--> traceTo from " +r.getParentFullTag() +" is " + r.getRequirementTraceTo());
	        		System.out.println(" toR is ");
	        		if (traceTo.contains(",(s)" + toR.getRequirementFullTag() + ",")){
	        			// a suspect trace exists

		        		cell.setCellValue(new HSSFRichTextString ("SUSPECT"));
		        		cell.setCellStyle(suspectStyle);
		        		
	        		}
	        		else if (traceTo.contains("," + toR.getRequirementFullTag() + ",")){
	        			// a regular trace exists
	        			cell.setCellValue(new HSSFRichTextString ("TRACE"));
		        		cell.setCellStyle(clearStyle);
		        		
	        		}
	        		else {
	        			// no trace
	        			cell.setCellValue(new HSSFRichTextString (""));
		        		cell.setCellStyle(wrappedStyle);
		        		
	        		}
	        		
        		}
				
			}
	    	


	    	
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
			}
	
			// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
			File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
			if (!(tempFolderRoot.exists() )){
				new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
			}
	
			filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
			FileOutputStream fileOut = new FileOutputStream(filename);
			wb.write(fileOut);
			fileOut.close();
			
			
			// lets send the email out to the toEmailId;
			ArrayList toArrayList = new ArrayList();
			toArrayList.add(user.getEmailId());
			
			
			ArrayList ccArrayList = new ArrayList();
			String subject = "Your Trace Panel Report";
			String message = "Hello,\n\n We are attaching your Trace Panel report here. \n\nBest Regards\n\nTraceCloud Admin";
			
			MessagePacket mP = new MessagePacket(toArrayList, ccArrayList, subject, message, filename);
			
			String mailHost = this.getServletContext().getInitParameter("mailHost");
			String transportProtocol = this.getServletContext().getInitParameter("transportProtocol");
			String smtpAuth = this.getServletContext().getInitParameter("smtpAuth");
			String smtpPort = this.getServletContext().getInitParameter("smtpPort");
			String smtpSocketFactoryPort = this.getServletContext().getInitParameter("smtpSocketFactoryPort");
			String emailUserId = this.getServletContext().getInitParameter("emailUserId");
			String emailPassword = this.getServletContext().getInitParameter("emailPassword");
			
			EmailUtil.emailWithAttachment(mP, mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );

			
			// now lets remove the temp file.
			File file = new File(filename);
			if (file != null){
				// lets drop the file.
				file.delete();
			}
			
		    PrintWriter out = response.getWriter();
		    out.println("<div class='alert alert-info'> An email with attachment has been sent out to  " + user.getEmailId() +" </div>");
		    out.close();

	    
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }


    
    private String exportListReportToWord
    	(HttpServletRequest request,  HttpServletResponse response, Project project, User user, String exportType, String databaseType) 
    	throws ServletException, IOException {

    	String filename = "";
    	try{
			// Get the session. It should have the last View Report in memory.
	    	HttpSession session = request.getSession(true);
	    	int folderId = Integer.parseInt(request.getParameter("folderId"));
	    	Folder folder = new Folder(folderId);
	        ArrayList reportArrayList = (ArrayList) session.getAttribute("listReportForFolder"+folderId);
	        String reportIdString = (String) session.getAttribute("listReportIdStringForFolder" + folderId);
    		String reportDefinition = (String) session.getAttribute("listReportDefinitionForFolder" + folderId);
    		String standardDisplay = (String) session.getAttribute("listReportStandardDisplay" + folderId );
    		String customAttributesDisplay = (String) session.getAttribute("listReportCustomAttributesDisplay" + folderId );
    		
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" Report " + today + ".doc";    		
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}

			// setting some response headers
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			// setting the content type
			response.setContentType("application/msword");

    		
	    	Document document = new Document();
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RtfWriter2.getInstance(document, baos);
			
			document.open();

			// lets add the heading page.
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( "Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " List Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( "Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( "Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( user.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
			

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

    		if ((reportDefinition != null) && !(reportDefinition == "")) {
    			if (reportDefinition.contains(":###")){
    				
            		document.add(new Chunk( "Report Definition" , new Font(Font.TIMES_ROMAN, 10)));
            		document.add(Chunk.NEWLINE);
        			document.add(Chunk.NEWLINE);
        			
    				String[] elements = reportDefinition.split(":###:");
    				for (String e : elements){
    					
    					try{
    					String[] eDetails = e.split(":--:");
    					
    					String filterName = eDetails[0];
    					String filterDetails = eDetails[1];
    					
    					if (
    						(filterName.equals("projectId"))
    							||
    						(filterName.equals("active"))
    						||
    						(filterName.equals("displayRequirementType"))
    						||
    						(filterName.contains("customA"))
    						||
    						(filterName.contains("sort"))
    						||
    						(filterName.contains("rowsPerPage"))
    						||
    						(filterName.contains("standardDisplay"))
    						)
    						{
    						continue;
    					}
    					if (filterName.equals("folderId")){
    						filterName = "Folder";
    						filterDetails = folder.getFolderPath();
    					}
    					if (filterName.equals("danglingSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Dangling";
    						filterDetails = "True";
    					}
    					if (filterName.equals("orphanSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Orphan";
    						filterDetails = "True";
    					}
    					if (filterName.equals("completedSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
							filterName = "Completed";
    						filterDetails = "True";
    					}
    					if (filterName.equals("incompleteSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "InComplete";
    						filterDetails = "True";
    					}
    					if (filterName.equals("includeSubFoldersSearch")){
    						if (filterDetails.equals("no")){
    							continue;
    						}
    						filterName = "Include Sub Folders";
    						filterDetails = "True";
    						
    						
    					}
    					if (filterName.equals("suspectUpStreamSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Suspects UpStream";
    						filterDetails = "True";
    					}
    					if (filterName.equals("suspectDownStreamSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Suspects DownStream";
    						filterDetails = "True";
    					}
    					if (filterName.equals("lockedSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Locked";
    						filterDetails = "True";
    					}
    					
    					if (filterName.equals("nameSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Name";
    					}
    					if (filterName.equals("descriptionSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Description";
    					}
    					if (filterName.equals("ownerSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Owner";
    					}
    					
    					if (filterName.equals("inRelease")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						Requirement requirement = new Requirement(Integer.parseInt(filterDetails),"mySQL");
    						filterName = "In Relase ";
    						filterDetails = requirement.getRequirementFullTag() + ":" + requirement.getRequirementName() ;
    					}
    					
    					if (filterName.equals("inRTBaselineSearch")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						RTBaseline rTBaseline = new RTBaseline(Integer.parseInt(filterDetails));
    						
    						filterName = "In Baseline ";
    						filterDetails = rTBaseline.getBaselineName() ;
    					}
    					
    					if (filterName.equals("changedAfterRTBaselineSearch")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						RTBaseline rTBaseline = new RTBaseline(Integer.parseInt(filterDetails));
    						filterName = "Changed After Baseline ";
    						filterDetails = rTBaseline.getBaselineName() ;
    					}
    					
    					
    					document.add(new Chunk(ControlChar.TAB) );
                		document.add(new Chunk( filterName , new Font(Font.TIMES_ROMAN, 10)));
                		document.add(colon);
                		document.add(new Chunk( filterDetails , new Font(Font.TIMES_ROMAN, 10)));
                		document.add(Chunk.NEWLINE);
            			
    					}
    					catch (Exception ex){
    						ex.printStackTrace();
    					}
    				}
    				
    				// custom attributes

    				if (elements.length > 0 ){
						document.add(new Chunk(ControlChar.TAB) );
						document.add(new Chunk( "Custom Attribute Filters" , new Font(Font.TIMES_ROMAN, 10)));
	            		document.add(Chunk.NEWLINE);
	            		document.add(Chunk.NEWLINE);
    				}
	        		
    				for (String e : elements){
    					
    					try{
    					String[] eDetails = e.split(":--:");
    					
    					String filterName = eDetails[0];
    					String filterDetails = eDetails[1];
    					
    					
    					if (!(filterName.contains("customA"))){
    						continue;
    					}
    				
    					
						filterName = filterName.replace("customA", "");
						RTAttribute rTAttribute = new RTAttribute(Integer.parseInt(filterName));
						filterName = rTAttribute.getAttributeName();
						filterDetails = filterDetails.replace(":##:", ", ");
					
    					document.add(new Chunk(ControlChar.TAB) );
    	        		document.add(new Chunk( filterName , new Font(Font.TIMES_ROMAN, 10)));
    	        		document.add(colon);
                		document.add(new Chunk( filterDetails , new Font(Font.TIMES_ROMAN, 10)));
                		document.add(Chunk.NEWLINE);
            			
                		
    					}
    					catch (Exception ex){
    						ex.printStackTrace();
    					}
    				}
        		  
    			}
    		}
			
    		document.add(Chunk.NEWLINE);
			
    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    		if ((reportIdString != null) && !(reportIdString == "")) {
    			int reportId = Integer.parseInt(reportIdString);
    			Report report = new Report(reportId);


    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);

    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        		
    		}
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( "Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( "Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( "Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();
			
	        Iterator i = reportArrayList.iterator();
	        while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();
	    		
	    		document.add(new Paragraph(" "));
	    		document.add(Chunk.NEWLINE);

	    		
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    	    Anchor anchor1 = new Anchor(r.getRequirementFullTag(), 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);
	    	    
	    		document.add(Chunk.NEWLINE);
	    	    document.add(new Chunk("Version: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( Integer.toString(r.getVersion()) , new Font(Font.TIMES_ROMAN, 10)));

	    		
	    		
	    		// lets customize the testingStatus color based on its value.
	    		if (standardDisplay.contains("testingStatus")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Testing Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		Chunk testingStatusChunk = new Chunk( "   " + r.getTestingStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));
		    		if (r.getTestingStatus().equals("Pending")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0xFF, 0x00));
		    		}
				    if (r.getTestingStatus().equals("Pass")){
		    			testingStatusChunk.setBackground(new Color(0x00, 0xFF, 0x00));			    	
		        	}
				    if (r.getTestingStatus().equals("Fail")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}
		    		document.add(testingStatusChunk);
	    		}

	    		
	    		// lets customize the status color based on its value.
	    		if (standardDisplay.contains("status")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Approval Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		Chunk statusChunk = new Chunk( "   " + r.getApprovalStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));
		    		if (r.getApprovalStatus().equals("Draft")){
		    			statusChunk.setBackground(new Color(0xEE, 0x82, 0xEE));
		    		}
				    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
		    			statusChunk.setBackground(new Color(0x99, 0xcc, 0xff));			    	
		        	}
				    if (r.getApprovalStatus().equals("Approved")){
		    			statusChunk.setBackground(new Color(0x57, 0xE9, 0x64));
		        	}
				    if (r.getApprovalStatus().equals("Rejected")){
		    			statusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}			    
		    		document.add(statusChunk);
	    		}

	    		if (standardDisplay.contains("priority")) {
		    		document.add(Chunk.NEWLINE);
		    	    document.add(new Chunk("Priority: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPriority() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("percentComplete")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Percent Complete: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPctComplete() + "%" , new Font(Font.TIMES_ROMAN, 10)));
		    		
	    		}
	    		
	    		document.add(Chunk.NEWLINE);
	    		document.add(new Chunk("Approved On: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getApprovedByAllDt() , new Font(Font.TIMES_ROMAN, 10)));

				
	    		if (standardDisplay.contains("lockedBy")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Locked By: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementLockedBy() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
				
				
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }

			    
			    if (standardDisplay.contains("approvedBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Approved By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( approvedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Rejected By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( rejectedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			    if (standardDisplay.contains("pendingBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Pending By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( pendingApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			
				
				if (standardDisplay.contains("owner")) {
					document.add(Chunk.NEWLINE);
					document.add(new Chunk("Owner : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementOwner() , new Font(Font.TIMES_ROMAN, 10)));
				}
	    		
	    		
	    		if (standardDisplay.contains("traceTo")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace To: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceTo() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
				
	    		if (standardDisplay.contains("traceFrom")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace From : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceFrom() , new Font(Font.TIMES_ROMAN, 10)));
	    		}

	    		if (standardDisplay.contains("externalURL")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("External URL: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
					if ((r.getRequirementExternalUrl() !=null) && (!r.getRequirementExternalUrl().equals(""))){
			    	    anchor1 = new Anchor(r.getRequirementExternalUrl(), 
			    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
			    	    anchor1.setReference(r.getRequirementExternalUrl());
			    	    document.add(anchor1);
					}
	    		}

	    		if (standardDisplay.contains("folderPath")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Folder Path : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getFolderPath() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("baselines")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Baselines : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementBaselineString(databaseType) , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("createdDate")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Created Date : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getCreatedDt() , new Font(Font.TIMES_ROMAN, 10)));
	    		}				
				
	    		if (standardDisplay.contains("attachments")) {
	    			document.add(Chunk.NEWLINE);
	    			document.add(new Chunk("Attachments : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    			ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        File Name : "  , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk(attachment.getFileName() , new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        Title : " , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk( attachment.getTitle(), new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
						}
					}
					
	    		}				
				
				// Printing custom attributes.
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12				
	    		String uda = r.getUserDefinedAttributes();
				String[] attribs = uda.split(":##:");
				for (int k=0; k<attribs.length; k++) {
					String[] attrib = attribs[k].split(":#:");
					
					// To avoid a array out of bounds exception where the attrib value wasn't filled in
					// we print the cell only if array has 2 items in it.					
					if (attrib.length ==2){
						if (customAttributesDisplay.contains(attrib[0]) ) {
							document.add(Chunk.NEWLINE);
							document.add(new Chunk(attrib[0]+ " : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    		document.add(new Chunk( attrib[1] , new Font(Font.TIMES_ROMAN, 10)));
						}
					}					
				}

				document.add(Chunk.NEWLINE);
	    		document.add(new Chunk("Name : ", new Font(Font.TIMES_ROMAN, 10,Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getRequirementName() , new Font(Font.TIMES_ROMAN, 10)));

	    		if (standardDisplay.contains("description")) {
					if (r.getRequirementDescription() != null) {
		    			document.add(Chunk.NEWLINE);
			    		document.add(new Chunk("Description : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    		document.add(new Chunk( r.getRequirementDescriptionNoHTML() , new Font(Font.TIMES_ROMAN, 10)));
		    		}
	    		}
	        }
			
	        document.add(new Paragraph("  "));
			document.close();
			
            // Write the output
	    	if (exportType.equals("HTML")){
				// the contentlength is needed for MSIE!!!
				response.setContentLength(baos.size());
				// write ByteArrayOutputStream to the ServletOutputStream
				ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
				out.flush();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
	    		}

	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
	    		}

	    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
				baos.writeTo(fileOut);
				fileOut.flush();
				fileOut.close();
	    	}
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (DocumentException de) {
            // DocumentExceptions arise if you add content to the document before opening or
            // after closing the document.
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }
    		

    
    
    private String exportListReportToPDF (HttpServletRequest request,
            HttpServletResponse response, Project project, User user, String exportType, String databaseType) throws ServletException, IOException {

    	String filename = "";
    	
    	try{
    		// Get the session. It should have the last View Report in memory.
        	HttpSession session = request.getSession(true);
        	int folderId = Integer.parseInt(request.getParameter("folderId"));
        	Folder folder = new Folder(folderId);
            ArrayList reportArrayList = (ArrayList) session.getAttribute("listReportForFolder" + folderId);
            String reportIdString = (String) session.getAttribute("listReportIdStringForFolder" + folderId);
    		String reportDefinition = (String) session.getAttribute("listReportDefinitionForFolder" + folderId);
    		String standardDisplay = (String) session.getAttribute("listReportStandardDisplay" + folderId );
    		String customAttributesDisplay = (String) session.getAttribute("listReportCustomAttributesDisplay" + folderId );
    		
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" Report " + today + ".pdf";
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}
    		// setting some response headers
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			// setting the content type
			response.setContentType("application/pdf");

    		
    		
    		Document document = new Document();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			document.open();
			

			// lets add the heading page.
			document.add(new Paragraph(" "));
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( "Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " List Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( "Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( "Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( user.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			

    		if ((reportDefinition != null) && !(reportDefinition == "")) {
    			if (reportDefinition.contains(":###")){
    				
            		document.add(new Chunk( "Report Definition" , new Font(Font.TIMES_ROMAN, 10)));
            		document.add(Chunk.NEWLINE);
        			document.add(Chunk.NEWLINE);
        			
    				String[] elements = reportDefinition.split(":###:");
    				for (String e : elements){
    					
    					try{
    					String[] eDetails = e.split(":--:");
    					
    					String filterName = eDetails[0];
    					String filterDetails = eDetails[1];
    					
    					if (
    						(filterName.equals("projectId"))
    							||
    						(filterName.equals("active"))
    						||
    						(filterName.equals("displayRequirementType"))
    						||
    						(filterName.contains("customA"))
    						||
    						(filterName.contains("sort"))
    						||
    						(filterName.contains("rowsPerPage"))
    						||
    						(filterName.contains("standardDisplay"))
    						)
    						{
    						continue;
    					}
    					if (filterName.equals("folderId")){
    						filterName = "Folder";
    						filterDetails = folder.getFolderPath();
    					}
    					if (filterName.equals("danglingSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Dangling";
    						filterDetails = "True";
    					}
    					if (filterName.equals("orphanSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Orphan";
    						filterDetails = "True";
    					}
    					if (filterName.equals("completedSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
							filterName = "Completed";
    						filterDetails = "True";
    					}
    					if (filterName.equals("incompleteSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "InComplete";
    						filterDetails = "True";
    					}
    					if (filterName.equals("includeSubFoldersSearch")){
    						if (filterDetails.equals("no")){
    							continue;
    						}
    						filterName = "Include Sub Folders";
    						filterDetails = "True";
    						
    						
    					}
    					if (filterName.equals("suspectUpStreamSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Suspects UpStream";
    						filterDetails = "True";
    					}
    					if (filterName.equals("suspectDownStreamSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Suspects DownStream";
    						filterDetails = "True";
    					}
    					if (filterName.equals("lockedSearch")){
    						if (filterDetails.equals("all")){
    							continue;
    						}
    						filterName = "Locked";
    						filterDetails = "True";
    					}
    					
    					if (filterName.equals("nameSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Name";
    					}
    					if (filterName.equals("descriptionSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Description";
    					}
    					if (filterName.equals("ownerSearch")){
    						if (filterDetails == null || filterDetails.equals("")){
    							continue;
    						}
       						filterName = "Owner";
    					}
    					
    					if (filterName.equals("inRelease")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						Requirement requirement = new Requirement(Integer.parseInt(filterDetails),"mySQL");
    						filterName = "In Relase ";
    						filterDetails = requirement.getRequirementFullTag() + ":" + requirement.getRequirementName() ;
    					}
    					
    					if (filterName.equals("inRTBaselineSearch")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						RTBaseline rTBaseline = new RTBaseline(Integer.parseInt(filterDetails));
    						
    						filterName = "In Baseline ";
    						filterDetails = rTBaseline.getBaselineName() ;
    					}
    					
    					if (filterName.equals("changedAfterRTBaselineSearch")){
    						if (filterDetails.equals("-1")){
    							continue;
    						}
    						RTBaseline rTBaseline = new RTBaseline(Integer.parseInt(filterDetails));
    						filterName = "Changed After Baseline ";
    						filterDetails = rTBaseline.getBaselineName() ;
    					}
    					
    					
    					document.add(new Chunk(ControlChar.TAB) );
                		document.add(new Chunk( filterName , new Font(Font.TIMES_ROMAN, 10)));
                		document.add(colon);
                		document.add(new Chunk( filterDetails , new Font(Font.TIMES_ROMAN, 10)));
                		document.add(Chunk.NEWLINE);
            			
    					}
    					catch (Exception ex){
    						ex.printStackTrace();
    					}
    				}
    				
    				// custom attributes

    				if (elements.length > 0 ){
						document.add(new Chunk(ControlChar.TAB) );
						document.add(new Chunk( "Custom Attribute Filters" , new Font(Font.TIMES_ROMAN, 10)));
	            		document.add(Chunk.NEWLINE);
	            		document.add(Chunk.NEWLINE);
    				}
	        		
    				for (String e : elements){
    					
    					try{
    					String[] eDetails = e.split(":--:");
    					
    					String filterName = eDetails[0];
    					String filterDetails = eDetails[1];
    					
    					
    					if (!(filterName.contains("customA"))){
    						continue;
    					}
    				
    					
						filterName = filterName.replace("customA", "");
						RTAttribute rTAttribute = new RTAttribute(Integer.parseInt(filterName));
						filterName = rTAttribute.getAttributeName();
						filterDetails = filterDetails.replace(":##:", ", ");
					
    					document.add(new Chunk(ControlChar.TAB) );
    	        		document.add(new Chunk( filterName , new Font(Font.TIMES_ROMAN, 10)));
    	        		document.add(colon);
                		document.add(new Chunk( filterDetails , new Font(Font.TIMES_ROMAN, 10)));
                		document.add(Chunk.NEWLINE);
            			
                		
    					}
    					catch (Exception ex){
    						ex.printStackTrace();
    					}
    				}
        		  
    			}
    		}
			
    		document.add(Chunk.NEWLINE);
    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    		if ((reportIdString != null) && !(reportIdString == "")) {
    			int reportId = Integer.parseInt(reportIdString);
    			Report report = new Report(reportId);


    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        		
    		}
    		
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( "Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( "Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( "Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();
			

    		Iterator i = reportArrayList.iterator();
            while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();

	    		document.add(new Paragraph(" "));
	    		document.add(Chunk.NEWLINE);

	    		
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    	    Anchor anchor1 = new Anchor(r.getRequirementFullTag(), 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);
	    	    
	    	    document.add(Chunk.NEWLINE);
	    	    document.add(new Chunk("Version: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( Integer.toString(r.getVersion()) , new Font(Font.TIMES_ROMAN, 10)));

	    		
	    		
	    		// lets customize the testingStatua color based on its value.
	    		if (standardDisplay.contains("testingStatus")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Testing Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		Chunk testingStatusChunk = new Chunk( "   " + r.getTestingStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));
		    		if (r.getTestingStatus().equals("Pending")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0xFF, 0x00));
		    		}
				    if (r.getTestingStatus().equals("Pass")){
		    			testingStatusChunk.setBackground(new Color(0x00, 0xFF, 0x00));			    	
		        	}
				    if (r.getTestingStatus().equals("Fail")){
		    			testingStatusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}
		    		document.add(testingStatusChunk);
	    		}
	    		
	    		if (standardDisplay.contains("status")) {
	    			document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Approval Status : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		
		    		// lets customize the status color based on its value.
		    		Chunk statusChunk = new Chunk( "   " + r.getApprovalStatus() + "   " , new Font(Font.TIMES_ROMAN, 10));	    		
		    		if (r.getApprovalStatus().equals("Draft")){
		    			statusChunk.setBackground(new Color(0xEE, 0x82, 0xEE));
		    		}
				    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
		    			statusChunk.setBackground(new Color(0x99, 0xcc, 0xff));			    	
		        	}
				    if (r.getApprovalStatus().equals("Approved")){
		    			statusChunk.setBackground(new Color(0x57, 0xE9, 0x64));
		        	}
				    if (r.getApprovalStatus().equals("Rejected")){
		    			statusChunk.setBackground(new Color(0xFF, 0x00, 0x00));
		        	}			    
		    		document.add(statusChunk);
	    		}

	    		if (standardDisplay.contains("priority")) {
	    			document.add(Chunk.NEWLINE);
		    	    document.add(new Chunk("Priority: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPriority() , new Font(Font.TIMES_ROMAN, 10)));
	    		}

	    		if (standardDisplay.contains("percentComplete")) {
	    			document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Percent Complete: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementPctComplete() + "%" , new Font(Font.TIMES_ROMAN, 10)));
		    		
	    		}
	    		
	    		document.add(Chunk.NEWLINE);
	    	    document.add(new Chunk("Approved On: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getApprovedByAllDt() , new Font(Font.TIMES_ROMAN, 10)));

	    		if (standardDisplay.contains("lockedBy")) {
		    		document.add(Chunk.NEWLINE);
		    		document.add(new Chunk("Locked By: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementLockedBy() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
				
	    		
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }

			    
			    if (standardDisplay.contains("approvedBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Approved By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( approvedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
			    	document.add(Chunk.NEWLINE);			    	
				    document.add(new Chunk("Rejected By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( rejectedApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			    if (standardDisplay.contains("pendingBy")) {
			    	document.add(Chunk.NEWLINE);
				    document.add(new Chunk("Pending By : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    document.add(new Chunk( pendingApprovers , new Font(Font.TIMES_ROMAN, 10)));			    
			    }

			    
				if (standardDisplay.contains("owner")) {
					document.add(Chunk.NEWLINE);
					document.add(new Chunk("Owner : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementOwner() , new Font(Font.TIMES_ROMAN, 10)));
				}
	    		
	    		
	    		
	    		if (standardDisplay.contains("traceTo")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace To: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceTo() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("traceFrom")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Trace From: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementTraceFrom(), new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("externalURL")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("External URL: ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
					if ((r.getRequirementExternalUrl() !=null) && (!r.getRequirementExternalUrl().equals(""))){
			    	    anchor1 = new Anchor(r.getRequirementExternalUrl(), 
			    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
			    	    anchor1.setReference(r.getRequirementExternalUrl());
			    	    document.add(anchor1);
					}
	    		}


	    		if (standardDisplay.contains("folderPath")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Folder Path : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getFolderPath() , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("baselines")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Baselines : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getRequirementBaselineString( databaseType) , new Font(Font.TIMES_ROMAN, 10)));
	    		}
	    		
	    		if (standardDisplay.contains("createdDate")) {
	    			document.add(Chunk.NEWLINE);
					document.add(new Chunk("Created Date : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
		    		document.add(new Chunk( r.getCreatedDt() , new Font(Font.TIMES_ROMAN, 10)));
	    		}				
				
	    		if (standardDisplay.contains("attachments")) {
	    			document.add(Chunk.NEWLINE);
	    			document.add(new Chunk("Attachments : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    			ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        File Name : "  , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk(attachment.getFileName() , new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
			    			document.add(new Chunk("        Title : " , new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    			document.add(new Chunk( attachment.getTitle(), new Font(Font.TIMES_ROMAN, 10)));
			    			document.add(Chunk.NEWLINE);
						}
					}
					
	    		}				
				
				// Printing custom attributes.
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12				
	    		String uda = r.getUserDefinedAttributes();
				String[] attribs = uda.split(":##:");
				for (int k=0; k<attribs.length; k++) {
					String[] attrib = attribs[k].split(":#:");
					
					// To avoid a array out of bounds exception where the attrib value wasn't filled in
					// we print the cell only if array has 2 items in it.					
					if (attrib.length ==2){
						if (customAttributesDisplay.contains(attrib[0]) ) {
							document.add(Chunk.NEWLINE);
							document.add(new Chunk(attrib[0]+ " : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
				    		document.add(new Chunk( attrib[1] , new Font(Font.TIMES_ROMAN, 10)));
						}
					}					
				}

				document.add(Chunk.NEWLINE);
	    		document.add(new Chunk("Name : ", new Font(Font.TIMES_ROMAN, 10,Font.BOLDITALIC )));
	    		document.add(new Chunk( r.getRequirementName() , new Font(Font.TIMES_ROMAN, 10)));
			
	    		if (standardDisplay.contains("description")) {
					if (r.getRequirementDescription() != null) {
		    			document.add(Chunk.NEWLINE);
			    		document.add(new Chunk("Description : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
			    		document.add(new Chunk( r.getRequirementDescriptionNoHTML() , new Font(Font.TIMES_ROMAN, 10)));
		    		}
	    		}
            }
			
            document.add(new Paragraph("   "));
			document.close();
			
            // Write the output
	    	if (exportType.equals("HTML")){
				// the contentlength is needed for MSIE!!!
				response.setContentLength(baos.size());
				// write ByteArrayOutputStream to the ServletOutputStream
				ServletOutputStream out = response.getOutputStream();
				baos.writeTo(out);
				out.flush();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
	    		}

	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
	    		}

	    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
				baos.writeTo(fileOut);
				fileOut.flush();
				fileOut.close();
	    	}
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (DocumentException de) {
            // DocumentExceptions arise if you add content to the document before opening or
            // after closing the document.
            de.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }
    		
        

	// This method uses the Apache POI module to print out XLS files.
    private String exportTraceTreeReportToExcel (HttpServletRequest request,
            HttpServletResponse response, Project project, User user, String exportType, String databaseType) throws ServletException, IOException {

    	String filename = "";
		// Get the session. It should have the last View Report in memory.
    	HttpSession session = request.getSession(true);
    	int folderId = Integer.parseInt(request.getParameter("folderId"));
        ArrayList traceTreeReport = (ArrayList) session.getAttribute("traceTreeReportForFolder" + folderId);
        String reportIdString = (String) session.getAttribute("traceTreeReportIdStringForFolder" + folderId);
        String reportDefinition = (String) session.getAttribute("traceTreeReportDefinitionForFolder" + folderId);
        int traceTreeReportDepth = Integer.parseInt((String) session.getAttribute("traceTreeReportDepthForFolder" +  folderId));
		String standardDisplay = (String) session.getAttribute("traceTreeReportStandardDisplay" + folderId );
		
		String displayRequirementType = (String) session.getAttribute("displayRequirementType" + folderId);
		if (displayRequirementType == null ){
			displayRequirementType = "all";
		}
		
		
		try {

        	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();

    		response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" TraceTreeReport " + today + ".xls";
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Expires", "0");
    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    			response.setHeader("Pragma", "public");
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}


    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 
	    		
    		
    		// cell style for clear traces is Green
    		HSSFCellStyle clearTraceStyle = wb.createCellStyle();
    	    clearTraceStyle.setFillForegroundColor(HSSFColor.GREEN.index);
    	    clearTraceStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    		// cell style for suspect traces is red.
    	    HSSFCellStyle suspectTraceStyle = wb.createCellStyle();
    	    suspectTraceStyle.setFillForegroundColor(HSSFColor.RED.index);
    	    suspectTraceStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

    	    
    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    
    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    	    HSSFCellStyle hlink_style = wb.createCellStyle();
            HSSFFont hlink_font = wb.createFont();
            hlink_font.setUnderline(HSSFFont.U_SINGLE);
            hlink_font.setColor(HSSFColor.BLUE.index);
            hlink_style.setFont(hlink_font);


    	    // lets pick the cell styles for pending / pass / fail
    		HSSFCellStyle testPendingStyle = wb.createCellStyle();
    		testPendingStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
    		testPendingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testPendingStyle.setWrapText(true);
    	    
    		HSSFCellStyle testPassStyle = wb.createCellStyle();
    		testPassStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
    		testPassStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testPassStyle.setWrapText(true);
    	    
    		HSSFCellStyle testFailStyle = wb.createCellStyle();
    		testFailStyle.setFillForegroundColor(HSSFColor.RED.index);
    		testFailStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    		testFailStyle.setWrapText(true);
 	    
    	   
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    HSSFSheet sheet  = wb.createSheet("Report Info");
    	    // lets start on the 5th Row.
    	    int startRow = 5; 
    		HSSFRow row     = sheet.createRow((short)startRow++);

    		
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Title"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString ("Trace Tree Report"));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		startRow += 2;

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Trace Tree Depth  "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (Integer.toString(traceTreeReportDepth)));

    		startRow += 2;

    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    		if ((reportIdString != null) && !(reportIdString == "")) {
    			int reportId = Integer.parseInt(reportIdString);
    			Report report = new Report(reportId);
    			
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Id"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (Integer.toString(report.getReportId())));
    			
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Created By"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getCreatedByEmailId() ));

        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report URL"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
        		cellB.setCellValue(new HSSFRichTextString ( reportURLString ));
			    
			    
        		
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Name"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportName()));
        		
        		row     = sheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Report Description"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		cellB.setCellValue(new HSSFRichTextString (report.getReportDescription() ));

    		}
    		
    		// project Info.
    		startRow += 4;
    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = sheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		

    		
    		
            
	    	
	    	//#########################################################################
    		
    	    // lets build the list report.
            Iterator i = traceTreeReport.iterator();
            
            int j = 1;

            // lets iterate through the TTR and find all distinct req types.
	    	
	    	ArrayList<String> headers = null;
	    	if (standardDisplay.contains("customAttributes")) {
	    		headers = ReportUtil.getColumnHeadersInTraceTreeReport(traceTreeReport);
	    	}
	    	
    		sheet = wb.createSheet("Trace Tree Report Details ");
    		
            
	    	while ( i.hasNext() ) {
	    		TraceTreeRow tTR = (TraceTreeRow) i.next();
	    		Requirement r = tTR.getRequirement();
	    		// since a trace tree can have different requirement types
	    		// and since we can't figure out how may attributes these
	    		// req Types can have, we will just display a UDA string.
	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		row     = sheet.createRow((short)0);
	        
	        	    
	        		
	        		// Print the header row for the excel file.

	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 1         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 2         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 3         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 4         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 5         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 6         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Level 7         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		
	        		
		    		// we can show the requirement description in excel
	        		// as the cell can easily be hidden in excel and will not
	        		// impede the effectiveness of the trace tree.
	    
	        		if (standardDisplay.contains("description")) {
						cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Description (Without formatting Do not use for upload)                                                                                                           "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (standardDisplay.contains("owner")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Owner                                                           "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("testingStatus")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing Status"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("percentComplete")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("priority")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Priority          "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("status")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval Status                     "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}

	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approved Dt                             "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		

	        		if (standardDisplay.contains("pendingBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Pending By                     "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("approvedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approved By                    "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("rejectedBy")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Rejected By                    "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("traceTo")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace To                       "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("traceFrom")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Trace From                     "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}

	        		
	        		if (standardDisplay.contains("externalURL")) {
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                                 "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		

	        		if (standardDisplay.contains("folderPath")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Folder Path                                                                                                        "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("baselines")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Baselines                      "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("createdDate")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Created Date                             "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}
	        		
	        		if (standardDisplay.contains("attachments")) {	        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Attachments                    "));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
	        		}

	        		if (standardDisplay.contains("customAttributes")) {
		        		for (String header:headers){
		        			cell = row.createCell(++cellNum); 
			        		cell.setCellValue(new HSSFRichTextString (header));
			        		cell.setCellStyle(headerStyle);
			        		sheet.autoSizeColumn(column++);
			        	}
							
							
		       	}
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Trace Description                                                    "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	    		}

	    		// lets skip displaying this row, if this req was not in the display list
	    		boolean shouldDisplay = false;
				if (displayRequirementType.contains("all")) {
					shouldDisplay = true;
				} else {
					
					// means some display restrictions are in place
					if (displayRequirementType.contains(r.getRequirementTypeId() + ",")){
						shouldDisplay = true;
					}
				}
		   		if (!shouldDisplay){continue;}
	    		j++;

		   		
	    		// print the data rows now.
	    		row     = sheet.createRow(j);

	    		int cellNum = 0;
			    // Create a cell and put a value in it.
	    		
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    		HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
                link.setAddress(url);
                
                HSSFCell cell = null;
                
	    		if (tTR.getLevel() == 1){
		    		
		    		cell = row.createCell(cellNum);
	    			if (r.getProjectId()== project.getProjectId()){
	    				cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()) + " : " + r.getRequirementName());
	    				cell.setHyperlink(link);
		                cell.setCellStyle(hlink_style);
		                
	    			}
	    			else {
	    				cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()) + " : " + r.getRequirementName());
	    				cell.setHyperlink(link);
		                cell.setCellStyle(hlink_style);
		            }
	    			

	    			
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    			
	    		}
 				else if (tTR.getLevel() == 2) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 		    		
 		    		cell = row.createCell(++cellNum);
 	                if (r.getProjectId()== project.getProjectId()){
 	    				cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		                
 	    			}
 	    			else {
 	    				cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		            }
			 		
	    			
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	    		
	    
 				}
 				else if (tTR.getLevel() == 3) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					
 					cell = row.createCell(++cellNum);
 	                if (r.getProjectId()== project.getProjectId()){
 	    				cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		                
 	    			}
 	    			else {
 	    				cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		            }
			 		
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 	               row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
					
	    				    
 				}
 				else if (tTR.getLevel() == 4) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	                
 					cell = row.createCell(++cellNum);
 	                if (r.getProjectId()== project.getProjectId()){
 	    				cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		                
 	    			}
 	    			else {
 	    				cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		            }
			 		
 	                row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
	                

	    
 				}
 				else if (tTR.getLevel() == 5) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));

 					cell = row.createCell(++cellNum);
 	                if (r.getProjectId()== project.getProjectId()){
 	    				cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		                
 	    			}
 	    			else {
 	    				cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		            }
			 		
 	                row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
					
	    		}
 				else if (tTR.getLevel() == 6) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					
 					cell = row.createCell(++cellNum);
 	                if (r.getProjectId()== project.getProjectId()){
 	    				cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		                
 	    			}
 	    			else {
 	    				cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		            }
			 		
 	                row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
					
	    		}
 				else if (tTR.getLevel() == 7) {
 					row.createCell(cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					row.createCell(++cellNum).setCellValue(new HSSFRichTextString (""));
 					
 					cell = row.createCell(++cellNum);
 	                if (r.getProjectId()== project.getProjectId()){
 	    				cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		                
 	    			}
 	    			else {
 	    				cell.setCellValue(new HSSFRichTextString (r.getProjectShortName() +":" + r.getRequirementFullTag()) + " : " + r.getRequirementName());
 	    				cell.setHyperlink(link);
 		                cell.setCellStyle(hlink_style);
 		            }
			 		
 	                
	    		}
	    		

			    
			    if (standardDisplay.contains("description")) {
					cell = row.createCell(++cellNum);
				    String reqDescNoHTML =  r.getRequirementDescriptionNoHTMLWithJSoup();
				    if (reqDescNoHTML == null){
				    	reqDescNoHTML = "";
				    }
				    if (reqDescNoHTML.length() > 1023){
				    	reqDescNoHTML = reqDescNoHTML.substring(0, 1020);
				    }
				    cell.setCellValue(new HSSFRichTextString (reqDescNoHTML));
			    }
			    
			    if (standardDisplay.contains("owner")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementOwner()));
			    	
			    }
			    

			 
			    
			    if (standardDisplay.contains("testingStatus")) {
			    	HSSFCell testingStatusCell = row.createCell(++cellNum);
	        	    testingStatusCell.setCellValue(new HSSFRichTextString (r.getTestingStatus() ));
			    	
	        	   
			    	if (r.getTestingStatus().equals("Pending")){
			    		testingStatusCell.setCellStyle(testPendingStyle);
    	        	    
    	        	}
        			if (r.getTestingStatus().equals("Pass")){
        				testingStatusCell.setCellStyle(testPassStyle);
    	        	    
		        	}
        			if (r.getTestingStatus().equals("Fail")){
        				testingStatusCell.setCellStyle(testFailStyle);
    	        	   
		        	}
        			
        			
        		}
			    
			    
			    
			    if (standardDisplay.contains("percentComplete")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementPctComplete() + ""));
			    }
			    
			    if (standardDisplay.contains("priority")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementPriority()));
			    }
			    
			    

			    if (standardDisplay.contains("status")) {
			    	// lets see if this requirement is in a folder that is enabled for approval work flow
			 		String folderIdApprovalCheck = "#" + r.getFolderId() + "#";
			 		
			 		if (!(foldersEnabledForApprovalWorkFlow.contains(folderIdApprovalCheck))){
	        			HSSFCell statusCell = row.createCell(++cellNum);
		        	    statusCell.setCellValue(new HSSFRichTextString ("Not Applicable"));
		        	    // lets set the status Cell color based on its value
		        	    HSSFCellStyle statusStyle = wb.createCellStyle();
		        		statusStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
					    statusCell.setCellStyle(statusStyle);
			 		}
			 		else {
	
				    	HSSFCell statusCell = row.createCell(++cellNum);
		        	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
		
		        	    // lets set the approval work flow status Cell color based on its value
		        	    HSSFCellStyle statusStyle = wb.createCellStyle();
					    if (r.getApprovalStatus().equals("Draft")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("Approved")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}
					    if (r.getApprovalStatus().equals("Rejected")){
			        		statusStyle.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
			        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			        	}			    
					    statusCell.setCellStyle(statusStyle);
			 		}
			    }
			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getApprovedByAllDt() ));
			    
			    
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}
			    	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }
			    
			    if (standardDisplay.contains("pendingBy")) {
				    HSSFCell  pendingCell = row.createCell(++cellNum);
				    pendingCell.setCellValue(new HSSFRichTextString (pendingApprovers ));
				    pendingCell.setCellStyle(pendingStyle);
			    }
			    
			    if (standardDisplay.contains("approvedBy")) {
				    HSSFCell  approvedCell = row.createCell(++cellNum);
				    approvedCell.setCellValue(new HSSFRichTextString (approvedApprovers ));
				    approvedCell.setCellStyle(approvedStyle);
			    }
			    
			    if (standardDisplay.contains("rejectedBy")) {
				    HSSFCell  rejectedCell = row.createCell(++cellNum);
				    rejectedCell.setCellValue(new HSSFRichTextString (rejectedApprovers ));
				    rejectedCell.setCellStyle(rejectedStyle);
			    }
			    
			    if (standardDisplay.contains("traceTo")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementTraceTo() ));
			    	// we don't set these to wrapped
			    }
			    
			    if (standardDisplay.contains("traceFrom")) {
			    	row.createCell(++cellNum).setCellValue(new HSSFRichTextString (r.getRequirementTraceFrom() ));
			    	// we don't set these to wrapped
			    }
			    
			    if (standardDisplay.contains("externalURL")) {
				   cell = row.createCell(++cellNum);
				   cell.setCellValue(new HSSFRichTextString (r.getRequirementExternalUrl() ));
				    
			    }
			    
			    if (standardDisplay.contains("folderPath")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getFolderPath() ));
			        
			    }
			    
			    if (standardDisplay.contains("baselines")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getRequirementBaselineString(databaseType) ));
			        
			    }
			    
			    if (standardDisplay.contains("createdDate")) {
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (r.getCreatedDt() ));
			        
			    }
			    

			    if (standardDisplay.contains("attachments")) {
			    	String attachmentString = "";
					ArrayList attachments = r.getRequirementAttachments(databaseType);
					if (attachments.size() > 0){  
						Iterator atachmentIterator = attachments.iterator();
						while (atachmentIterator.hasNext()) {
							RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
							attachmentString += "File :  " + attachment.getFileName()  + "; ";
							attachmentString += "Title :  " + attachment.getTitle()  + "; ";
						}															
					} 
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(new HSSFRichTextString (attachmentString ));
			        
			    }
			    
			    if (standardDisplay.contains("customAttributes")) {

					/*
					for every row : {

						make a map of key  values
						iterate through the 'headers'{
							for each , find the value for this key from this map and print
							REMOVE fro mmap
						}

					}

					After all rows are printed, print the custome header in Excel output
					*/
				
		 				HashMap<String, String> aMap = r.getUserDefinedAttributesHashMap();
		 				
		 				// iterate through known custom headers
		 				Iterator<String> hI = headers.iterator();
		 				String aName = "";
		 				String aValue = "";
		 				while (hI.hasNext()){
		 					aName = hI.next().trim();
		 					aValue = aMap.get(aName);
		 					if (aValue==null){aValue="";}
		 					cell = row.createCell(++cellNum);
					    	cell.setCellValue(new HSSFRichTextString (aValue )); 
		 				}
					    	
		 			
			    }
		    	
			    // trace description
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (tTR.getTraceDescription() ));
			    
	    	}
	    	
	    	
	    	//#########################################################################
	    	
	    	
	    	
	    	// lets see if the user  has chosen to print out the Revision History.
	    	// if yes, we will need to add another tab to the excel file and include revision history of every
	    	// requirement in this output.
	    	 HSSFCellStyle wrappedStyle = wb.createCellStyle();
	    	 wrappedStyle.setWrapText(true);
	    	    
	    	String includeRevisionHistory = request.getParameter("includeRevisionHistory");
	    	if( (includeRevisionHistory != null ) && (includeRevisionHistory.equals("yes"))) {
	    		// lets add a new sheet to the excel output.
	    	    sheet = wb.createSheet("Requirement Version History");
	    		// for the first row, print the header and user defined columns headers. etc..

	    	    int rowNum = 0;
        		
        		
		
        		
        		row     = sheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		int cellNum = 0;
        		int column = 0;
        		
        		HSSFCell cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                       "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approvers                        "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Approval Status "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Priority"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		
    			cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        	
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                            "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace To                           "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Trace From                         "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created By                        "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);

        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Custom Attributes                            "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
    		
        		i = traceTreeReport.iterator();
    	    			            
		    	while ( i.hasNext() ) {
    	    		TraceTreeRow tTR = (TraceTreeRow) i.next();
    	    		Requirement r = tTR.getRequirement();

		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement versions.
		    		ArrayList requirementVersions = r.getRequirementVersions( databaseType);
		    		Iterator m = requirementVersions.iterator();
		    		
		    		while (m.hasNext()) {
		    			RequirementVersion v = (RequirementVersion) m.next();
		    			// for each req version, lets print a row.
			    		// print the data rows now.
			    		row     = sheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    
					    cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionName()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    String reqDescNoHTML =  r.getRequirementDescriptionNoHTML();
					    if (reqDescNoHTML == null){
					    	reqDescNoHTML = "";
					    }
					    if (reqDescNoHTML.length() > 1023){
					    	reqDescNoHTML = reqDescNoHTML.substring(0, 1020);
					    }
					    cell.setCellValue(new HSSFRichTextString (reqDescNoHTML));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionApprovers() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionStatus() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionPriority() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionOwner() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(v.getVersionPctComplete())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionExternalURL() ));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceTo() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionTraceFrom() ));
					    // we are not wrapping this, as it can get quiet big
					    //cell.setCellStyle(wrappedStyle);
					    
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedBy()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (v.getVersionCreatedDt()));
					    cell.setCellStyle(wrappedStyle);

					    
					    String uDA = v.getVersionUserDefinedAttributes();
					    String formattedUDA = "";
					    if ((uDA != null) && (uDA.contains(":##:"))) {
					    	String [] uDAs = uDA.split(":##:") ;
					    	
					    	for (int u=0; u<uDAs.length; u++) {
					    		formattedUDA += uDAs[u] + "\n";
					    	}
					    }
					    cell = row.createCell(++cellNum);
			    		cell.setCellValue(new HSSFRichTextString (formattedUDA));
			    		
		    		}
		    	}
		    	
	    		// lets create a new sheet and print all the comments made by people for all these requiremetns
	    	    sheet = wb.createSheet("Requirement Comments");
	    		// for the first row, print the header and user defined columns headers. etc..

	    	    rowNum = 0;
        		row     = sheet.createRow(rowNum++);	
        		// Print the header row for the excel file.
        		cellNum = 0;
        		column = 0;
        		
        		cell = row.createCell(cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Tag     "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum);
        		cell.setCellValue(new HSSFRichTextString ("Version"));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Commenter                                    "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Comment                                                                                                           "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		        

        		cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString ("Date                                        "));
        		cell.setCellStyle(headerStyle);
        		sheet.autoSizeColumn(column++);
        		
	    	    
	    	    i = traceTreeReport.iterator();
	            
		    	while ( i.hasNext() ) {
		    		TraceTreeRow tTR = (TraceTreeRow) i.next();
    	    		Requirement r = tTR.getRequirement();
		    		

		    		// for every Requirement in the result set, lets get an arraylist of
		    		// requirement comments.
		    		ArrayList requirementComments = r.getRequirementComments( databaseType);
		    		Iterator m = requirementComments.iterator();
		    		
		    		while (m.hasNext()) {
		    			Comment c = (Comment) m.next();
		    			// for each req comment, lets print a row.
			    		// print the data rows now.
			    		row     = sheet.createRow(rowNum++);
					

			    		// Create a cell and put a value in it.
						 // make cell0 a hyperlink
			    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

			    		cellNum = 0;
					    
			    		cell = row.createCell(cellNum);
					    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (url));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (Integer.toString(c.getVersion())));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommenterEmailId()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getComment_note()));
					    cell.setCellStyle(wrappedStyle);
					    
					    cell = row.createCell(++cellNum);
					    cell.setCellValue(new HSSFRichTextString (c.getCommentDate()));
					    cell.setCellStyle(wrappedStyle);
					}
		    	}	    		
	    	}

            // Write the output
	    	if (exportType.equals("HTML")){
	    		OutputStream out = response.getOutputStream();
	            wb.write(out);
	            out.close();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
	    		}

	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
	    		}

	    		filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
	    	}
        
        } catch (FileNotFoundException fnfe) {
            // It might not be possible to create the target file.
            fnfe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filename;
    }
    
    


	public static void cleanUpFont(com.aspose.words.Font font) {
		try {
			font.setSize(10);
			
			font.setStrikeThrough(false);
			font.setColor(Color.BLACK);
			font.setUnderline(0);
			font.setBold(false);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static void setUpCell(DocumentBuilder builder, int colWidth ){
		try {
			builder.getCellFormat().setWidth(colWidth);
			builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
			builder.getCellFormat().getBorders().setLineStyle(LineStyle.SINGLE);
		} catch (Exception e) {
			
			e.printStackTrace();
		}		
	}
	

	
	public static void printColumnHeaderCell(com.aspose.words.Font font, int cellSize, String cellName, com.aspose.words.DocumentBuilder builder ){
		try {
			builder.insertCell();

			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
			cleanUpFont(font);
			setUpCell(builder, cellSize);
			font.setColor(Color.BLUE);
			font.setBold(true);
			builder.write(cellName + " ");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	
	

	public static void writeCellHeader(com.aspose.words.Font font, int cellSize, String cellValue, com.aspose.words.DocumentBuilder builder ){
		try {

			builder.insertCell();
			cleanUpFont(font);
			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
			setUpCell(builder, cellSize);
			builder.write(cellValue);

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	
	public static void writeCell(com.aspose.words.Font font, int cellSize, String cellValue, com.aspose.words.DocumentBuilder builder ){
		try {

			builder.insertCell();
			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
			cleanUpFont(font);
			setUpCell(builder, cellSize);
			builder.write(cellValue);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	public static void writeCellWithColor(com.aspose.words.Font font, int cellSize, String cellValue, Color color, com.aspose.words.DocumentBuilder builder ){
		try {

			builder.insertCell();
			builder.getCellFormat().getShading().setBackgroundPatternColor(color);
			cleanUpFont(font);
			setUpCell(builder, cellSize);
			builder.write(cellValue);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	public static void writeCellHTML(com.aspose.words.Font font, int cellSize, String cellValue, com.aspose.words.DocumentBuilder builder ){
		try {
			builder.insertCell();
			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
			cleanUpFont(font);
			setUpCell(builder, cellSize);
			builder.insertHtml(cellValue);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static void writeCellURL(com.aspose.words.Font font, int cellSize, String cellValue, String url, com.aspose.words.DocumentBuilder builder ){
		try {
			builder.insertCell();

			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
			cleanUpFont(font);

			font.setColor(Color.BLUE);
			font.setUnderline(1);
			setUpCell(builder, cellSize);
			builder.insertHyperlink(cellValue, url, false);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
    public String exportTraceTreeReportToWord2(
    		HttpServletRequest request,  
    		HttpServletResponse response, Project project, User user, 
    		SecurityProfile securityProfile,  String databaseType, String templateFilePath, String rootDataDirectory)	throws ServletException, IOException {
    	String filename = "";
			try {
				
	
				System.out.println("insde exportTTRW2 function");
				// Get the session. It should have the last View Report in memory.
		    	HttpSession session = request.getSession(true);
		        
		    	int folderId = Integer.parseInt(request.getParameter("folderId"));
		        ArrayList traceTreeReport = (ArrayList) session.getAttribute("traceTreeReportForFolder" + folderId);
		        String reportIdString = (String) session.getAttribute("traceTreeReportIdStringForFolder" + folderId);	        
		        String reportDefinition = (String) session.getAttribute("traceTreeReportDefinitionForFolder" + folderId);
		        int traceTreeReportDepth = Integer.parseInt((String) session.getAttribute("traceTreeReportDepthForFolder" +  folderId)); 
		        String standardDisplay = (String) session.getAttribute("traceTreeReportStandardDisplay" + folderId );
	
		        
		        System.out.println("srt reportDefinition is "  + reportDefinition );
		        System.out.println("srt standard display is "  + standardDisplay);
		        
		        
				com.aspose.words.Document doc = new  com.aspose.words.Document(templateFilePath);
			 	com.aspose.words.DocumentBuilder builder = new com.aspose.words.DocumentBuilder(doc);
	
			 	builder.getPageSetup().setOrientation( Orientation.LANDSCAPE);
			 	com.aspose.words.Font font = builder.getFont();
				
				// Note : 
				// since different req types can exist in the same doc, and since each req types can have different custom attributes, 
				// we need to print different req types in different tables. 
				// The problem is we don't know when the req type can change. So we use lastReqTypePrinted to keep track of the last req type printed
				// and compare with any new req we are printing and if different , end the prev table and start a new one, with headers etc...
				
				
				// Hyperlinks in a Word documents are fields, select all field start
				// nodes so we can find the hyperlinks.
				NodeList fieldStarts = doc.selectNodes("//FieldStart");
				Iterator fs = fieldStarts.iterator();
				
				while (fs.hasNext()) {
					//String displayAttributes = selectedDisplayAttributes;
					
					Node node = (Node) fs.next();
					// lets move to the node and start printing
					builder.moveTo(node);
					
					
					FieldStart fieldStart = (FieldStart) node;
					
					if (fieldStart.getFieldType() == FieldType.FIELD_HYPERLINK) {
						
						
					
						// for some reason, smallcaps are being set to On. So we are 
						// physically turnign them off.
						//font.setSmallCaps(false);
						
	
						Iterator i = traceTreeReport.iterator();
				
				        
						builder.writeln("");
						try {
							builder.startTable();
						}
						catch (Exception e){
							e.printStackTrace();
						}
				        
						printColumnHeaderCell(font, 120, "Level 1", builder );
						printColumnHeaderCell(font, 120, "Level 2", builder );
						printColumnHeaderCell(font, 120, "Level 3", builder );
						printColumnHeaderCell(font, 120, "Level 4", builder );
						printColumnHeaderCell(font, 120, "Level 5", builder );
						printColumnHeaderCell(font, 120, "Level 6", builder );
						printColumnHeaderCell(font, 120, "Level 7", builder );
						printColumnHeaderCell(font, 510, "Details", builder );
	
						builder.endRow();
						
						while ( i.hasNext() ) {
				        	TraceTreeRow tTR = (TraceTreeRow) i.next();
				    		Requirement r = (Requirement) tTR.getRequirement();
				    		
				    		String url = ProjectUtil.getURL(request, r.getRequirementId(),"requirement");
							
				
				    		
				    		String traceSpacer = "";
			        		if (tTR.getLevel() == 1){
			        			// insert the req name and 6 empty cells
			        			
								writeCellURL(font, 120, r.getRequirementFullTag(), url , builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
															
			        		}
			    			else if (tTR.getLevel() == 2) {					
			    				
			    				writeCell(font, 120, "   ", builder );
			    				writeCellURL(font, 120, r.getRequirementFullTag(), url , builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								
			       			}
			    			else if (tTR.getLevel() == 3) {
								
			      				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCellURL(font, 120, r.getRequirementFullTag(), url , builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								
							}
			    			else if (tTR.getLevel() == 4) {
					
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCellURL(font, 120, r.getRequirementFullTag(), url , builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
					
			    			}
			    			else if (tTR.getLevel() == 5) {
								
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    		    	writeCell(font, 120, "   ", builder );
			    		    	writeCellURL(font, 120, r.getRequirementFullTag(), url , builder );
								writeCell(font, 120, "   ", builder );
								writeCell(font, 120, "   ", builder );
					
			    			
			    			}
			    			else if (tTR.getLevel() == 6) {
					
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    		    	writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCellURL(font, 120, r.getRequirementFullTag(), url , builder );
								writeCell(font, 120, "   ", builder );
					
			    					
			    				
							}
			    			else if (tTR.getLevel() == 7) {
			        			
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    		    	writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCell(font, 120, "   ", builder );
			    				writeCellURL(font, 120, r.getRequirementFullTag(), url , builder );
								
			    											
			    			}
			    			else {
			    				// nothing
			    			}
			        		
			        		

		        			builder.insertCell();
		        			setUpCell(builder, 510);
		        			
		        			builder.startTable();
			        		
			        			
			        			/*
			        			builder.insertCell();
			        			cleanUpFont(font);
			        			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
				        		setUpCell(builder, 500);
			        			builder.write("Name");
			        			builder.endRow();
								*/
			        		
				        		
				    		    builder.insertCell();
			        			cleanUpFont(font);
			        			
			        			font.setColor(Color.BLUE);
			        			font.setUnderline(1);
			        			setUpCell(builder, 500);
			        			builder.insertHyperlink(r.getRequirementFullTag(), url, false);
			        			cleanUpFont(font);
			        			builder.write(" : ");
			        			
			        			builder.write(r.getRequirementNameForHTML());
			        			builder.endRow();
								
				    		    /*
				    		    builder.insertCell();
			        			cleanUpFont(font);
			        			builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
				        		setUpCell(builder, 500);
			        			builder.write("Description");
			        			builder.endRow();
								*/
				    		    

			        			if (standardDisplay.contains("description")){
									writeCellHTML(font, 500, r.getRequirementDescription(), builder );
									builder.endRow();
			        			}
			        			if (standardDisplay.contains("owner")){
			        				writeCellHeader(font, 120, "Owner", builder );
				    				writeCell(font, 380, r.getRequirementOwner(), builder );
				    				builder.endRow();
								}
			        			
			        			if (standardDisplay.contains("traceTo")){
			        				writeCellHeader(font, 120, "Traces To ", builder );
				    				writeCell(font, 380, r.getRequirementTraceTo(), builder );
				    				builder.endRow();
								}
			        			
			        			if (standardDisplay.contains("traceFrom")){
			        				writeCellHeader(font, 120, "Traces From ", builder );
				    				writeCell(font, 380, r.getRequirementTraceFrom(), builder );
				    				builder.endRow();
								}
			        			
			        			if (standardDisplay.contains("status")){
			        				writeCellHeader(font, 120, "Approval Status", builder );
				    				writeCell(font, 380, r.getApprovalStatus() , builder );
				    				builder.endRow();
								}
			        		
			        			if (standardDisplay.contains("percentComplete")){
			        				writeCellHeader(font, 120, "Completion Percentage", builder );
				    				writeCell(font, 380, Integer.toString(r.getRequirementPctComplete()) + " % " , builder );
				    				builder.endRow();
								}
			        		
			        			
			        			if (standardDisplay.contains("testingStatus")){
			        				Color testingStatusColor = Color.WHITE;
			        				if (r.getTestingStatus().equals("Pass") ){
			        					testingStatusColor  = Color.GREEN;
			        				}
			        				
			        				if (r.getTestingStatus().equals("Fail") ){
			        					testingStatusColor  = Color.RED;
			        				}
			        				
			        				if (r.getTestingStatus().equals("Pending") ){
			        					testingStatusColor  = Color.YELLOW;
			        				}
			        				
			        				writeCellHeader(font, 120, "Testing Status ", builder );
				    				writeCellWithColor(font, 380, r.getTestingStatus() , testingStatusColor, builder );
				    				builder.endRow();
								}
			        		
			        			if (standardDisplay.contains("priority")){
			        				writeCellHeader(font, 120, "Priority", builder );
				    				writeCell(font, 380, r.getRequirementPriority(), builder );
				    				builder.endRow();
								}
			        			
				        		if (standardDisplay.contains("attachments")){
				        			ArrayList attachments = r.getRequirementAttachments(databaseType);
									if (attachments.size() > 0 ){	
				        				writeCellHeader(font, 120, "File Attachments", builder );
					    				
				        				
				        				
	
				        				com.aspose.words.Cell cell =  builder.insertCell();
										cleanUpFont(font);
										setUpCell(builder, 400);
										// lets iterate through all the attachments and print them out.
										Iterator a = attachments.iterator();
										while (a.hasNext()){
											try{
											
												RequirementAttachment attachment = (RequirementAttachment) a.next();
												builder.writeln(attachment.getTitle());
												builder.writeln("");
												if (
														(attachment.getFileName().toLowerCase().endsWith(".jpg"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".jpeg"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".jpe"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".jfif"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".gif"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".tif"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".tiff"))
														||
														(attachment.getFileName().toLowerCase().endsWith(".png"))
													){
													// this is an image file, so lets display the image
													
													FileInputStream input = new FileInputStream(attachment.getFilePath());
													
													Shape shape = builder.insertImage(input);
													double shapeWidth = shape.getWidth();
													double cellWidth = cell.getCellFormat().getWidth();
													
													if (shapeWidth > cellWidth){
														// if the image is larger than the cell, lets shrink its height and weight by a proportion .
														shape.setWidth(shape.getWidth() * (cellWidth / shapeWidth));
														shape.setHeight(shape.getHeight() * (cellWidth / shapeWidth));
													}
													else {
														shape.setWidth(cell.getCellFormat().getWidth());
													}
													builder.writeln();
												}
												else {
													// this is not an image file, so lets display a download link to the file.
													builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
									        		
													font.setColor(Color.BLUE);
													font.setUnderline(Underline.SINGLE);
													url = "https://" + request.getServerName() +  
										       				"/GloreeJava2/servlet/DisplayAction?dO=attachment&dAttachmentId=" + attachment.getRequirementAttachmentId() ;
													// Insert the link.
													builder.insertHyperlink(attachment.getFileName(), url, false);
													// Revert to default formatting.
													font.setColor(Color.BLACK);
													font.setUnderline(0);
													builder.writeln();
												}
											}
											catch (Exception e){
												e.printStackTrace();
											}
											
										}
												        				
				        				builder.endRow();
									}// attachments array > 0 
								}
			        			
			        			if (standardDisplay.contains("customAttributes")){
			        				

									// a typical uda looks like this
									// Customer:#: SBI:##:Delivery
									// Estimate:#:01/01/12
									
									
									
									String uda = r.getUserDefinedAttributes();
									
									if (uda != null) {
										
										String[] attribs = null;
										
										// lets split uda and put it in the attribs array.
										if (uda.contains(":##:")) {
											attribs = uda.split(":##:");
										}
										else {
											attribs = new String[1];
											attribs[0] = uda;
										}
										
										
										for (int k = 0; k < attribs.length; k++) {
											if ((attribs[k] != null) && (attribs[k].contains(":#:"))) {
												String[] attrib = attribs[k].split(":#:");
												if (attrib.length > 1) {
													// attrib[1] exists. so lets
													// call it.

													if (attrib[1].length() > 0){
								        				writeCellHeader(font, 120, attrib[0], builder );
									    				writeCell(font, 380, attrib[1], builder );
									    				builder.endRow();
													}
												}
											}
										}
									} 
											        				
			        				
			        				
			        				
								}
			        			
			        			
			        			
							builder.endTable();
							
							
							
							builder.endRow();
							
						
						
					} // end of while
					
					
				}// end of if
		
				try {
					builder.endTable();
				}
				catch (Exception e){
					e.printStackTrace();
				}
				
		
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
				Calendar cal = Calendar.getInstance();
				String today = sdf.format(cal.getTime());
				filename = user.getFirstName() + " " + user.getLastName()  + " Report " + today;
				filename.replace(' ', '_');
	
				
				// we used to support docx, html and pdf output formats
				// ran into trouble, and didn't see the value in debugging
				// so pulled them out. 
				// can re write that code later. User reportFormat param for that.
				filename += ".doc";
	    	
				System.out.println("output fileName is " + filename);
				// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
	    		}
	
	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp").mkdir();
	    		}
	
	    		filename = rootDataDirectory + File.separator +  "TraceCloud" + File.separator +  "Temp/" + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	
	    		doc.save(fileOut, SaveFormat.DOC);
				fileOut.flush();
				fileOut.close();
	
	
			} 
		}
			
		catch (Exception e) {
		e.printStackTrace();
		}
		return filename;
	}

    
    private static ArrayList<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        ArrayList<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }
    
    private String exportTraceTreeReportToWord(HttpServletRequest request,  
    		HttpServletResponse response, Project project, User user, String exportType)	throws ServletException, IOException {

    	String filename = "";
		try{
			// Get the session. It should have the last View Report in memory.
	    	HttpSession session = request.getSession(true);
	        
	    	int folderId = Integer.parseInt(request.getParameter("folderId"));
	        ArrayList traceTreeReport = (ArrayList) session.getAttribute("traceTreeReportForFolder" + folderId);
	        String reportIdString = (String) session.getAttribute("traceTreeReportIdStringForFolder" + folderId);	        
	        String reportDefinition = (String) session.getAttribute("traceTreeReportDefinitionForFolder" + folderId);
	        int traceTreeReportDepth = Integer.parseInt((String) session.getAttribute("traceTreeReportDepthForFolder" +  folderId)); 
	        String standardDisplay = (String) session.getAttribute("traceTreeReportStandardDisplay" + folderId );

			
			// create a file name and set it to it.
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
			String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" TraceTreeReport " + today + ".doc";			
			filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}
			
			
			// setting some response headers
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			// setting the content type
			response.setContentType("application/msword");
			
			
	    	Document document = new Document(PageSize.A4.rotate());
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RtfWriter2.getInstance(document, baos);		
			document.open();
			
			StyleSheet styles = new StyleSheet();
			styles.loadTagStyle("ol", "leading", "16,0");
	
			// lets add the heading page.
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( " Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " Trace Tree Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( " Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( " Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( user.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    		if ((reportIdString != null) && !(reportIdString == "")) {
    			int reportId = Integer.parseInt(reportIdString);
    			Report report = new Report(reportId);


    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);

    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        		
    		}
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);


			
			
			
			
			
			heading = new Chunk( " Trace Tree Depth " , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( Integer.toString(traceTreeReportDepth), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);

			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( " Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( " Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( " Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();

			Iterator i = traceTreeReport.iterator();
	
	        
	        
	        while ( i.hasNext() ) {
	        	TraceTreeRow tTR = (TraceTreeRow) i.next();
	    		Requirement r = (Requirement) tTR.getRequirement();
	    		
	
	    		String traceSpacer = "";
        		if (tTR.getLevel() == 1){
        			traceSpacer = "     ";
        		}
    			else if (tTR.getLevel() == 2) {					
    					traceSpacer = 	"            ";
    			}
    			else if (tTR.getLevel() == 3) {
    					traceSpacer = 	"                    ";
    			}
    			else if (tTR.getLevel() == 4) {
    					traceSpacer = 	"                            ";
    			}
    			else if (tTR.getLevel() == 5) {
    					traceSpacer = 	"                                    ";
    			}
    			else if (tTR.getLevel() == 6) {
    					traceSpacer = 	"                                            ";
    			}
    			else if (tTR.getLevel() == 7) {
    					traceSpacer = 	"                                                ";
    			}
	    		String postTagSpacer = "  ";
	
	    		String requirementFullTag = "";
	    		if (r.getProjectId()== project.getProjectId()){
	    			requirementFullTag = r.getRequirementFullTag();
	    		}
	    		else {
	    			requirementFullTag = r.getProjectShortName() + ":" + r.getRequirementFullTag();
	    		}
	    		if (tTR.getLevel() == 1) {
	    			// for level 1 reqs, do not show green or red.
	    			document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0x00, 0x00) )));
	    		}
	    		if ((tTR.getLevel() >1 ) && (tTR.getTracesToSuspectRequirement() == 0)) {
					document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0xFF, 0x00))));
				}
				if ((tTR.getLevel() > 1 && (tTR.getTracesToSuspectRequirement() != 0))){
					document.add(new Chunk(traceSpacer + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0xFF, 0x00, 0x00))));
				}
	    		
	
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    		String requirementName = r.getRequirementName();
	    		if ((requirementName != null) && (requirementName.length() > 80)){
	    			requirementName = requirementName.substring(0, 77) + "...";
	    		}
	    	    Anchor anchor1 = new Anchor(requirementName, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);	
	    		document.add(Chunk.NEWLINE);
	          }
			
			document.close();
			

			
	    // Write the output
		if (exportType.equals("HTML")){
			// the contentlength is needed for MSIE!!!
			response.setContentLength(baos.size());
			// write ByteArrayOutputStream to the ServletOutputStream
			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		}
		if (exportType.equals("file")){
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator + "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
			}
	
			// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
			File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
			if (!(tempFolderRoot.exists() )){
				new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
			}
	
			filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
			FileOutputStream fileOut = new FileOutputStream(filename);
			baos.writeTo(fileOut);
			fileOut.flush();
			fileOut.close();
		}
	
	} catch (FileNotFoundException fnfe) {
	    // It might not be possible to create the target file.
	    fnfe.printStackTrace();
	} catch (DocumentException de) {
	    // DocumentExceptions arise if you add content to the document before opening or
	    // after closing the document.
	    de.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return filename;
	}
   
    
    private String exportTraceTreeReportToPDF(HttpServletRequest request,
            HttpServletResponse response, Project project, User user, String exportType, String databaseType) throws ServletException, IOException {

    	String filename = "";
    	try{
    		// Get the session. It should have the last View Report in memory.
	    	HttpSession session = request.getSession(true);
	        
	    	int folderId = Integer.parseInt(request.getParameter("folderId"));
	        ArrayList traceTreeReport = (ArrayList) session.getAttribute("traceTreeReportForFolder" + folderId);
	        String reportIdString = (String) session.getAttribute("traceTreeReportIdStringForFolder" + folderId);
	        String reportDefinition = (String) session.getAttribute("traceTreeReportDefinitionForFolder" + folderId);
	        int traceTreeReportDepth = Integer.parseInt((String) session.getAttribute("traceTreeReportDepthForFolder" +  folderId)); 
	        String standardDisplay = (String) session.getAttribute("traceTreeReportStandardDisplay" + folderId );
			
			// create a file name and set it to it.
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
			String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" TraceTreeReport " + today + ".pdf";			
			filename.replace(' ', '_');
			if (exportType.equals("HTML")){
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}
			
			
			// setting some response headers
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			// setting the content type
			response.setContentType("application/pdf");
			
            
            
        	Document document = new Document(PageSize.A4.rotate());
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			document.open();

			StyleSheet styles = new StyleSheet();
			styles.loadTagStyle("ol", "leading", "16,0");

			// lets add the heading page.
			document.add(new Paragraph(" "));
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
			
			
			Chunk colon = new Chunk( " : " , new Font(Font.TIMES_ROMAN, 10));
			Chunk heading = new Chunk( " Report Title" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		Chunk value =  new Chunk( " Trace Tree Report" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		
			heading = new Chunk( " Report Date" , new Font(Font.TIMES_ROMAN, 10));    		
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( today, new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
			heading = new Chunk( " Report Generated By" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( user.getEmailId(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			
    		// if this is a saved report, then lets print the report info.
    		// note : reportIdString is available only for saved reports.
    		if ((reportIdString != null) && !(reportIdString == "")) {
    			int reportId = Integer.parseInt(reportIdString);
    			Report report = new Report(reportId);


    			heading = new Chunk( "Report Id" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(Integer.toString(report.getReportId()) , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
    			
    			heading = new Chunk( "Report Created By" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getCreatedByEmailId() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);


    			heading = new Chunk( "Report URL" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		String reportURLString = ProjectUtil.getURL(request, report.getReportId(), "report") ;
	    	    Anchor anchor1 = new Anchor(reportURLString, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(reportURLString);
	    	    document.add(anchor1);
        		document.add(Chunk.NEWLINE);

    			heading = new Chunk( "Report Name" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportName() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
			    
    			heading = new Chunk( "Report Description" , new Font(Font.TIMES_ROMAN, 10));    		
        		document.add(heading);
        		document.add(colon);
        		value =  new Chunk(report.getReportDescription() , new Font(Font.TIMES_ROMAN, 10));
        		document.add(value);
        		document.add(Chunk.NEWLINE);
        		
    		}
			
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);


			heading = new Chunk( " Trace Tree Depth " , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( Integer.toString(traceTreeReportDepth), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);
			document.add(Chunk.NEWLINE);

			
			heading = new Chunk( " Project Prefix" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getShortName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
    		heading = new Chunk( " Project Name" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectName(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
    		
    		heading = new Chunk( " Project Description" , new Font(Font.TIMES_ROMAN, 10));
    		document.add(heading);
    		document.add(colon);
    		value =  new Chunk( project.getProjectDescription(), new Font(Font.TIMES_ROMAN, 10));
    		document.add(value);
    		document.add(Chunk.NEWLINE);
			
			document.newPage();

			
    		Iterator i = traceTreeReport.iterator();

            

	        while ( i.hasNext() ) {
	        	TraceTreeRow tTR = (TraceTreeRow) i.next();
	    		Requirement r = (Requirement) tTR.getRequirement();
	    		
	
	    		String traceSpacer = "";
	    		String traceArrow = "";
        		if (tTR.getLevel() == 1){
        			traceSpacer = "";
        			traceArrow  = ""; 
        		}
    			else if (tTR.getLevel() == 2) {					
    					traceSpacer = 	"            ";
    					traceArrow  =   "|___________";
    			}
    			else if (tTR.getLevel() == 3) {
    					traceSpacer = 	"                    ";
    					traceArrow  =   "            |_______";
    			}
    			else if (tTR.getLevel() == 4) {
    					traceSpacer = 	"                            ";
    					traceArrow  =   "                     |______";
    			}
    			else if (tTR.getLevel() == 5) {
    					traceSpacer = 	"                                    ";
    					traceArrow  =   "                            |_______";
    			}
    			else if (tTR.getLevel() == 6) {
    					traceSpacer = 	"                                            ";
    					traceArrow  =   "                                     |______";
    			}
    			else if (tTR.getLevel() == 7) {
    					traceSpacer = 	"                                                ";
    					traceArrow  =   "                                            |___";
    			}
	    		String postTagSpacer = "  ";
	
	    		String nameDescSpacer = traceSpacer  + "       ";
	    		String requirementFullTag = "";
	    		if (r.getProjectId()== project.getProjectId()){
	    			requirementFullTag = r.getRequirementFullTag();
	    		}
	    		else {
	    			requirementFullTag = r.getProjectShortName() + ":" + r.getRequirementFullTag();
	    		}
	    		
	    		if (tTR.getLevel() == 1) {
	    			// for level 1 reqs, do not show green or red.
	    			document.add(new Chunk(traceArrow + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0x00, 0x00) )));
	    		}
	    		if ((tTR.getLevel() >1 ) && (tTR.getTracesToSuspectRequirement() == 0)) {
					document.add(new Chunk(traceArrow + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0x00, 0xFF, 0x00))));
				}
				if ((tTR.getLevel() > 1 && (tTR.getTracesToSuspectRequirement() != 0))){
					document.add(new Chunk(traceArrow + requirementFullTag + postTagSpacer ,
							new Font(Font.TIMES_ROMAN, 10, 
							Font.BOLDITALIC , new Color(0xFF, 0x00, 0x00))));
				}
	    		
	
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");
	    		
	    		Anchor anchor1 = new Anchor("...", 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);
	    	    
	    	    
	    		String requirementName = r.getRequirementName();
	    		if ((requirementName != null) && (requirementName.length() > 120)){
	    			requirementName = requirementName.substring(0, 119) + "...";
	    		}

	    		/*
	    		
	    		document.add(Chunk.NEWLINE);
	    		document.add(new Chunk(nameDescSpacer));
	    	    Anchor anchor1 = new Anchor(requirementName, 
	    	    		FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.UNDERLINE, new Color(0, 0, 255)));
	    	    anchor1.setReference(url);
	    	    document.add(anchor1);	
	    		*/
	    		
	    		document.add(Chunk.NEWLINE);
	    		//document.add(new Chunk(nameDescSpacer + "Name : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
	    		document.add(new Chunk(nameDescSpacer + requirementName , new Font(Font.TIMES_ROMAN, 10)));
	    		
	    		
	    		if (standardDisplay.contains("description")) {
					if (r.getRequirementDescription() != null) {
						//String descToPrint = r.getRequirementDescriptionNoHTML();
						String descToPrint = r.getRequirementDescription();
						document.add(Chunk.NEWLINE);
						com.lowagie.text.html description = new  
			    		document.add(new Chunk(nameDescSpacer+ descToPrint , new Font(Font.TIMES_ROMAN, 10)));
			    		
			    		/*
						
						if (descToPrint == null ) {descToPrint = "";}
						if (descToPrint.length() < 80){
							document.add(Chunk.NEWLINE);
				    		document.add(new Chunk(nameDescSpacer+ descToPrint , new Font(Font.TIMES_ROMAN, 10)));
						}
						else {
							ArrayList<String> chunksToPrint = splitEqually(descToPrint, 80);
							for (String c : chunksToPrint){
								document.add(Chunk.NEWLINE);
					    		document.add(new Chunk(nameDescSpacer+ c , new Font(Font.TIMES_ROMAN, 10)));
							}
						}
						*/
		    		}
	    		}
	    		
    			if (standardDisplay.contains("customAttributes")){
    				

					// a typical uda looks like this
					// Customer:#: SBI:##:Delivery
					// Estimate:#:01/01/12
					
					
					
					String uda = r.getUserDefinedAttributes();
					
					if (uda != null) {
						
						String[] attribs = null;
						
						// lets split uda and put it in the attribs array.
						if (uda.contains(":##:")) {
							attribs = uda.split(":##:");
						}
						else {
							attribs = new String[1];
							attribs[0] = uda;
						}
						
						
						for (int k = 0; k < attribs.length; k++) {
							if ((attribs[k] != null) && (attribs[k].contains(":#:"))) {
								String[] attrib = attribs[k].split(":#:");
								if (attrib.length > 1) {
									// attrib[1] exists. so lets
									// call it.

									if (attrib[1].length() > 0){
				        				String attributeToPrint = attrib[0] + " : " + attrib[1];
					    				document.add(Chunk.NEWLINE);
							    		//document.add(new Chunk(nameDescSpacer + "Description : ", new Font(Font.TIMES_ROMAN, 10, Font.BOLDITALIC )));
							    		document.add(new Chunk(nameDescSpacer+ attributeToPrint , new Font(Font.TIMES_ROMAN, 10)));
									}
								}
							}
						}
					} 
							        				
    				
    				
    				
				}
	    		document.add(Chunk.NEWLINE);

	          }            
            document.close();
			
			
	    // Write the output
		if (exportType.equals("HTML")){
			// the contentlength is needed for MSIE!!!
			response.setContentLength(baos.size());
			// write ByteArrayOutputStream to the ServletOutputStream
			ServletOutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
		}
		if (exportType.equals("file")){
			String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
			// if rootDataDirectory/TraceCloud does not exist, lets create it.
			File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
			if (!(traceCloudRoot.exists() )){
			    new File(rootDataDirectory + File.separator +  "TraceCloud").mkdir();
			}
	
			// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
			File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
			if (!(tempFolderRoot.exists() )){
				new File(rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp").mkdir();
			}
	
			filename = rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp" + File.separator + filename;
			FileOutputStream fileOut = new FileOutputStream(filename);
			baos.writeTo(fileOut);
			fileOut.flush();
			fileOut.close();
		}
	
	} catch (FileNotFoundException fnfe) {
	    // It might not be possible to create the target file.
	    fnfe.printStackTrace();
	} catch (DocumentException de) {
	    // DocumentExceptions arise if you add content to the document before opening or
	    // after closing the document.
	    de.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return filename;
	}
		


	// This method uses the Apache POI module to print out XLS files.
    // used to print out the Metrics reports to excel.
    private String exportMetricsReportToExcel (HttpServletRequest request,
            HttpServletResponse response, String dataType, Project project, User user, String exportType, String databaseType) 
    	throws ServletException, IOException {

    	String filename = "";
		// Get the session. It should have the last View Report in memory.
    	HttpSession session = request.getSession(true);
    	ArrayList reportArrayList = null;
    	if (dataType.equals("projectReport")) {
    		reportArrayList = (ArrayList) session.getAttribute("projectRequirements");
    	}
    	if (dataType.equals("releaseReport")) {
    		reportArrayList = (ArrayList) session.getAttribute("releaseRequirements");
    	}
    	if (dataType.equals("baselineReport")) {
    		reportArrayList = (ArrayList) session.getAttribute("baselineRequirements");
    	}
    	if (dataType.equals("userReport")) {
    		reportArrayList = (ArrayList) session.getAttribute("userRequirements");
    	}

        try {

        	String foldersEnabledForApprovalWorkFlow = project.getFoldersEnabledForApprovalWorkFlow();
        	
    		response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" Report " + today + ".xls";
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Expires", "0");
    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    			response.setHeader("Pragma", "public");
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}

    		

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    pendingStyle.setWrapText(true);
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    approvedStyle.setWrapText(true);
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    rejectedStyle.setWrapText(true);

    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    HSSFSheet sheet  = wb.createSheet("List Report");
            Iterator i = reportArrayList.iterator();
            
            int j = 0;
            
	    	while ( i.hasNext() ) {
	    		Requirement r = (Requirement) i.next();
	    		
	    		// a typical uda looks like this 
	    		// Customer:#: SBI:##:Delivery Estimate:#:01/01/12
	    		String uda = r.getUserDefinedAttributes();
				
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		
	    		
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		HSSFRow row     = sheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Tag               "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("URL To Requirement                                                                                                          "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("Version"));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Name                                                                                                              "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Description                                                                                                        "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Owner                                  "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Testing Status          "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approval Status                "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Priority          "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Percent Complete"));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approved Dt                             "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Pending By                        "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Approved By                        "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Rejected By                        "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Trace To                           "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Trace From                         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("External URL                                                                                            "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Folder Path                                                                                             "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Baselines                          "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Created Date                              "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		cell = row.createCell(++cellNum); 
	        		cell.setCellValue(new HSSFRichTextString ("Attachments                         "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		
	        		
					// lets display the custom attribute based on the display field value
					cell = row.createCell(++cellNum); 
	        		cell.setCellValue("Custom Attributes                                                ");
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        	}

	    	
	    		// print the data rows now.
	    		HSSFRow row     = sheet.createRow(j);
			    
	    		// Create a cell and put a value in it.
				 // make cell0 a hyperlink
	    		
	    		String url = ProjectUtil.getURL(request,r.getRequirementId() ,"requirement");

	    		int cellNum = 0;
			    
			    			    
			    HSSFCell cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementFullTag()));
			    cell.setCellStyle(wrappedStyle);

			    
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (url));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (Integer.toString(r.getVersion())));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementName()));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementDescriptionNoHTML()));
			    cell.setCellStyle(wrappedStyle);

		    	cell = row.createCell(++cellNum);
		    	cell.setCellValue(new HSSFRichTextString (r.getRequirementOwner()));
			    cell.setCellStyle(wrappedStyle);

			    
			    
		    	
		    	HSSFCell testingStatusCell = row.createCell(++cellNum);
		    	testingStatusCell.setCellValue(new HSSFRichTextString (r.getTestingStatus() ));
		    	
		    	HSSFCellStyle testingStatusStyle = wb.createCellStyle();
    			if (r.getTestingStatus().equals("Pending")){
	        		testingStatusStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
	        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	        	}
    			if (r.getTestingStatus().equals("Pass")){
	        		testingStatusStyle.setFillForegroundColor(HSSFColor.GREEN.index);
	        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	        	}
    			if (r.getTestingStatus().equals("Fail")){
	        		testingStatusStyle.setFillForegroundColor(HSSFColor.RED.index);
	        	    testingStatusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	        	}
    			testingStatusCell.setCellStyle(testingStatusStyle);
    		
    			
    			
        	    HSSFCell statusCell = row.createCell(++cellNum);
 
        	    // lets set the status Cell color based on its value
        	    HSSFCellStyle statusStyle = wb.createCellStyle();
		 		// lets see if this requirement is in a folder that is enabled for approval work flow
		 		String folderIdApprovalCheck = "#" + r.getFolderId() + "#";
		 		if (!(foldersEnabledForApprovalWorkFlow.contains(folderIdApprovalCheck))){
		       	    statusCell.setCellValue(new HSSFRichTextString ("Not Applicable"));
		 			statusStyle.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
	        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		 		}
		 		else {
	        	    if (r.getApprovalStatus().equals("Draft")){
	               	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
	        	    	statusStyle.setFillForegroundColor(HSSFColor.PINK.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("In Approval WorkFlow")){
			       	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
				    	statusStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("Approved")){
			       	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
				    	statusStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}
				    if (r.getApprovalStatus().equals("Rejected")){
			       	    statusCell.setCellValue(new HSSFRichTextString (r.getApprovalStatus()));
				    	statusStyle.setFillForegroundColor(HSSFColor.RED.index);
		        	    statusStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		        	}			    
		 		}
			    statusCell.setCellStyle(statusStyle);
    			
			    cell = row.createCell(++cellNum);
			    cell.setCellValue(new HSSFRichTextString (r.getRequirementPriority()));
			    cell.setCellStyle(wrappedStyle);

    			cell = row.createCell(++cellNum);
    			cell.setCellValue(new HSSFRichTextString (r.getRequirementPctComplete() + "" ));
			    cell.setCellStyle(wrappedStyle);

			   cell = row.createCell(++cellNum);
			   cell.setCellValue(new HSSFRichTextString (r.getApprovedByAllDt() ));
		       cell.setCellStyle(wrappedStyle);

			    
			    
			    // lets handle the approvers.
			    String pendingApprovers = "";
			    String approvedApprovers = "";
			    String rejectedApprovers = "";
			    
			    String[] approvers = new String[0] ;
			    if ((r.getApprovers()!= null) && (r.getApprovers().contains(","))){
			    	approvers = r.getApprovers().split(",");
			    }
			    
			    
			    for (int k=0;k<approvers.length;k++){
			    	if (approvers[k].contains("(P)")){
			    		pendingApprovers += approvers[k].replace("(P)","") + ", ";
			    	}
			    	
			    	if (approvers[k].contains("(A)")){
			    		approvedApprovers += approvers[k].replace("(A)","") + ", ";
			    	}
			    	if (approvers[k].contains("(R)")){
			    		rejectedApprovers += approvers[k].replace("(R)","") + ", ";
			    	}
			    }
			    
			    // lets drop the last ,
			    if (pendingApprovers.contains(",")){
			    	pendingApprovers = (String) pendingApprovers.subSequence(0,pendingApprovers.lastIndexOf(","));
			    }			    
			    if (approvedApprovers.contains(",")){
			    	approvedApprovers = (String) approvedApprovers.subSequence(0,approvedApprovers.lastIndexOf(","));
			    }
			    if (rejectedApprovers.contains(",")){
			    	rejectedApprovers = (String) rejectedApprovers.subSequence(0,rejectedApprovers.lastIndexOf(","));
			    }
			    
		    	HSSFCell  pendingCell = row.createCell(++cellNum);
		    	pendingCell.setCellValue(new HSSFRichTextString (pendingApprovers ));
		    	pendingCell.setCellStyle(pendingStyle);
		    	
		    	HSSFCell  approvedCell = row.createCell(++cellNum);
		    	approvedCell.setCellValue(new HSSFRichTextString (approvedApprovers ));
		    	approvedCell.setCellStyle(approvedStyle);
		    	
		    	HSSFCell  rejectedCell = row.createCell(++cellNum);
		    	rejectedCell.setCellValue(new HSSFRichTextString (rejectedApprovers ));
		    	rejectedCell.setCellStyle(rejectedStyle);
		    	
		    	cell = row.createCell(++cellNum);
		    	cell.setCellValue(new HSSFRichTextString (r.getRequirementTraceTo() ));
			    cell.setCellStyle(wrappedStyle);

		    	cell = row.createCell(++cellNum);
		    	cell.setCellValue(new HSSFRichTextString (r.getRequirementTraceFrom() ));
			    cell.setCellStyle(wrappedStyle);

		    	
		    	cell = row.createCell(++cellNum);
		    	cell.setCellValue(new HSSFRichTextString (r.getRequirementExternalUrl()));
			    cell.setCellStyle(wrappedStyle);

		    	cell = row.createCell(++cellNum);
		    	cell.setCellValue(r.getFolderPath());
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(r.getRequirementBaselineString( databaseType));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			    cell.setCellValue(r.getCreatedDt());
			    cell.setCellStyle(wrappedStyle);

			    
		    	String attachmentString = "";
				ArrayList attachments = r.getRequirementAttachments(databaseType);
				if (attachments.size() > 0){  
					Iterator atachmentIterator = attachments.iterator();
					while (atachmentIterator.hasNext()) {
						RequirementAttachment attachment = (RequirementAttachment) atachmentIterator.next();
						attachmentString += "File :  " + attachment.getFileName()  + " \n";
						attachmentString += "Title :  " + attachment.getTitle()  + " \n";
					}															
				} 
		    	cell = row.createCell(++cellNum);
		    	cell.setCellValue(new HSSFRichTextString (attachmentString ));
			    cell.setCellStyle(wrappedStyle);


			    
			    // now to print the custom values.
				cell = row.createCell(++cellNum); 
        		cell.setCellValue(new HSSFRichTextString (r.getUserDefinedAttributesFormatted("newLine") ));
			    cell.setCellStyle(wrappedStyle);
	    	}

	        // Write the output
	    	if (exportType.equals("HTML")){
	    		OutputStream out = response.getOutputStream();
	            wb.write(out);
	            out.close();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
	    		}
	
	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud"  + File.separator + "Temp").mkdir();
	    		}
	
	    		filename = rootDataDirectory + File.separator + "TraceCloud"  + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
	    	}
	    
	    } catch (FileNotFoundException fnfe) {
	        // It might not be possible to create the target file.
	        fnfe.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return filename;
	}
	    
    

	// This method uses the Apache POI module to print out XLS files.
    // used to print out the Metrics reports to excel.
    private String exportProjectDashboardDataTableToExcel (HttpServletRequest request,
            HttpServletResponse response, String dataType, Project project, User user, String exportType, String databaseType) 
    	throws ServletException, IOException {

    	String filename = "";
		// Get the session. It should have the last View Report in memory.
    	HttpSession session = request.getSession(true);
    	ArrayList projectDashboardDataTable =  (ArrayList) session.getAttribute("projectDashboardDataTable");
    	String levelOfDetail = (String) session.getAttribute("levelOfDetail");
    	String focusOn = (String) session.getAttribute("focusOn");
    	
    	

        try {
    		response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" Project Dashboard  " + today + ".xls";
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Expires", "0");
    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    			response.setHeader("Pragma", "public");
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}

    		

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    pendingStyle.setWrapText(true);
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    approvedStyle.setWrapText(true);
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    rejectedStyle.setWrapText(true);

    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    HSSFSheet sheet  = wb.createSheet("List Report");
            Iterator i = projectDashboardDataTable.iterator();
            
            int j = 0;
            
	    	while ( i.hasNext() ) {
	    		String dataTableString = (String) i.next();
	    		String[] dataRow = dataTableString.split(":##:");
	    		
				String dataLoadDt = "";
				String requirementTypeShortName = "";
				String folderPath = "";
				double numOfRequirements = 0.0;
				double numOfDraftRequirements = 0.0;
				double numOfInApprovalWorkflowRequirements = 0.0;
				double numOfRejectedRequirements = 0.0;
				double numOfApprovedRequirements = 0.0;
				double numOfDanglingRequirements = 0.0;
				double numOfOrphanRequirements = 0.0;
				double numOfSuspectUpstreamRequirements = 0.0;
				double numOfSuspectDownstreamRequirements = 0.0;
				double numOfCompletedRequirements = 0.0;
				double numOfIncompleteRequirements = 0.0;
				double numOfTestPendingRequirements = 0.0;
				double numOfTestPassRequirements = 0.0;
				double numOfTestFailRequirements = 0.0;
				int folderId = 0;
	    		
	    		if (dataRow.length > 0) {
					dataLoadDt = dataRow[0];
	    		}
	    		if (dataRow.length > 1) {
					requirementTypeShortName = dataRow[1];
					folderPath = dataRow[1];
	    		}
	    		
	    		try {
		    		if (dataRow.length > 2) {
		    			numOfRequirements =  Double.parseDouble ( dataRow[2]);
		    		}
		    		if (dataRow.length > 3) {
						numOfDraftRequirements = Double.parseDouble( dataRow[3]);
		    		}
		    		if (dataRow.length > 4) {
						numOfInApprovalWorkflowRequirements = Double.parseDouble( dataRow[4] );
		    		}
		    		if (dataRow.length > 5) {
						numOfRejectedRequirements = Double.parseDouble(  dataRow[5] );
		    		}
		    		if (dataRow.length > 6) {
						numOfApprovedRequirements = Double.parseDouble( dataRow[6] );
		    		}
		    		if (dataRow.length > 7) {
						numOfDanglingRequirements = Double.parseDouble(  dataRow[7] );
		    		}
		    		if (dataRow.length > 8) {
						numOfOrphanRequirements = Double.parseDouble(  dataRow[8] );
		    		}
		    		if (dataRow.length > 9) {
						numOfSuspectUpstreamRequirements = Double.parseDouble( dataRow[9] );
		    		}
		    		if (dataRow.length > 10) {
						numOfSuspectDownstreamRequirements = Double.parseDouble( dataRow[10] );
		    		}
		    		if (dataRow.length > 11) {
		    			numOfCompletedRequirements = Double.parseDouble(  dataRow[11] );
		    		}
		    		if (dataRow.length > 12) {
						numOfIncompleteRequirements = Double.parseDouble( dataRow[12] );
		    		}
		    		
		    		if (dataRow.length > 13) {
						numOfTestPendingRequirements = Double.parseDouble( dataRow[13] );
		    		}
		    		if (dataRow.length > 14) {
						numOfTestPassRequirements = Double.parseDouble(  dataRow[14] );
		    		}
		    		if (dataRow.length > 15) {
						numOfTestFailRequirements = Double.parseDouble( dataRow[15] );
		    		}
	    		}
	    		catch (Exception e){
	    			// do nothing.
	    		}
	    		
	    		if (dataRow.length > 16) {
	    			try {
					folderId = Integer.parseInt(dataRow[16]);
	    			}
	    			catch (Exception e){
	    				folderId = 0;
	    			}
	    		}	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		
	    		
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		HSSFRow row     = sheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		if (levelOfDetail.equals("project")){
	        			cell.setCellValue(new HSSFRichTextString ("Req Type                "));
	        		}
	        		else {
	        			cell.setCellValue(new HSSFRichTextString ("Folder                             				                             "));
	        		}
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("All             "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		if (focusOn.contains("Approval")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Draft"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Pending"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Rejected"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Approved"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (focusOn.contains("Traceability")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Dangling"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Orphan"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Suspect UpStream"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Suspect DownStream"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (focusOn.contains("Testing")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing - Pending"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing - Pass"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing - Fail"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (focusOn.contains("Completion")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Completed"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("InComplete"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	    		}

	    	
	    		// print the data rows now.
	    		HSSFRow row     = sheet.createRow(j);
			    
	    		// Create a cell and put a value in it.
				 // make cell0 a hyperlink
	    		
	    		int cellNum = 0;
			    
	    	
			    			    
			    HSSFCell cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString (requirementTypeShortName));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			
			    cell.setCellValue(numOfRequirements);
			    cell.setCellStyle(wrappedStyle);

			    if (focusOn.contains("Approval")){ 
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(numOfDraftRequirements);
				    cell.setCellStyle(wrappedStyle);
	
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(numOfInApprovalWorkflowRequirements);
				    cell.setCellStyle(wrappedStyle);
	
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfRejectedRequirements);
				    cell.setCellStyle(wrappedStyle);
				
			
					cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfApprovedRequirements);
				    cell.setCellStyle(wrappedStyle);
			    }
			    
			    if (focusOn.contains("Traceability")){ 
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfDanglingRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfOrphanRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfSuspectUpstreamRequirements);
				    cell.setCellStyle(wrappedStyle);
			    
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfSuspectDownstreamRequirements);
				    cell.setCellStyle(wrappedStyle);
			    }
			    
			    if (focusOn.contains("Testing")){ 
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfTestPendingRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfTestPassRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfTestFailRequirements);
				    cell.setCellStyle(wrappedStyle);
				    }
	    	
	    		if (focusOn.contains("Completion")){ 
			        cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfCompletedRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfIncompleteRequirements);
				    cell.setCellStyle(wrappedStyle);
				}
			    
	    	}

	        // Write the output
	    	if (exportType.equals("HTML")){
	    		OutputStream out = response.getOutputStream();
	            wb.write(out);
	            out.close();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
	    		}
	
	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud"  + File.separator + "Temp").mkdir();
	    		}
	
	    		filename = rootDataDirectory + File.separator + "TraceCloud"  + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
	    	}
	    
	    } catch (FileNotFoundException fnfe) {
	        // It might not be possible to create the target file.
	        fnfe.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return filename;
	}

    
    private String exportReleaseDashboardDataTableToExcel (HttpServletRequest request,
            HttpServletResponse response, String dataType, Project project, User user, String exportType, String databaseType) 
    	throws ServletException, IOException {

    	String filename = "";
		// Get the session. It should have the last View Report in memory.
    	HttpSession session = request.getSession(true);
    	ArrayList releaseDashboardDataTable =  (ArrayList) session.getAttribute("releaseDashboardDataTable");
    	String levelOfDetail = (String) session.getAttribute("levelOfDetail");
    	String focusOn = (String) session.getAttribute("focusOn");
    	
    	

        try {
    		response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		filename = user.getFirstName() + " " + user.getLastName()  +" Release Dashboard  " + today + ".xls";
    		filename.replace(' ', '_');
    		if (exportType.equals("HTML")){
    			response.setHeader("Expires", "0");
    			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    			response.setHeader("Pragma", "public");
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    		}

    		

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		HSSFCreationHelper createHelper = (HSSFCreationHelper) wb.getCreationHelper(); 

    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    

    	    HSSFCellStyle pendingStyle = wb.createCellStyle();
    	    HSSFFont pendingFont = wb.createFont();
    	    pendingFont.setColor(HSSFColor.INDIGO.index);
    	    pendingStyle.setFont(pendingFont);   		
    	    pendingStyle.setWrapText(true);
    	    
    	    HSSFCellStyle approvedStyle = wb.createCellStyle();
    	    HSSFFont approvedFont = wb.createFont();
    	    approvedFont.setColor(HSSFColor.GREEN.index);
    	    approvedStyle.setFont(approvedFont);   		
    	    approvedStyle.setWrapText(true);
    	    
    	    HSSFCellStyle rejectedStyle = wb.createCellStyle();
    	    HSSFFont rejectedFont = wb.createFont();
    	    rejectedFont.setColor(HSSFColor.RED.index);
    	    rejectedStyle.setFont(rejectedFont);   		
    	    rejectedStyle.setWrapText(true);

    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    HSSFSheet sheet  = wb.createSheet("List Report");
            Iterator i = releaseDashboardDataTable.iterator();
            
            int j = 0;
            
	    	while ( i.hasNext() ) {
	    		String dataTableString = (String) i.next();
	    		String[] dataRow = dataTableString.split(":##:");
	    		
				String dataLoadDt = "";
				String requirementTypeShortName = "";
				String folderPath = "";
				double numOfRequirements = 0.0;
				double numOfDraftRequirements = 0.0;
				double numOfInApprovalWorkflowRequirements = 0.0;
				double numOfRejectedRequirements = 0.0;
				double numOfApprovedRequirements = 0.0;
				double numOfDanglingRequirements = 0.0;
				double numOfOrphanRequirements = 0.0;
				double numOfSuspectUpstreamRequirements = 0.0;
				double numOfSuspectDownstreamRequirements = 0.0;
				double numOfCompletedRequirements = 0.0;
				double numOfIncompleteRequirements = 0.0;
				double numOfTestPendingRequirements = 0.0;
				double numOfTestPassRequirements = 0.0;
				double numOfTestFailRequirements = 0.0;
				int folderId = 0;
	    		
	    		if (dataRow.length > 0) {
					dataLoadDt = dataRow[0];
	    		}
	    		if (dataRow.length > 1) {
					requirementTypeShortName = dataRow[1];
					folderPath = dataRow[1];
	    		}
	    		
	    		try {
		    		if (dataRow.length > 2) {
		    			numOfRequirements =  Double.parseDouble ( dataRow[2]);
		    		}
		    		if (dataRow.length > 3) {
						numOfDraftRequirements = Double.parseDouble( dataRow[3]);
		    		}
		    		if (dataRow.length > 4) {
						numOfInApprovalWorkflowRequirements = Double.parseDouble( dataRow[4] );
		    		}
		    		if (dataRow.length > 5) {
						numOfRejectedRequirements = Double.parseDouble(  dataRow[5] );
		    		}
		    		if (dataRow.length > 6) {
						numOfApprovedRequirements = Double.parseDouble( dataRow[6] );
		    		}
		    		if (dataRow.length > 7) {
						numOfDanglingRequirements = Double.parseDouble(  dataRow[7] );
		    		}
		    		if (dataRow.length > 8) {
						numOfOrphanRequirements = Double.parseDouble(  dataRow[8] );
		    		}
		    		if (dataRow.length > 9) {
						numOfSuspectUpstreamRequirements = Double.parseDouble( dataRow[9] );
		    		}
		    		if (dataRow.length > 10) {
						numOfSuspectDownstreamRequirements = Double.parseDouble( dataRow[10] );
		    		}
		    		if (dataRow.length > 11) {
		    			numOfCompletedRequirements = Double.parseDouble(  dataRow[11] );
		    		}
		    		if (dataRow.length > 12) {
						numOfIncompleteRequirements = Double.parseDouble( dataRow[12] );
		    		}
		    		
		    		if (dataRow.length > 13) {
						numOfTestPendingRequirements = Double.parseDouble( dataRow[13] );
		    		}
		    		if (dataRow.length > 14) {
						numOfTestPassRequirements = Double.parseDouble(  dataRow[14] );
		    		}
		    		if (dataRow.length > 15) {
						numOfTestFailRequirements = Double.parseDouble( dataRow[15] );
		    		}
	    		}
	    		catch (Exception e){
	    			// do nothing.
	    		}
	    		
	    		if (dataRow.length > 16) {
	    			try {
					folderId = Integer.parseInt(dataRow[16]);
	    			}
	    			catch (Exception e){
	    				folderId = 0;
	    			}
	    		}	    		
	    		// Create a row and put some cells in it. Rows are 0 based.
	    		j++;
	    		
	    		// for the first row, print the header and user defined columns headers. etc..
	    		
	    		
	    		if (j == 1){

	        		// Create a row and put some cells in it. Rows are 0 based.
	        		HSSFRow row     = sheet.createRow((short)0);	
	        		

	        		// Print the header row for the excel file.
	        		int cellNum = 0;
	        		int column = 0;
	        		
	        		HSSFCell cell = row.createCell(cellNum);
	        		if (levelOfDetail.equals("project")){
	        			cell.setCellValue(new HSSFRichTextString ("Req Type                "));
	        		}
	        		else {
	        			cell.setCellValue(new HSSFRichTextString ("Folder                             				                             "));
	        		}
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		cell = row.createCell(++cellNum);
	        		cell.setCellValue(new HSSFRichTextString ("All             "));
	        		cell.setCellStyle(headerStyle);
	        		sheet.autoSizeColumn(column++);
	        		
	        		if (focusOn.contains("Approval")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Draft"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Pending"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Rejected"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Approval - Approved"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (focusOn.contains("Traceability")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Dangling"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Orphan"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Suspect UpStream"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Traceability - Suspect DownStream"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (focusOn.contains("Testing")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing - Pending"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing - Pass"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Testing - Fail"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	        		
	        		if (focusOn.contains("Completion")){ 
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("Completed"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
		        		
		        		
		        		cell = row.createCell(++cellNum); 
		        		cell.setCellValue(new HSSFRichTextString ("InComplete"));
		        		cell.setCellStyle(headerStyle);
		        		sheet.autoSizeColumn(column++);
	        		}
	    		}

	    	
	    		// print the data rows now.
	    		HSSFRow row     = sheet.createRow(j);
			    
	    		// Create a cell and put a value in it.
				 // make cell0 a hyperlink
	    		
	    		int cellNum = 0;
			    
	    	
			    			    
			    HSSFCell cell = row.createCell(cellNum);
			    cell.setCellValue(new HSSFRichTextString (requirementTypeShortName));
			    cell.setCellStyle(wrappedStyle);

			    cell = row.createCell(++cellNum);
			
			    cell.setCellValue(numOfRequirements);
			    cell.setCellStyle(wrappedStyle);

			    if (focusOn.contains("Approval")){ 
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(numOfDraftRequirements);
				    cell.setCellStyle(wrappedStyle);
	
				    cell = row.createCell(++cellNum);
				    cell.setCellValue(numOfInApprovalWorkflowRequirements);
				    cell.setCellStyle(wrappedStyle);
	
			    	cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfRejectedRequirements);
				    cell.setCellStyle(wrappedStyle);
				
			
					cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfApprovedRequirements);
				    cell.setCellStyle(wrappedStyle);
			    }
			    
			    if (focusOn.contains("Traceability")){ 
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfDanglingRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfOrphanRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfSuspectUpstreamRequirements);
				    cell.setCellStyle(wrappedStyle);
			    
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfSuspectDownstreamRequirements);
				    cell.setCellStyle(wrappedStyle);
			    }
			    
			    if (focusOn.contains("Testing")){ 
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfTestPendingRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfTestPassRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfTestFailRequirements);
				    cell.setCellStyle(wrappedStyle);
				    }
	    	
	    		if (focusOn.contains("Completion")){ 
			        cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfCompletedRequirements);
				    cell.setCellStyle(wrappedStyle);
				    
				    cell = row.createCell(++cellNum);
			    	cell.setCellValue(numOfIncompleteRequirements);
				    cell.setCellStyle(wrappedStyle);
				}
			    
	    	}

	        // Write the output
	    	if (exportType.equals("HTML")){
	    		OutputStream out = response.getOutputStream();
	            wb.write(out);
	            out.close();
	    	}
	    	if (exportType.equals("file")){
	    		String rootDataDirectory = this.getServletContext().getInitParameter("rootDataDirectory");
	    		// if rootDataDirectory/TraceCloud does not exist, lets create it.
	    		File traceCloudRoot = new File (rootDataDirectory + File.separator +  "TraceCloud");
	    		if (!(traceCloudRoot.exists() )){
	    		    new File(rootDataDirectory + File.separator + "TraceCloud").mkdir();
	    		}
	
	    		// if rootDataDirectory/TraceCloud/Temp does not exist, lets create it.
	    		File tempFolderRoot  = new File (rootDataDirectory + File.separator + "TraceCloud" + File.separator + "Temp");
	    		if (!(tempFolderRoot.exists() )){
	    			new File(rootDataDirectory + File.separator + "TraceCloud"  + File.separator + "Temp").mkdir();
	    		}
	
	    		filename = rootDataDirectory + File.separator + "TraceCloud"  + File.separator + "Temp" + File.separator + filename;
	    		FileOutputStream fileOut = new FileOutputStream(filename);
	    		wb.write(fileOut);
	    		fileOut.close();
	    	}
	    
	    } catch (FileNotFoundException fnfe) {
	        // It might not be possible to create the target file.
	        fnfe.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return filename;
	}

    
    
    private String exportProjectTrends (HttpServletRequest request,
            HttpServletResponse response, Project project, User user, String action, int releaseId, int rTBaselineId,
            int folderId, String databaseType) 
    	throws ServletException, IOException {

    	String filename = "";
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
        try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		
    		
    		if (action.equals("exportProjectTrendData")){
    			filename = user.getFirstName() + " " + user.getLastName()  +" Project Trend Report " + today + ".xls";
    		}
    		if (action.equals("exportReleaseTrendData")){
    			filename = user.getFirstName() + " " + user.getLastName()  +" Release Trend Report " + today + ".xls";
    		}
    		if (action.equals("exportBaselineTrendData")){
    			filename = user.getFirstName() + " " + user.getLastName()  +" Baseline Trend Report " + today + ".xls";
    		}
    		if (action.equals("exportFolderTrendData")){
    			filename = user.getFirstName() + " " + user.getLastName()  +" Folder Trend Report " + today + ".xls";
    		}
    		
    		response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + action + ".xls\"");
		
    		
    		
    		
    		filename.replace(' ', '_');


			

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		
    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    HSSFSheet infoSheet  = wb.createSheet("Summary");
    	    int startRow = 5; 
    		HSSFRow row     = infoSheet.createRow((short)startRow++);


    		String reportType = "";
    		if (action.equals("exportProjectTrendData")){
    			reportType = "Project Trend Data";
    		}
    		if (action.equals("exportReleaseTrendData")){
    			reportType = "Release Trend Data";
    		}
    		if (action.equals("exportBaselineTrendData")){
    			reportType = "Baseline Trend Data";
    		}
  
    		if (action.equals("exportFolderTrendData")){
    			reportType = "Folder Trend Data";
    		}    		
    		
    		
    		row     = infoSheet.createRow((short)startRow++);
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Type"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(reportType);
    		
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		startRow += 2;
    		

    		// project Info.
    		startRow += 4;
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		
    		
    		if (action.equals("exportReleaseTrendData")){
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Release Name"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		Requirement release = new  Requirement(releaseId, databaseType);
        		cellB.setCellValue(release.getRequirementFullTag() + " : " + release.getRequirementName());
    		}
    	    
    		if (action.equals("exportBaselineTrendData")){
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Baseline Name"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		RTBaseline rTBaseline = new  RTBaseline (rTBaselineId);
        		cellB.setCellValue(rTBaseline.getBaselineName());
    		}

    		if (action.equals("exportFolderTrendData")){
        		row     = infoSheet.createRow((short)startRow++);
        		cellA = row.createCell(2);
        		cellA.setCellValue(new HSSFRichTextString ("Folder Name"));
        		cellA.setCellStyle(headerStyle);
        		row.createCell(3).setCellStyle(headerStyle);
        		cellB = row.createCell(4);
        		Folder folder = new  Folder (folderId);
        		cellB.setCellValue(folder.getFolderPath() );
    		}
    	    
    		
    		
    		
    	    HSSFSheet allSheet  = wb.createSheet("All Requirements");
    	    HSSFSheet draftSheet  = wb.createSheet("Draft Reqs");
    	    HSSFSheet pendingSheet  = wb.createSheet("Pending Approval Reqs");
    	    HSSFSheet rejectedSheet  = wb.createSheet("Rejected Reqs");
    	    HSSFSheet approvedSheet  = wb.createSheet("Approved Reqs");
    	    HSSFSheet danglingSheet  = wb.createSheet("Dangling Reqs");
    	    HSSFSheet orphanSheet  = wb.createSheet("Orphan Reqs");
    	    HSSFSheet suspectUpstreamSheet  = wb.createSheet("Suspect Upstream Reqs");
    	    HSSFSheet suspectDownstreamSheet  = wb.createSheet("Suspect Downstream Reqs");
    	    HSSFSheet testPendingSheet  = wb.createSheet("Test Pending Requirements");
    	    HSSFSheet testPassSheet  = wb.createSheet("Test Pass Requirements");
    	    HSSFSheet testFailSheet  = wb.createSheet("Test Fail Requirements");
    	    HSSFSheet completedSheet  = wb.createSheet("Completed Requirements");
    	    HSSFSheet incompleteSheet  = wb.createSheet("Incomplete Requirements");
    	    
    	    // use the hashmap to store the dataloaddate to the excel file row num map.
    	    HashMap rowDateMap = new HashMap();
    	    
    	    String sql = "";
    	    
    	    
    		if (action.equals("exportProjectTrendData")){
    			sql = "select short_name from  gr_requirement_types where project_id = ?" +
        	    		" order by display_sequence ";
    			
    		}
    		if (action.equals("exportReleaseTrendData")){
    			sql = " select distinct rm.requirement_type_short_name 'short_name' " +
	    				" from  gr_requirement_types rt, gr_release_metrics rm " +
	    				" where rm.project_id = ? " +
	    				" and rm.release_id = ? " +
	    				" and rm.project_id = rt.project_id " +
	        	    	" order by display_sequence ";
    		}
    		if (action.equals("exportBaselineTrendData")){
    			sql = " select distinct bm.requirement_type_short_name 'short_name' " +
	    				" from   gr_baseline_metrics bm " +
	    				" where bm.project_id = ? " +
	    				" and bm.rt_baseline_id = ? " ;
    		}   
    		if (action.equals("exportFolderTrendData")){
    			sql = " select distinct fm.requirement_type_short_name 'short_name' " +
	    				" from   gr_folder_metrics fm " +
	    				" where fm.project_id = ? " +
	    				" and fm.folder_id = ? " ;
    		}   
    		
    		
    	    prepStmt = con.prepareStatement(sql);

    	    
    		if (action.equals("exportProjectTrendData")){
    			prepStmt.setInt(1, project.getProjectId());
    		}

    		if (action.equals("exportReleaseTrendData")){
    			prepStmt.setInt(1, project.getProjectId());
    			prepStmt.setInt(2, releaseId);
    		}
    		if (action.equals("exportBaselineTrendData")){
    			prepStmt.setInt(1, project.getProjectId());
    			prepStmt.setInt(2, rTBaselineId);
    		}
    		
    		if (action.equals("exportFolderTrendData")){
    			prepStmt.setInt(1, project.getProjectId());
    			prepStmt.setInt(2, folderId);
    		}
    		
    	    rs   = prepStmt.executeQuery();
			int colNum = 0;
			while (rs.next()){
				// Iterate through the db results and print the metrics for this req type
				colNum++;
				
				String reqType = rs.getString("short_name");
				

	    		if (databaseType.equals("mySQL")){
		    		if (action.equals("exportProjectTrendData")){
		    			sql = " select date_format(data_load_dt, '%Y-%m-%d') 'dataLoadDt' , " +
								" num_of_requirements, " +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs', " +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs," +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs, " +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs," +
								" num_of_incomplete_reqs  " +
								" from gr_project_metrics " +
								" where project_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
		    		}
		    		if (action.equals("exportReleaseTrendData")){
		    			sql = " select date_format(data_load_dt, '%Y-%m-%d') 'dataLoadDt' , " +
								" num_of_requirements, " +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs', " +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs," +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs, " +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs," +
								" num_of_incomplete_reqs  " +
								" from gr_release_metrics " +
								" where project_id = ? " +
								" and release_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
		    			
		    		}
		    		if (action.equals("exportBaselineTrendData")){
		    			sql = " select date_format(data_load_dt, '%Y-%m-%d') 'dataLoadDt' , " +
								" num_of_requirements, " +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs', " +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs," +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs, " +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs," +
								" num_of_incomplete_reqs  " +
								" from gr_baseline_metrics " +
								" where project_id = ? " +
								" and rt_baseline_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
		    			

		    		}	
		    		
		    		if (action.equals("exportFolderTrendData")){
		    			sql = " select date_format(data_load_dt, '%Y-%m-%d') 'dataLoadDt' , " +
								" num_of_requirements, " +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs', " +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs," +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs, " +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs," +
								" num_of_incomplete_reqs  " +
								" from gr_folder_metrics " +
								" where project_id = ? " +
								" and folder_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
		    			

		    		}		    		
					
				}
				else {
					
					if (action.equals("exportProjectTrendData")){
						sql = " select to_char(data_load_dt, 'DD MON YYYY')  \"data_load_dt\"  ," +
								" num_of_requirements," +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs'," +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs, " +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs," +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs, " +
								" num_of_incomplete_reqs  " +
								" from gr_project_metrics " +
								" where project_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
							
		    		}
		    		if (action.equals("exportReleaseTrendData")){
		    			sql = " select to_char(data_load_dt, 'DD MON YYYY')  \"data_load_dt\"  ," +
								" num_of_requirements," +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs'," +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs, " +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs," +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs, " +
								" num_of_incomplete_reqs  " +
								" from gr_release_metrics " +
								" where project_id = ? " +
								" and release_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
		    			
		    		}
		    		if (action.equals("exportBaselineTrendData")){
		    			sql = " select to_char(data_load_dt, 'DD MON YYYY')  \"data_load_dt\"  ," +
								" num_of_requirements," +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs'," +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs, " +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs," +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs, " +
								" num_of_incomplete_reqs  " +
								" from gr_baseline_metrics " +
								" where project_id = ? " +
								" and rt_baseline_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
		    			
		    		}	
		    		if (action.equals("exportFolderTrendData")){
		    			sql = " select to_char(data_load_dt, 'DD MON YYYY')  \"data_load_dt\"  ," +
								" num_of_requirements," +
								" num_of_draft_reqs, " +
								" num_of_in_workflow_reqs 'num_of_pending_reqs'," +
								" num_of_rejected_reqs, " +
								" num_of_approved_reqs," +
								" num_of_dangling_reqs, " +
								" num_of_orphan_reqs, " +
								" num_of_suspect_upstream_reqs, " +
								" num_of_suspect_downstream_reqs, " +
								" num_of_test_pending_reqs, " +
								" num_of_test_pass_reqs," +
								" num_of_test_fail_reqs, " +
								" num_of_completed_reqs, " +
								" num_of_incomplete_reqs  " +
								" from gr_folder_metrics " +
								" where project_id = ? " +
								" and folder_id = ? " +
								" and requirement_type_short_name = ?" +
								" order by data_load_dt asc";
		    			
		    		}			    		
				}
								
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				if (action.equals("exportProjectTrendData")){
					prepStmt2.setInt(1, project.getProjectId());
					prepStmt2.setString(2, reqType);
						
	    		}
	    		if (action.equals("exportReleaseTrendData")){
	    			prepStmt2.setInt(1, project.getProjectId());
	    			prepStmt2.setInt(2, releaseId);
					prepStmt2.setString(3, reqType);
					
	    		}
	    		
	    		if (action.equals("exportBaselineTrendData")){
	    			prepStmt2.setInt(1, project.getProjectId());
	    			prepStmt2.setInt(2, rTBaselineId);
					prepStmt2.setString(3, reqType);
					
	    		}	    		
	    		
	    		if (action.equals("exportFolderTrendData")){
	    			prepStmt2.setInt(1, project.getProjectId());
	    			prepStmt2.setInt(2, folderId);
					prepStmt2.setString(3, reqType);
					
	    		}	
	    		
				ResultSet rs2 = prepStmt2.executeQuery();
				
				int rowNum = 0;
				while (rs2.next()){
					rowNum++;
					
					String dataLoadDt = rs2.getString("dataLoadDt");
					int numOfRequirements = rs2.getInt("num_of_requirements");
					int numOfDraftRequirements  = rs2.getInt("num_of_draft_reqs");
					int numOfPendingRequirements = rs2.getInt("num_of_pending_reqs");
					int numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
					int numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
					int numOfDanglingRequirements  = rs2.getInt("num_of_dangling_reqs");
					int numOfOrphanRequirements  = rs2.getInt("num_of_orphan_reqs");
					int numOfSuspectUpstreamRequirements  = rs2.getInt("num_of_suspect_upstream_reqs");
					int numOfSuspectDownstreamRequirements  = rs2.getInt("num_of_suspect_downstream_reqs");
					int numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
					int numOfTestPassRequirements  = rs2.getInt("num_of_test_pass_reqs");
					int numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
					int numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
					int numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
					
					System.out.println("srt running exportReleaseTrendData  for req type  " + reqType + " dataLoadDt is  " + dataLoadDt + " numOfREq is " + numOfRequirements);
					
					
		    		
					// If colNum == 1, then Print the date column. In this, the first row has the 'Date' label, and all the other rows have the
					// actual date value.
				    if (colNum == 1){
						// this is the first iteration, so lets print the Column 0 (date values)
				    	if (rowNum ==1){
							// this is the first row, so lets print the column headers
							
				    		setStringCellValue(allSheet, 0, colNum - 1, "Date");
				    		setStringCellValue(draftSheet, 0, colNum - 1, "Date");
							setStringCellValue(pendingSheet, 0, colNum - 1, "Date");
							setStringCellValue(rejectedSheet, 0, colNum - 1, "Date");
							setStringCellValue(approvedSheet, 0, colNum - 1, "Date");
							setStringCellValue(danglingSheet, 0, colNum - 1, "Date");
							setStringCellValue(orphanSheet, 0, colNum - 1, "Date");
							setStringCellValue(suspectUpstreamSheet, 0, colNum - 1, "Date");
							setStringCellValue(suspectDownstreamSheet, 0, colNum - 1, "Date");
							setStringCellValue(testPendingSheet, 0, colNum - 1, "Date");
							setStringCellValue(testPassSheet, 0, colNum - 1, "Date");
							setStringCellValue(testFailSheet, 0, colNum - 1, "Date");
							setStringCellValue(completedSheet, 0, colNum - 1, "Date");
							setStringCellValue(incompleteSheet, 0, colNum - 1, "Date");
							
						}
				    	
				    	
				    	// lets print the Data Load Dt in column 1 for each of the sheets
						setStringCellValue(allSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(draftSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(pendingSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(rejectedSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(approvedSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(danglingSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(orphanSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(suspectUpstreamSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(suspectDownstreamSheet, rowNum, colNum - 1, dataLoadDt);
						
						setStringCellValue(testPendingSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(testPassSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(testFailSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(completedSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(incompleteSheet, rowNum, colNum - 1, dataLoadDt);
						
						// for the first time , when we print the dataLoadDt, lets store the excel row number here.
						System.out.println("puttin dataLoadDt " + dataLoadDt + " rowNum " + rowNum);
						rowDateMap.put(dataLoadDt, new Integer(rowNum));
						
				   }
				    
				    
			    	if (rowNum ==1){
						// For the first row, print the name of the req type in each sheet, one per column
						setStringCellValue(allSheet, 0, colNum, reqType);
						setStringCellValue(draftSheet, 0, colNum, reqType);
						setStringCellValue(pendingSheet, 0, colNum, reqType);
						setStringCellValue(rejectedSheet, 0, colNum, reqType);
						setStringCellValue(approvedSheet, 0, colNum, reqType);
						setStringCellValue(danglingSheet, 0, colNum, reqType);
						setStringCellValue(orphanSheet, 0, colNum, reqType);
						setStringCellValue(suspectUpstreamSheet, 0, colNum, reqType);
						setStringCellValue(suspectDownstreamSheet, 0, colNum, reqType);
						
						setStringCellValue(testPendingSheet, 0, colNum, reqType);
						setStringCellValue(testPassSheet, 0, colNum, reqType);
						setStringCellValue(testFailSheet, 0, colNum, reqType);
						setStringCellValue(completedSheet, 0, colNum, reqType);
						setStringCellValue(incompleteSheet, 0, colNum, reqType);
						
					}
			    	
			    	
			    	// Lets print the number of requirements in each of the sheets for this req type column.
		    		
			    	int excelRowNum = 0;
			    	// for any dataLoadDt, lets get the excel row num where it should go based on the HashMap.
			    	Integer excelRowNumInteger = (Integer) rowDateMap.get(dataLoadDt);
			    	try{
			    		excelRowNum = excelRowNumInteger.intValue();
			    	}
			    	catch(Exception excelRowNumIntegerException){
			    		excelRowNumIntegerException.printStackTrace();
			    	}
					setIntCellValue(allSheet, excelRowNum, colNum, numOfRequirements);
					setIntCellValue(draftSheet, excelRowNum, colNum, numOfDraftRequirements);
					setIntCellValue(pendingSheet, excelRowNum, colNum, numOfPendingRequirements);
					setIntCellValue(rejectedSheet, excelRowNum, colNum, numOfRejectedRequirements);
					setIntCellValue(approvedSheet, excelRowNum, colNum, numOfApprovedRequirements);
					setIntCellValue(danglingSheet, excelRowNum, colNum, numOfDanglingRequirements);
					setIntCellValue(orphanSheet, excelRowNum, colNum, numOfOrphanRequirements);
					setIntCellValue(suspectUpstreamSheet, excelRowNum, colNum, numOfSuspectUpstreamRequirements);
					setIntCellValue(suspectDownstreamSheet, excelRowNum, colNum, numOfSuspectDownstreamRequirements);
					
					setIntCellValue(testPendingSheet, excelRowNum, colNum, numOfTestPendingRequirements);
					setIntCellValue(testPassSheet, excelRowNum, colNum, numOfTestPassRequirements);
					setIntCellValue(testFailSheet, excelRowNum, colNum, numOfTestFailRequirements);
					setIntCellValue(completedSheet, excelRowNum, colNum, numOfCompletedRequirements);
					setIntCellValue(incompleteSheet, excelRowNum, colNum, numOfIncompleteRequirements);
					
				}
    	    
    	    
			}
	        
	 
			

			// lets write the output out.
    		OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
	    	
			rs.close();
			prepStmt.close();
			con.close();
			
			
	    } catch (FileNotFoundException fnfe) {
	        // It might not be possible to create the target file.
	        fnfe.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
        finally {
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
	    return filename;
	}
    
    
    private String exportReleaseTrends (HttpServletRequest request,
            HttpServletResponse response, Project project, User user, String action, int releaseId, int rTBaselineId,
            int folderId, String databaseType) 
    	throws ServletException, IOException {

    	String filename = "";
		
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
        try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context
					.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();
			
			response.setContentType("application/vnd.ms-excel");
    		// create a file name and set it to it.
    		Calendar cal = Calendar.getInstance();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    		String today =  sdf.format(cal.getTime());
    		
    		
    		
    		filename = user.getFirstName() + " " + user.getLastName()  +" Release Trend Report " + today + ".xls";
    		
    		response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Pragma", "public");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + action + ".xls\"");
		
    		
    		
    		
    		filename.replace(' ', '_');


			

    		
    		HSSFWorkbook wb = new HSSFWorkbook();
    		
    		HSSFCellStyle headerStyle = wb.createCellStyle();
    		headerStyle.setFillForegroundColor(HSSFColor.AQUA.index);
    	    headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    	    
    	    HSSFCellStyle wrappedStyle = wb.createCellStyle();
    	    wrappedStyle.setWrapText(true);
    	    /////////////////////////////////////////
    	    //
    	    // lets build the Report Cover Page.
    	    //
    	    /////////////////////////////////////////
    	    
    	    HSSFSheet infoSheet  = wb.createSheet("Summary");
    	    int startRow = 5; 
    		HSSFRow row     = infoSheet.createRow((short)startRow++);


    		String reportType = "Release Trend Data";
    		  		
    		
    		row     = infoSheet.createRow((short)startRow++);
    		HSSFCell cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Type"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		HSSFCell cellB = row.createCell(4);
    		cellB.setCellValue(reportType);
    		
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Date"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (today));
    		
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Report Generated By "));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (user.getEmailId()));

    		startRow += 2;
    		

    		// project Info.
    		startRow += 4;
    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Prefix"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getShortName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectName()));

    		row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Project Description"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		cellB.setCellValue(new HSSFRichTextString (project.getProjectDescription()));
    		
    		
			row     = infoSheet.createRow((short)startRow++);
    		cellA = row.createCell(2);
    		cellA.setCellValue(new HSSFRichTextString ("Release Name"));
    		cellA.setCellStyle(headerStyle);
    		row.createCell(3).setCellStyle(headerStyle);
    		cellB = row.createCell(4);
    		Requirement release = new  Requirement(releaseId, databaseType);
    		cellB.setCellValue(release.getRequirementFullTag() + " : " + release.getRequirementName());
		
    	    
    		
    		
    		
    	    HSSFSheet allSheet  = wb.createSheet("All Requirements");
    	    HSSFSheet draftSheet  = wb.createSheet("Draft Reqs");
    	    HSSFSheet pendingSheet  = wb.createSheet("Pending Approval Reqs");
    	    HSSFSheet rejectedSheet  = wb.createSheet("Rejected Reqs");
    	    HSSFSheet approvedSheet  = wb.createSheet("Approved Reqs");
    	    HSSFSheet danglingSheet  = wb.createSheet("Dangling Reqs");
    	    HSSFSheet orphanSheet  = wb.createSheet("Orphan Reqs");
    	    HSSFSheet suspectUpstreamSheet  = wb.createSheet("Suspect Upstream Reqs");
    	    HSSFSheet suspectDownstreamSheet  = wb.createSheet("Suspect Downstream Reqs");
    	    HSSFSheet testPendingSheet  = wb.createSheet("Test Pending Requirements");
    	    HSSFSheet testPassSheet  = wb.createSheet("Test Pass Requirements");
    	    HSSFSheet testFailSheet  = wb.createSheet("Test Fail Requirements");
    	    HSSFSheet completedSheet  = wb.createSheet("Completed Requirements");
    	    HSSFSheet incompleteSheet  = wb.createSheet("Incomplete Requirements");
    	    
    	    
    	    // use the hashmap to store the dataloaddate to the excel file row num map.
    	    HashMap rowDateMap = new HashMap();
    	    // we need to find all unique dates and store them in rowDateMap.
    	    String sql = " select distinct date_format(data_load_dt, '%Y-%m-%d') 'dataLoadDt'  " +
					" from gr_release_metrics " +
					" where project_id = ? " +
					" and release_id = ? " +
					" order by data_load_dt asc";
    	    prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, project.getProjectId());
			prepStmt.setInt(2, releaseId);
			rs = prepStmt.executeQuery();
			
			int rowNum = 0;
			while (rs.next()){
				rowNum++;
				String dataLoadDt = rs.getString("dataLoadDt");
				System.out.println("puttin dataLoadDt " + dataLoadDt + " rowNum " + rowNum);
				rowDateMap.put(dataLoadDt, new Integer(rowNum));
			}
    	    
    	    sql  = " select distinct rm.requirement_type_short_name 'short_name' " +
    				" from  gr_requirement_types rt, gr_release_metrics rm " +
    				" where rm.project_id = ? " +
    				" and rm.release_id = ? " +
    				" and rm.project_id = rt.project_id " +
        	    	" order by display_sequence ";
		
    		
    	    prepStmt = con.prepareStatement(sql);

			prepStmt.setInt(1, project.getProjectId());
			prepStmt.setInt(2, releaseId);
    		
    	    rs   = prepStmt.executeQuery();
			int colNum = 0;
			while (rs.next()){
				// Iterate through the db results and print the metrics for this req type
				colNum++;
				
				String reqType = rs.getString("short_name");
				

				sql = " select date_format(data_load_dt, '%Y-%m-%d') 'dataLoadDt' , " +
						" num_of_requirements, " +
						" num_of_draft_reqs, " +
						" num_of_in_workflow_reqs 'num_of_pending_reqs', " +
						" num_of_rejected_reqs, " +
						" num_of_approved_reqs," +
						" num_of_dangling_reqs, " +
						" num_of_orphan_reqs," +
						" num_of_suspect_upstream_reqs, " +
						" num_of_suspect_downstream_reqs, " +
						" num_of_test_pending_reqs, " +
						" num_of_test_pass_reqs, " +
						" num_of_test_fail_reqs, " +
						" num_of_completed_reqs," +
						" num_of_incomplete_reqs  " +
						" from gr_release_metrics " +
						" where project_id = ? " +
						" and release_id = ? " +
						" and requirement_type_short_name = ?" +
						" order by data_load_dt asc";
    		
		    			    		
			
								
				
				PreparedStatement prepStmt2 = con.prepareStatement(sql);
				
    			prepStmt2.setInt(1, project.getProjectId());
    			prepStmt2.setInt(2, releaseId);
				prepStmt2.setString(3, reqType);
					
				ResultSet rs2 = prepStmt2.executeQuery();
				
				rowNum = 0;
				
				while (rs2.next()){
					rowNum++;
					
					String dataLoadDt = rs2.getString("dataLoadDt");
					int numOfRequirements = rs2.getInt("num_of_requirements");
					int numOfDraftRequirements  = rs2.getInt("num_of_draft_reqs");
					int numOfPendingRequirements = rs2.getInt("num_of_pending_reqs");
					int numOfRejectedRequirements = rs2.getInt("num_of_rejected_reqs");
					int numOfApprovedRequirements = rs2.getInt("num_of_approved_reqs");
					int numOfDanglingRequirements  = rs2.getInt("num_of_dangling_reqs");
					int numOfOrphanRequirements  = rs2.getInt("num_of_orphan_reqs");
					int numOfSuspectUpstreamRequirements  = rs2.getInt("num_of_suspect_upstream_reqs");
					int numOfSuspectDownstreamRequirements  = rs2.getInt("num_of_suspect_downstream_reqs");
					int numOfTestPendingRequirements = rs2.getInt("num_of_test_pending_reqs");
					int numOfTestPassRequirements  = rs2.getInt("num_of_test_pass_reqs");
					int numOfTestFailRequirements = rs2.getInt("num_of_test_fail_reqs");
					int numOfCompletedRequirements = rs2.getInt("num_of_completed_reqs");
					int numOfIncompleteRequirements = rs2.getInt("num_of_incomplete_reqs");
					
					
					
					System.out.println("srt running exportReleaseTrendData  for req type  " + reqType + " dataLoadDt is  " + dataLoadDt + " numOfREq is " + numOfRequirements);
					
					
		    		
					// If colNum == 1, then Print the date column. In this, the first row has the 'Date' label, and all the other rows have the
					// actual date value.
				    if (colNum == 1){
						// this is the first iteration, so lets print the Column 0 (date values)
				    	if (rowNum ==1){
							// this is the first row, so lets print the column headers
							
				    		setStringCellValue(allSheet, 0, colNum - 1, "Date");
				    		setStringCellValue(draftSheet, 0, colNum - 1, "Date");
							setStringCellValue(pendingSheet, 0, colNum - 1, "Date");
							setStringCellValue(rejectedSheet, 0, colNum - 1, "Date");
							setStringCellValue(approvedSheet, 0, colNum - 1, "Date");
							setStringCellValue(danglingSheet, 0, colNum - 1, "Date");
							setStringCellValue(orphanSheet, 0, colNum - 1, "Date");
							setStringCellValue(suspectUpstreamSheet, 0, colNum - 1, "Date");
							setStringCellValue(suspectDownstreamSheet, 0, colNum - 1, "Date");
							setStringCellValue(testPendingSheet, 0, colNum - 1, "Date");
							setStringCellValue(testPassSheet, 0, colNum - 1, "Date");
							setStringCellValue(testFailSheet, 0, colNum - 1, "Date");
							setStringCellValue(completedSheet, 0, colNum - 1, "Date");
							setStringCellValue(incompleteSheet, 0, colNum - 1, "Date");
							
						}
				    	
				    	
				    	// lets print the Data Load Dt in column 1 for each of the sheets
						setStringCellValue(allSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(draftSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(pendingSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(rejectedSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(approvedSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(danglingSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(orphanSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(suspectUpstreamSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(suspectDownstreamSheet, rowNum, colNum - 1, dataLoadDt);
						
						setStringCellValue(testPendingSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(testPassSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(testFailSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(completedSheet, rowNum, colNum - 1, dataLoadDt);
						setStringCellValue(incompleteSheet, rowNum, colNum - 1, dataLoadDt);
						
						
						
				   }
				    
				    
			    	if (rowNum ==1){
						// For the first row, print the name of the req type in each sheet, one per column
						setStringCellValue(allSheet, 0, colNum, reqType);
						setStringCellValue(draftSheet, 0, colNum, reqType);
						setStringCellValue(pendingSheet, 0, colNum, reqType);
						setStringCellValue(rejectedSheet, 0, colNum, reqType);
						setStringCellValue(approvedSheet, 0, colNum, reqType);
						setStringCellValue(danglingSheet, 0, colNum, reqType);
						setStringCellValue(orphanSheet, 0, colNum, reqType);
						setStringCellValue(suspectUpstreamSheet, 0, colNum, reqType);
						setStringCellValue(suspectDownstreamSheet, 0, colNum, reqType);
						
						setStringCellValue(testPendingSheet, 0, colNum, reqType);
						setStringCellValue(testPassSheet, 0, colNum, reqType);
						setStringCellValue(testFailSheet, 0, colNum, reqType);
						setStringCellValue(completedSheet, 0, colNum, reqType);
						setStringCellValue(incompleteSheet, 0, colNum, reqType);
						
					}
			    	
			    	
			    	// Lets print the number of requirements in each of the sheets for this req type column.
		    		
			    	int excelRowNum = 0;
			    	// for any dataLoadDt, lets get the excel row num where it should go based on the HashMap.
			    	Integer excelRowNumInteger = (Integer) rowDateMap.get(dataLoadDt);
			    	try{
			    		excelRowNum = excelRowNumInteger.intValue();
			    	}
			    	catch(Exception excelRowNumIntegerException){
			    		excelRowNumIntegerException.printStackTrace();
			    	}
					setIntCellValue(allSheet, excelRowNum, colNum, numOfRequirements);
					setIntCellValue(draftSheet, excelRowNum, colNum, numOfDraftRequirements);
					setIntCellValue(pendingSheet, excelRowNum, colNum, numOfPendingRequirements);
					setIntCellValue(rejectedSheet, excelRowNum, colNum, numOfRejectedRequirements);
					setIntCellValue(approvedSheet, excelRowNum, colNum, numOfApprovedRequirements);
					setIntCellValue(danglingSheet, excelRowNum, colNum, numOfDanglingRequirements);
					setIntCellValue(orphanSheet, excelRowNum, colNum, numOfOrphanRequirements);
					setIntCellValue(suspectUpstreamSheet, excelRowNum, colNum, numOfSuspectUpstreamRequirements);
					setIntCellValue(suspectDownstreamSheet, excelRowNum, colNum, numOfSuspectDownstreamRequirements);
					
					setIntCellValue(testPendingSheet, excelRowNum, colNum, numOfTestPendingRequirements);
					setIntCellValue(testPassSheet, excelRowNum, colNum, numOfTestPassRequirements);
					setIntCellValue(testFailSheet, excelRowNum, colNum, numOfTestFailRequirements);
					setIntCellValue(completedSheet, excelRowNum, colNum, numOfCompletedRequirements);
					setIntCellValue(incompleteSheet, excelRowNum, colNum, numOfIncompleteRequirements);
					
				}
				
    	    
    	    
			}
	        
	 
			

			// lets write the output out.
    		OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
	    	
			rs.close();
			prepStmt.close();
			con.close();
			
			
	    } catch (FileNotFoundException fnfe) {
	        // It might not be possible to create the target file.
	        fnfe.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
        finally {
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
	    return filename;
	}
    
    
    private void setIntCellValue (HSSFSheet sheet, int rowNum , int colNum, int value) {
    	Row row = sheet.getRow(rowNum);
		if (row == null){
			row = sheet.createRow(rowNum);
		}
		Cell cell = row.createCell(colNum);
		cell.setCellValue(value);
    }
   
    private void setStringCellValue (HSSFSheet sheet, int rowNum , int colNum, String value) {
    	Row row = sheet.getRow(rowNum);
		if (row == null){
			row = sheet.createRow(rowNum);
		}
		Cell cell = row.createCell(colNum);
		cell.setCellValue(value);
    }
    
}
