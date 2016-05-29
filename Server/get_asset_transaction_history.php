<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params 
$assetID = $_GET["id"];

if(!$assetID) {
	$error["error"] = "Invalid Request";
	echo json_encode($error); exit;
}

$query  =  "SELECT * FROM TRANSACTIONS WHERE AssetID = '$assetID' ORDER BY TID DESC LIMIT 20";

connectDB();
$fetch = mysqli_query($con, $query);
$response = [];
while($results = mysqli_fetch_assoc($fetch)) 
	$response[] =  $results;

echo json_encode($response);
disconnectDB();
?>