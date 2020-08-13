package com.gloree.utils;

//GloreeJava2

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.gloree.beans.*;

import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;





public class ReportUtil {

	
	// loop through a TraceTree report, and get a list of req types and then 
	// for each req type, get the attributes and put them in a hashmap
	public static ArrayList<String> getColumnHeadersInTraceTreeReport(ArrayList<TraceTreeRow> traceTreeReport) {
		
		
		HashSet<Integer> rTSet = new HashSet<Integer>();
		for (TraceTreeRow tTR:traceTreeReport){
			rTSet.add(tTR.getRequirement().getRequirementTypeId());
		}
		// lets iterate through the rTSet and add all the attributes found
		TreeSet<String> headersTSet = new TreeSet<String>();
		for (int rTId:rTSet){
			RequirementType rT = new RequirementType(rTId);
			ArrayList<RTAttribute> rTAttributes = rT.getAllAttributesInRequirementType();
			for (RTAttribute rTAttribute:rTAttributes){
				// lets add them to the linked list if not exists
				headersTSet.add(rTAttribute.getAttributeName().trim());
			}
		}

		ArrayList<String> headers = new ArrayList<String>(headersTSet);
		
		
		return headers;
	}
	
	public static ArrayList<String> getAllAttributesInAProject(Project project) {
		
		TreeSet<String> headersTSet = new TreeSet<String>();
		
		ArrayList requirementTypes = project.getMyRequirementTypes();
		Iterator rT = requirementTypes.iterator() ;
		
		while (rT.hasNext()){
			RequirementType requirementType = (RequirementType) rT.next();
			ArrayList rTAttributes = requirementType.getAllAttributesInRequirementType();
			Iterator rTAs = rTAttributes.iterator();
			while (rTAs.hasNext()){
				RTAttribute rTAttribute = (RTAttribute) rTAs.next();
				headersTSet.add(rTAttribute.getAttributeName());
			}
		}
		ArrayList<String> attributeNames = new ArrayList<String>(headersTSet);		
		return attributeNames;
	}
	
	public static String getExcelImportTRClass(ArrayList columnNames, String attributeName) {
		String trClass = "";
		if (columnNames != null){
	    	Iterator c = columnNames.iterator();
	    	while ( c.hasNext() ) {
	    		String columnNameString = (String) c.next();
	    		String[] columnName = columnNameString.split(":##:");
	    		if (columnName[0].trim().toLowerCase().equals(attributeName)){
	    			trClass = "info";
			   }
	    		
	    	}
	    }
		return trClass;
	}

	public static boolean matchFilterConditionMultiAttribute(Requirement r, String multiFilterCondition) {
		// default assume there is no match.
		// set to true ONLY if condition is met by ALL for AND and ANY for Or
		boolean thereIsAMatch = false;
		if (multiFilterCondition == null){multiFilterCondition = "";}
		multiFilterCondition = multiFilterCondition.trim().toLowerCase();
		
		try {
			// if there is an AND logic, match is set to true ONLY if every one of the condition matches
			if (multiFilterCondition.contains("and")){
				boolean meetsAllConditions = true;
				for (String singleFilterCondition : multiFilterCondition.split("and")){
					if (!(matchFilterConditionSingleAttribute(r, singleFilterCondition))){
						meetsAllConditions = false;
					}
				}
				if (meetsAllConditions == true){
					thereIsAMatch = true;
				}
			}
			else if (multiFilterCondition.contains("or")){
				boolean meetsAnyOneCondition = false;
				for (String singleFilterCondition : multiFilterCondition.split("or")){
					if (matchFilterConditionSingleAttribute(r, singleFilterCondition)){
						meetsAnyOneCondition = true;
					}
				}
				if (meetsAnyOneCondition == true){
					thereIsAMatch = true;
				}
			}
			else {
				String singleFilterCondition  = multiFilterCondition;
				if (matchFilterConditionSingleAttribute(r, singleFilterCondition)){
					thereIsAMatch = true;
				}
			}
			// if there is OR logic, then match is set to true if ANY one of the conditions matches
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return thereIsAMatch;
	}
	
	public static boolean matchFilterConditionSingleAttribute(Requirement r, String singleFilterCondition) {

		boolean thereIsAMatch = false;
		
		if (singleFilterCondition==null){singleFilterCondition="";}
		singleFilterCondition = singleFilterCondition.trim().toLowerCase();
		
		
		
		
		// lets get requirement attribtes
		HashMap<String, String> aMap = r.getUserDefinedAttributesHashMapLowerCaseKey();

		// lets do the simple case of 1 atttribute = 1 value
		// example : customer=cisco
		
		// lets split the filter
		if (singleFilterCondition.contains("=")){
			String[] attribs = singleFilterCondition.split("=");
			String attribName = attribs[0].toLowerCase().trim();
			String attribFilters = attribs[1].trim().toLowerCase();
			
			// what is stored in the requirement for this attribName???
			String storedValue = aMap.get(attribName);
			
			thereIsAMatch =  doesStoredValueMatchFilter(storedValue, attribFilters , "equals");
		}
		if (singleFilterCondition.toLowerCase().contains("like")){
			String[] attribs = singleFilterCondition.split("like");
			String attribName = attribs[0].trim().toLowerCase();
			String attribFilters = attribs[1].trim().toLowerCase();
			
			// what is stored in the requirement for this attribName???
			String storedValue = aMap.get(attribName.toLowerCase().trim());
			
			thereIsAMatch =  doesStoredValueMatchFilter(storedValue, attribFilters, "like");
		}
		
		if (singleFilterCondition.toLowerCase().contains("not")){
			String[] attribs = singleFilterCondition.split("not");
			String attribName = attribs[0].trim().toLowerCase();
			String attribFilters = attribs[1].trim().toLowerCase();
			
			// what is stored in the requirement for this attribName???
			String storedValue = aMap.get(attribName.toLowerCase().trim());
			
			thereIsAMatch =  doesStoredValueMatchFilter(storedValue, attribFilters, "not");
		}
		
		
		return thereIsAMatch;
	}
	
	
	public static boolean doesStoredValueMatchFilter(String storedValue, String attribFilter , String matchType){
		// if attribFilter has || we split and check if any of them match the stored value
		// else we check to see if the storedvalue matches the attribFilter
		boolean thereIsAMatch = false;
		

		if (storedValue == null){storedValue = "";}
		storedValue = storedValue.trim().toLowerCase();

		if (attribFilter == null){attribFilter = "";}
		attribFilter = attribFilter.trim().toLowerCase();
		
		System.out.println("=========>stored value " + storedValue + "  individual filter " + attribFilter + " match ask is:  " + matchType);
		
		if (matchType.equals("equals")){
			if (storedValue.length() == 0){
				return thereIsAMatch;
			}
			// if attribute filters have pipes in them, then if the stored value matches ANY one of them, then it's a match
			if (attribFilter.contains("|")){
				// lets split by || and iterate through them looking for a potential match
				for (String individualFilter : attribFilter.split("[|]") ){
					System.out.println("------->Individual Filter is -->" + individualFilter.trim() + "<--");
					if (storedValue.equals(individualFilter.trim())){
						System.out.println("Pipe. one of the values matched : stored value " + storedValue + " matched individual filter " + individualFilter);
						thereIsAMatch = true;
					}
				}
		
			}
			else {
				// No Pipe , so single value match
				if (storedValue.equals(attribFilter)){
					System.out.println("No Pipe : Single value match : stored value " + storedValue + " matched individual filter " + attribFilter);
					thereIsAMatch = true;
				}
			}
		}
		if (matchType.equals("like")){
			if (storedValue.length() == 0){
				return thereIsAMatch;
			}
			// if attribute filters have pipes in them, then if the stored value matches ANY one of them, then it's a match
			if (attribFilter.contains("|")){
				// lets split by || and iterate through them looking for a potential match
				for (String individualFilter : attribFilter.split("[|]") ){
					System.out.println("------->stored value is -->" + storedValue + "--->Individual Filter is -->" + individualFilter.trim() + "<--");
					if (storedValue.contains(individualFilter.trim())){
						thereIsAMatch = true;
					}
				}
			}
			else {
			// No Pipe , so single value match
				if (storedValue.contains(attribFilter)){
					thereIsAMatch = true;
				}
			
			}
		}
		
		if (matchType.equals("not")){
			
			// if attribute filters have pipes in them, then if the stored value matches ANY one of them, then it's a match
			if (attribFilter.contains("|")){
				// lets split by || and iterate through them looking for a potential match
				boolean inAnyOne = false;
				for (String individualFilter : attribFilter.split("[|]") ){
					System.out.println("------->stored value is -->" + storedValue + "--->Individual Filter is -->" + individualFilter.trim() + "<--");
					if (storedValue.contains(individualFilter.trim())){
						inAnyOne = true;
					}
				}
				
				if (inAnyOne){
					thereIsAMatch = false;
				}
				else {
					thereIsAMatch = true;
				}
			}
			else {
			// No Pipe , so single value match
				if (!(storedValue.contains(attribFilter))){
					thereIsAMatch = true;
				}
			
			}
		}		
		
		System.out.println("Boolean match status is " + thereIsAMatch);
		return thereIsAMatch;
	}
	
	
	
	
	public static ArrayList runGenericReport(SecurityProfile securityProfile, Report report, int projectId,
			String databaseType) {
		ArrayList genericReport = new ArrayList();

		if (report.getProectId() != projectId) {
			// NOTE : CRITICAL .
			// this means that the report does not belong to this
			// project, and is a major security hole. Hence we do
			// not run this report.

			return genericReport;
		}

		String reportDefinition = report.getReportDefinition();

		String danglingSearch = "all";
		if (!(reportDefinition.contains("danglingSearch:--:all"))) {
			danglingSearch = "danglingOnly";
		}
		String orphanSearch = "all";
		if (!(reportDefinition.contains("orphanSearch:--:all"))) {
			orphanSearch = "orphanOnly";
		}
		String completedSearch = "all";
		if (!(reportDefinition.contains("completedSearch:--:all"))) {
			completedSearch = "completedOnly";
		}
		String incompleteSearch = "all";
		if (!(reportDefinition.contains("incompleteSearch:--:all"))) {
			incompleteSearch = "incompleteOnly";
		}
		String suspectUpStreamSearch = "all";
		if (!(reportDefinition.contains("suspectUpStreamSearch:--:all"))) {
			suspectUpStreamSearch = "suspectUpStreamOnly";
		}
		String suspectDownStreamSearch = "all";
		if (!(reportDefinition.contains("suspectDownStreamSearch:--:all"))) {
			suspectDownStreamSearch = "suspectDownStreamOnly";
		}
		String lockedSearch = "all";
		// if report definition does not have a locked search parameter, we
		// default it to ALL.
		// if it does have a definition and it's not ALL, then we restrict it to
		// locked ONly.
		// We are doing this because, by the time we rolled out this feature,
		// some reports were already
		// saved, and their report definition wouldn't have 'lockedSearch:--:'
		// criteria.
		if (reportDefinition.contains("lockedSearch:--:")) {
			if (!(reportDefinition.contains("lockedSearch:--:all"))) {
				lockedSearch = "lockedOnly";
			}
		}
		String includeSubFoldersSearch = "no";
		if (!(reportDefinition.contains("includeSubFoldersSearch:--:no"))) {
			includeSubFoldersSearch = "includeSubFoldersOnly";
		}

		// lets get the text box values for standard attributes.
		String[] values = reportDefinition.split(":###:");

		int inRTBaselineSearch = -1;
		int changedAfterRTBaselineSearch = -1;
		String testingStatusSearch = "";

		String nameSearch = "";
		String descriptionSearch = "";
		String ownerSearch = "";
		String externalURLSearch = "";
		String approvedBySearch = "";
		String rejectedBySearch = "";
		String pendingBySearch = "";
		String traceToSearch = "";
		String traceFromSearch = "";
		String statusSearch = "";
		String prioritySearch = "";
		String pctCompleteSearch = "";
		String customAttributeSearch = "";

		String displayRequirementType = "all";

		String sortBy = "";
		String sortByType = "";

		String inRelease = "-1";

		for (int j = 0; j < values.length; j++) {
			String value = "";
			value = values[j];

			if (value.contains("inRTBaselineSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					inRTBaselineSearch = Integer.parseInt(a[1]);
				}
			}

			if (value.contains("changedAfterRTBaselineSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					changedAfterRTBaselineSearch = Integer.parseInt(a[1]);
				}
			}

			if (value.contains("testingStatusSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					testingStatusSearch = a[1];
				}
			}

			if (value.contains("nameSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					nameSearch = a[1];
				}
			}
			if (value.contains("descriptionSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					descriptionSearch = a[1];
				}
			}
			if (value.contains("ownerSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					ownerSearch = a[1];
				}
			}
			if (value.contains("externalURLSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					externalURLSearch = a[1];
				}
			}
			if (value.contains("approvedBySearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					approvedBySearch = a[1];
				}
			}
			if (value.contains("rejectedBySearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					rejectedBySearch = a[1];
				}
			}
			if (value.contains("pendingBySearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					pendingBySearch = a[1];
				}
			}
			if (value.contains("traceToSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					traceToSearch = a[1];
				}
			}
			if (value.contains("traceFromSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					traceFromSearch = a[1];
				}
			}
			if (value.contains("statusSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					statusSearch = a[1];
				}
			}
			if (value.contains("prioritySearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					prioritySearch = a[1];
				}
			}
			if (value.contains("pctCompleteSearch")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					pctCompleteSearch = a[1];
				}
			}

			if (value.contains("inRelease")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					inRelease = a[1];
				}
			}

			if (value.contains("customA")) {
				// the customAttribute filter in the report definition is stored
				// as
				// customA34:--:filterValue. Here 34 is the id id of the
				// rTAttribute.

				String attributeFilter = value;
				// at this point an attributeFilter looks like customA42:--:mona
				try {
					if ((attributeFilter != null) && (attributeFilter.contains(":--:"))) {

						String[] filterDetails = attributeFilter.split(":--:");
						String filterName = filterDetails[0];
						String filterValue = filterDetails[1];

						if ((filterValue != null) && (!(filterValue.equals("")))) {
							// at this point we have a valid filter value and a
							// filter Name.
							// the problem is that filter Name is in the format
							// of customA42, wehre 42
							// is the RTAttributeId.
							if ((filterName != null) && (!(filterName.equals("")))) {
								int rTAttributeId = Integer.parseInt(filterName.replace("customA", ""));

								RTAttribute rTAttribute = new RTAttribute(rTAttributeId);

								customAttributeSearch += rTAttribute.getAttributeName() + ":#:" + filterValue + ":--:";
							}

						}

					}
				} catch (Exception e) {
					// do nothing
				}

			}

			if (value.contains("displayRequirementType:--:")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					displayRequirementType = a[1];
				}
			}

