<?php

require_once('configi.php');

connectDB();

$query = "SELECT * FROM USERS";
$fetch = mysqli_query($con, $query);

if(!$fetch) {
	echo "error";
	exit;
}

$response = array();

while($results = mysqli_fetch_assoc($fetch)) 
{
	echo "\n";
	$response[] =  $results;
}

// echo "Hello";
echo json_encode($response);

?>