<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$assetID = $_GET["id"];

if(!$assetID) {
	$error["error"] = "Invalid AssetID";
	$response["status"] = "error";
	echo json_encode($error); exit;
}

$query  =   "UPDATE ASSETS SET Status = 'NOT_IN_USE' WHERE ID = '$assetID'";

connectDB();
$result = mysqli_query($con, $query);
if(!$result) {
	$error["error"] = "Error changing the status of Asset to Damaged";
	$response["status"] = "error";
	echo json_encode($error); exit;
}
else {
	$query  =  "SELECT AssetType FROM ASSETS WHERE ID = '$assetID'";
	$result = mysqli_query($con, $query);
	$row = mysqli_fetch_assoc($result);
	$assetType = $row["AssetType"];

	$response["AssetType"] = $assetType;
	$response["status"] = "success";
	echo json_encode($response);
}

?>