<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  
</head>
<body>
		<table >
			<tr>
				<td valign='top'>
				</td>
				<td>					
					<div class="main-text">
						<%
						String installationType = this.getServletContext().getInitParameter("installationType");
						if ((installationType != null) && (installationType.equals("saas"))){
						%>
							<div>
							<jsp:include page="WhyTraceCloud/WhyTraceCloud.html" />
							</div>
						<%	
						}
						%>		
					</div>
				</td>
			</tr>
		</table>
</body>
</html>
