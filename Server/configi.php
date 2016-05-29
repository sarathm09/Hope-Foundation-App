<?php

# <<<<<Database Details
$db_host = "localhost";
$db_user_name = "sarathm09";
$db_password = "rset123";
$db_name = "sarathm0_c4c";
# Database Details >>>>>

#<<<<<set timezone
date_default_timezone_set('Asia/Calcutta');
#set timezone>>>>>

#<<<<<Connect to DB
function connectDB()
{
	global $con,$db_name,$db_password,$db_host,$db_user_name;
	$action=TRUE; include('connecti.php');
}
//Connect to DB>>>>>

# <<<<< Disconnect from DB
function disconnectDB()
{
	global $con,$db_name,$db_password,$db_host,$db_user_name;
	$action=FALSE; include('connecti.php');
}
# Disconnect from DB >>>>>

?>