<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$location = $_GET["location"];

if(!$location) {
	$error["error"] = "Invalid Request";
	echo json_encode($error); exit;
}

$query  =  "SELECT * FROM TRANSACTIONS WHERE FromLocation = '$location' OR ToLocation = '$location' ORDER BY TimeStamp DESC LIMIT 20";

connectDB();
$fetch = mysqli_query($con, $query);
$response = [];
while($results = mysqli_fetch_assoc($fetch)) 
	$response[] =  $results;

echo json_encode($response);

?>