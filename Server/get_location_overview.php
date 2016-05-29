<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$location = $_GET["location"];

if(!$location) {
	$error["error"] = "Invalid Request";
	echo json_encode($error); exit;
}

$query  =   "SELECT SUM(1) AS TotalCount, SUM(IF(Status = 'IN_USE', 1, 0)) AS InUse, SUM(IF(Status = 'NOT_IN_USE', 1, 0)) AS NotInUse, SUM(IF(Status = 'DAMAGED', 1, 0)) AS Damaged, SUM(IF(Status = 'SHIPPING', 1, 0)) AS Shipping FROM ASSETS WHERE Location = '$location'";

connectDB();
$fetch = mysqli_query($con, $query);

$response = [];
while($results = mysqli_fetch_assoc($fetch)) 
	$response[] =  $results;

echo json_encode($response);

?>