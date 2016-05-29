<?php

if($action == TRUE)
{
	$con = mysqli_connect($db_host,$db_user_name,$db_password);
	mysqli_select_db($con,$db_name);
} 
else 
{
	mysqli_close($con);
}

?>