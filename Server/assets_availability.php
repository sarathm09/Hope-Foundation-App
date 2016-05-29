<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$location = $_GET["location"];
$assetType = $_GET["assetType"];
$status = "NOT_IN_USE";

if(!$location || !$assetType) {
	$error["error"] = "Invalid Request";
	echo json_encode($error); exit;
}

$query  =   "SELECT Location, COUNT(*) AvailableCount  FROM ASSETS WHERE Location != '$location' AND AssetType='$assetType' AND Status = '$status' GROUP BY AssetType, Location";

connectDB();
$fetch = mysqli_query($con, $query);

$response = [];
while($results = mysqli_fetch_assoc($fetch)) 
	$response[] =  $results;

echo json_encode($response);

?>