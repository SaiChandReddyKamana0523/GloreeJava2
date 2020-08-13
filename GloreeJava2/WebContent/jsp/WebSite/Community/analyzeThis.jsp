<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml" lang="en"><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

	
	<title>TraceCloud - SAAS Agile Scrum Requirements Management - Collaborate, Define, Manage and Deliver your Customer Requirements</title>
    <meta name="description" content="Collaboration tools to define, manage and deliver your customer requirements on time and within budget. Significantly improves customer satisfaction ">
	<meta name="keywords" content="free requirements management, saas requirements management tool, online requirements management, doors, requisitepro, customer requirements, shared requirements, tl9000, project management, project requirements, agile, agile requirements management.">


	
	<script src="/GloreeJava2/js/oPExplorer.js?v=20200630"></script>

	
	
	<!--  Bootstratp  JS and CSS files -->
	 <script src="/GloreeJava2/js/jquery-3.1.1.min.js"></script>
	 <script src="/GloreeJava2/js/bootstrap.min.js"></script>
	 <link href="/GloreeJava2/css/bootstrap.min.css" rel="stylesheet" media="screen">
	
	
	
	
	
	<!--  Google Analytics Tracking  -->	
	<script type="text/javascript">
	
	  var _gaq = _gaq || [];
	  _gaq.push(['_setAccount', 'UA-31449327-1']);
	  _gaq.push(['_trackPageview']);
	
	  (function() {
	    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
	  })();
	
	</script>
	
	
		
</head>
<body >


<%

	String word = request.getParameter("word");
	String originalTextToAnalyze = request.getParameter("textToAnalyze");

	String textToAnalyze = originalTextToAnalyze.replace("\n", "<br>");
	textToAnalyze = textToAnalyze.replace("%0A", "<br>");
	
	String textAfterReplace = textToAnalyze.replace(word, "<font color='red' size='12' >" + word + "</font>");

	%>
	
	
	
	<%
	
	
	
	String[] words = textToAnalyze.split(" ");
	
	
	// lets split this guy in to size
	int partLength  = 3000;
	int len = textToAnalyze.length();

    // Number of parts
    int nparts = (len + partLength - 1) / partLength;
    String parts[] = new String[nparts];

    // Break into parts
    int offset= 0;
    int i = 0;
    while (i < nparts)
    {
        parts[i] = textToAnalyze.substring(offset, Math.min(offset + partLength, len));
        offset += partLength;
        i++;
    }

    for (i= 0 ; i < parts.length; i++ ){
    	
    	String part = parts[i];

    	part = part.replace("\n", "<br>");
    	part = part.replace("%0A", "<br>");
    	
    	part = part.replace(word, "<font color='red' size='12' ><b>" + word + "</b></font>");
    	%>
    		<div class='alert alert-danger' style='width:1000px'>
    		<br><br><br>
    		<%=part %>
    		<br><br><br>
    		</div>
    		
    	<%
    }

	
	%>
	
	
	
	
					
	

	


</body></html>