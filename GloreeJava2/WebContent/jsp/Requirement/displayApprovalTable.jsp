<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>
<head>

	<!--  Since this page is likely shown in a different window, we need to load the css and java script files. -->
	<link rel="stylesheet" type="text/css" href="/GloreeJava2/css/common.css"> 
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>
	
	
</head>
<body onLoad='document.getElementById("googleSearchString").focus();'>
<%@ page import="java.util.*" %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	String databaseType = this.getServletContext().getInitParameter("databaseType");
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);
	
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
	
	// you need to be a member of this project to see this
	if (isMember ){
		
		
		
%>
	
	
	<table id='approversTable'  class='table '>
		<tr class='info'>
			<td style='width:200px'>Role </td>
			<td style='width:100px'>Acceptance Rank</td>
			<td style='width:150px'>Role Type</td>
			<td style='width:200px'>Acceptor</td>
			<td style='width:100px'>Acceptance Status</td>
			<td style='width:150px'>Date</td>
			
		</tr>	
		<%
		ArrayList<String> approversAndStatus = requirement.getApproversAndStatus();
		
		
	
		for (String aS : approversAndStatus){
			
			String[] approversArray = aS.split(":##:");
			
			 String roleName = "";
			 String approvalRank = "";
			 String approvalType = "";
			 String emailId = "";
			 String userName = "";
			 String status = "";
			 String date = "";
			 String note = "";
			 String approvedRoles = "";
			 String currentRoleApprovalNote = "";
			 String currentRoleApprovalDt = "";
				
			 try {
				 roleName =  approversArray[0];
				 approvalRank =  approversArray[1];
				 approvalType =  approversArray[2];
				 
				 if (approvalType.equals("ApprovalByAll")){
					 approvalType = "Acceptance By All";
				 }
				 if (approvalType.equals("ApprovalByAny")){
					 approvalType = "Acceptance By Anyone";
				 }
				 emailId =  approversArray[3];
				 userName =  approversArray[4];
				 status =  approversArray[5];
				 note =  approversArray[6];
				 date =  approversArray[7];
				 approvedRoles = approversArray[8];
			 }
			 catch (Exception e){
				 e.printStackTrace();
			 }
			 	
			 
			// loop through the approvedRoles, till you come across current role (for this role). 
			// parse that to get the approval note and approved date
			 try {
				if (approvedRoles.contains("#")){
					// lets split approvedRoles to get the approval date and note
					String[] approverDetails = approvedRoles.split(",");
					
					for (String aD :approverDetails ){
						if (aD.contains(roleName) ){
							// lets split aD by :#:
							String[] noteAndDate = aD.split(":#:");	
							currentRoleApprovalNote = noteAndDate[1];
							currentRoleApprovalDt = noteAndDate[2];
							
							note = currentRoleApprovalNote;
							date = currentRoleApprovalDt;
						}
					}
					
					
				}
			 }
			 catch (Exception e){}
		 	String rowClass="";
			if (status.equals("Approved")){
				rowClass="success";
			}if (status.equals("Pending")){
				rowClass="warning";
				
			}
			if (status.equals("Rejected")){
				rowClass="danger";
			}
			if (status.equals("Waiting")){
				rowClass="info";
				status = "Waitng for Others";
			}

			if (approvedRoles.contains(roleName)){
				rowClass="success";
				status = "Accepted";
			}
			 %>
				<tr class='<%=rowClass%>'>
					<td style='width:200px'><%=roleName %></td>
					<td style='width:100px'><%=approvalRank %></td>
					<td style='width:150px'><%=approvalType%></td>
					<td style='width:200px'><%=userName%></td>
					<td style='width:100px'><%=status%></td>
					<td style='width:150px'><%=date%></td>
					
				</tr>	
				<%if ((note!=null && (note.length() > 0 ))){ %>
				<tr class='<%=rowClass%>'>
					<td colspan="6" style="border-top-style:none">
						<b>Note : </b><%=note %>
					</td>
					
					
				</tr>	
				<%} %>
			<%
		}
		
		%>
		</table>		
		    	 
   <%	 
   }
   %>
	
</body>

