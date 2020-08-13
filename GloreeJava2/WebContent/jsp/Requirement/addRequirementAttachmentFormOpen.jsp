<!-- GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>
<%
	String databaseType = this.getServletContext().getInitParameter("databaseType");

	int requirementId = Integer.parseInt(request.getParameter("requirementId"));
	Requirement requirement = new Requirement(requirementId, databaseType);

	///////////////////////////////SECURITY CODE ////////////////////////////
	
	///////////////////////////////SECURITY CODE ////////////////////////////

	int folderId = Integer.parseInt(request.getParameter("folderId"));
	int webFormId = Integer.parseInt(request.getParameter("webFormId"));
%>
	<%@ page import="java.util.*" %>
	<%@ page import="com.gloree.beans.*" %>
	<%@ page import="com.gloree.utils.*" %>
	
	<div id='addRequirementAttachmentsDiv' class='alert alert-success'> 
		
		<form method="post"  ENCTYPE='multipart/form-data' acceptcharset="UTF-8"  id="addRequirementAttachment" 
		action="/GloreeJava2/servlet/AddRequirementAttachmentActionOpen">
			<input type="hidden" name="actionToDo" value="addRequirementAttachmentOpen" >
			<input type="hidden" name="folderId" value="<%=requirement.getFolderId() %>" >
	 		<input type="hidden" name="requirementId" value="<%=requirementId%>" >
	 	
	 		<input type="hidden" id="addExistingFilesHidden" name="addExistingFilesHidden" value="" >
	 		<input type="hidden" id="webFormId" name="webFormId"  value="<%=webFormId %>" >
	 	
		<br><br>
	 	
		
		<table class='paddedTable'  >
			<tr> 
				<td> 
					<span class='normalText'>
					Description <sup><span style="color: #ff0000;">*</span></sup> 
					</span>
				</td>
				<td>
					 <span class='normalText'>
					<textarea id="title" name="title" rows="5" cols="80" ></textarea>
					</span> 
				</td>
			</tr>
				
			<tr>
				<td> 
					<span class='headingText'>
					Attachment <font color='red'>(Size < 10MB)</font>
					<sup><span style="color: #ff0000;">*</span></sup> 
					</span>
				</td>
			 
				<td>
					<span class='normalText'>
					<INPUT TYPE='file'  NAME='attachment'>
					</span>
				</td>
				
			</tr>	
			<tr>
				<td colspan=2 align="center">
					<span class='normalText'>
					<input type="button"  class='btn btn-primary btn-sm'  name="Upload File"  id="uploadFileButton" value="  Upload File  " 
					onClick='addRequirementAttachment(this.form);'>
					
					&nbsp;&nbsp;
					
					<input type='button'  class='btn btn-danger btn-sm'  name='Cancel' value=' Close ' 
					onClick='document.getElementById("addRequirementAttachmentsDiv").style.display="none"'>
					</span>
				</td>
			</tr> 			
		</table>
		</form>
	</div>
	