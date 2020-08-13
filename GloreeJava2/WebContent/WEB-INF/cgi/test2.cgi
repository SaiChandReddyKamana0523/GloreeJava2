
#!C:\Perl64\bin\perl -w
#
use lib 'C:\Perl64\lib';
use CGI;

use CQPerlExt;
use Win32::OLE;








## this is what TraceCloud needs
# Assume that the CQID is an input variable and is the value of the unique id in CQ.
my $q = new CGI;
my $CQID = $q->param('CQID');



# Now connect to CQ, make the query call and get me the mastership url.
###########
# CQ Info #
###########
$dbname = "DBD";
$dbsetname = "CQMS.DIEBOLD.DNA"; 


# START CQ STUFF
$CQsession = CQSession::Build(); 
$CQsession->UserLogon($username, $pw, $dbname, $dbsetname); 

$CQWorkspace = $CQsession->GetWorkSpace(); 
$querydef = $CQWorkspace->GetQueryDef("Public Queries/DBD CQ Reports/STS Updates/$product");

# RUN THE QUERY
$resultset = $CQsession->BuildResultSet($querydef); 
$ct = $resultset->ExecuteAndCountRecords(); 
$resultset->MoveNext(); 

my $redirectURL = $resultset->GetColumnValue(0);




	
# lets redirec to the redirector URL.
print $q->redirect($redirectURL);
# end code


