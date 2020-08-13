<%

	int total = 0;

	int draft = 0;
	int pending = 0;
	int rejected = 0;
	int approved = 0;
	
	int dangling = 0;
	int orphan = 0;
	
	int suspectUp = 0;
	int suspectDown = 0;

	int testPending = 0;
	int testPass = 0;
	int testFail = 0;
	
	int completed = 0;
	int incomplete = 0;
	
	
	try {
		 total = Integer.parseInt(request.getParameter("total"));

			 
		 draft = Integer.parseInt(request.getParameter("draft"));
		 pending = Integer.parseInt(request.getParameter("pending"));
		 rejected = Integer.parseInt(request.getParameter("rejected"));
		 approved = Integer.parseInt(request.getParameter("approved"));
		
		 dangling = Integer.parseInt(request.getParameter("dangling"));
		 orphan = Integer.parseInt(request.getParameter("orphan"));
		
		 suspectUp = Integer.parseInt(request.getParameter("suspectUp"));
		 suspectDown = Integer.parseInt(request.getParameter("suspectDown"));

		 testPending = Integer.parseInt(request.getParameter("testPending"));
		 testPass = Integer.parseInt(request.getParameter("testPass"));
		 testFail = Integer.parseInt(request.getParameter("testFail"));
		
		 completed = Integer.parseInt(request.getParameter("completed"));
		 incomplete = Integer.parseInt(request.getParameter("incomplete"));
	}
	catch (Exception e){
	}
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title>
      Google Visualization API Sample
    </title>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>


    <script type="text/javascript">
      google.load('visualization', '1', {packages: ['corechart']});
    </script>
    <script type="text/javascript">
      function drawVisualization() {
        // Some raw data (not necessarily accurate)
        <%
        String chartData = "";

        	chartData = " ['Status','Objects', 'Total'], " +
			" ['Draft', " + draft + " ,  "+ total +  "]," + 
        	" ['Pending', " + pending + " ,  "+ total +  "]," +
			" ['Approved', " + approved + " ,  "+ total +  "]," +
			" ['Rejected', " + rejected + " ,  "+ total +  "]," +
			" ['Dangling', " + dangling + " ,  "+ total +  "]," +
			" ['Orphan', " + orphan + " ,  "+ total +  "]," + 
        	" ['SuspectUp', " + suspectUp + " ,  "+ total +  "]," +
			" ['SuspectDown', " + suspectDown + " ,  "+ total +  "]," +
			" ['Test Pending', " + testPending + " ,  "+ total +  "]," +
			" ['Test Pass', " + testPass + " ,  "+ total +  "]," +
			" ['Test Fail', " + testFail + " ,  "+ total +  "]," + 
        	" ['Complete', " + completed + " ,  "+ total +  "]," +
			" ['Incomplete', " + incomplete + " ,  "+ total +  "]";
        %>
        var data = google.visualization.arrayToDataTable([
                <%=chartData%> ]);

        var options = {
          vAxis: {title: "No of Objects"},
          seriesType: "bars",
          series: {1: {type: "line"}}
        };

        var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
      google.setOnLoadCallback(drawVisualization);
    </script>
  </head>
  <body>
    <div id="chart_div" style="width: 980px; height: 180px;"></div>
  </body>
</html>