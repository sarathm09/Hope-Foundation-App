<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$location = $_GET["location"];

if(!$location) {
	$error["error"] = "Invalid Request";
	echo json_encode($error); exit;
}
if($_REQUEST["type"] == "internal"){
	$query  =  "SELECT t.* FROM TRANSACTIONS t, ASSETS a WHERE (FromLocation = '$location' OR ToLocation = '$location') AND t.AssetID = a.ID AND a.AssetSource='INTERNAL' ORDER BY TimeStamp DESC LIMIT 20";

	connectDB();
	$fetch = mysqli_query($con, $query);
	$response = [];
	while($results = mysqli_fetch_assoc($fetch))
		$response[] =  $results;

	echo json_encode($response);
}else{
	$query  =  "SELECT t.* FROM TRANSACTIONS t, ASSETS a WHERE (FromLocation = '$location' OR ToLocation = '$location') AND t.AssetID = a.ID AND a.AssetSource='EXTERNAL' ORDER BY TimeStamp DESC LIMIT 20";

	connectDB();
	$fetch = mysqli_query($con, $query);
	$response = [];
	while($results = mysqli_fetch_assoc($fetch))
		$response[] =  $results;

	echo json_encode($response);
}
?>