<!--  GloreeJava2 -->
<!-- pageEncoding -->
<%@page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*" %>
<%@ page import="javax.servlet.http.HttpSession"  %>
<%@ page import="com.gloree.beans.*" %>
<%@ page import="com.gloree.utils.*" %>


	
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userAccount.js?v=20200630"></script>
	<script src="/GloreeJava2/js/userDashboard.js?v=20200630"></script>  
	
	
	<link rel="stylesheet" href="/GloreeJava2/css/greeny.css" type="text/css" >
	<link rel="stylesheet" href="/GloreeJava2/css/common.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_global.css" type="text/css">
	<link rel="stylesheet" href="/GloreeJava2/css/sales_home.css" type="text/css" media="screen">
	
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	
	
	
<%
	// THIS IS A TRACENOW FORM. NO AUTHENTICATION REQUIRED
	// USER WILL USE THIS TO UPLOAD A FORM AND A CONFIRMATION KEY IS SENT TO THE USER
	

		
		int maxImportExcelFileSize = Integer.parseInt(this.getServletContext().getInitParameter("maxImportExcelFileSize"));
		


	%>

	
	<div id='importFromExcelFormDiv' class='level1Box'>
		<form method="post"  ENCTYPE='multipart/form-data' id="importFromExcelForm" 
		action="/GloreeJava2/servlet/ImportFromExcelActionTN">
			<input type="hidden" name="action" value="uploadFile" >
			 
		<table class='paddedTable' width='100%'>
			
			<tr>
				<td colspan="2">
					<div >
					<span class='normalText'>
					<br>
					<font color='red'> For a successful upload, Please follow these guidelines : </font> 
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; Ensure that the 1st sheet of your Excel file has the data .
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; Ensure that your 1st row of your 1st sheet has column headers
					The column names are automatically mapped to the attribute names
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; Only the first 20,000 rows are processed.
					<br><br>&nbsp;&nbsp;&nbsp;&nbsp; We currently support ONLY .XLS format
					If you have .XLX files, please save that file as a .XLS file and then upload
					
					</span>
					</div>	
				</td> 
			</tr>
			<tr>
				<td colspan='2'> &nbsp; </td>
			</tr>
			
				
			
			<tr>
				<td> 
					<span class='headingText'>
					Excel File <font color='red'>(Size < <%=maxImportExcelFileSize/(1024*1024) %>MB)</font>
					<sup><span style="color: #ff0000;">*</span></sup> 
					</span>
				</td>
			 
				<td>
					<span class='normalText'>
					<INPUT TYPE='file' NAME='importFile' class='btn btn-sm btn-primary'>
					</span>
				</td>
				
			</tr>	
			
			<tr>
				<td></td>
				<td >
					<span class='normalText'>
					<input type="button" name="Upload File"  id="uploadFileButton" value="Upload File"
					 class='btn btn-sm btn-primary' 
					onClick='uploadExcel(this.form);'>
					
					</span>
				</td>
			</tr> 	
		</table>
		
		</form>
	
	</div>
	
	
		