			if (value.contains("sortBy:--:")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					sortBy = a[1];
				}
			}

			if (value.contains("sortByType:--:")) {
				String[] a = value.split(":--:");
				if (a.length > 1) {
					sortByType = a[1];
				}
			}

		}

		// lets drop the last :--:
		if (customAttributeSearch.contains(":--:")) {
			customAttributeSearch = (String) customAttributeSearch.subSequence(0,
					customAttributeSearch.lastIndexOf(":--:"));
		}

		if (report.getReportType().equals("list")) {
			// this is a list report
			genericReport = ReportUtil.runListReport(securityProfile, projectId, report.getFolderId(), "active",
					danglingSearch, orphanSearch, completedSearch, incompleteSearch, suspectUpStreamSearch,
					suspectDownStreamSearch, lockedSearch, includeSubFoldersSearch, inRTBaselineSearch,
					changedAfterRTBaselineSearch, testingStatusSearch, nameSearch, descriptionSearch, ownerSearch,
					externalURLSearch, approvedBySearch, rejectedBySearch, pendingBySearch, traceToSearch,
					traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch, customAttributeSearch, sortBy,
					sortByType, inRelease, databaseType);

		}

		if (report.getReportType().equals("traceTree")) {
			// this is a traceTree report.
			// NOTE : runTraceTreeReport gives an arraylist of traceTreeobjects
			// which are requirements + tree info.
			// since we need a list of requirement objects, we call a variation
			// of that proc that returns only arraylist of requirements.
			genericReport = ReportUtil.runTraceTreeReportReturnRequirementArrayList(securityProfile, projectId,
					report.getFolderId(), "active", report.getTraceTreeDepth(), danglingSearch, orphanSearch,
					completedSearch, incompleteSearch, suspectUpStreamSearch, suspectDownStreamSearch, lockedSearch,
					includeSubFoldersSearch, inRTBaselineSearch, changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch, approvedBySearch, rejectedBySearch,
					pendingBySearch, traceToSearch, traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch,
					customAttributeSearch, databaseType, displayRequirementType);
		}

		return genericReport;
	}

	// when called with a folder id, and a search string,
	// it returns an array list of requirements in this folder.
	// depending on the filter (active, deleted, all),
	// it returns either active reqs, deleted reqs or all reqs.
	public static ArrayList runListReport(SecurityProfile securityProfile, int projectId, int folderId, String filter,
			String danglingSearch, String orphanSearch, String completedSearch, String incompleteSearch,
			String suspectUpStreamSearch, String suspectDownStreamSearch, String lockedSearch,
			String includeSubFoldersSearch, int inRTBaselineSearch, int changedAfterRTBaselineSearch,
			String testingStatusSearch, String nameSearch, String descriptionSearch, String ownerSearch,
			String externalURLSearch, String approvedBySearch, String rejectedBySearch, String pendingBySearch,
			String traceToSearch, String traceFromSearch, String statusSearch, String prioritySearch,
			String pctCompleteSearch, String customAttributeSearch, String sortBy, String sortByType, String inRelease,
			String databaseType) {

		// if inRelease is not empty, lets try to get a list of objects in this
		// release tree
		// lets make a really big , separated list of all requirementids in this
		// release.
		// this is for efficiency
		boolean inReleaseFilter = false;
		int inReleaseId = -1;
		Requirement release = null;
		String inReleaseObjects = ",";

		if ((inRelease != null) && !(inRelease.equals(""))) {

			inReleaseId = Integer.parseInt(inRelease);
			if (inReleaseId > 0) {
				// we were sent in a valid inRelease number
				inReleaseFilter = true;
				release = new Requirement(inReleaseId, databaseType);
				ArrayList releaseDownStream = new ArrayList();
				releaseDownStream = release.getDownStreamCIARequirements(securityProfile, 8, 10000, databaseType);

				// lets iterate through the releaseDownStream and just keep
				// adding to the inReleaseObjects String
				Iterator i = releaseDownStream.iterator();
				while (i.hasNext()) {
					TraceTreeRow traceTreeRow = (TraceTreeRow) i.next();
					Requirement r = traceTreeRow.getRequirement();
					inReleaseObjects += r.getRequirementId() + ",";
				}
			}
		}

		ArrayList report = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {

			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = buildSQL(projectId, folderId, filter, danglingSearch, orphanSearch, completedSearch,
					incompleteSearch, suspectUpStreamSearch, suspectDownStreamSearch, lockedSearch,
					includeSubFoldersSearch, inRTBaselineSearch, changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch, approvedBySearch, rejectedBySearch,
					pendingBySearch, traceToSearch, traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch,
					customAttributeSearch, sortBy, sortByType, databaseType);

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();

			int requirementId = 0;
			int requirementTypeId = 0;
			projectId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy = "";
			String lastModifiedDt = "";
			String requirementTypeName = "";

			Requirement requirement = null;

			// lets see if the user has defined any attribute filters.
			// Because of the way attributes are stored and for efficiency
			// reasons
			// it makes sense to filter attributes of type number , after they
			// have been queried.

			// lets figure out if we need to filter any values based on
			// attribute filters
			boolean customAttributeFilter = false;
			String temp = "";
			String[] customAttributeSearches = customAttributeSearch.split(":--:");
			for (int i = 0; i < customAttributeSearches.length; i++) {
				String cAS = customAttributeSearches[i];
				// at this point cAS is something like Severity:#:1:##:2:##:

				if ((cAS != null) && (!(cAS.equals("")))) {
					// this is regular text box.
					if (cAS.contains(":#:")) {
						// cAS has something like Customer:#:IBM or Numbr:#:>3
						String[] customAttribute = cAS.split(":#:");
						String customLabel = customAttribute[0];
						String customValue = customAttribute[1];

						// At this point, this could be a Text box or a Date or
						// Number
						// For text box / date , we can use the string like
						// search with % at each end
						// For number , we have to do exact matches. So, based
						// on the label, we will get the
						// Attribute object, and figure out whether this is a
						// number or text or date and treat accordingly.
						RTAttribute rTAttribute = new RTAttribute(FolderUtil.getRequirementTypeId(folderId),
								customLabel);
						if (rTAttribute.getAttributeType().equals("Number")) {
							// this is a number, so exact match.
							if ((customValue.trim().startsWith(">")) || (customValue.trim().startsWith("<"))
									|| (customValue.trim().startsWith("="))) {
								// if a greater than or less than filter has
								// been used then
								// we will filter it out while displaying the
								// results.
								customAttributeFilter = true;
								temp += customLabel + ":#:" + customValue + ":--:";
							} else {
								customAttributeFilter = true;
								temp += customLabel + ":#:" + "=" + customValue + ":--:";
							}

						}
						if (rTAttribute.getAttributeType().contains("Drop Down")) {
							customAttributeFilter = true;
							temp += customLabel + ":#:" + "contains" + customValue + ":--:";
						}
						if (rTAttribute.getAttributeType().contains("Text Box")) {
							customAttributeFilter = true;
							temp += customLabel + ":#:" + "textEquals" + customValue + ":--:";
						}
					}

				}
			}

			// drop the last :--: from attributeFilter
			if (temp.contains(":--:")) {
				temp = (String) temp.subSequence(0, temp.lastIndexOf(":--:"));
			}

			customAttributeSearch = temp;
			// at this point customAttributeSearch has only number attributes
			// with > or < than.
			customAttributeSearches = customAttributeSearch.split(":--:");

			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				requirementName = rs.getString("name");
				requirementDescription = rs.getString("description");
				requirementTag = rs.getString("tag");
				requirementFullTag = rs.getString("full_tag");
				version = rs.getInt("version");
				approvedByAllDt = rs.getString("approved_by_all_dt");
				approvers = rs.getString("approvers");
				requirementStatus = rs.getString("status");
				requirementPriority = rs.getString("priority");
				requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				requirementPctComplete = rs.getInt("pct_complete");
				requirementExternalUrl = rs.getString("external_url");
				traceTo = rs.getString("trace_to");
				traceFrom = rs.getString("trace_from");
				userDefinedAttributes = rs.getString("user_defined_attributes");
				testingStatus = rs.getString("testing_status");
				deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				createdBy = rs.getString("created_by");
				createdDt = rs.getString("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				lastModifiedDt = rs.getString("last_modified_dt");
				requirementTypeName = rs.getString("requirement_type_name");

				String[] attribs = userDefinedAttributes.split(":##:");
				boolean metCustomAttributeNumberFilterCritia = true;
				boolean metCustomAttributeDropDownFilterCritia = true;

				// if we have inReleaseFilter and if this object is not part of
				// the release, lets skip it.
				if (inReleaseFilter) {
					// lets see if this req id is in the object
					if (inReleaseObjects.contains("," + requirementId + ",")) {
						// ignore. this object is in the release tree and we
						// have been asked to filter on it
					} else {
						System.out.println("srt : Not adding " + requirementFullTag
								+ " as its not in the release and the inRelease filter is on . In Rel string is " + 
								inReleaseObjects );
						continue;
					}
				}

				// lets see if this requirement should be added to the list or
				// if we should skip it.
				// if this row fits the attribute filter criteria, we keep it,
				// otherwise we skip it.
				if (customAttributeFilter) {
					for (int k = 0; k < customAttributeSearches.length; k++) {
						// AT THIS POINT EVERY ATTRIBUTE IN THE
						// CUSTOMATTRIBUTESEARCHES IS GUARANTEED TO BE A
						// NUMBER , drop down or text box TYPE ATTRIBUTE
						String cAS = customAttributeSearches[k];

						// at this point cAS is something like
						// Severity:#:1:##:2:##:
						// cAS has something like Customer:#:IBM or Numbr:#:>3
						String[] customAttribute = cAS.split(":#:");
						String filterLabel = customAttribute[0];
						String filterValueString = customAttribute[1];

						// lets see if the filter value for drop down meets
						// criteria

						// lets find this requirement's attrib value for this
						// filterable attribute.
						double attribValue = 0.0;
						for (int l = 0; l < attribs.length; l++) {
							String[] attrib = attribs[l].split(":#:");
							// To avoid a array out of bounds exception where
							// the attrib value wasn't filled in
							// we print the cell only if array has 2 items in
							// it.
							String attribValueString = "";
							if (attrib.length == 2) {
								attribValueString = attrib[1];
							}

							if (attrib[0].trim().equals(filterLabel.trim())) {
								if (filterValueString.trim().startsWith("contains")) {

									// this is a drop down type.
									if ((filterValueString != null) && (filterValueString.contains(":##:"))) {
										filterValueString = (String) filterValueString.subSequence(0,
												filterValueString.lastIndexOf(":##:"));
									}
									filterValueString = filterValueString.replace("contains", "").trim();
									System.out.println("srt filterValue String is " + filterValueString);
									System.out.println("srt attribValue String is " + attribValueString);

									if (filterValueString.contains(":##:")) {
										// searching against a multi select
										// value in a drop down
										String[] filterValues = filterValueString.split(":##:");

										// lets iterate through all the values
										// and se if there is a match.
										boolean positiveMatch = false;
										for (int i = 0; i < filterValues.length; i++) {
											if ((attribValueString.contains(filterValues[i]))) {
												// No, this row does not meet
												// criteria
												positiveMatch = true;
											}
										}
										// now we have iterated through all the
										// invidividual filter values and if
										// there
										// was a custom attribute withi this
										// filter value , then we include in the
										// output
										// else exclude
										if (!positiveMatch) {
											metCustomAttributeDropDownFilterCritia = false;
										}

									} else {
										// searching against a single value in a
										// drop down
										if (!(attribValueString.contains(filterValueString))) {
											// No, this row does not meet
											// criteria
											metCustomAttributeDropDownFilterCritia = false;
											System.out.println(
													"srt single select setting metCustomattrib criteria to false");

										}

									}

								} else if (filterValueString.trim().startsWith("textEquals")) {

									// this is a text box type.
									if ((filterValueString != null) && (filterValueString.contains(":##:"))) {
										filterValueString = (String) filterValueString.subSequence(0,
												filterValueString.lastIndexOf(":##:"));
									}
									filterValueString = filterValueString.replace("textEquals", "").trim();
									if (filterValueString.contains("%")) {
										// has wild cards. So lets use contains
										filterValueString = filterValueString.replace("%", "");
										if (!(attribValueString.trim().toUpperCase()
												.contains(filterValueString.trim().toUpperCase()))) {
											// No, this row does not meet
											// criteria
											metCustomAttributeDropDownFilterCritia = false;
										}

									} else {
										// exact match
										if (!(filterValueString.trim().toUpperCase()
												.equals(attribValueString.trim().toUpperCase()))) {
											// No, this row does not meet
											// criteria
											metCustomAttributeDropDownFilterCritia = false;
										}
									}

								} else {
									// this is a number type
									try {
										attribValue = Double.parseDouble(attribValueString);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

							}

						}

						if (filterValueString.trim().startsWith(">")) {
							double filterValue = 0.0;
							try {
								filterValueString = filterValueString.replace(">", "").trim();
								filterValue = Double.parseDouble(filterValueString);
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (!(attribValue > filterValue)) {
								metCustomAttributeNumberFilterCritia = false;
							}
						}

						if (filterValueString.trim().startsWith("<")) {
							double filterValue = 0.0;
							try {
								filterValueString = filterValueString.replace("<", "").trim();
								filterValue = Double.parseDouble(filterValueString);
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (!(attribValue < filterValue)) {
								metCustomAttributeNumberFilterCritia = false;
							}
						}

						if (filterValueString.trim().startsWith("=")) {
							double filterValue = 0.0;
							try {
								filterValueString = filterValueString.replace("=", "").trim();
								filterValue = Double.parseDouble(filterValueString);
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (!(attribValue == filterValue)) {
								metCustomAttributeNumberFilterCritia = false;
							}
						}

					}
				}

				if (metCustomAttributeNumberFilterCritia && metCustomAttributeDropDownFilterCritia) {

					// lets check if the drop down value is in the user defined
					// values.

					requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId,
							requirementName, requirementDescription, requirementTag, requirementFullTag, version,
							approvedByAllDt, approvers, requirementStatus, requirementPriority, requirementOwner,
							requirementLockedBy, requirementPctComplete, requirementExternalUrl, traceTo, traceFrom,
							userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
							requirementTypeName, createdDt);

					requirement.setLastModifiedDt(lastModifiedDt);

					// if the user does not have read permissions on this
					// requirement,
					// lets redact it. i.e. remove all sensitive infor from it.
					if (!(securityProfile.getPrivileges()
							.contains("readRequirementsInFolder" + requirement.getFolderId()))) {
						requirement.redact();
					}

					report.add(requirement);

				}

			}

			rs.close();
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
		return (report);
	}

	// when called with a folder id, and a search string,
	// it returns an array list of requirements in this folder.
	// depending on the filter (active, deleted, all),
	// it returns either active reqs, deleted reqs or all reqs.
	public static ArrayList runTraceTreeReport(SecurityProfile securityProfile, int projectId, int folderId,
			String filter, int traceTreeDepth, String danglingSearch, String orphanSearch, String completedSearch,
			String incompleteSearch, String suspectUpStreamSearch, String suspectDownStreamSearch, String lockedSearch,
			String includeSubFoldersSearch, int inRTBaselineSearch, int changedAfterRTBaselineSearch,
			String testingStatusSearch, String nameSearch, String descriptionSearch, String ownerSearch,
			String externalURLSearch, String approvedBySearch, String rejectedBySearch, String pendingBySearch,
			String traceToSearch, String traceFromSearch, String statusSearch, String prioritySearch,
			String pctCompleteSearch, String customAttributeSearch, String databaseType, int maxRowsInTraceTree,
			HttpServletRequest request, String displayRequirementType) {

		if (displayRequirementType == null) {
			displayRequirementType = "all";
		}

		ArrayList traceTreeRows = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			int rowsInTraceTree = 0;
			String sortBy = "";
			String sortByType = "";


			String sql = buildSQL(projectId, folderId, filter, danglingSearch, orphanSearch, completedSearch,
					incompleteSearch, suspectUpStreamSearch, suspectDownStreamSearch, lockedSearch,
					includeSubFoldersSearch, inRTBaselineSearch, changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch, approvedBySearch, rejectedBySearch,
					pendingBySearch, traceToSearch, traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch,
					customAttributeSearch, sortBy, sortByType, databaseType);

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();

			int requirementId = 0;
			int requirementTypeId = 0;
			projectId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			String requirementLockedBy = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy = "";
			String requirementTypeName = "";

			Requirement l1Requirement = null;
			Requirement requirement2 = null;
			Requirement requirement3 = null;
			Requirement requirement4 = null;
			Requirement requirement5 = null;
			Requirement requirement6 = null;
			Requirement requirement7 = null;

			TraceTreeRow l1TraceTreeRow = null;
			TraceTreeRow l2TraceTreeRow = null;
			TraceTreeRow l3TraceTreeRow = null;
			TraceTreeRow l4TraceTreeRow = null;
			TraceTreeRow l5TraceTreeRow = null;
			TraceTreeRow l6TraceTreeRow = null;
			TraceTreeRow l7TraceTreeRow = null;
			TraceTreeRow l8TraceTreeRow = null;
			TraceTreeRow l9TraceTreeRow = null;
			TraceTreeRow l10TraceTreeRow = null;


			// lets execute the filtered SQL to get the root level requirements.
			// we will build a trace tree for each one of these requirements.
			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				requirementName = rs.getString("name");
				requirementDescription = rs.getString("description");
				requirementTag = rs.getString("tag");
				requirementFullTag = rs.getString("full_tag");
				version = rs.getInt("version");
				approvedByAllDt = rs.getString("approved_by_all_dt");
				approvers = rs.getString("approvers");
				requirementStatus = rs.getString("status");
				requirementPriority = rs.getString("priority");
				requirementOwner = rs.getString("owner");
				requirementLockedBy = rs.getString("locked_by");
				requirementPctComplete = rs.getInt("pct_complete");
				requirementExternalUrl = rs.getString("external_url");
				traceTo = rs.getString("trace_to");
				traceFrom = rs.getString("trace_from");
				userDefinedAttributes = rs.getString("user_defined_attributes");
				testingStatus = rs.getString("testing_status");
				deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				createdBy = rs.getString("created_by");
				createdDt = rs.getString("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				requirementTypeName = rs.getString("requirement_type_name");
				// in the first row (first level of a trace tree) there are no
				// trace objects.
				// hence this filed is empty. as you go further down the trace
				// tree you start
				// seeing trace object.s. Same with traceid
				String traceDescription = " ";
				int traceId = 0;

				l1Requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId, requirementName,
						requirementDescription, requirementTag, requirementFullTag, version, approvedByAllDt, approvers,
						requirementStatus, requirementPriority, requirementOwner, requirementLockedBy,
						requirementPctComplete, requirementExternalUrl, traceTo, traceFrom, userDefinedAttributes,
						testingStatus, deleted, folderPath, createdBy, lastModifiedBy, requirementTypeName, createdDt);

				// if the user does not have read permissions on this
				// requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + l1Requirement.getFolderId()))) {
					l1Requirement.redact();
				}
				l1TraceTreeRow = new TraceTreeRow(1, 0, traceId, traceDescription, l1Requirement);
				
				traceTreeRows.add(l1TraceTreeRow);
				/*
				if (displayRequirementType.contains("all")) {
					traceTreeRows.add(l1TraceTreeRow);
				} else {
					// means some display restrictions are in place
					if (displayRequirementType.contains(l1Requirement.getRequirementTypeId() + ",")){
						traceTreeRows.add(l1TraceTreeRow);
					}
				}
				*/
				
				

				if (maxRowsInTraceTree <= ++rowsInTraceTree) {
					rs.close();
					prepStmt.close();
					con.close();
					request.setAttribute("maxRowsInTraceTreeExceeded", "true");
					return (traceTreeRows);
				}
				
				
				
				// check if we need to go to L2
				if (traceTreeDepth > 1) {

					ArrayList<TraceTreeRow> level2TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 2, 
							displayRequirementType,l1Requirement.getRequirementId(), securityProfile);
					Iterator<TraceTreeRow> l2 = level2TraceTreeRows.iterator();
					while (l2.hasNext()){
						l2TraceTreeRow = (TraceTreeRow) l2.next();
						
						traceTreeRows.add(l2TraceTreeRow);
						
						// lets check if we exceeded max
						if (maxRowsInTraceTree <= ++rowsInTraceTree) {
							rs.close();prepStmt.close();con.close();
							request.setAttribute("maxRowsInTraceTreeExceeded", "true");
							return (traceTreeRows);
						}
						
						
						// check if we need to got to L3
						if (traceTreeDepth > 2) {
							ArrayList<TraceTreeRow> l3TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 3, 
									displayRequirementType,l2TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
							Iterator<TraceTreeRow> l3 = l3TraceTreeRows.iterator();
							while (l3.hasNext()){
								l3TraceTreeRow = (TraceTreeRow) l3.next();
								
								traceTreeRows.add(l3TraceTreeRow);
								
								// lets check if we exceeded max
								if (maxRowsInTraceTree <= ++rowsInTraceTree) {
									rs.close();prepStmt.close();con.close();
									request.setAttribute("maxRowsInTraceTreeExceeded", "true");
									return (traceTreeRows);
								}
								
								
								// check if we need to go to L4
								if (traceTreeDepth > 3) {
									ArrayList<TraceTreeRow> l4TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 4, 
											displayRequirementType,l3TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
									Iterator<TraceTreeRow> l4 = l4TraceTreeRows.iterator();
									while (l4.hasNext()){
										l4TraceTreeRow = (TraceTreeRow) l4.next();
										traceTreeRows.add(l4TraceTreeRow);
										
										// lets check if we exceeded max
										if (maxRowsInTraceTree <= ++rowsInTraceTree) {
											rs.close();prepStmt.close();con.close();
											request.setAttribute("maxRowsInTraceTreeExceeded", "true");
											return (traceTreeRows);
										}
										
										
										// check if we need to go to L5
										if (traceTreeDepth > 4) {
											ArrayList<TraceTreeRow> l5TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 5, 
													displayRequirementType,l4TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
											Iterator<TraceTreeRow> l5 = l5TraceTreeRows.iterator();
											while (l5.hasNext()){
												l5TraceTreeRow = (TraceTreeRow) l5.next();
												traceTreeRows.add(l5TraceTreeRow);
												
												// lets check if we exceeded max
												if (maxRowsInTraceTree <= ++rowsInTraceTree) {
													rs.close();prepStmt.close();con.close();
													request.setAttribute("maxRowsInTraceTreeExceeded", "true");
													return (traceTreeRows);
												}

												// check if we need to got L6
												if (traceTreeDepth > 5) {
													ArrayList<TraceTreeRow> l6TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 6, 
															displayRequirementType,l5TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
													Iterator<TraceTreeRow> l6 = l6TraceTreeRows.iterator();
													while (l6.hasNext()){
														l6TraceTreeRow = (TraceTreeRow) l6.next();
														traceTreeRows.add(l6TraceTreeRow);
														
														// lets check if we exceeded max
														if (maxRowsInTraceTree <= ++rowsInTraceTree) {
															rs.close();prepStmt.close();con.close();
															request.setAttribute("maxRowsInTraceTreeExceeded", "true");
															return (traceTreeRows);
														}

														// check if we need to go to L7
														if (traceTreeDepth > 6) {
															ArrayList<TraceTreeRow> l7TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 7, 
																	displayRequirementType,l6TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
															Iterator<TraceTreeRow> l7 = l7TraceTreeRows.iterator();
															while (l7.hasNext()){
																l7TraceTreeRow = (TraceTreeRow) l7.next();
																traceTreeRows.add(l7TraceTreeRow);
																
																// lets check if we exceeded max
																if (maxRowsInTraceTree <= ++rowsInTraceTree) {
																	rs.close();prepStmt.close();con.close();
																	request.setAttribute("maxRowsInTraceTreeExceeded", "true");
																	return (traceTreeRows);
																}

																
																// check if we need to go to L8
																if (traceTreeDepth > 7) {
																	ArrayList<TraceTreeRow> l8TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 8, 
																			displayRequirementType,l7TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
																	Iterator<TraceTreeRow> l8 = l8TraceTreeRows.iterator();
																	while (l8.hasNext()){
																		l8TraceTreeRow = (TraceTreeRow) l8.next();
																		traceTreeRows.add(l8TraceTreeRow);
																		
																		// lets check if we exceeded max
																		if (maxRowsInTraceTree <= ++rowsInTraceTree) {
																			rs.close();prepStmt.close();con.close();
																			request.setAttribute("maxRowsInTraceTreeExceeded", "true");
																			return (traceTreeRows);
																		}

																		
																		// check if we need to go to L9
																		if (traceTreeDepth > 8) {
																			ArrayList<TraceTreeRow> l9TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 9, 
																					displayRequirementType,l8TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
																			Iterator<TraceTreeRow> l9 = l9TraceTreeRows.iterator();
																			while (l9.hasNext()){
																				l9TraceTreeRow = (TraceTreeRow) l9.next();
																				traceTreeRows.add(l9TraceTreeRow);
																				
																				// lets check if we exceeded max
																				if (maxRowsInTraceTree <= ++rowsInTraceTree) {
																					rs.close();prepStmt.close();con.close();
																					request.setAttribute("maxRowsInTraceTreeExceeded", "true");
																					return (traceTreeRows);
																				}

																				
																				
																				
																				// check if we need to go to L10
																				if (traceTreeDepth > 9) {
																					ArrayList<TraceTreeRow> l10TraceTreeRows = getOneLevelDownStreamTraceTreeRows(con, 10, 
																							displayRequirementType,l9TraceTreeRow.getRequirement().getRequirementId(), securityProfile);
																					Iterator<TraceTreeRow> l10 = l10TraceTreeRows.iterator();
																					while (l10.hasNext()){
																						l10TraceTreeRow = (TraceTreeRow) l10.next();
																						traceTreeRows.add(l10TraceTreeRow);
																						
																						// lets check if we exceeded max
																						if (maxRowsInTraceTree <= ++rowsInTraceTree) {
																							rs.close();prepStmt.close();con.close();
																							request.setAttribute("maxRowsInTraceTreeExceeded", "true");
																							return (traceTreeRows);
																						}

																					}// end of L10 while Loop
																				}// end of L10
																			}// end of L9 while Loop
																		}// end of L9
																	}// end of L8 while Loop
																}// end of L8
															}// end of L7 while Loop
														}// end of L7
													}// end of L6 while Loop
												}// end of L6
											}// end of L5 while Loop
										} // end of L5
									}// end of L4 while loop
								}// end of L4
							} // end of L3 while loop
						} // end of L3
					}// end of L2 while loop
									
				} // end of L2
			
				
				
				
				
				
				
				
				
				
				
			}
			// end of First Level Trace Tree Data

			// if the displayRequirementType is not all (i.e the user chose to
			// display only a certain type of req
			// then we need to remove the duplicates.

			
			// Removing the logic for removing duplicates when only 1 req type is selected, as this can mess up structure.
			
			// do this math only if only 1 req is selected in displayReqType
			if (displayRequirementType.contains(",")) {
				displayRequirementType = (String) displayRequirementType.subSequence(0,displayRequirementType.lastIndexOf(","));
				
				if (!(displayRequirementType.equals("all"))){
					// not ALL, so lets see if we have to remove duplicates
					String[] displayRTArray = displayRequirementType.split(",");
				
					if (displayRTArray.length == 1 ){
						// Not ALL, and ONly one req type has been selected, so we have to remove duplicates
						// there is only one elment, so lets remove duplicates
						HashMap notDuplicateTTR = new HashMap();
						Iterator i = traceTreeRows.iterator();
						while (i.hasNext()) {
							TraceTreeRow tTR = (TraceTreeRow) i.next();
							notDuplicateTTR.put(Integer.toString(tTR.getRequirement().getRequirementId()), tTR);
						}
						traceTreeRows = new ArrayList(notDuplicateTTR.values());
					}
				}
			}
			
			
			
			rs.close();
			prepStmt.close();
			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

		return (traceTreeRows);
	}

	// NOTE : This is an exact replica of runTraceTreeReport, with the exception
	// that this returns an array list of Requirements.
	// runTraceTreeReport retuns an array list of traceTreeObjects, which are
	// a superset of Requirements. i.e. Requirment stuff + tree info.

	// when called with a folder id, and a search string,
	// it returns an array list of requirements in this folder.
	// depending on the filter (active, deleted, all),
	// it returns either active reqs, deleted reqs or all reqs.
	public static ArrayList runTraceTreeReportReturnRequirementArrayList(SecurityProfile securityProfile, int projectId,
			int folderId, String filter, int traceTreeDepth, String danglingSearch, String orphanSearch,
			String completedSearch, String incompleteSearch, String suspectUpStreamSearch,
			String suspectDownStreamSearch, String lockedSearch, String includeSubFoldersSearch, int inRTBaselineSearch,
			int changedAfterRTBaselineSearch, String testingStatusSearch, String nameSearch, String descriptionSearch,
			String ownerSearch, String externalURLSearch, String approvedBySearch, String rejectedBySearch,
			String pendingBySearch, String traceToSearch, String traceFromSearch, String statusSearch,
			String prioritySearch, String pctCompleteSearch, String customAttributeSearch, String databaseType,
			String displayRequirementType) {

		if (displayRequirementType == null) {
			displayRequirementType = "all";
		}

		ArrayList<Requirement> traceTreeRows = new ArrayList<Requirement>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// here is the sql that we will use to get reqs that trace to this
			// requirement.
			String traceTreeSQL = "";

			if (databaseType.equals("mySQL")) {
				traceTreeSQL = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id," + " r.name, "
						+ " r.description, r.tag, r.full_tag,"
						+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
						+ " r.approvers  ," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
						+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
						+ " f.folder_path, r.deleted, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\",  r.last_modified_by, "
						+ " r.last_modified_dt, rt.name \"requirement_type_name\", t.suspect "
						+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t, gr_folders f  "
						+ " where t.to_requirement_id = ? " + " and t.from_requirement_id = r.id "
						+ " and r.requirement_type_id = rt.id " + " and r.folder_id = f.id " + " and r.deleted= 0 "
						+ " order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag";
			} else {

				traceTreeSQL = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id," + " r.name, "
						+ "   substr(to_char(r.description),1,4000) \"description\", r.tag, r.full_tag,"
						+ " r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\","
						+ " r.approvers  ," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
						+ " r.external_url,  " + "  substr(to_char(r.trace_to),1,4000) \"trace_to\", "
						+ "  substr(to_char(r.trace_from),1,4000) \"trace_from\",  "
						+ "  substr(to_char(r.user_defined_attributes), 1, 4000) \"user_defined_attributes\", r.testing_status, "
						+ " r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\","
						+ " r.last_modified_by, "
						+ " r.last_modified_dt, rt.name \"requirement_type_name\", t.suspect, "
						+ " t.description \"traceDescription\"  "
						+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t, gr_folders f "
						+ " where t.to_requirement_id = ? " + " and t.from_requirement_id = r.id "
						+ " and r.requirement_type_id = rt.id " + " and r.folder_id = f.id " + " and r.deleted= 0 "
						+ " order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag";

			}

			String sortBy = "";
			String sortByType = "";

			String sql = buildSQL(projectId, folderId, filter, danglingSearch, orphanSearch, completedSearch,
					incompleteSearch, suspectUpStreamSearch, suspectDownStreamSearch, lockedSearch,
					includeSubFoldersSearch, inRTBaselineSearch, changedAfterRTBaselineSearch, testingStatusSearch,
					nameSearch, descriptionSearch, ownerSearch, externalURLSearch, approvedBySearch, rejectedBySearch,
					pendingBySearch, traceToSearch, traceFromSearch, statusSearch, prioritySearch, pctCompleteSearch,
					customAttributeSearch, sortBy, sortByType, databaseType);

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			rs = prepStmt.executeQuery();

			int requirementId = 0;
			int requirementTypeId = 0;
			projectId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			String requirementLockedBy = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy = "";
			String requirementTypeName = "";

			Requirement l1Requirement = null;
			Requirement l2Requirement = null;
			Requirement l3Requirement = null;
			Requirement l4Requirement = null;
			Requirement l5Requirement = null;
			Requirement l6Requirement = null;
			Requirement l7Requirement = null;
			Requirement l8Requirement = null;
			Requirement l9Requirement = null;
			Requirement l10Requirement = null;



			// lets execute the filtered SQL to get the root level requirements.
			// we will build a trace tree for each one of these requirements.
			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				requirementName = rs.getString("name");
				requirementDescription = rs.getString("description");
				requirementTag = rs.getString("tag");
				requirementFullTag = rs.getString("full_tag");
				version = rs.getInt("version");
				approvedByAllDt = rs.getString("approved_by_all_dt");
				approvers = rs.getString("approvers");
				requirementStatus = rs.getString("status");
				requirementPriority = rs.getString("priority");
				requirementOwner = rs.getString("owner");
				requirementLockedBy = rs.getString("locked_by");
				requirementPctComplete = rs.getInt("pct_complete");
				requirementExternalUrl = rs.getString("external_url");
				traceTo = rs.getString("trace_to");
				traceFrom = rs.getString("trace_from");
				userDefinedAttributes = rs.getString("user_defined_attributes");
				testingStatus = rs.getString("testing_status");
				deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				createdBy = rs.getString("created_by");
				createdDt = rs.getString("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				requirementTypeName = rs.getString("requirement_type_name");

				l1Requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId,
						requirementName, requirementDescription, requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus, requirementPriority, requirementOwner,
						requirementLockedBy, requirementPctComplete, requirementExternalUrl, traceTo, traceFrom,
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);

				// if the user does not have read permissions on this
				// requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges()
						.contains("readRequirementsInFolder" + l1Requirement.getFolderId()))) {
					l1Requirement.redact();
				}

				// lets see if this TraceTree Requirements is one of the display
				// Requirement Types.
				// i.e the user chose to either see 'all' requirements of the
				// TraceTree or
				// see only the ones he / she decided to see.
				// we add only the resulting row to the arraylist only if the
				// user choose to see ALL requirement types
				// or if this requirement fits the bill.
				if (displayRequirementType.equals("all")) {
					traceTreeRows.add(l1Requirement);
				} else {
					// means some display restrictions are in place
					if (l1Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
						// every req full tag is
						// requriementtypeshortname-number. eg BR-11.
						// so if the user chose to see only Product
						// Requirements, then the displayRequirementType will be
						// PR
						// in this case, lets skip it.
						traceTreeRows.add(l1Requirement);
					}
				}

				// check if we need to go to L2
				if (traceTreeDepth > 1) {
					ArrayList<Requirement> level2Requirements = getOneLevelDownStreamRequirements(con,  l1Requirement.getRequirementId(), securityProfile);
					Iterator<Requirement> l2 = level2Requirements.iterator();
					while (l2.hasNext()){
						l2Requirement = (Requirement) l2.next();
						if (displayRequirementType.equals("all")) {
							traceTreeRows.add(l2Requirement);
						} else {
							if (l2Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
								traceTreeRows.add(l2Requirement);
							}
						}
						// check if we need to got to L3
						if (traceTreeDepth > 2) {
							ArrayList<Requirement> level3Requirements = getOneLevelDownStreamRequirements(con,  l2Requirement.getRequirementId(), securityProfile);
							Iterator<Requirement> l3 = level3Requirements.iterator();
							while (l3.hasNext()){
								l3Requirement = (Requirement) l3.next();
								if (displayRequirementType.equals("all")) {
									traceTreeRows.add(l3Requirement);
								} else {
									if (l3Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
										traceTreeRows.add(l3Requirement);
									}
								}
								
								// check if we need to go to L4
								if (traceTreeDepth > 3) {
									ArrayList<Requirement> level4Requirements = getOneLevelDownStreamRequirements(con,  l3Requirement.getRequirementId(), securityProfile);
									Iterator<Requirement> l4 = level4Requirements.iterator();
									while (l4.hasNext()){
										l4Requirement = (Requirement) l4.next();
										if (displayRequirementType.equals("all")) {
											traceTreeRows.add(l4Requirement);
										} else {
											if (l4Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
												traceTreeRows.add(l4Requirement);
											}
										}
										// check if we need to go to L5
										if (traceTreeDepth > 4) {
											ArrayList<Requirement> level5Requirements = getOneLevelDownStreamRequirements(con,  l4Requirement.getRequirementId(), securityProfile);
											Iterator<Requirement> l5 = level5Requirements.iterator();
											while (l5.hasNext()){
												l5Requirement = (Requirement) l5.next();
												if (displayRequirementType.equals("all")) {
													traceTreeRows.add(l5Requirement);
												} else {
													if (l5Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
														traceTreeRows.add(l5Requirement);
													}
												}
												// check if we need to got L6
												if (traceTreeDepth > 5) {
													ArrayList<Requirement> level6Requirements = getOneLevelDownStreamRequirements(con,  l5Requirement.getRequirementId(), securityProfile);
													Iterator<Requirement> l6 = level6Requirements.iterator();
													while (l6.hasNext()){
														l6Requirement = (Requirement) l6.next();
														if (displayRequirementType.equals("all")) {
															traceTreeRows.add(l6Requirement);
														} else {
															if (l6Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
																traceTreeRows.add(l6Requirement);
															}
														}
														
														// check if we need to go to L7
														if (traceTreeDepth > 6) {
															ArrayList<Requirement> level7Requirements = getOneLevelDownStreamRequirements(con,  l6Requirement.getRequirementId(), securityProfile);
															Iterator<Requirement> l7 = level7Requirements.iterator();
															while (l7.hasNext()){
																l7Requirement = (Requirement) l7.next();
																if (displayRequirementType.equals("all")) {
																	traceTreeRows.add(l7Requirement);
																} else {
																	if (l7Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
																		traceTreeRows.add(l7Requirement);
																	}
																}
																// check if we need to go to L8
																if (traceTreeDepth > 7) {
																	ArrayList<Requirement> level8Requirements = getOneLevelDownStreamRequirements(con,  l7Requirement.getRequirementId(), securityProfile);
																	Iterator<Requirement> l8 = level8Requirements.iterator();
																	while (l8.hasNext()){
																		l8Requirement = (Requirement) l8.next();
																		if (displayRequirementType.equals("all")) {
																			traceTreeRows.add(l8Requirement);
																		} else {
																			if (l8Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
																				traceTreeRows.add(l8Requirement);
																			}
																		}
																		// check if we need to go to L9
																		if (traceTreeDepth > 8) {
																			ArrayList<Requirement> level9Requirements = getOneLevelDownStreamRequirements(con,  l8Requirement.getRequirementId(), securityProfile);
																			Iterator<Requirement> l9 = level9Requirements.iterator();
																			while (l9.hasNext()){
																				l9Requirement = (Requirement) l9.next();
																				if (displayRequirementType.equals("all")) {
																					traceTreeRows.add(l9Requirement);
																				} else {
																					if (l9Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
																						traceTreeRows.add(l9Requirement);
																					}
																				}
																				// check if we need to go to L10
																				if (traceTreeDepth > 9) {
																					ArrayList<Requirement> level10Requirements = getOneLevelDownStreamRequirements(con,  l9Requirement.getRequirementId(), securityProfile);
																					Iterator<Requirement> l10 = level10Requirements.iterator();
																					while (l10.hasNext()){
																						l10Requirement = (Requirement) l10.next();
																						if (displayRequirementType.equals("all")) {
																							traceTreeRows.add(l10Requirement);
																						} else {
																							if (l10Requirement.getRequirementFullTag().startsWith(displayRequirementType)) {
																								traceTreeRows.add(l10Requirement);
																							}
																						}
																					}// end of L10 while Loop
																				}// end of L10
																			}// end of L9 while Loop
																		}// end of L9
																	}// end of L8 while Loop
																}// end of L8
															}// end of L7 while Loop
														}// end of L7
													}// end of L6 while Loop
												}// end of L6
											}// end of L5 while Loop
										} // end of L5
									}// end of L4 while loop
								}// end of L4
							} // end of L3 while loop
						} // end of L3
					}// end of L2 while loop
				} // end of L2
			}
			// end of First Level Trace Tree Data

			rs.close();
			prepStmt.close();
			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

		return (traceTreeRows);
	}

	public static String buildSQL(int projectId, int folderId, String filter, String danglingSearch,
			String orphanSearch, String completedSearch, String incompleteSearch, String suspectUpStreamSearch,
			String suspectDownStreamSearch, String lockedSearch, String includeSubFoldersSearch, int inRTBaselineSearch,
			int changedAfterRTBaselineSearch, String testingStatusSearch, String nameSearch, String descriptionSearch,
			String ownerSearch, String externalURLSearch, String approvedBySearch, String rejectedBySearch,
			String pendingBySearch, String traceToSearch, String traceFromSearch, String statusSearch,
			String prioritySearch, String pctCompleteSearch, String customAttributeSearch, String sortBy,
			String sortByType, String databaseType) {

		String sqlSelect = "";
		String sqlRemaining = "";

		// Note : we are NOT using foldeId to filter for the requirements in a
		// folder, for a specific
		// reason. To implement 'look for reqs in sub folder', we are leverage
		// the 'folder_path' column
		// which keeps the full path of the folder in the requirements.
		// so to get all the sub folders we just look for requirement.path like
		// 'foldername%'.
		// this model works only if we search by folder name, even when we are
		// not looking to include
		// sub folders.
		Folder folder = new Folder(folderId);

		//
		// This sql gets the list of requirements in this folder and puts them
		// in the arrray list.
		// creates a requirement object for each row and puts them the array
		// list.

		//

		if (databaseType.equals("mySQL")) {

			sqlSelect = "  SELECT distinct  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
					+ " r.description, r.tag, r.full_tag, "
					+ " r.version, "
					+ " date_format(r.approved_by_all_dt, '%d-%b-%Y') \"approved_by_all_dt\" ,"
					+ " r.approvers ,  " + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, "
					+ " date_format(r.created_dt, '%d-%b-%Y') \"created_dt\",  r.last_modified_by, "
					+ " date_format(r.last_modified_dt, '%d-%b-%Y') \"last_modified_dt\",  rt.name \"requirement_type_name\", "
					+ " r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 ,ltrim(r.name), ltrim(r.description) ";
			sqlRemaining = " FROM gr_requirements r , gr_requirement_types rt, gr_folders f";

		} else {
			sqlSelect = " SELECT distinct r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
					+ "  substr(to_char(r.description),1,4000) \"description\", r.tag, r.full_tag, "
					+ " r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\" ,"
					+ " r.approvers ,  " + " r.status, r.priority, r.owner, r.locked_by , r.pct_complete, "
					+ " r.external_url,  substr(to_char(r.trace_to),1,4000) \"trace_to\","
					+ "  substr(to_char(r.trace_from),1,4000) \"trace_from\","
					+ "   substr(to_char(r.user_defined_attributes),1,4000) \"user_defined_attributes\", r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\",  r.last_modified_by, "
					+ " r.last_modified_dt, rt.name \"requirement_type_name\" , "
					+ " r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4, ltrim(r.name), ltrim(to_char(r.description)) ";
			sqlRemaining = " FROM gr_requirements r , gr_requirement_types rt, gr_folders f";

		}
		if ((inRTBaselineSearch > 0) || (changedAfterRTBaselineSearch > 0)) {
			// this means we have been asked to narrow the requirements to this
			// baseline only.
			// so lets add the baselines table to the sql.
			sqlRemaining += " , gr_requirement_baselines rb, gr_requirement_versions rv ";
		}

		// Its' critical to add project_id to the search as it will prevent
		// people from searching for stuff in projects they don't have
		// access to by altering the folder_id, requirement_type_id etc...
		sqlRemaining += " where r.project_id = ? " + " and   r.folder_id = f.id "
				+ " and r.requirement_type_id = rt.id ";

		if ((inRTBaselineSearch > 0) || (changedAfterRTBaselineSearch > 0)) {
			// Since baselines are inolved, lets join the baseline and version
			// table to the other tables.
			sqlRemaining += " and rb.version_id = rv.id " + " and r.id  =  rb.requirement_id ";

		}
		if (inRTBaselineSearch > 0) {
			// this means we have been asked to narrow the requirements to this
			// baseline only.
			// so lets add the and clause
			sqlRemaining += " and rb.rt_baseline_id = " + inRTBaselineSearch + " ";
		}

		if (changedAfterRTBaselineSearch > 0) {
			// this means we have been asked to narrow the requirements to those
			// that have changed after this baseline only.

			sqlRemaining += " and rb.rt_baseline_id = " + changedAfterRTBaselineSearch
					+ " and r.version > rv.version  ";
		}

		// testingStatus search came from a multi select. so needs special
		// handling.
		if ((testingStatusSearch != null) && (!(testingStatusSearch.equals("")))) {
			String[] testingStatusArray = testingStatusSearch.split(",");
			String testingStatusSQL = "";
			for (int i = 0; i < testingStatusArray.length; i++) {
				testingStatusSQL += "'" + testingStatusArray[i] + "',";
			}
			// drop the last ,
			if ((testingStatusSQL != null) && (testingStatusSQL.contains(","))) {
				testingStatusSQL = (String) testingStatusSQL.subSequence(0, testingStatusSQL.lastIndexOf(","));
			}

			// now build the sql.
			sqlRemaining += " and r.testing_status in (" + testingStatusSQL + ") ";
		}

		// Lets add custom sql so that we pick up only reqs of a certain status.

		if (filter.equals("active")) {
			sqlRemaining += " and r.deleted = 0  ";
		}

		if (filter.equals("deleted")) {
			sqlRemaining += " and r.deleted = 1  ";
		}

		if (filter.equals("all")) {
			sqlRemaining += "  ";
		}

		// lets build the SQL based on the search attributes.
		// note: even if a requirement is dangling, it can only re reported as
		// dangling
		// if this requirement type has the 'can be reported as dangling' set to
		// yes
		// this is to catch scenarios where TR req types can not have any traces
		// going in to them, but are not technically dangling requirements.

		if ((danglingSearch != null) && ((danglingSearch.equals("danglingOnly")))) {
			if (databaseType.equals("mySQL")) {
				sqlRemaining += " and (" + " (r.trace_from is null or r.trace_from = '') " + " and "
						+ " (rt.can_be_dangling = 1) " + " )";
			} else {
				sqlRemaining += " and ("
						+ " (upper(to_char(r.trace_from)) is null or upper(to_char(r.trace_from)) = '') " + " and "
						+ " (rt.can_be_dangling = 1) " + " )";
			}

		}
		// note: even if a requirement is orphan, it can only re reported as
		// orphan
		// if this requirement type has the 'can be reported as orphan' set to
		// yes
		// this is to catch scenarios where REL req types can not have any
		// traces
		// going out of them, but are not technically orphan requirements.
		if ((orphanSearch != null) && ((orphanSearch.equals("orphanOnly")))) {
			if (databaseType.equals("mySQL")) {
				sqlRemaining += " and (" + " (r.trace_to is null or r.trace_to = '')" + " and"
						+ " (rt.can_be_orphan = 1)" + " ) ";
			} else {
				sqlRemaining += " and (" + " (upper(to_char(r.trace_to)) is null or upper(to_char(r.trace_to)) = '')"
						+ " and" + " (rt.can_be_orphan = 1)" + " ) ";
			}

		}
		if ((completedSearch != null) && ((completedSearch.equals("completedOnly")))) {
			sqlRemaining += " and r.pct_complete = 100 ";
		}
		if ((incompleteSearch != null) && ((incompleteSearch.equals("incompleteOnly")))) {
			sqlRemaining += " and r.pct_complete <> 100 ";
		}

		if ((suspectUpStreamSearch != null) && ((suspectUpStreamSearch.equals("suspectUpStreamOnly")))) {
			if (databaseType.equals("mySQL")) {
				sqlRemaining += "and upper(r.trace_to) like '%(S)%' ";
			} else {
				sqlRemaining += "and upper(to_char(r.trace_to)) like '%(S)%' ";
			}

		}

		if ((suspectDownStreamSearch != null) && ((suspectDownStreamSearch.equals("suspectDownStreamOnly")))) {
			if (databaseType.equals("mySQL")) {
				sqlRemaining += "and upper(r.trace_from) like '%(S)%' ";
			} else {
				sqlRemaining += "and upper(to_char(r.trace_from)) like '%(S)%' ";
			}

		}

		if ((lockedSearch != null) && ((lockedSearch.equals("lockedOnly")))) {
			if (databaseType.equals("mySQL")) {
				sqlRemaining += " and r.locked_by is not null and r.locked_by != '' ";
			} else {
				sqlRemaining += " and r.locked_by is not null and r.locked_by != '' ";
			}

		}
		if ((includeSubFoldersSearch != null) && ((includeSubFoldersSearch.equals("includeSubFoldersOnly")))) {
			sqlRemaining += " and f.folder_path like  '" + folder.getFolderPath() + "%'";
		} else {
			// in this case, i.e. sub folders are not to be included, we still
			// need to
			// narrow the search to this folder.
			sqlRemaining += " and f.folder_path =  '" + folder.getFolderPath() + "'";
		}

		if ((nameSearch != null) && (!(nameSearch.equals("")))) {
			if (databaseType.equals("mySQL")) {
				if (nameSearch.toUpperCase().contains("#OR#")) {
					String[] nameSearchArray = nameSearch.toUpperCase().split("#OR#");

					sqlRemaining += " and ( ";
					for (int i = 0; i < nameSearchArray.length; i++) {

						String nameSearchString = nameSearchArray[i];
						if (nameSearchString.startsWith("=")){
							nameSearchString = nameSearchString.replace("=", "");
							sqlRemaining += " ( upper(r.name) =  '" + nameSearchString + "' ) or ";
						}
						else {
							sqlRemaining += " ( upper(r.name) like '%" + nameSearchArray[i] + "%' ) or ";
						}
						
					}
					// drop the last or
					if (sqlRemaining.contains("or")) {
						sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf("or"));
					}
					sqlRemaining += " ) ";
				} else {
					if (nameSearch.startsWith("=")){
						nameSearch = nameSearch.replace("=", "");
						sqlRemaining += " and upper(r.name) = '" + nameSearch.toUpperCase() + "'";
					}
					else {
						sqlRemaining += " and upper(r.name) like '%" + nameSearch.toUpperCase() + "%'";
					}
					
				}
			} else {
				if (nameSearch.toUpperCase().contains("#OR#")) {
					String[] nameSearchArray = nameSearch.toUpperCase().split("#OR#");
					sqlRemaining += " and ( ";
					for (int i = 0; i < nameSearchArray.length; i++) {
						sqlRemaining += "  upper(to_char(r.name)) like '%" + nameSearchArray[i].trim() + "%' ) or ";
					}
					// drop the last or
					if (sqlRemaining.contains("or")) {
						sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf("or"));
					}
					sqlRemaining += " ) ";
				} else {
					sqlRemaining += " and upper(to_char(r.name)) like '%" + nameSearch.trim().toUpperCase() + "%'";
				}
			}

		}
		if ((descriptionSearch != null) && (!(descriptionSearch.equals("")))) {
			if (databaseType.equals("mySQL")) {
				if (descriptionSearch.toUpperCase().contains("#OR#")) {
					String[] descriptionSearchArray = descriptionSearch.toUpperCase().split("#OR#");

					sqlRemaining += " and ( ";
					for (int i = 0; i < descriptionSearchArray.length; i++) {

						sqlRemaining += " ( upper(r.description) like '%" + descriptionSearchArray[i].trim()
								+ "%' ) or ";
					}
					// drop the last or
					if (sqlRemaining.contains("or")) {
						sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf("or"));
					}
					sqlRemaining += " ) ";
				} else {
					sqlRemaining += " and upper(r.description) like '%" + descriptionSearch.trim().toUpperCase() + "%'";
				}
			} else {
				if (descriptionSearch.toUpperCase().contains("#OR#")) {
					String[] descriptionSearchArray = descriptionSearch.toUpperCase().split("#OR#");
					sqlRemaining += " and ( ";
					for (int i = 0; i < descriptionSearchArray.length; i++) {
						sqlRemaining += "  upper(to_char(r.name)) like '%" + descriptionSearchArray[i].trim()
								+ "%' ) or ";
					}
					// drop the last or
					if (sqlRemaining.contains("or")) {
						sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf("or"));
					}
					sqlRemaining += " ) ";
				} else {
					sqlRemaining += " and upper(to_char(r.description)) like '%"
							+ descriptionSearch.trim().toUpperCase() + "%'";
				}
			}

		}
		if ((ownerSearch != null) && (!(ownerSearch.equals("")))) {
			if (databaseType.equals("mySQL")) {
				if (ownerSearch.toUpperCase().contains("#OR#")) {
					String[] ownerSearchArray = ownerSearch.toUpperCase().split("#OR#");

					sqlRemaining += " and ( ";
					for (int i = 0; i < ownerSearchArray.length; i++) {

						sqlRemaining += " ( upper(r.owner) like '%" + ownerSearchArray[i].trim() + "%' ) or ";
					}
					// drop the last or
					if (sqlRemaining.contains("or")) {
						sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf("or"));
					}
					sqlRemaining += " ) ";
				} else {
					sqlRemaining += " and upper(r.owner) like '%" + ownerSearch.trim().toUpperCase() + "%'";
				}
			} else {
				if (ownerSearch.toUpperCase().contains("#OR#")) {
					String[] ownerSearchArray = ownerSearch.toUpperCase().split("#OR#");

					sqlRemaining += " and ( ";
					for (int i = 0; i < ownerSearchArray.length; i++) {

						sqlRemaining += " ( upper(to_char(r.owner)) like '%" + ownerSearchArray[i].trim() + "%' ) or ";
					}
					// drop the last or
					if (sqlRemaining.contains("or")) {
						sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf("or"));
					}
					sqlRemaining += " ) ";

				} else {
					sqlRemaining += " and upper(to_char(r.owner)) like '%" + ownerSearch.trim().toUpperCase() + "%'";
				}
			}

		}
		if ((externalURLSearch != null) && (!(externalURLSearch.equals("")))) {
			if (databaseType.equals("mySQL")) {
				sqlRemaining += " and upper(r.external_url) like '%" + externalURLSearch.trim().toUpperCase() + "%'";
			} else {
				sqlRemaining += " and upper(to_char(r.external_url)) like '%" + externalURLSearch.trim().toUpperCase()
						+ "%'";
			}

		}
		if ((approvedBySearch != null) && (!(approvedBySearch.equals("")))) {
			approvedBySearch = approvedBySearch.replace("%", "");
			if (databaseType.equals("mySQL")) {
				sqlRemaining += " and upper(r.approvers) like '%(A)" + approvedBySearch.trim().toUpperCase() + "%'";
			} else {
				sqlRemaining += " and upper(to_char(r.approvers)) like '%(A)" + approvedBySearch.trim().toUpperCase()
						+ "%'";
			}

		}
		if ((rejectedBySearch != null) && (!(rejectedBySearch.equals("")))) {
			rejectedBySearch = rejectedBySearch.replace("%", "");
			if (databaseType.equals("mySQL")) {
				sqlRemaining += " and upper(r.approvers) like '%(R)" + rejectedBySearch.trim().toUpperCase() + "%'";
			} else {
				sqlRemaining += " and upper(to_char(r.approvers)) like '%(R)" + rejectedBySearch.trim().toUpperCase()
						+ "%'";
			}

		}
		if ((pendingBySearch != null) && (!(pendingBySearch.equals("")))) {
			pendingBySearch = pendingBySearch.replace("%", "");
			if (databaseType.equals("mySQL")) {
				sqlRemaining += " and upper(r.approvers) like '%(P)" + pendingBySearch.trim().toUpperCase() + "%'";
			} else {
				sqlRemaining += " and upper(to_char(r.approvers)) like '%(P)" + pendingBySearch.trim().toUpperCase()
						+ "%'";
			}

		}

		if ((traceToSearch != null) && (!(traceToSearch.equals("")))) {
			// lets support , separated list . like BR-1,BR-2,BR-3 etc...

			if (traceToSearch.contains(",")) {
				// , separated values were sent in. lets split the traceToSearch
				// by ,
				sqlRemaining += " and ( ";
				String[] traceToArray = traceToSearch.split(",");
				for (int i = 0; i < traceToArray.length; i++) {
					if ((traceToArray[i] != null) && (!(traceToArray[i].equals("")))) {
						sqlRemaining += "  concat(upper(r.trace_to),',') like '%" + traceToArray[i].trim().toUpperCase()
								+ ",%'  or ";
					}
				}
				// drop the last or
				if ((sqlRemaining != null) && (sqlRemaining.contains(" or "))) {
					sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf(" or "));
				}
				sqlRemaining += " ) ";

			} else {
				sqlRemaining += " and concat(upper(r.trace_to),',') like '%" + traceToSearch.trim().toUpperCase()
						+ ",%'";
			}
		}

		if ((traceFromSearch != null) && (!(traceFromSearch.equals("")))) {

			if (traceFromSearch.contains(",")) {
				// , separated values were sent. lets split the traceToSearch by
				// ,
				sqlRemaining += " and ( ";
				String[] traceFromArray = traceFromSearch.split(",");
				for (int i = 0; i < traceFromArray.length; i++) {
					if ((traceFromArray[i] != null) && (!(traceFromArray[i].equals("")))) {
						sqlRemaining += "  concat(upper(r.trace_from),',') like '%"
								+ traceFromArray[i].trim().toUpperCase() + ",%'  or ";
					}
				}
				// drop the last or
				if ((sqlRemaining != null) && (sqlRemaining.contains(" or "))) {
					sqlRemaining = (String) sqlRemaining.subSequence(0, sqlRemaining.lastIndexOf(" or "));
				}
				sqlRemaining += " ) ";

			} else {
				sqlRemaining += " and concat(upper(r.trace_from),',') like '%" + traceFromSearch.trim().toUpperCase()
						+ ",%'";
			}
		}

		// status search came from a multi select. so needs special handling.
		if ((statusSearch != null) && (!(statusSearch.equals("")))) {
			String[] statusArray = statusSearch.split(",");
			String statusSQL = "";
			for (int i = 0; i < statusArray.length; i++) {
				statusSQL += "'" + statusArray[i] + "',";
			}
			// drop the last ,
			if ((statusSQL != null) && (statusSQL.contains(","))) {
				statusSQL = (String) statusSQL.subSequence(0, statusSQL.lastIndexOf(","));
			}

			// now build the sqlRemaining.
			sqlRemaining += " and r.status in (" + statusSQL + ") ";
		}

		// priority search came from a multi select. so needs special handling.
		if ((prioritySearch != null) && (!(prioritySearch.equals("")))) {
			String[] priorityArray = prioritySearch.split(",");
			String prioritySQL = "";
			for (int i = 0; i < priorityArray.length; i++) {
				prioritySQL += "'" + priorityArray[i] + "',";
			}
			// drop the last ,
			if ((prioritySQL != null) && (prioritySQL.contains(","))) {
				prioritySQL = (String) prioritySQL.subSequence(0, prioritySQL.lastIndexOf(","));
			}

			// now build the sqlRemaining.
			sqlRemaining += " and r.priority in (" + prioritySQL + ") ";
		}

		// pctComplete search came from a text box
		if ((pctCompleteSearch != null) && (!(pctCompleteSearch.equals("")))) {

			// now build the sqlRemaining.
			sqlRemaining += " and r.pct_complete < " + pctCompleteSearch + " ";

		}

		// Now, lets handle custom attributes.
		// customAttributeSearch will have
		// avalue1:--:avalu2sel1:##:avalu2sel1:##:avalu2sel2:--:avalue3

		String[] customAttributeSearches = customAttributeSearch.split(":--:");
		for (int i = 0; i < customAttributeSearches.length; i++) {
			String cAS = customAttributeSearches[i];
			// at this point cAS is something like Severity:#:1:##:2:##:

			if ((cAS != null) && (!(cAS.equals("")))) {
				if (cAS.contains(":##:")) {
					// this is a drop down with multiple selection.
					// needs special handling.
					// lets get the label
					String[] split1 = cAS.split(":#:");
					String attributeLabel = split1[0];
					String attributeValueString = split1[1];

					String[] attributeValues = attributeValueString.split(":##:");

					String orSQL = "";
					for (int j = 0; j < attributeValues.length; j++) {
						if ((attributeValues[j] != null) && !(attributeValues[j].equals(""))) {

							if (databaseType.equals("mySQL")) {
								orSQL += " upper(r.user_defined_attributes) like  ('%" + attributeLabel.toUpperCase()
										+ ":#:" + "%" + attributeValues[j].trim().toUpperCase() + "%') " + "or";
							} else {
								orSQL += " upper(to_char(r.user_defined_attributes)) like  ('%"
										+ attributeLabel.toUpperCase() + ":#:" + "%"
										+ attributeValues[j].trim().toUpperCase() + "%') " + "or";
							}
						}
					}
					// drop the last 'or'
					if (orSQL.contains("or")) {
						orSQL = (String) orSQL.subSequence(0, orSQL.lastIndexOf("or"));
					}
					sqlRemaining += " and (" + orSQL + ") ";
				} else {
					// this is regular text box.
					if (cAS.contains(":#:")) {
						// cAS has something like Customer:#:IBM
						String[] customAttribute = cAS.split(":#:");
						String customLabel = customAttribute[0];
						String customValue = customAttribute[1];
						// At this point, this could be a Text box or a Date or
						// Number
						// For text box / date , we can use the string like
						// search with % at each end
						// For number , we have to do exact matches. So, based
						// on the label, we will get the
						// Attribute object, and figure out whether this is a
						// number or text or date and treat accordingly.
						RTAttribute rTAttribute = new RTAttribute(folder.getRequirementTypeId(), customLabel);
						String customSearchString = "";
						if (rTAttribute.getAttributeType().equals("Number")) {
							// this is a number, so exact match.
							// do nothing. we will control this at the calling
							// report end.
						} else {
							// this is a URL / Date / Text box. so wild cards in
							// search string is OK.
							customSearchString = "%" + customLabel + ":#:" + "%" + customValue.trim() + "%";
						}

						if (databaseType.equals("mySQL")) {
							if (!(customSearchString.equals(""))) {
								sqlRemaining += " and upper(r.user_defined_attributes) like  ('"
										+ customSearchString.trim().toUpperCase() + "') ";
							}
						} else {
							if (!(customSearchString.equals(""))) {
								sqlRemaining += " and upper(to_char(r.user_defined_attributes)) like  ('"
										+ customSearchString.toUpperCase() + "') ";
							}
						}
					}

				}
			}
		}

		if ((sortBy != null) && (!sortBy.equals(""))) {
			if (sortBy.equals("tag")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.tag_level1 desc ,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
				} else {
					sqlRemaining += "  order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
				}
			}

			if (sortBy.equals("name")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by ltrim(r.name) desc ";
				} else {
					sqlRemaining += "  order by ltrim(r.name) ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("description")) {

				if ((sortByType != null) && (sortByType.equals("descending"))) {
					if (databaseType.equals("mySQL")) {
						sqlRemaining += "  order by ltrim(r.description) desc ";
					} else {
						sqlRemaining += "  order by ltrim(to_char(r.description)) desc ";
					}

				} else {
					if (databaseType.equals("mySQL")) {
						sqlRemaining += "  order by ltrim(r.description) ";
					} else {
						sqlRemaining += "  order by ltrim(to_char(r.description)) ";
					}

				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("owner")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.owner desc ";
				} else {
					sqlRemaining += "  order by r.owner ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("external_url")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.external_url desc ";
				} else {
					sqlRemaining += "  order by r.external_url ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("approval_status")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.status desc ";
				} else {
					sqlRemaining += "  order by r.status ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("priority")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.priority desc ";
				} else {
					sqlRemaining += "  order by r.priority ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("pct_complete")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.pct_complete desc ";
				} else {
					sqlRemaining += "  order by r.pct_complete ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("testing_status")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.testing_status desc ";
				} else {
					sqlRemaining += "  order by r.testing_status ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("folder_path")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by f.folder_path desc ";
				} else {
					sqlRemaining += "  order by f.folder_path ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("created_by")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.created_by desc ";
				} else {
					sqlRemaining += "  order by r.created_by ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("created_dt")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.created_dt desc ";
				} else {
					sqlRemaining += "  order by r.created_dt ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("last_modified_by")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.last_modified_by desc ";
				} else {
					sqlRemaining += "  order by r.last_modified_by ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.equals("last_modified_dt")) {
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					sqlRemaining += "  order by r.last_modified_dt desc ";
				} else {
					sqlRemaining += "  order by r.last_modified_dt ";
				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}

			if (sortBy.contains("CustomAttribute")) {
				String customAttribute = sortBy.replace("CustomAttribute", "");
				if (databaseType.equals("mySQL")) {
					sqlSelect += ", ltrim(substr(user_defined_attributes,locate('" + customAttribute
							+ "', user_defined_attributes)+ " + customAttribute.length() + ", 6 ))  ";
				} else {
					sqlSelect += ", ltrim(substr(to_char(user_defined_attributes),instr( to_char(user_defined_attributes), '"
							+ customAttribute + "')+ " + customAttribute.length() + ", 6 ))  ";
				}

				// this is a sneaky , but powerful way of sorting by custom
				// attributes
				// since custom attribs are strored in a long string called user
				// defined atrributes
				// its easier to locate the custom value from this tring and
				// sort based on that
				// than to join to a table called gr_requiremnt_attributes
				// since sorting mostly is done by the first 6 chars, we try to
				// locate the first 6 chars of the custom attribute value
				// and order by that.
				if ((sortByType != null) && (sortByType.equals("descending"))) {
					if (databaseType.equals("mySQL")) {
						sqlRemaining += " order by ltrim(substr(user_defined_attributes,locate('" + customAttribute
								+ "', user_defined_attributes)+ " + customAttribute.length() + ", 6 )) desc";
					} else {
						sqlRemaining += " order by ltrim(substr(to_char(user_defined_attributes),instr(to_char(user_defined_attributes), '"
								+ customAttribute + "')+ " + customAttribute.length() + ", 6 )) desc";
					}

				} else {
					if (databaseType.equals("mySQL")) {
						sqlRemaining += " order by ltrim(substr(user_defined_attributes,locate('" + customAttribute
								+ "', user_defined_attributes)+ " + customAttribute.length() + ", 6)) ";
					} else {
						sqlRemaining += " order by ltrim(substr(to_char(user_defined_attributes),instr(to_char(user_defined_attributes), '"
								+ customAttribute + "' )+ " + customAttribute.length() + ", 6)) ";
					}

				}
				sqlRemaining += ", r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";
			}
		} else {
			sqlRemaining += "  order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag";
		}

		String sql = sqlSelect + sqlRemaining;
		return (sql);
	}

	// called to service bulk edits, with a target Value and a :##: separated
	// list of
	// req ids.
	public static String bulkEdit(Project project, String targetArtifact, String targetValue, String targetRequirements,
			SecurityProfile securityProfile, int projectId, HttpServletRequest request, String databaseType,
			String mailHost,  String  transportProtocol, String  smtpAuth,String  smtpPort,String  smtpSocketFactoryPort,
			String  emailUserId,String  emailPassword) {
		String setBulkEditMessage = "";
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			User user = securityProfile.getUser();
			String serverName = request.getServerName();
			// purging reqs one at a time, is too slow and is maxing out CPU
			// so, we will collect all reqs to be purged (that the user has
			// permissions to purge)
			// into one array list and then call, the purgeRequirements in Bulk
			// routine at the
			// end of the for loop.
			ArrayList requirementsToPurge = new ArrayList();

			// lets split the targetRequirements.
			String[] reqIds = targetRequirements.split(":##:");
			for (int i = 0; i < reqIds.length; i++) {
				int requirementId = Integer.parseInt(reqIds[i]);

				// first , lets create the requirement bean for the req id.
				Requirement requirement = new Requirement(requirementId, databaseType);

				//////////////////////////////////////// SECURITY//////////////////////////
				//
				// To ensure that a hacker did not inject req ids from a diff
				//////////////////////////////////////// project to this
				// bulk edit routine, we will double check that requirement's
				//////////////////////////////////////// Project Id is
				// the same project Id that the user has logged into. If not we
				//////////////////////////////////////// will skip the row.
				//
				//////////////////////////////////////// SECURITY//////////////////////////
				if (requirement.getProjectId() != projectId) {
					System.out.println(user.getEmailId() + "tried bulk editing requirement id "
							+ requirement.getRequirementId() + " while logged into project id " + projectId
							+ " A POTENTIAL SECURITY VIOLATION. Not processing this row ");
					continue;
				}

				// lets get the security privilges to work on this req.
				// note: we get the folderId from req. This ensures that even if
				// the user is digging into sub folders (by using the include
				// sub folders
				// check box, the security model is enforced.
				boolean updateDisabled = false;
				if (!(securityProfile.getPrivileges()
						.contains("updateRequirementsInFolder" + requirement.getFolderId()))) {
					updateDisabled = true;
				}

				boolean requirementLockedByAnotherUser = false;
				// if this requirement is locked and its locked by someone other
				// than this user, then all updates to this req are disabled.
				if ((!(requirement.getRequirementLockedBy().equals("")))
						&& (!(requirement.getRequirementLockedBy().equals(user.getEmailId())))) {
					updateDisabled = true;
					requirementLockedByAnotherUser = true;
				}

				boolean deleteDisabled = false;
				if (!(securityProfile.getPrivileges()
						.contains("deleteRequirementsInFolder" + requirement.getFolderId()))) {
					deleteDisabled = true;
				}

				boolean approveDisabled = false;
				if (!(securityProfile.getPrivileges()
						.contains("approveRequirementsInFolder" + requirement.getFolderId()))) {
					approveDisabled = true;
				}

				// NOTE : If I were smarter, I would have used a CASE / SWITCH
				// statement,
				// but a multiple IFs with Continue seem to be doing the same
				// thing, with
				// more readability.

				// Some work flow actions don't require the user to have update
				// prives.
				// so we will handle them before the update restriction kicks
				// in.
				// SubmitForApproval requires update privs.

				if (targetArtifact.equals("refreshApproverList")) {
					// lets call the refreshAproverList code.
					if (requirement.getApprovalStatus().equals("In Approval WorkFlow")) {
						RequirementUtil.refreshRequirementApprovalHistory(requirement.getRequirementId(), databaseType);
						RequirementUtil.setFinalApprovalStatus(requirement, databaseType);
						setBulkEditMessage += "<br>" + " This Requirement approver list has been updated - "
								+ requirement.getRequirementFullTag();

					} else {
						setBulkEditMessage += "<br>" + " This Requirement in not currently in Approval Work Flow - "
								+ requirement.getRequirementFullTag();

					}

					continue;
				}

				if (targetArtifact.equals("remindApprovers")) {
					// lets call the refreshAproverList code.
					if (requirement.getApprovalStatus().equals("In Approval WorkFlow")) {
						RequirementUtil.refreshRequirementApprovalHistory(requirement.getRequirementId(), databaseType);
						RequirementUtil.setFinalApprovalStatus(requirement, databaseType);
						RequirementUtil.remindPendingApprovers(requirement.getRequirementId(), databaseType,
								serverName);
						setBulkEditMessage += "<br>" + " This Requirement's pending approvers will be notified - "
								+ requirement.getRequirementFullTag();

					} else {
						setBulkEditMessage += "<br>" + " This Requirement in not currently in Approval Work Flow - "
								+ requirement.getRequirementFullTag();

					}

					continue;
				}

				if (targetArtifact.equals("approveRequirement")) {
					if (approveDisabled) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>" + "You do not have privileges to Approve this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}
					if ((requirement.getApprovalStatus().equals("In Approval WorkFlow"))
							|| (requirement.getApprovalStatus().equals("Rejected"))) {
						// the req status is either rejected or in workflow, so
						// can still be approved.
						RequirementUtil.approvalWorkFlowAction(requirementId, "approve", "", user, request,
								databaseType);
						setBulkEditMessage += "<br>" + " This Requirement is approved - "
								+ requirement.getRequirementFullTag();
					} else {
						setBulkEditMessage += "<br>"
								+ " This Requirement needs to be either in Rejected status or In WorkFlow status to be Approved - "
								+ requirement.getRequirementFullTag();
					}
					continue;
				}

				
				if (targetArtifact.equals("setDynamicApprover")) {
					if (updateDisabled) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>" + "You do not have privileges to add dynamic approvers to this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}
					
					System.out.println("srt targetValue is " + targetValue);
					String[] tV = targetValue.split(":#:");
					int roleId = Integer.parseInt(tV[0]);
					int approvalRank = Integer.parseInt(tV[1]);
					
					Role role = new Role(roleId);
					
					RequirementUtil.addDynamicApprovalRole(con, requirement, role , approvalRank);
					
					
					
					setBulkEditMessage += "<br> Role : " +  role.getRoleName() + " has been added as a Dynamic Approval Role to " + 
							requirement.getRequirementFullTag();
					
					continue;
				}
				
				
				
				if (targetArtifact.equals("removeDynamicApprover")) {
					if (updateDisabled) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>" + "You do not have privileges to add dynamic approvers to this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}
					
					System.out.println("srt targetValue is " + targetValue);
					int roleId = Integer.parseInt(targetValue);
					Role role = new Role(roleId);
					
					RequirementUtil.removeDynamicApprovalRole(con, requirement, role);
					
					
					
					setBulkEditMessage += "<br> Role : " +  role.getRoleName() + " has been removed as a Dynamic Approval Role to " + 
							requirement.getRequirementFullTag();
					
					continue;
				}
				// process reject Requirements.
				if (targetArtifact.equals("rejectRequirement")) {
					if (approveDisabled) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>" + "You do not have privileges to Reject this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}
					if (requirement.getApprovalStatus().equals("In Approval WorkFlow")) {
						String approvalNote = "Rejected : " + targetValue;
						RequirementUtil.approvalWorkFlowAction(requirementId, "reject", approvalNote, user, request,
								databaseType);
						setBulkEditMessage += "<br>" + " This Requirement is rejected - "
								+ requirement.getRequirementFullTag();
					} else {
						setBulkEditMessage += "<br>"
								+ " This Requirement needs to be In WorkFlow status to be Rejected - "
								+ requirement.getRequirementFullTag();
					}
					continue;
				}

				// process Submit for Approval.
				if ((targetArtifact.equals("submitRequirementForApproval"))) {
					// A user needs to have update requirements on a folder to
					// be
					// able to submit the eq for approval.
					// if no permissions, add to error message and skip.
					if (!RequirementUtil.approversForRequirementExist(requirementId)) {
						// means no approvers exist for this req type. Lets
						// skip.
						setBulkEditMessage += "<br>" + "No Approvers have been defined for this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}
					if (updateDisabled) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>"
								+ "You do not have privileges to Submit this Requirement for approval - "
								+ requirement.getRequirementFullTag();
						continue;
					} else {
						// means the user has permissions to submit this req for
						// approval.
						// and this req is in draft and can be submitted for
						// approval.

						RequirementUtil.submitRequirementForApproval(requirementId, user.getEmailId(), databaseType,
								serverName);
						setBulkEditMessage += "<br>" + " This Requirement is Pending approval - "
								+ requirement.getRequirementFullTag();
					}
					continue;
				}

				// lets see if this is a create Trace To action.
				if (targetArtifact.equals("traceTo")) {
					// lets see if the user has permissions to create Trace To
					// this req.
					if (!(securityProfile.getPrivileges()
							.contains("traceToRequirementsInFolder" + requirement.getFolderId()))) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>"
								+ "You do not have privileges to create traces to this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}

					// this is a create Trace To
					// Call RequirementUtil.createTraces
					// Get the error / status message

					String status = RequirementUtil.createTraces(project, requirementId, targetValue, "", projectId,
							securityProfile, databaseType);

					if ((status != null) && ((!status.equals("")))) {
						// since the error message comes when the entered trace
						// to objects are
						// invalid, we don't want to repeat the message for
						// every req object.
						// so we are not appending to set Bulk Edit Message.
						setBulkEditMessage += "<br>" + status;
					}
					continue;
				}

				// lets see if this is a tracefrom action.
				if (targetArtifact.equals("traceFrom")) {
					// lets see if the user has permissions to create Trace To
					// this req.
					if (!(securityProfile.getPrivileges()
							.contains("traceFromRequirementsInFolder" + requirement.getFolderId()))) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>"
								+ "You do not have privileges to create traces from this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}

					// this is a create Trace From request.
					// Call RequirementUtil.createTraces
					// Get the error / status message

					String status = RequirementUtil.createTraces(project, requirementId, "", targetValue, projectId,
							securityProfile, databaseType);

					if ((status != null) && ((!status.equals("")))) {
						// since the error message comes when the entered trace
						// to objects are
						// invalid, we don't want to repeat the message for
						// every req object.
						// so we are not appending to set Bulk Edit Message.
						setBulkEditMessage += "The following Requirement Ids are not valid --" + status;
					}
					continue;
				}

				// lets handle clear suspect Traces to.
				if ((targetArtifact.equals("clearSuspectTraceTo"))) {
					// lets see if the user has permissions to create Trace To
					// this req.
					if (!(securityProfile.getPrivileges()
							.contains("traceToRequirementsInFolder" + requirement.getFolderId()))) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>"
								+ "You do not have privileges to Create Traces To (or Clear Suspect Trace To) this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}

					// this is a clear Suspect Traces call.
					RequirementUtil.clearSuspectTraceTo(requirementId, user.getEmailId(), securityProfile,
							databaseType);
					continue;
				}

				// lets handle clearSuspectTracesFrom
				if ((targetArtifact.equals("clearSuspectTraceFrom"))) {
					// lets see if the user has permissions to create Trace To
					// this req.
					if (!(securityProfile.getPrivileges()
							.contains("traceFromRequirementsInFolder" + requirement.getFolderId()))) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>"
								+ "You do not have privileges to Create Traces From (or Clear Suspect Trace From) this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}

					// this is a clear Suspect Traces call.
					RequirementUtil.clearSuspectTraceFrom(requirementId, user.getEmailId(), securityProfile,
							databaseType);
					continue;
				}

				// lets handle deleteTraceTo.
				if ((targetArtifact.equals("deleteAllTraceTo"))) {
					// lets see if the user has permissions to create Trace To
					// this req.
					if (!(securityProfile.getPrivileges()
							.contains("traceToRequirementsInFolder" + requirement.getFolderId()))) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>"
								+ "You do not have privileges to Create Traces To (or Delete Trace To) this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}

					// this is a clear Suspect Traces call.
					RequirementUtil.deleteAllTraceTo(requirementId, user.getEmailId(), securityProfile, databaseType);
					continue;
				}

				if ((targetArtifact.equals("deleteAllTraceFrom"))) {
					// lets see if the user has permissions to create Trace To
					// this req.
					if (!(securityProfile.getPrivileges()
							.contains("traceFromRequirementsInFolder" + requirement.getFolderId()))) {
						// means no permissions to submit this req for approval.
						// Lets skip.
						setBulkEditMessage += "<br>"
								+ "You do not have privileges to Create Traces From (or Delete Trace From) this Requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					}
					// this is a clear Suspect Traces call.
					RequirementUtil.deleteAllTraceFrom(requirementId, user.getEmailId(), securityProfile, databaseType);
					continue;
				}

				if (!(securityProfile.getPrivileges()
						.contains("updateRequirementsInFolder" + requirement.getFolderId()))) {
					// lets see if this user is permitted to update this
					// requirement.
					// if not, we add it to the error message, and skip this
					// row.
					// this applies to requirement, attributes , traceability
					// and move.
					// User is does not have update privs on this row. So lets
					// skip this row.
					setBulkEditMessage += "<br>" + "You do not have privileges to update the requirement - "
							+ requirement.getRequirementFullTag();
					continue;
				}

				/////////////////////////////////////////////////////////////////////////////////////////////////////
				//
				// IF YOU GET BEYOND THIS LINE, THE ASSUMPTION IS YOU HAVE
				// UPDATE
				// PRIVILEGES
				/// ON
				/// THIS
				/// REQ
				//
				////////////////////////////////////////////////////////////////////////////////////////////////////

				if ((targetArtifact.equals("setFolder"))) {
					// this is a move Requirements to a new folder call
					if (requirementLockedByAnotherUser) {
						// means this req is locked by another user.
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is locked by " + requirement.getRequirementLockedBy();
						continue;
					}
					int moveTargetFolderId = Integer.parseInt(targetValue);
					// RequirementUtil.moveRequirementToAnotherFolder(requirementId,
					// moveTargetFolderId, user.getEmailId(), databaseType);

					// instead of move, lets use copy / purge
					int targetFolderId = moveTargetFolderId;
					Folder targetFolder = new Folder(targetFolderId);
					int targetProjectId = project.getProjectId();

					// if source requirementtype is same as target requirement
					// type, then use move. else use copy & delete.
					if (requirement.getRequirementTypeId() == targetFolder.getRequirementTypeId()) {
						// move within the same req type, so use Move
						RequirementUtil.moveRequirementToAnotherFolder(requirement, targetFolderId, user.getEmailId(),
								databaseType);

					} else {
						// move to a new req type. so use Copy to new / purge
						// old
						RequirementType targetRequirementType = new RequirementType(
								targetFolder.getRequirementTypeId());
						// TODO : when you clone requirements, copy the comments
						// and version and attachments
						Requirement targetRequirement = RequirementUtil.cloneRequirement(requirement,
								targetRequirementType, targetProjectId, targetFolderId, true, true, user,
								securityProfile, databaseType);

						// lets also add a log at the new requirement level that it was creaed by moving another requirement
						String log = "This requirement " + targetRequirement.getRequirementFullTag() + 
						" was created by moving " + requirement.getRequirementFullTag() + " From folder <b> " +
						requirement.getFolderPath()	+ "</b>";
									
						
						
						ProjectUtil.purgeRequirementExceptAttachments(requirement.getRequirementId(), databaseType);
						ProjectUtil.createProjectLog(requirement.getProjectId(), requirement.getRequirementFullTag(),
								" Move ",
								"Moving Requirement : " + requirement.getRequirementFullTag() + " : "
										+ requirement.getRequirementName() + " to  "
										+ targetRequirement.getRequirementFullTag(),
								user.getEmailId(), databaseType);
						
						RequirementUtil.createRequirementLog(targetRequirement.getRequirementId(), log,user.getEmailId(),  databaseType);

					}

				} else if ((targetArtifact.equals("setTestingStatus"))) {
					// this is a setTestingStatus of the Test Result object call
					String manualTestingStatus = targetValue;
					requirement.setTestingStatus(manualTestingStatus, user.getEmailId(), databaseType);
				} else if ((targetArtifact.equals("addComment"))) {
					// this is a setTestingStatus of the Test Result object call
					String commentNote = targetValue;
					
					RequirementUtil.createComment(requirementId,user,commentNote, request, databaseType);
				}else if ((targetArtifact.equals("addRequirementToBaseline"))) {
					int rTBaselineId = Integer.parseInt(targetValue);
					if (rTBaselineId > 0) {
						// lets see if this baseline is already linked to this
						// req.
						boolean exists = RequirementUtil.requirementBaselineAlreadyExists(requirementId, rTBaselineId);
						if (exists) {
							// means this req is already in this baseline.
							setBulkEditMessage += "<br>" + "This Requirement is already in this baseline - "
									+ requirement.getRequirementFullTag();
							continue;
						} else {
							// lets add this req to the baseline.
							RequirementUtil.addRequirementToBaseline(requirementId, rTBaselineId, user, request,
									databaseType);
							continue;
						}
					}
				} else if ((targetArtifact.equals("removeRequirementsFromBaseline"))) {
					int rTBaselineId = Integer.parseInt(targetValue);
					if (rTBaselineId > 0) {
						// lets see if this baseline is already linked to this
						// req.
						boolean exists = RequirementUtil.requirementBaselineAlreadyExists(requirementId, rTBaselineId);
						if (exists) {
							// lets remove this req from the baseline.
							int requirementBaselineId = RequirementUtil.getRequirementBaselineId(requirementId,
									rTBaselineId);
							RequirementUtil.removeRequirementFromBaseline(requirementId, requirementBaselineId, user,
									request, databaseType);
							continue;
						} else {
							// means this req is does not exist in this
							// baseline.
							setBulkEditMessage += "<br>" + "This Requirement doe not exist in this baseline - "
									+ requirement.getRequirementFullTag();
							continue;
						}
					}

				} else if ((targetArtifact.equals("deleteRequirements"))) {
					// this is a delete Requirement call. So we need to check
					// for permissions.
					// if no permissions, add to error message and skip.
					if (deleteDisabled) {
						// means no permissions to delete this req. Lets skip.
						setBulkEditMessage += "<br>" + "You do not have privileges to Delete this requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					} else if (RequirementUtil.getAllChildrenInFamilyRequirementIds(requirementId).size() > 0) {
						// means no permissions to delete this req. Lets skip.
						setBulkEditMessage += "<br>"
								+ "Please ensure that the requirement you are trying to delete does not have any child requirements. "
								+ " <br> If you have any previous deleted Child Requirements, please purge them first.  - "
								+ requirement.getRequirementFullTag();
						continue;
					} else if (RequirementUtil.requirementInLockedBaseline(requirementId)) {
						// means this req is in a baseline that has been locked
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is in a locked Baseline " + "and hence it can not be deleted.";
						continue;
					} else if (requirementLockedByAnotherUser) {
						// means this req is in a baseline that has been locked
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is locked by " + requirement.getRequirementLockedBy();
						continue;
					} else {
						// means the user has permissions to delete this req.
						ProjectUtil.deleteRequirement(requirementId, user.getEmailId(), databaseType);
					}
				} else if ((targetArtifact.equals("purgeRequirements"))) {
					// this is a purge Requirement call. So we need to check
					// for permissions.
					// if no permissions, add to error message and skip.
					if (deleteDisabled) {
						// means no permissions to delete this req. Lets skip.
						setBulkEditMessage += "<br>" + "You do not have privileges to Purge this requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					} else if (RequirementUtil.requirementInLockedBaseline(requirementId)) {
						// means this req is in a baseline that has been locked
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is in a locked Baseline " + "and hence it can not be purged.";
						continue;
					} else if (RequirementUtil.getAllChildrenInFamilyRequirementIds(requirementId).size() > 0) {
						// means no permissions to delete this req. Lets skip.
						setBulkEditMessage += "<br>"
								+ "Please ensure that the requirement you are trying to delete does not have any child requirements. "
								+ " <br> If you have any previous deleted Child Requirements, please purge them first.  - "
								+ requirement.getRequirementFullTag();
						continue;
					} else if (requirementLockedByAnotherUser) {
						// means this req is in a baseline that has been locked
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is locked by " + requirement.getRequirementLockedBy();
						continue;
					} else {
						// means the user has permissions to delete this req.
						// from a performance stand point, its more efficient
						// to collect all requirements to be purged into an
						// arraylist
						// and then call the purgeRequirementsInBulk routine.
						requirementsToPurge.add(new Integer(requirementId));
					}
				} else if ((targetArtifact.equals("copyRequirements"))) {

					// we will split targetValue in the action servlet to get
					// targetproject, copy folder and copy attribs and copy
					// traceability options.
					String[] options = targetValue.split("#");
					int targetProjectId = Integer.parseInt(options[0]);
					int targetFolderId = Integer.parseInt(options[1]);
					String createTraceToSource = options[2];
					String copyCommonAttributes = options[3];
					String copyTraceability = options[4];
					int numOfCopies =1 ; 
					try{
						System.out.println("SRT : Num of copies string is " + options[5]);
						numOfCopies = Integer.parseInt( options[5]);
					}
					catch (Exception e){
						e.printStackTrace();
					}

					/////////////////////////////// SECURITY CODE
					/////////////////////////////// ////////////////////////////
					// if the user does not have Read Permissions on this
					/////////////////////////////// Requirement reject him.
					if (!(securityProfile.getPrivileges()
							.contains("readRequirementsInFolder" + requirement.getFolderId()))) {
						setBulkEditMessage += "<br>"
								+ "<span class='normalText'><font color='red'> You do not have Read permissions on this Requirement"
								+ " </font></span> - " + requirement.getRequirementFullTag();
						continue;

					}

					// if the user does not have create permissions on the
					// Target Folder reject him.
					if (!(securityProfile.getPrivileges().contains("createRequirementsInFolder" + targetFolderId))) {
						setBulkEditMessage += "<br>"
								+ "<span class='normalText'><font color='red'> You do not have permissions to create Requriements in the Target Folder "
								+ " </font></span> - " + requirement.getRequirementFullTag();
						continue;
					}
					/////////////////////////////// SECURITY CODE
					/////////////////////////////// ////////////////////////////

					Folder targetFolder = new Folder(targetFolderId);
					RequirementType targetRequirementType = new RequirementType(targetFolder.getRequirementTypeId());
					boolean cloneAttributes = false;
					if (copyCommonAttributes.equals("yes")) {
						cloneAttributes = true;
					}
					boolean cloneTraceability = false;
					if (copyTraceability.equals("yes")) {
						cloneTraceability = true;
					}
					for (int j = 0 ; j<numOfCopies; j++){
						Requirement targetRequirement = RequirementUtil.cloneRequirement(requirement, targetRequirementType,
								targetProjectId, targetFolderId, cloneAttributes, cloneTraceability, user, securityProfile,
								databaseType);

						String status = "";
						if ((createTraceToSource != null) && (createTraceToSource.equals("yes"))) {
							status = RequirementUtil.createTraces(project, requirement.getRequirementId(), "",
									targetRequirement.getRequirementFullTag(), project.getProjectId(), securityProfile,
									databaseType);
							status = "A trace was created from " + targetRequirement.getRequirementFullTag() + " to "
									+ requirement.getRequirementFullTag();
						}

						if (targetProjectId == project.getProjectId()) {
							setBulkEditMessage += "<br>" + "<span class='normalText'> A new Requirement "
									+ targetRequirement.getRequirementFullTag() + " has been created in the Folder "
									+ targetFolder.getFolderPath() + " <br> " + status + " </span>  ";
						} else {
							setBulkEditMessage += "<br>" + "<span class='normalText'> A new Requirement "
									+ targetRequirement.getRequirementFullTag() + " has been created in the Folder "
									+ targetFolder.getFolderPath() + " in project "
									+ targetRequirement.getProjectShortName() + " </span> <br> " + status + "<br>";
						}	
					}
					
				}

				else if ((targetArtifact.equals("lockRequirements"))) {
					// this is a lock Requirement call. So we need to check
					// for permissions.
					// if no permissions, add to error message and skip.
					if ((!(requirement.getRequirementLockedBy().equals("")))
							&& (!(requirement.getRequirementLockedBy().equals(user.getEmailId())))) {

						// this requirement is locked by someone else already.
						// so can not be locked.
						setBulkEditMessage += "<br>" + " This requirement is locked by  "
								+ requirement.getRequirementLockedBy()
								+ ". Please work with your project administrator to unlock it. "
								+ requirement.getRequirementFullTag();

					} else if (updateDisabled) {
						// means no permissions to delete this req. Lets skip.
						setBulkEditMessage += "<br>" + "You do not have privileges to Lock this requirement - "
								+ requirement.getRequirementFullTag();
						continue;
					} else {
						// means the user has permissions to lock this req.
						requirement.setLockedBy(user.getEmailId(), databaseType);
					}
				}

				else if ((targetArtifact.equals("unlockRequirements"))) {
					// this is a unlock Requirement call. So we need to check
					// for permissions.
					// if no permissions, add to error message and skip.

					// unlocking can only be done by an admin or by the person
					// who locked the requirement.
					if ((securityProfile.getRoles().contains("AdministratorInProject" + project.getProjectId()))
							|| (requirement.getRequirementLockedBy().equals(user.getEmailId()))) {
						// an admin can unlock the requirement
						// or the user who locked it can unlock it.
						requirement.setUnlockedBy(user.getEmailId(), databaseType);
					} else {
						setBulkEditMessage += "<br>" + " This requirement is locked by  "
								+ requirement.getRequirementLockedBy()
								+ ". Please work with your project administrator to unlock it. "
								+ requirement.getRequirementFullTag();
						continue;
					}
				} else if ((targetArtifact.equals("reGlossarize"))) {
					// this is a unlock Requirement call. So we need to check
					// for permissions.
					// if no permissions, add to error message and skip.

					requirement.refreshRequirementGlossary();
				}
				// lets see if this is a custom attribute and handle it
				// differently
				else if (targetArtifact.contains("customAttribute")) {
					if (requirementLockedByAnotherUser) {
						// means this req is locked by another user.
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is locked by " + requirement.getRequirementLockedBy();
						continue;
					}

					// since this a custom attribute we don't need to updated
					// the
					// gr_requirements table directly. The RTAttributes bean may
					// do so indirectly.
					String[] custom = targetArtifact.split(":##:");
					int attributeId = Integer.parseInt(custom[1]);

					// lets get the attributeValueId for this reqId and
					// attributeId.

					String sql = "select id from gr_r_attribute_values "
							+ " where requirement_id = ? and attribute_id = ? ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, requirementId);
					prepStmt.setInt(2, attributeId);
					rs = prepStmt.executeQuery();
					int attributeValueId = 0;
					while (rs.next()) {
						attributeValueId = rs.getInt("id");
					}

					RTAttribute rTAttribute = new RTAttribute(attributeId);

					// lets make sure that this user is permitted to update this
					// attribute in this requirement.
					// lets determine which all attributes this user has
					// permission to update , based on his roles
					String updateAttributes = securityProfile.getUpdateAttributesForFolder(requirement.getFolderId());
					// If the user does not have updateAttributes permission on
					// this attribute in this folder, then we need
					// add an error message and skip updating this attribute.
					if (!(updateAttributes.contains(":#:" + rTAttribute.getAttributeName() + ":#:"))) {
						// SINCE THE USER DOES NOT HAVE UPDATE PERMISSIONS ON
						// THIS ATTRIBUTE ON THIS REQ type in this folder,
						// lets skip this attribute for updating.

						setBulkEditMessage += "<br>" + " You do not have permissions to update attribute  "
								+ rTAttribute.getAttributeName() + " for requirement "
								+ requirement.getRequirementFullTag();

						continue;
					}

					// if this is a child attribute with a parent, lets make
					// sure that this value is one
					// of the permitted ones based on the parent value. Eg : if
					// Parent is Porche, the Child has to one of
					// Porsche:911,Porsche:Panamera.

					int parentAttributeId = rTAttribute.getParentAttributeId();
					if (parentAttributeId > 0) {
						// first we look at all the drop down options like
						// Porsche:911,Porsche:Panamera
						// and skip the ones that don't have the Parent value
						// 'Porsche'. So drop the ones like
						// Jaguar:XJ etc..
						String parentAttributeValue = requirement.getAttributeValue(parentAttributeId);
						boolean isValidValue = false;
						String childAttributeOptions = rTAttribute.getAttributeDropDownOptions();
						String[] optionsArray = null;
						if (childAttributeOptions.contains(",")) {
							optionsArray = childAttributeOptions.split(",");
						} else {
							optionsArray = new String[1];
							optionsArray[0] = childAttributeOptions;
						}
						for (int j = 0; j < optionsArray.length; j++) {
							String optionName = optionsArray[j].trim();
							if (optionName.contains(parentAttributeValue)) {
								// this means that the optionName
								// 'Porsche:911,Porsche:Panamera' matched the
								// parent value 'Porsche'
								// now we need to strip out 'Porsche' from the
								// 'Porsche:911,Porsche:Panamera' and see if the
								// input value is in either 911, or Panamera.
								if ((optionName != null) && (optionName.contains(":"))) {
									String[] oN = optionName.split(":");
									optionName = oN[1];
								}
								if (optionName.equals(targetValue.trim())) {
									isValidValue = true;
								}
							}
						}
						if (!isValidValue) {
							setBulkEditMessage += "<br>" + "For the Requirement " + requirement.getRequirementFullTag()
									+ "' you are trying to set " + rTAttribute.getAttributeName() + " to " + targetValue
									+ ". However since its parent value is " + parentAttributeValue
									+ " this value is not permitted.";

							continue;
						}
					}

					// lets get the old Attribute Value object, and compare its
					// value with the new value.
					// only if the value has changed, do we need to update the
					// system and trigger
					// version, traceability and workflow.
					
					RAttributeValue oldAttributeValue = new RAttributeValue(attributeValueId, databaseType);
					
					System.out.println("srt about to setCustomText : targetArtifact is " + targetArtifact + " target value is "+ targetValue);
					String newValue = targetValue;
					if(targetArtifact.contains("customAttributeAppendText")){
						newValue = oldAttributeValue.getAttributeEnteredValue().trim() + " " + targetValue;
					}
					if(targetArtifact.contains("customAttributeReplaceText")){
						try {
							String[] replaceArray = targetValue.split(":##:");
							String replace = replaceArray[0];
							String replaceWith = replaceArray[1];
							newValue = oldAttributeValue.getAttributeEnteredValue().trim().replace(replace, replaceWith);
						}
						catch (Exception cARTException){
							newValue = oldAttributeValue.getAttributeEnteredValue().trim();
						}
					}
					System.out.println("srt about to update   " + oldAttributeValue.getAttributeEnteredValue() + " to new value "+ targetValue);
					
					if (!(oldAttributeValue.getAttributeEnteredValue().trim().equals(newValue.trim()))) {

						// NOTE : do not delete rAV . when you create this
						// object,
						// an entry is made in the db for this
						// attribute value. we may need a different way to
						// implement
						// this.
						RAttributeValue rAV = new RAttributeValue(attributeValueId, newValue, databaseType, user.getEmailId());

						String log = "Updated attribute " + rAV.getAttributeName() + " to " + newValue;
						RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);

						// if this attribute has been flagged as impacts
						// versioning , then
						// lets update the Req Version.
						if (rAV.getAttributeImpactsVersion() == 1) {
							RequirementUtil.updateVersion(requirementId, request, databaseType);
							// at this point, lets create an entry in the
							// gr_requirement_version table
							RequirementUtil.createRequirementVersion(requirementId);

						}

						// if the attributes has been flagged as impacts
						// traceability , then
						// lets update the Req Traceability.
						if (rAV.getAttributeImpactsTraceability() == 1) {
							String traceDefinition = rAV.getAttributeName() + ":" + rAV.getAttributeEnteredValue()
									+ "  ";
							RequirementUtil.updateTraceability(traceDefinition, requirementId, request,
									user.getEmailId(), databaseType);
						}

						// if the attributes has been flagged as impacts
						// traceability , then
						// lets update the Req ApprovalWorkFlow .
						if (rAV.getAttributeImpactsApprovalWorkflow() == 1) {
							RequirementUtil.updateApprovalWorkflow(requirementId, request);
						}
					}

					// NOTE : this is critical. After attributes are created /
					// modified, call the setter
					// requirementUtil.setUserDefinedAttributes method.
					RequirementUtil.setUserDefinedAttributes(requirementId, user.getEmailId(), databaseType);

				}

				else if (targetArtifact.contains("addValuetoCustomDDM")) {
					if (requirementLockedByAnotherUser) {
						// means this req is locked by another user.
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is locked by " + requirement.getRequirementLockedBy();
						continue;
					}

					// since this a custom attribute we don't need to updated
					// the
					// gr_requirements table directly. The RTAttributes bean may
					// do so indirectly.
					String[] custom = targetArtifact.split(":##:");
					int attributeId = Integer.parseInt(custom[1]);

					// lets get the attributeValueId for this reqId and
					// attributeId.

					String sql = "select id from gr_r_attribute_values "
							+ " where requirement_id = ? and attribute_id = ? ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, requirementId);
					prepStmt.setInt(2, attributeId);
					rs = prepStmt.executeQuery();
					int attributeValueId = 0;
					while (rs.next()) {
						attributeValueId = rs.getInt("id");
					}

					RTAttribute rTAttribute = new RTAttribute(attributeId);

					// lets make sure that this user is permitted to update this
					// attribute in this requirement.
					// lets determine which all attributes this user has
					// permission to update , based on his roles
					String updateAttributes = securityProfile.getUpdateAttributesForFolder(requirement.getFolderId());
					// If the user does not have updateAttributes permission on
					// this attribute in this folder, then we need
					// add an error message and skip updating this attribute.
					if (!(updateAttributes.contains(":#:" + rTAttribute.getAttributeName() + ":#:"))) {
						// SINCE THE USER DOES NOT HAVE UPDATE PERMISSIONS ON
						// THIS ATTRIBUTE ON THIS REQ type in this folder,
						// lets skip this attribute for updating.

						setBulkEditMessage += "<br>" + " You do not have permissions to update attribute  "
								+ rTAttribute.getAttributeName() + " for requirement "
								+ requirement.getRequirementFullTag();

						continue;
					}

					// lets get the old Attribute Value object,
					RAttributeValue oldAttributeValue = new RAttributeValue(attributeValueId, databaseType);

					// if oldAttributeValue contains, this new value, then we
					// need to do nothing.
					// else we need to update it.
					if (oldAttributeValue.getAttributeEnteredValue().contains(targetValue.trim())) {
						// do nothing.
					} else {
						// lets update the entered value.
						String olAttributeValueString = oldAttributeValue.getAttributeEnteredValue().trim();

						String newAttributeValue = "";
						if ((olAttributeValueString == null) || (olAttributeValueString.length() == 0)) {
							newAttributeValue = targetValue;
						} else {
							newAttributeValue = olAttributeValueString + "," + targetValue;
						}

						// before we update, lets clean up by replacing the ,,
						// with ,
						if (newAttributeValue.contains(",,")) {
							newAttributeValue = newAttributeValue.replace(",,", ",");
						}
						if (newAttributeValue.endsWith(",")) {
							newAttributeValue = (String) newAttributeValue.subSequence(0,
									newAttributeValue.lastIndexOf(","));
						}

						// NOTE : do not delete rAV . when you create this
						// object,
						// an entry is made in the db for this
						// attribute value. we may need a different way to
						// implement
						// this.
						RAttributeValue rAV = new RAttributeValue(attributeValueId, newAttributeValue, databaseType, user.getEmailId());

						String log = "Updated attribute " + rAV.getAttributeName() + " to " + newAttributeValue;
						RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);

						// if this attribute has been flagged as impacts
						// versioning , then
						// lets update the Req Version.
						if (rAV.getAttributeImpactsVersion() == 1) {
							RequirementUtil.updateVersion(requirementId, request, databaseType);
							// at this point, lets create an entry in the
							// gr_requirement_version table
							RequirementUtil.createRequirementVersion(requirementId);

						}

						// if the attributes has been flagged as impacts
						// traceability , then
						// lets update the Req Traceability.
						if (rAV.getAttributeImpactsTraceability() == 1) {
							String traceDefinition = rAV.getAttributeName() + ":" + rAV.getAttributeEnteredValue()
									+ "  ";
							RequirementUtil.updateTraceability(traceDefinition, requirementId, request,
									user.getEmailId(), databaseType);
						}

						// if the attributes has been flagged as impacts
						// traceability , then
						// lets update the Req ApprovalWorkFlow .
						if (rAV.getAttributeImpactsApprovalWorkflow() == 1) {
							RequirementUtil.updateApprovalWorkflow(requirementId, request);
						}

						// NOTE : this is critical. After attributes are created
						// /
						// modified, call the setter
						// requirementUtil.setUserDefinedAttributes method.
						RequirementUtil.setUserDefinedAttributes(requirementId, user.getEmailId(), databaseType);

					}
				} else if (targetArtifact.contains("removeValueFromCustomDDM")) {
					if (requirementLockedByAnotherUser) {
						// means this req is locked by another user.
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is locked by " + requirement.getRequirementLockedBy();
						continue;
					}

					// since this a custom attribute we don't need to updated
					// the
					// gr_requirements table directly. The RTAttributes bean may
					// do so indirectly.
					String[] custom = targetArtifact.split(":##:");
					int attributeId = Integer.parseInt(custom[1]);

					// lets get the attributeValueId for this reqId and
					// attributeId.

					String sql = "select id from gr_r_attribute_values "
							+ " where requirement_id = ? and attribute_id = ? ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, requirementId);
					prepStmt.setInt(2, attributeId);
					rs = prepStmt.executeQuery();
					int attributeValueId = 0;
					while (rs.next()) {
						attributeValueId = rs.getInt("id");
					}

					RTAttribute rTAttribute = new RTAttribute(attributeId);

					// lets make sure that this user is permitted to update this
					// attribute in this requirement.
					// lets determine which all attributes this user has
					// permission to update , based on his roles
					String updateAttributes = securityProfile.getUpdateAttributesForFolder(requirement.getFolderId());
					// If the user does not have updateAttributes permission on
					// this attribute in this folder, then we need
					// add an error message and skip updating this attribute.
					if (!(updateAttributes.contains(":#:" + rTAttribute.getAttributeName() + ":#:"))) {
						// SINCE THE USER DOES NOT HAVE UPDATE PERMISSIONS ON
						// THIS ATTRIBUTE ON THIS REQ type in this folder,
						// lets skip this attribute for updating.

						setBulkEditMessage += "<br>" + " You do not have permissions to update attribute  "
								+ rTAttribute.getAttributeName() + " for requirement "
								+ requirement.getRequirementFullTag();

						continue;
					}

					// lets get the old Attribute Value object,
					RAttributeValue oldAttributeValue = new RAttributeValue(attributeValueId, databaseType);

					// if oldAttributeValue contains, this new value, then we
					// need to do nothing.
					// else we need to update it.
					if (!(oldAttributeValue.getAttributeEnteredValue().contains(targetValue.trim()))) {
						// do nothing.
					} else {
						// lets update the entered value.
						String oldAttributeValueString = oldAttributeValue.getAttributeEnteredValue().trim();
						// lets remove the dangling , from the targetValue
						targetValue = targetValue.trim();
						if (targetValue.endsWith(",")) {
							targetValue = (String) targetValue.subSequence(0, targetValue.lastIndexOf(","));
						}

						// if the oldAttributeValue contains targetvalue, lets
						// drop it.
						String newAttributeValue = oldAttributeValueString;
						// let remove targetValue with comma and then the
						// targetValue without the comma.
						if (newAttributeValue.contains(targetValue + ",")) {
							newAttributeValue = newAttributeValue.replace(targetValue + ",", "");
						}
						if (newAttributeValue.contains(targetValue)) {
							newAttributeValue = newAttributeValue.replace(targetValue, "");
						}

						// before we update, lets clean up by dropping trailing
						// ,s and ,, s
						if (newAttributeValue.contains(",,")) {
							newAttributeValue = newAttributeValue.replace(",,", ",");
						}
						if (newAttributeValue.endsWith(",")) {
							newAttributeValue = (String) newAttributeValue.subSequence(0,
									newAttributeValue.lastIndexOf(","));
						}

						// NOTE : do not delete rAV . when you create this
						// object,
						// an entry is made in the db for this
						// attribute value. we may need a different way to
						// implement
						// this.
						RAttributeValue rAV = new RAttributeValue(attributeValueId, newAttributeValue, databaseType, user.getEmailId());

						String log = "Updated attribute " + rAV.getAttributeName() + " to " + newAttributeValue;
						RequirementUtil.createRequirementLog(requirementId, log, user.getEmailId(), databaseType);

						// if this attribute has been flagged as impacts
						// versioning , then
						// lets update the Req Version.
						if (rAV.getAttributeImpactsVersion() == 1) {
							RequirementUtil.updateVersion(requirementId, request, databaseType);
							// at this point, lets create an entry in the
							// gr_requirement_version table
							RequirementUtil.createRequirementVersion(requirementId);

						}

						// if the attributes has been flagged as impacts
						// traceability , then
						// lets update the Req Traceability.
						if (rAV.getAttributeImpactsTraceability() == 1) {
							String traceDefinition = rAV.getAttributeName() + ":" + rAV.getAttributeEnteredValue()
									+ "  ";
							RequirementUtil.updateTraceability(traceDefinition, requirementId, request,
									user.getEmailId(), databaseType);
						}

						// if the attributes has been flagged as impacts
						// traceability , then
						// lets update the Req ApprovalWorkFlow .
						if (rAV.getAttributeImpactsApprovalWorkflow() == 1) {
							RequirementUtil.updateApprovalWorkflow(requirementId, request);
						}

						// NOTE : this is critical. After attributes are created
						// /
						// modified, call the setter
						// requirementUtil.setUserDefinedAttributes method.
						RequirementUtil.setUserDefinedAttributes(requirementId, user.getEmailId(), databaseType);

					}
				}

				else {
					if (requirementLockedByAnotherUser) {
						// means this req is locked by another user.
						setBulkEditMessage += "<br>" + "The Requirement " + requirement.getRequirementFullTag()
								+ " is locked by " + requirement.getRequirementLockedBy();
						continue;
					}
					// since this is a standard attribute stored in the
					// gr_requirements
					// table, we use the Requirement Bean directly.
					if (targetArtifact.equals("owner")) {
						String oldOwnerEmailId = requirement.getRequirementOwner();
						// lets notify the old owner.
						RequirementUtil.notifyOldOwner(user, request, requirement.getRequirementId() , oldOwnerEmailId, targetValue,
								requirement.getProjectShortName(),  requirement.getRequirementFullTag() , requirement.getRequirementName(), 
								mailHost, transportProtocol, smtpAuth, smtpPort, smtpSocketFactoryPort, emailUserId, emailPassword );
						
						String newRequirementName = requirement.getRequirementName();
						String newRequirementDescription = requirement.getRequirementDescription();
						String newRequirementPriority = requirement.getRequirementPriority();
						String newRequirementOwner = targetValue;
						int newRequirementPctComplete = requirement.getRequirementPctComplete();
						String newRequirementExternalURL = requirement.getRequirementExternalUrl();
						// Note , to update the requirement, we just need to re
						// create the
						// requirement bean, , but this time giving the
						// requirement id , along
						// with old values for unchanged stuff, and the new
						// value for the stuff
						// you want to change.

						// if new requirement owner is not the same as the old
						// guys
						// lets collect all the requirement's changing.
						String url = ProjectUtil.getURL(request, requirement.getRequirementId(), "requirement");

						setBulkEditMessage += " <tr><td>Object </td><td> <a href='" + url + "'>"
								+ requirement.getProjectShortName() + ":" + requirement.getRequirementFullTag()
								+ "</a></td></tr>" + " <tr><td>Name  </td><td> " + requirement.getRequirementName()
								+ "</td></tr><tr><td colspan=2>&nbsp;</td></tr> ";
						requirement = new Requirement(requirementId, newRequirementName, newRequirementDescription,
								newRequirementPriority, newRequirementOwner, newRequirementPctComplete,
								newRequirementExternalURL, user.getEmailId(), request, databaseType);

					}

					if (targetArtifact.equals("status")) {
						// we probably aren't using this condition ever.
						// we may be using the approve, reject , submit for
						// approval options.
						String newRequirementName = requirement.getRequirementName();
						String newRequirementDescription = requirement.getRequirementDescription();
						String newRequirementPriority = requirement.getRequirementPriority();
						String newRequirementOwner = requirement.getRequirementOwner();
						int newRequirementPctComplete = requirement.getRequirementPctComplete();
						String newRequirementExternalURL = requirement.getRequirementExternalUrl();

						// Note , to update the requirement, we just need to re
						// create the
						// requirement bean, , but this time giving the
						// requirement id , along
						// with old values for unchanged stuff, and the new
						// value for the stuff
						// you want to change.
						requirement = new Requirement(requirementId, newRequirementName, newRequirementDescription,
								newRequirementPriority, newRequirementOwner, newRequirementPctComplete,
								newRequirementExternalURL, user.getEmailId(), request, databaseType);

					}

					if (targetArtifact.equals("priority")) {
						String newRequirementName = requirement.getRequirementName();
						String newRequirementDescription = requirement.getRequirementDescription();
						String newRequirementPriority = targetValue;
						String newRequirementOwner = requirement.getRequirementOwner();
						int newRequirementPctComplete = requirement.getRequirementPctComplete();
						String newRequirementExternalURL = requirement.getRequirementExternalUrl();

						// Note , to update the requirement, we just need to re
						// create the
						// requirement bean, , but this time giving the
						// requirement id , along
						// with old values for unchanged stuff, and the new
						// value for the stuff
						// you want to change.
						requirement = new Requirement(requirementId, newRequirementName, newRequirementDescription,
								newRequirementPriority, newRequirementOwner, newRequirementPctComplete,
								newRequirementExternalURL, user.getEmailId(), request, databaseType);
					}

					if (targetArtifact.equals("externalURL")) {
						String newRequirementName = requirement.getRequirementName();
						String newRequirementDescription = requirement.getRequirementDescription();
						String newRequirementPriority = requirement.getRequirementPriority();
						String newRequirementOwner = requirement.getRequirementOwner();
						int newRequirementPctComplete = requirement.getRequirementPctComplete();
						String newRequirementExternalURL = targetValue;

						// Note , to update the requirement, we just need to re
						// create the
						// requirement bean, , but this time giving the
						// requirement id , along
						// with old values for unchanged stuff, and the new
						// value for the stuff
						// you want to change.
						requirement = new Requirement(requirementId, newRequirementName, newRequirementDescription,
								newRequirementPriority, newRequirementOwner, newRequirementPctComplete,
								newRequirementExternalURL, user.getEmailId(), request, databaseType);

					}

					if (targetArtifact.equals("pctComplete")) {

						String newRequirementName = requirement.getRequirementName();
						String newRequirementDescription = requirement.getRequirementDescription();
						String newRequirementPriority = requirement.getRequirementPriority();
						String newRequirementOwner = requirement.getRequirementOwner();
						int newRequirementPctComplete = Integer.parseInt(targetValue);
						String newRequirementExternalURL = requirement.getRequirementExternalUrl();

						// Note , to update the requirement, we just need to re
						// create the
						// requirement bean, , but this time giving the
						// requirement id , along
						// with old values for unchanged stuff, and the new
						// value for the stuff
						// you want to change.
						requirement = new Requirement(requirementId, newRequirementName, newRequirementDescription,
								newRequirementPriority, newRequirementOwner, newRequirementPctComplete,
								newRequirementExternalURL, user.getEmailId(), request, databaseType);

					}

					if (targetArtifact.equals("replaceText")) {

						try {
							// targetValue is in this followingFormat.
							// searchString:##:replaceString:##:case:##:replaceIn
							String[] inputParams = targetValue.split(":##:");
							String searchString = inputParams[0];
							String replaceString = inputParams[1];
							String caseString = inputParams[2];
							String replaceIn = inputParams[3];

							String newRequirementName = requirement.getRequirementName();
							String newRequirementDescription = requirement.getRequirementDescription();
							String newRequirementPriority = requirement.getRequirementPriority();
							String newRequirementOwner = requirement.getRequirementOwner();
							int newRequirementPctComplete = requirement.getRequirementPctComplete();
							String newRequirementExternalURL = requirement.getRequirementExternalUrl();

							if (replaceIn.equals("name")) {
								if (caseString.equals("exactText")) {
									newRequirementName = newRequirementName.replace(searchString, replaceString);
								} else {
									newRequirementName = newRequirementName.replaceAll("(?i)" + searchString,
											replaceString);
								}
							} else if (replaceIn.equals("description")) {
								if (caseString.equals("exactText")) {
									newRequirementDescription = newRequirementDescription.replace(searchString,
											replaceString);
								} else {
									newRequirementDescription = newRequirementDescription
											.replaceAll("(?i)" + searchString, replaceString);
								}
							} else {
								// this is for both Name and Description
								if (caseString.equals("exactText")) {
									newRequirementName = newRequirementName.replace(searchString, replaceString);
									newRequirementDescription = newRequirementDescription.replace(searchString,
											replaceString);
								} else {
									newRequirementName = newRequirementName.replaceAll("(?i)" + searchString,
											replaceString);
									newRequirementDescription = newRequirementDescription
											.replaceAll("(?i)" + searchString, replaceString);
								}

							}

							// Note , to update the requirement, we just need to
							// re
							// create the
							// requirement bean, , but this time giving the
							// requirement id , along
							// with old values for unchanged stuff, and the new
							// value for the stuff
							// you want to change.
							requirement = new Requirement(requirementId, newRequirementName, newRequirementDescription,
									newRequirementPriority, newRequirementOwner, newRequirementPctComplete,
									newRequirementExternalURL, user.getEmailId(), request, databaseType);

						} catch (Exception e) {
							// ignore.
						}
					}

				}

			}
			// lets purge the requirements in bulk. we are doing this outside
			// the for loop
			// because its more efficient to purge them in bulk
			//
			//
			// DON'T FREAK OUT
			//
			//
			// The following command has no effect if 'requrimentToPurge' list
			// is empty. It is generally empty , unless
			// there was a action to puge some requirements.
			ProjectUtil.purgeRequirementsInBulk(requirementsToPurge);

			if ((setBulkEditMessage == null ) || (setBulkEditMessage.equals(""))){
				setBulkEditMessage += "<br>" + "Your changes has been appled.  " ;
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}
		return (setBulkEditMessage);
	}

	public static void saveReport(int projectId, int folderId, String reportVisibility, String reportName,
			String reportDescription, String reportType, int traceTreeDepth, String reportDefinition,
			String actorEmailId, String databaseType) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "";
			if (databaseType.equals("mySQL")) {
				sql = "insert into gr_reports (project_id, folder_id, name, description,"
						+ "report_type, visibility, trace_tree_depth, report_sql , report_definition," + " created_by, "
						+ " created_dt, last_modified_by, "
						+ "last_modified_dt) values (?,?,?,?,?,?,?,?,?,?,now(), ?, now())";
			} else {
				sql = "insert into gr_reports (project_id, folder_id, name, description,"
						+ "report_type, visibility, trace_tree_depth, report_sql , report_definition," + " created_by, "
						+ " created_dt, last_modified_by, "
						+ "last_modified_dt) values (?,?,?,?,?,?,?,?,?,?, sysdate, ?, sysdate)";
			}
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, projectId);
			prepStmt.setInt(2, folderId);
			prepStmt.setString(3, reportName);
			prepStmt.setString(4, reportDescription);
			prepStmt.setString(5, reportType);
			prepStmt.setString(6, reportVisibility);
			prepStmt.setInt(7, traceTreeDepth);
			prepStmt.setString(8, " ");
			prepStmt.setString(9, reportDefinition);
			prepStmt.setString(10, actorEmailId);
			prepStmt.setString(11, actorEmailId);

			prepStmt.execute();
			prepStmt.close();
			// lets close the database connection
			con.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}
	}
	
	
	public static void updateReport(int reportId,  String reportDefinition) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql  = "update gr_reports set report_definition = ? where id = ? ";
			
			prepStmt = con.prepareStatement(sql);
			
			prepStmt.setString(1, reportDefinition);
			prepStmt.setInt(2, reportId); 
			

			prepStmt.execute();
			prepStmt.close();
			// lets close the database connection
			con.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}
	}

	public static void deleteReport(int reportId) {
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			String sql = "delete from gr_reports where id = ? ";
			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, reportId);

			prepStmt.execute();
			prepStmt.close();

			// lets close the datbase conneciton
			con.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}
	}

	// takes a search string and does either a context search or a search on
	// reqid.
	public static ArrayList getProjectSearchReport(SecurityProfile securityProfile, int projectId, String searchString,
			String searchType, String databaseType) {

		ArrayList projectSearchReport = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the report_sql from the db for this reportId.
			String sql = "";
			if (searchType.equals("reqId")) {
				// in this scenario, we have comma separated list of full tags.
				String reqIdSearchString = "";

				String[] reqIds = searchString.split(",");
				for (int i = 0; i < reqIds.length; i++) {
					if ((reqIds[i] != null) && !(reqIds[i].equals(""))) {
						// lets ignore the cases where we had BR-1 BR-2
						// that got converted to BR-1,,,,BR-2
						reqIdSearchString += "'" + reqIds[i] + "'" + ",";
					}

				}
				// drop the last ,
				if (reqIdSearchString.contains(",")) {
					reqIdSearchString = (String) reqIdSearchString.subSequence(0, reqIdSearchString.lastIndexOf(","));
				}

				if (databaseType.equals("mySQL")) {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag,"
							+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\" "
							+ " from  gr_requirements r, gr_requirement_types rt, gr_folders f "
							+ " where r.project_id = ? " + " and r.requirement_type_id = rt.id "
							+ " and  r.full_tag in (" + reqIdSearchString + ") " + " and r.folder_id = f.id "
							+ " order by f.folder_path, r.tag " + " limit 0,100";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);
				} else {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag,"
							+ " r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\" "
							+ " from  gr_requirements r, gr_requirement_types rt, gr_folders f "
							+ " where r.project_id = ? " + " and r.requirement_type_id = rt.id "
							+ " and  upper(r.full_tag) in (" + reqIdSearchString.toUpperCase() + ") "
							+ " and r.folder_id = f.id " + " and rownum < 101  " + " order by f.folder_path, r.tag ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);
				}

			} else {
				// in this scenario we have search against a generic search
				// string.
				if (databaseType.equals("mySQL")) {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag, "
							+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\" , "
							+ " match (s.object_text) against ('" + searchString + "')" + " as score "
							+ " from gr_search s, gr_requirements r, gr_requirement_types rt , gr_folders f "
							+ " where match (s.object_text) against ('" + searchString + "')" + " and s.project_id = ? "
							+ " and s.object_id = r.id " + " and r.folder_id = f.id "
							+ " and r.requirement_type_id = rt.id " + " order by score desc " + " LIMIT  0, 100";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);

				} else {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag, "
							+ " r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\"  "
							+ " from gr_search s, gr_requirements r, gr_requirement_types rt , gr_folders f "
							+ " where CONTAINS(object_text, ? , 1) > 0 " + " and s.project_id = ? "
							+ " and s.object_id = r.id " + " and r.folder_id = f.id "
							+ " and r.requirement_type_id = rt.id " + " and rownum < 101 " + " order by score(1) desc ";

					String accumSearchString = "";
					if ((searchString != null) && (!searchString.equals(""))) {
						if (searchString.contains(" ")) {
							String[] tokens = searchString.split(" ");
							for (int i = 0; i < tokens.length; i++) {
								String token = tokens[i];
								if ((token != null) && (!token.equals(""))) {
									accumSearchString += token + " ACCUM ";
								}
							}
						} else {
							accumSearchString = searchString;
						}
					}
					if (accumSearchString.contains("ACCUM")) {
						accumSearchString = (String) accumSearchString.subSequence(0,
								accumSearchString.lastIndexOf("ACCUM"));
					}
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, accumSearchString);
					prepStmt.setInt(2, projectId);
				}
			}

			rs = prepStmt.executeQuery();

			while (rs.next()) {
				int requirementId = rs.getInt("id");
				int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				// we will use the projectId that came in as a param
				// int projectId = rs.getInt("project_id");
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
				String requirementTag = rs.getString("tag");
				String requirementFullTag = rs.getString("full_tag");
				int version = rs.getInt("version");
				String approvedByAllDt = rs.getString("approved_by_all_dt");
				String approvers = rs.getString("approvers");
				String requirementStatus = rs.getString("status");
				String requirementPriority = rs.getString("priority");
				String requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				int requirementPctComplete = rs.getInt("pct_complete");
				String requirementExternalUrl = rs.getString("external_url");
				String traceTo = rs.getString("trace_to");
				String traceFrom = rs.getString("trace_from");
				String userDefinedAttributes = rs.getString("user_defined_attributes");
				String testingStatus = rs.getString("testing_status");
				int deleted = rs.getInt("deleted");
				String folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId,
						requirementName, requirementDescription, requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus, requirementPriority, requirementOwner,
						requirementLockedBy, requirementPctComplete, requirementExternalUrl, traceTo, traceFrom,
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);

				// if the user does not have read permissions on this
				// requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges()
						.contains("readRequirementsInFolder" + requirement.getFolderId()))) {
					requirement.redact();
				}
				projectSearchReport.add(requirement);
			}

			rs.close();
			prepStmt.close();
			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

		// IMPORTANT :
		// ORACLE can not do select distinct with clob objects. so
		// we read data into a hash set and then put it to a array list to get
		// rid of duplicates
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = projectSearchReport.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		projectSearchReport.clear();
		projectSearchReport.addAll(newList);
		return (projectSearchReport);
	}

	// takes a search string and does either a context search or a search on
	// reqid.
	// this is similar to getProjectSearchReport. The difference is that the
	// backlog report ONLY looks for
	// requirements that meet the search criteria in requirements types that are
	// enabled for agile scrum.
	public static ArrayList getProjectBacklogSearchReport(SecurityProfile securityProfile, int projectId,
			String searchString, String searchType, String databaseType) {
		ArrayList projectSearchReport = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the report_sql from the db for this reportId.
			String sql = "";
			if (searchType.equals("reqId")) {
				// in this scenario, we have comma separated list of full tags.
				String reqIdSearchString = "";

				String[] reqIds = searchString.split(",");
				for (int i = 0; i < reqIds.length; i++) {
					if ((reqIds[i] != null) && !(reqIds[i].equals(""))) {
						// lets ignore the cases where we had BR-1 BR-2
						// that got converted to BR-1,,,,BR-2
						reqIdSearchString += "'" + reqIds[i] + "'" + ",";
					}

				}
				// drop the last ,
				if (reqIdSearchString.contains(",")) {
					reqIdSearchString = (String) reqIdSearchString.subSequence(0, reqIdSearchString.lastIndexOf(","));
				}

				if (databaseType.equals("mySQL")) {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag,"
							+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\" "
							+ " from  gr_requirements r, gr_requirement_types rt, gr_folders f "
							+ " where r.project_id = ? " + " and r.requirement_type_id = rt.id "
							+ " and  r.full_tag in (" + reqIdSearchString + ") " + " and r.folder_id = f.id "
							+ " and rt.enable_agile_scrum = 1 " + " order by f.folder_path, r.tag " + " limit 0,100";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);
				} else {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag,"
							+ " r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\" "
							+ " from  gr_requirements r, gr_requirement_types rt, gr_folders f "
							+ " where r.project_id = ? " + " and r.requirement_type_id = rt.id "
							+ " and  upper(r.full_tag) in (" + reqIdSearchString.toUpperCase() + ") "
							+ " and r.folder_id = f.id " + " and rownum < 101  " + " and rt.enable_agile_scrum = 1 "
							+ " order by f.folder_path, r.tag ";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);
				}

			} else {
				// in this scenario we have search against a generic search
				// string.
				if (databaseType.equals("mySQL")) {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag, "
							+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\" , "
							+ " match (s.object_text) against ('" + searchString + "')" + " as score "
							+ " from gr_search s, gr_requirements r, gr_requirement_types rt , gr_folders f "
							+ " where match (s.object_text) against ('" + searchString + "')" + " and s.project_id = ? "
							+ " and s.object_id = r.id " + " and r.folder_id = f.id "
							+ " and r.requirement_type_id = rt.id " + " and rt.enable_agile_scrum = 1 "
							+ " order by score desc " + " LIMIT  0, 100";
					prepStmt = con.prepareStatement(sql);
					prepStmt.setInt(1, projectId);

				} else {
					sql = " select  r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
							+ " r.description, r.tag, r.full_tag, "
							+ " r.version, to_char(r.approved_by_all_dt, 'DD MON YYYY') \"approved_by_all_dt\","
							+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
							+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
							+ " r.deleted, f.folder_path, r.created_by, to_char(r.created_dt, 'DD MON YYYY') \"created_dt\" ,  r.last_modified_by, "
							+ " r.last_modified_dt, rt.name \"requirement_type_name\"  "
							+ " from gr_search s, gr_requirements r, gr_requirement_types rt , gr_folders f "
							+ " where CONTAINS(object_text, ? , 1) > 0 " + " and s.project_id = ? "
							+ " and s.object_id = r.id " + " and r.folder_id = f.id "
							+ " and r.requirement_type_id = rt.id " + " and rownum < 101 "
							+ " and rt.enable_agile_scrum = 1 " + " order by score(1) desc ";

					String accumSearchString = "";
					if ((searchString != null) && (!searchString.equals(""))) {
						if (searchString.contains(" ")) {
							String[] tokens = searchString.split(" ");
							for (int i = 0; i < tokens.length; i++) {
								String token = tokens[i];
								if ((token != null) && (!token.equals(""))) {
									accumSearchString += token + " ACCUM ";
								}
							}
						} else {
							accumSearchString = searchString;
						}
					}
					if (accumSearchString.contains("ACCUM")) {
						accumSearchString = (String) accumSearchString.subSequence(0,
								accumSearchString.lastIndexOf("ACCUM"));
					}
					prepStmt = con.prepareStatement(sql);
					prepStmt.setString(1, accumSearchString);
					prepStmt.setInt(2, projectId);
				}
			}

			rs = prepStmt.executeQuery();

			while (rs.next()) {
				int requirementId = rs.getInt("id");
				int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				// we will use the projectId that came in as a param
				// int projectId = rs.getInt("project_id");
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
				String requirementTag = rs.getString("tag");
				String requirementFullTag = rs.getString("full_tag");
				int version = rs.getInt("version");
				String approvedByAllDt = rs.getString("approved_by_all_dt");
				String approvers = rs.getString("approvers");
				String requirementStatus = rs.getString("status");
				String requirementPriority = rs.getString("priority");
				String requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				int requirementPctComplete = rs.getInt("pct_complete");
				String requirementExternalUrl = rs.getString("external_url");
				String traceTo = rs.getString("trace_to");
				String traceFrom = rs.getString("trace_from");
				String userDefinedAttributes = rs.getString("user_defined_attributes");
				String testingStatus = rs.getString("testing_status");
				int deleted = rs.getInt("deleted");
				String folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId,
						requirementName, requirementDescription, requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus, requirementPriority, requirementOwner,
						requirementLockedBy, requirementPctComplete, requirementExternalUrl, traceTo, traceFrom,
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);

				// if the user does not have read permissions on this
				// requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges()
						.contains("readRequirementsInFolder" + requirement.getFolderId()))) {
					requirement.redact();
				}
				projectSearchReport.add(requirement);
			}

			rs.close();
			prepStmt.close();
			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

		// IMPORTANT :
		// ORACLE can not do select distinct with clob objects. so
		// we read data into a hash set and then put it to a array list to get
		// rid of duplicates
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = projectSearchReport.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		projectSearchReport.clear();
		projectSearchReport.addAll(newList);
		return (projectSearchReport);
	}

	// takes a list of projectIds (comma separated), search string, the user id
	// and
	// searchs for requirements that have the search string in intersection of
	// projects requested
	// the list of projects the user has access to
	public static ArrayList getglobalSearchReport(SecurityProfile securityProfile, String searchProjects,
			String searchString, User user, String databaseType, int projectId, int targetRequirementTypeId,
			int targetFolderId) {
		ArrayList globalSearchReport = new ArrayList();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the report_sql from the db for this reportId.
			// from a security perspective, lets make sure we return only
			// the data the users has access to.
			String sql = "";

			// in this scenario we have search against a generic search
			// string.

			sql = " select distinct p.name \"project_name\", "
					+ " p.short_name \"project_prefix\", p.description \"project_description\" , "
					+ " r.id, r.requirement_type_id, r.folder_id, r.project_id, r.name, "
					+ " r.description, r.tag, r.full_tag, "
					+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
					+ " r.approvers," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\" ,  r.last_modified_by, "
					+ " r.last_modified_dt, rt.name \"requirement_type_name\"  "
					+ " from gr_user_roles ur, gr_search s, gr_projects p,"
					+ " gr_requirements r, gr_requirement_types rt , gr_folders f " + " where ur.user_id = ? ";

			if (projectId > 0) {
				sql += " and ur.project_id = " + projectId;
			}

			sql += " and ur.project_id = p.id " + " and ur.project_id = s.project_id " + " and s.object_text like '%"
					+ searchString + "%' " + " and s.object_id = r.id " + " and r.folder_id = f.id "
					+ " and r.requirement_type_id = rt.id " + " and p.archived = 0 ";

			if (targetRequirementTypeId > 0) {
				sql += " and r.requirement_type_id = " + targetRequirementTypeId;
			}

			if (targetFolderId > 0) {
				sql += " and r.folder_id = " + targetFolderId ;
			}

			// lets limit the project list to only those in the projects
			// selected by the user.

			// lets drop the last ,
			if (searchProjects.contains(",")) {
				searchProjects = (String) searchProjects.subSequence(0, searchProjects.lastIndexOf(","));
			}
			if ((searchProjects != null) && !(searchProjects.equals(""))) {
				sql += " and s.project_id in (" + searchProjects + ")";
			}

			sql += " order by rt.display_sequence, rt.short_name, r.tag  ";

			prepStmt = con.prepareStatement(sql);
			prepStmt.setInt(1, user.getUserId());

			rs = prepStmt.executeQuery();

			while (rs.next()) {
				int requirementId = rs.getInt("id");
				int folderId = rs.getInt("folder_id");
				int requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				String requirementName = rs.getString("name");
				String requirementDescription = rs.getString("description");
				String requirementTag = rs.getString("tag");
				String requirementFullTag = rs.getString("full_tag");
				int version = rs.getInt("version");
				String approvedByAllDt = rs.getString("approved_by_all_dt");
				String approvers = rs.getString("approvers");
				String requirementStatus = rs.getString("status");
				String requirementPriority = rs.getString("priority");
				String requirementOwner = rs.getString("owner");
				String requirementLockedBy = rs.getString("locked_by");
				int requirementPctComplete = rs.getInt("pct_complete");
				String requirementExternalUrl = rs.getString("external_url");
				String traceTo = rs.getString("trace_to");
				String traceFrom = rs.getString("trace_from");
				String userDefinedAttributes = rs.getString("user_defined_attributes");
				String testingStatus = rs.getString("testing_status");
				int deleted = rs.getInt("deleted");
				String folderPath = rs.getString("folder_path");
				String createdBy = rs.getString("created_by");
				String createdDt = rs.getString("created_dt");
				String lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt = rs.getDate("last_modified_by");
				String requirementTypeName = rs.getString("requirement_type_name");

				Requirement requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId,
						requirementName, requirementDescription, requirementTag, requirementFullTag, version,
						approvedByAllDt, approvers, requirementStatus, requirementPriority, requirementOwner,
						requirementLockedBy, requirementPctComplete, requirementExternalUrl, traceTo, traceFrom,
						userDefinedAttributes, testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);

				// if the user does not have read permissions on this
				// requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges()
						.contains("readRequirementsInFolder" + requirement.getFolderId()))) {
					requirement.redact();
				}
				String projectName = rs.getString("project_name");
				String projectPrefix = rs.getString("project_prefix");
				String projectDescription = rs.getString("project_description");
				GlobalRequirement globalRequirement = new GlobalRequirement(projectId, projectName, projectPrefix,
						projectDescription, requirement);
				globalSearchReport.add(globalRequirement);
			}

			rs.close();
			prepStmt.close();
			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

		// IMPORTANT :
		// ORACLE can not do select distinct with clob objects. so
		// we read data into a hash set and then put it to a array list to get
		// rid of duplicates
		/*
		 * Set set = new HashSet(); List newList = new ArrayList(); for
		 * (Iterator iter = globalSearchReport.iterator(); iter.hasNext(); ) {
		 * Object element = iter.next(); if (set.add(element))
		 * newList.add(element); } globalSearchReport.clear();
		 * globalSearchReport.addAll(newList);
		 */

		return (globalSearchReport);
	}

	
	
	// Seaches the Old Versions of Requirements
	public static ArrayList<RequirementVersion> getOldVersionSearchReport(SecurityProfile securityProfile, String searchProjects,
			String searchString, User user, String databaseType, int projectId, int targetRequirementTypeId,
			int targetFolderId) {
		ArrayList<RequirementVersion> globalSearchReport = new ArrayList<RequirementVersion>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		java.sql.Connection con = null;
		try {
			javax.naming.InitialContext context = new InitialContext();
			javax.sql.DataSource dataSource = (javax.sql.DataSource) context.lookup("java:comp/env/jdbc/gloree");
			con = dataSource.getConnection();

			// lets get the report_sql from the db for this reportId.
			// from a security perspective, lets make sure we return only
			// the data the users has access to.
			String sql = "";

			// in this scenario we have search against a generic search
			// string.

			// search in a project / folder / req type, 
			// look in requirement_versions to see if name / description / attributes have the search string
			
			
			
			
			sql = "  select r.id \"requirement_id\",r.folder_id , v.id, v.version, v.name, v.description, " +
					" v.approvers, v.priority, v.status, v.owner, v.pct_complete, v.external_url," +
					" v.trace_to, v.trace_from, v.user_defined_attributes, " +
					" v.created_by," +
					" date_format(v.created_dt, '%d %M %Y %r ') \"created_dt\"" 
					+ "  from gr_requirements r, gr_requirement_versions v, gr_folders f "
					+ "  where r.id = v.requirement_id "
					+ " and r.folder_id = f.id "
					+ " and r.project_id = "  + projectId  
					+ "	and concat (v.name, v.description, v.user_defined_attributes) like '%"+ searchString +"%' ";
			
			

			if (targetRequirementTypeId > 0) {
				sql += " and r.requirement_type_id = " + targetRequirementTypeId;
			}

			if (targetFolderId > 0) {
				sql += " and r.folder_id = " + targetFolderId ;
			}

			sql += " order by f.id, r.tag, v.id  ";
			
			System.out.println("sql of oldversionsearch is \n\n" + sql );

			prepStmt = con.prepareStatement(sql);

			rs = prepStmt.executeQuery();

			while (rs.next()) {
				
				
				//////
				int requirementId = rs.getInt("requirement_id");
				int folderId  = rs.getInt("folder_id");
				int versionId = rs.getInt("id");
				int version = rs.getInt("version");
				String versionName = rs.getString("name");
				String versionDescription = rs.getString("description");
				String versionCreatedBy = rs.getString("created_by");
				String versionCreatedDt = rs.getString("created_dt");
				String versionApprovers = rs.getString("approvers");
				String versionPriority= rs.getString("priority");
				String versionStatus = rs.getString("status");
				String versionOwner= rs.getString("owner");
				int versionPctComplete= rs.getInt("pct_complete");
				String versionExternalURL= rs.getString("external_url");
				String versionTraceTo = rs.getString("trace_to");
				String versionTraceFrom = rs.getString("trace_from");
				String versionUserDefinedAttributes = rs.getString("user_defined_attributes");
				
				// if the user does not have read permissions on this
				// requirement,
				// lets redact it. i.e. remove all sensitive infor from it.
				if (!(securityProfile.getPrivileges()
						.contains("readRequirementsInFolder" + folderId))) {
					versionName = "No READ Permission";
					versionDescription = "No READ Permission";
					versionUserDefinedAttributes = "No READ Permission";
				}
				
				RequirementVersion requirementVersion = new RequirementVersion(versionId,requirementId,
					version, versionName, versionDescription,
					versionCreatedBy, versionCreatedDt, 
					versionApprovers , versionStatus, versionPriority, versionOwner,
					versionPctComplete, versionExternalURL , versionTraceTo, 
					versionTraceFrom, versionUserDefinedAttributes );
				//////


				
				globalSearchReport.add(requirementVersion);
			}

			rs.close();
			prepStmt.close();
			con.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
				}
				con = null;
			}
		}

		

		return (globalSearchReport);
	}


		
	
	public static ArrayList<Requirement> getOneLevelDownStreamRequirements(java.sql.Connection con, int requirementId, SecurityProfile securityProfile) {
		ArrayList<Requirement> downStreamRequirements = new ArrayList<Requirement>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			
			
			Requirement requirement = null;
			
			int folderId = 0;
			int requirementTypeId = 0;
			int projectId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			String requirementLockedBy = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy = "";
			String requirementTypeName = "";
			String traceDescription = " ";
			int traceId = 0;


			// lets get the report_sql from the db for this reportId.
			// from a security perspective, lets make sure we return only
			// the data the users has access to.
			String traceTreeSQL = "";

			traceTreeSQL = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id," + " r.name, "
					+ " r.description, r.tag, r.full_tag,"
					+ " r.version, date_format(r.approved_by_all_dt, '%d %M %Y %r ') \"approved_by_all_dt\","
					+ " r.approvers  ," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, date_format(r.created_dt, '%d %M %Y %r ') \"created_dt\","
					+ " r.last_modified_by, " + " r.last_modified_dt, rt.name \"requirement_type_name\", t.suspect, "
					+ " t.description \"traceDescription\", t.id \"traceId\"  "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t, gr_folders f "
					+ " where t.to_requirement_id = ? " + " and t.from_requirement_id = r.id "
					+ " and r.requirement_type_id = rt.id " + " and r.folder_id = f.id " + " and r.deleted= 0 "
					+ " order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";




			prepStmt = con.prepareStatement(traceTreeSQL);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			
			



			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				requirementName = rs.getString("name");
				requirementDescription = rs.getString("description");
				requirementTag = rs.getString("tag");
				requirementFullTag = rs.getString("full_tag");
				version = rs.getInt("version");
				approvedByAllDt = rs.getString("approved_by_all_dt");
				approvers = rs.getString("approvers");
				requirementStatus = rs.getString("status");
				requirementPriority = rs.getString("priority");
				requirementOwner = rs.getString("owner");
				requirementLockedBy = rs.getString("locked_by");
				requirementPctComplete = rs.getInt("pct_complete");
				requirementExternalUrl = rs.getString("external_url");
				traceTo = rs.getString("trace_to");
				traceFrom = rs.getString("trace_from");
				userDefinedAttributes = rs.getString("user_defined_attributes");
				testingStatus = rs.getString("testing_status");
				deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				createdBy = rs.getString("created_by");
				createdDt = rs.getString("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt =
				// rs.getDate("last_modified_by");
				requirementTypeName = rs.getString("requirement_type_name");
				traceDescription = rs.getString("traceDescription");
				traceId = rs.getInt("traceId");

				requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId,
						requirementName, requirementDescription, requirementTag, requirementFullTag,
						version, approvedByAllDt, approvers, requirementStatus, requirementPriority,
						requirementOwner, requirementLockedBy, requirementPctComplete,
						requirementExternalUrl, traceTo, traceFrom, userDefinedAttributes,
						testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);

				// if the user does not have read permissions on
				// this requirement,
				// lets redact it. i.e. remove all sensitive
				// info from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + requirement.getFolderId()))) {
					requirement.redact();
				}
				
				downStreamRequirements.add(requirement);
			}
			rs.close();
			prepStmt.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			// don't close con as it needs to go back
		}


		return (downStreamRequirements);
	}
	
	
	
	
	
	public static ArrayList<TraceTreeRow> getOneLevelDownStreamTraceTreeRows(java.sql.Connection con, int nextLevel, String displayRequirementType,
		int requirementId, SecurityProfile securityProfile) {
		ArrayList<TraceTreeRow> downStreamTraceTreeRows = new ArrayList<TraceTreeRow>();
		PreparedStatement prepStmt = null;
		ResultSet rs = null;
		try {
			
			
			Requirement requirement = null;
			TraceTreeRow traceTreeRow = null;
			
			int folderId = 0;
			int requirementTypeId = 0;
			int projectId = 0;
			String requirementName = "";
			String requirementDescription = "";
			String requirementTag = "";
			String requirementFullTag = "";
			int version = 0;
			String approvedByAllDt = "";
			String approvers = "";
			String requirementStatus = "";
			String requirementPriority = "";
			String requirementOwner = "";
			String requirementLockedBy = "";
			int requirementPctComplete = 0;
			String requirementExternalUrl = "";
			String traceTo = "";
			String traceFrom = "";
			String userDefinedAttributes = "";
			String testingStatus = "";
			int deleted = 0;
			String folderPath = "";
			String createdBy = "";
			String createdDt = "";
			String lastModifiedBy = "";
			String requirementTypeName = "";
			String traceDescription = " ";
			int traceId = 0;


			// lets get the report_sql from the db for this reportId.
			// from a security perspective, lets make sure we return only
			// the data the users has access to.
			String traceTreeSQL = "";



			traceTreeSQL = " SELECT r.id, r.requirement_type_id, r.folder_id, r.project_id," + " r.name, "
					+ " r.description, r.tag, r.full_tag,"
					+ " r.version, "
					+ " date_format(r.approved_by_all_dt, '%d-%b-%Y') \"approved_by_all_dt\","
					+ " r.approvers  ," + " r.status, r.priority, r.owner, r.locked_by, r.pct_complete, "
					+ " r.external_url, r.trace_to, r.trace_from,  r.user_defined_attributes, r.testing_status, "
					+ " r.deleted, f.folder_path, r.created_by, "
					+ " date_format(r.created_dt, '%d-%b-%Y') \"created_dt\","
					+ " r.last_modified_by, " 
					+ " date_format(r.last_modified_dt, '$d-%b-%Y') \"last_modified_dt\" , "
					+ " rt.name \"requirement_type_name\", t.suspect, "
					+ " t.description \"traceDescription\", t.id \"traceId\"  "
					+ " FROM gr_requirements r , gr_requirement_types rt, gr_traces t, gr_folders f "
					+ " where t.to_requirement_id = ? " + " and t.from_requirement_id = r.id "
					+ " and r.requirement_type_id = rt.id " + " and r.folder_id = f.id " + " and r.deleted= 0 "
					+ " order by r.tag_level1,r.tag_level2, r.tag_level3, r.tag_level4 , r.tag ";

			

			prepStmt = con.prepareStatement(traceTreeSQL);
			prepStmt.setInt(1, requirementId);
			rs = prepStmt.executeQuery();
			
			



			while (rs.next()) {
				requirementId = rs.getInt("id");
				folderId = rs.getInt("folder_id");
				requirementTypeId = rs.getInt("requirement_type_id");
				projectId = rs.getInt("project_id");
				requirementName = rs.getString("name");
				requirementDescription = rs.getString("description");
				requirementTag = rs.getString("tag");
				requirementFullTag = rs.getString("full_tag");
				version = rs.getInt("version");
				approvedByAllDt = rs.getString("approved_by_all_dt");
				approvers = rs.getString("approvers");
				requirementStatus = rs.getString("status");
				requirementPriority = rs.getString("priority");
				requirementOwner = rs.getString("owner");
				requirementLockedBy = rs.getString("locked_by");
				requirementPctComplete = rs.getInt("pct_complete");
				requirementExternalUrl = rs.getString("external_url");
				traceTo = rs.getString("trace_to");
				traceFrom = rs.getString("trace_from");
				userDefinedAttributes = rs.getString("user_defined_attributes");
				testingStatus = rs.getString("testing_status");
				deleted = rs.getInt("deleted");
				folderPath = rs.getString("folder_path");
				createdBy = rs.getString("created_by");
				createdDt = rs.getString("created_dt");
				lastModifiedBy = rs.getString("last_modified_by");
				// lastModifiedDt =
				// rs.getDate("last_modified_by");
				requirementTypeName = rs.getString("requirement_type_name");
				traceDescription = rs.getString("traceDescription");
				traceId = rs.getInt("traceId");

				requirement = new Requirement(requirementId, requirementTypeId, folderId, projectId,
						requirementName, requirementDescription, requirementTag, requirementFullTag,
						version, approvedByAllDt, approvers, requirementStatus, requirementPriority,
						requirementOwner, requirementLockedBy, requirementPctComplete,
						requirementExternalUrl, traceTo, traceFrom, userDefinedAttributes,
						testingStatus, deleted, folderPath, createdBy, lastModifiedBy,
						requirementTypeName, createdDt);

				// if the user does not have read permissions on
				// this requirement,
				// lets redact it. i.e. remove all sensitive
				// info from it.
				if (!(securityProfile.getPrivileges().contains("readRequirementsInFolder" + requirement.getFolderId()))) {
					requirement.redact();
				}
				
				traceTreeRow = new TraceTreeRow(nextLevel, rs.getInt("suspect"), traceId, traceDescription, requirement);
				// lets see if this TraceTree Requirements is
				// one of the display Requirement Types.
				// i.e the user chose to either see 'all'
				// requirements of the TraceTree or
				// see only the ones he / she decided to see.
				// we add only the resulting row to the
				// arraylist only if the user choose to see ALL
				// requirement types
				// or if this requirement fits the bill.
				// we add every thing to the report and control display at the displayTraceTreeReportData
				downStreamTraceTreeRows.add(traceTreeRow);
				
				/*
				if (displayRequirementType.contains("all")) {
					downStreamTraceTreeRows.add(traceTreeRow);
				} else {
					// means some display restrictions are in
					// place
					if (displayRequirementType.contains(requirement.getRequirementTypeId() + ",")){
						// every req full tag is
						// requriementtypeshortname-number. eg
						// BR-11.
						// so if the user chose to see only
						// Product Requirements, then the
						// displayRequirementType will be PR
						// in this case, lets skip it.
						downStreamTraceTreeRows.add(traceTreeRow);
					}
				}
				*/
				
			}
			rs.close();
			prepStmt.close();

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (prepStmt != null) {
				try {
					prepStmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			// don't close con as it needs to go back
		}


		return (downStreamTraceTreeRows);
	}	
	
	
	
	
}
