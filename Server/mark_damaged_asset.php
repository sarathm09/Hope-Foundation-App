<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$assetID = $_GET["id"];

if(!$assetID) {
	$error["error"] = "Invalid AssetID";
	$error["status"] = "Invalid AssetID";
	echo json_encode($error); exit;
}

$query  =   "UPDATE ASSETS SET Status = 'DAMAGED' WHERE ID = '$assetID'";

connectDB();
$result = mysqli_query($con, $query);
if(!$result) {
	$error["error"] = "Error changing the status of Asset to Damaged";
	$error["status"] = "Error changing the status of Asset to Damaged";
	echo json_encode($error); exit;
}
else {
	$response["status"] = "success";
	$response["message"] = "Asset Marked as Damanged";
	echo json_encode($response);
}

?>