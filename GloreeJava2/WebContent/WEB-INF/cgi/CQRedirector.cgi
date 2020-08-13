#!E:\Program Files (x86)\IBM\RationalSDLC\common\perl -w

#print "Content-type:text/html\n\n";

use lib 'E:\Program Files (x86)\IBM\RationalSDLC\common\lib\perl5\5.8.6';
use CGI;


#print "<br> CGI loaded ";

use lib 'E:\Program Files (x86)\IBM\RationalSDLC\common\lib\perl5\site_perl\5.8.6';
use CQPerlExt;


#print "<br> CQPERLExt loaded ";
use lib 'E:\Program Files (x86)\IBM\RationalSDLC\common\lib\perl5\site_perl\5.8.6\MSWin32-x86-multi-thread';
use OLE;





#print "<br> OLE loaded ";
my $q = new CGI;
my $cQID = $q->param('CQID');
my $cQType = $q->param('CQTYPE');


#print "<br> CQID is $cQID" ;
#print "<br> CQTYPE is $cQType" ;

# Now connect to CQ, make the query call and get me the mastership url.
###########
# CQ Info #
###########
$dbname = "DBD";
$dbsetname = "CQMS.DIEBOLD.DNA"; 


# START CQ STUFF
$CQsession = CQSession::Build(); 


#print "<br> Got session ";
$CQsession->UserLogon('TCAdmin','blahblah',$dbname,$dbsetname); 


#print "<br> Logged in as TCAdmin ";
$CQWorkspace = $CQsession->GetWorkSpace();


#print "<br> Got workspace ";
my $querydef ;
if ($cQType eq "SCR"){
	$querydef = $CQWorkspace->GetQueryDef("Public Queries/TraceCloud Queries/SCR- By ID");
	
#print "<br> Got querydef for Test Result ";
}

if ($cQType eq "Test Result"){
	$querydef = $CQWorkspace->GetQueryDef("Public Queries/TraceCloud Queries/Configured Test Cases - By ID");
	
#print "<br> Got querydef for SCR";
}
	
# RUN THE QUERY
my $operator = $querydef->BuildFilterOperator($CQPerlExt::CQ_BOOL_OP_AND);

#print "<br> Got filter operator";
$operator->BuildFilter("id", $CQPerlExt::CQ_COMP_OP_EQ,["$cQID"]);

#print "<br> Set filter operator for cQID to  $cQID";
$resultset = $CQsession->BuildResultSet($querydef);


#print "<br> Got result set"; 
$ct = $resultset->ExecuteAndCountRecords();


#print "<br> Count of result set is  $ct" ; 
$resultset->MoveNext(); 

my $redirectURL = $resultset->GetColumnValue(6);

#print "<br> redirectURL is  $redirectURL";

print $q->redirect($redirectURL);