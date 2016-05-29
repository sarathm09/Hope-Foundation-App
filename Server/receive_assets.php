<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$assetID = $_GET["id"];
$location = $_GET["location"];

if(!$location || !$assetID) {
	$error["status"] = "Invalid Request";
	echo json_encode($error); exit;
}

connectDB();

// Getting data of Asset from ID
$query  =  "SELECT * FROM ASSETS WHERE ID = '$assetID'";
$result = mysqli_query($con, $query);
$row = mysqli_fetch_assoc($result);
$assetID = $row["ID"];
$assetType = $row["AssetType"];
$category = $row["Category"];
$fromLocation = $row["Location"];
// Getting data of Asset from ID

// Getting last inserted transaction ID
$query = "SELECT TID FROM TRANSACTIONS ORDER BY TID DESC LIMIT 1";
$fetch = mysqli_query($con, $query);
$rows = mysqli_fetch_assoc($fetch);
$currentTransactionsCount = $rows['TID'];
$newTransactionID = $currentTransactionsCount + 1;
// Getting last inserted transaction ID

// Updating Assets Table
$query  =   "UPDATE ASSETS SET Location = '$location', Status = 'IN_USE' WHERE ID = '$assetID'";


$result = mysqli_query($con, $query);
if(!$result) {
	disconnectDB();
	$error["status"] = "Error";
	echo json_encode($error); exit;
}
else {
	$query  =  "INSERT INTO TRANSACTIONS (TID, AssetID, AssetType, Category, FromLocation, ToLocation) VALUES ('$newTransactionID','$assetID', '$assetType', '$category','$fromLocation', '$location')";
	mysqli_query($con, $query);
	disconnectDB();
	$response["status"] = "success";
	$response["AssetType"] = $assetType;
	echo json_encode($response);
}


?>