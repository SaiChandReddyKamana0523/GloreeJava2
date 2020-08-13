<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>

<%
	// authentication only
	String isLoggedIn = (String ) session.getAttribute("isLoggedIn");
	if ((isLoggedIn  == null) || (isLoggedIn.equals(""))){
		// this means that the user is not logged in. So lets forward him to the 
		// log in page.
%>
		<jsp:forward page="/jsp/WebSite/startPage.jsp"/>
<% }
	
	Project project= (Project) session.getAttribute("project");
	SecurityProfile securityProfile = (SecurityProfile) session.getAttribute("securityProfile");
	
	// lets see if this user is a member of this project.
	// we are leaving this page open to member of this project (which includes admins also)
	boolean isMember = false;
	if (securityProfile.getRoles().contains("MemberInProject" + project.getProjectId())){
		isMember = true;
	}
	
 	if (isMember){
		int sharedRequirementTypeId = Integer.parseInt(request.getParameter("sharedRequirementTypeId"));
		SharedRequirementType sRT = new SharedRequirementType(sharedRequirementTypeId);
%>
	
	<div id='sharedRequirementsFilterSectionDiv' class='level2BoxColored'>	
	<table width='100%'>
		<%if ((sRT.getSRInstructions() != null) && !(sRT.getSRInstructions().trim().equals(""))) {%>
			<tr> 
				<td width='190' >
					<span class='normalText'>Instructions</span>
				</td>
				<td>
					<span class='normalText'><%=sRT.getSRInstructions()%> </span>
				</td>
			</tr>
		<%} %>
		<tr> 
			<td width='190' >
				<span class='normalText'>Domain Administrators</span>
			</td>
			<td>
				<span class='normalText'><%=sRT.getSRDomainAdministrators()%> </span>
			</td>
		</tr>
		<tr><td colspan='2'>&nbsp;</td></tr>
		<tr> 
			<td colspan='2'>
				<span class='normalText'><b>Filters</b></span>
			</td>
		</tr>

		<tr>		
			<td width='190' valign='top'>
				<span class='normalText'>
				Published Groupings (Baselines)
				</span>
			</td>
			<td>
				<span class='normalText'>
	    		<select id='sRRTBaselineId' >
					<%
					String sRRTBaselineIds = sRT.getSRRTBaselineIDs();
					// the string is in the format rTBaselineId8,rTBaselineId4,
					if (sRRTBaselineIds.contains(",")){
						String [] o = sRRTBaselineIds.split(","); 
						for (int j=0 ; j < o.length; j++){
							if ((o[j] != null) && (!o[j].equals("")) && (o[j].contains("rTBaselineId"))){
								int rTBaselineId = 0;
								try {
									rTBaselineId = Integer.parseInt(o[j].replace("rTBaselineId",""));
								}
								catch (Exception e){
									rTBaselineId = 0;
								}
								if (rTBaselineId > 0){
									RTBaseline rTBaseline = new RTBaseline(rTBaselineId);
									%>
									<option value='<%=rTBaseline.getBaselineId()%>'><%=rTBaseline.getBaselineName()%></option>
									<%
								}
							}
						}
					}
					%>
				</select>
				</span>
				</td>
		</tr>		




		<tr>		
			<td width='190' valign='top'>
				<span class='normalText'>
				Display only New or Changed
				</span>
			</td>
			<td>
				<span class='normalText'>
	    		<input type='checkbox' name='onlyNewOrChanged' id='onlyNewOrChanged' CHECKED></input>
				</span>
			</td>
		</tr>		




		<%
		ArrayList sharedAttributes = sRT.getAllSharedAttributesInRequirementType();
		int counter = 0;
		Iterator i = sharedAttributes.iterator();
		// we use attributeIdString to figure out which attribute are present in this req type
		// during JavaScript parsint fo check copyable , filterable check boxes.
		String attributeIdStringDropDown = "";
		String attributeIdStringTextBox = "";
		
		while (i.hasNext()){
			SharedRequirementTypeAttribute sRTAttribute = (SharedRequirementTypeAttribute) i.next();
			RTAttribute rTAttribute = sRTAttribute.getRTAttribute();
			if (sRTAttribute.getSRAFilterable() ==1 ){
			%>
			<tr> 
				<%
	    		if (rTAttribute.getAttributeType().equals("Drop Down")){
					counter++;
					attributeIdStringDropDown += rTAttribute.getAttributeId() + "##";

	    		%>
				<td width='190' valign='top'>
					<span class='normalText' title='<%=rTAttribute.getAttributeDescription()%>'>
					 <%=rTAttribute.getAttributeName()%><br>
					 (Ctrl + Click to Select)
					 </span>
				</td>
				<td>
					<span class='normalText'>
	    		
					<select id='filterAttribute<%=rTAttribute.getAttributeId()%>' MULTIPLE SIZE='4'>
					<%
					String [] o = rTAttribute.getAttributeDropDownOptions().split(",");
					for (int j=0 ; j < o.length; j++){
					%>
						<option value='<%=o[j]%>'><%=o[j]%></option>
					<%
					}
					%>
					</select>
					</span>
				</td>
	    		<%}

				
	    		if (
	    			((rTAttribute.getAttributeType().equals("Text Box"))) ||
	    			((rTAttribute.getAttributeType().equals("URL")))
	    		) 	
	    		{
	    			counter++;
					attributeIdStringTextBox += rTAttribute.getAttributeId() + "##";
	    		%>
					<td width='190' valign='top'>
						<span class='normalText' title='<%=rTAttribute.getAttributeDescription()%>'>
						 <%=rTAttribute.getAttributeName()%>
						 </span>
					</td>
					<td>
						<span class='normalText'>
	    				<input type='textbox' size='30' id='filterAttribute<%=rTAttribute.getAttributeId()%>'>
	    				</span>
					</td>
	    		<%}	%>
			</tr>
			<%
			}
		}
		if (counter == 0){
		%>
			<tr> 
				<td colspan='2'>
					<span class='normalText'>No Filters have been defined for this Shared Requirement Type</span>
				</td>
			</tr>
		<%	
		}
		%>
		<tr> 
			<td colspan='2'>
				<span class='normalText'>
				 <input type='button' id='displaySharedRequirements' value='Display Shared Requirements'
				 onClick='filterSharedRequirements("<%=attributeIdStringDropDown%>","<%=attributeIdStringTextBox%>")'></input>
				 </span>
			</td>
		</tr>
	</table>
	</div>
	<div id='displayFilteredSharedRequiremensDiv'></div>
	
<%}%>