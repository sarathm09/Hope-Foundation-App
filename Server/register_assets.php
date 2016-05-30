<?php

require_once('configi.php');
header('Content-Type: application/json');

# <<< To get JSON BOdy from POST Reuqest 
$POST_JSON = file_get_contents('php://input');
$POST_Body = json_decode($POST_JSON, TRUE);
# To get JSON BOdy from POST Reuqest >>>

/* Request : 
{
	"assetType" : "Projector",
	"category" : "Electronics",
	"specifications" : "Some junk data... description etc.. "
	"owner" : "Whitefield",
	"quantity" : 10,
	"source" : "SAP",
	"status" : "IN_USE" // Or NOT_IN_USE
}
*/

# Params
$assetType = $POST_Body["assetType"];
$category = $POST_Body["category"];
$specifications = $POST_Body["specifications"];
$owner = $POST_Body["owner"];
$location = $POST_Body["owner"];
$quantity = $POST_Body["quantity"];
$source = $POST_Body["source"];
$type = $POST_Body["type"];
$status = $POST_Body["status"];
$depr = $POST_Body["depreciation"];

if (!$status)
    $status = "IN_USE";

if (!$assetType || !$category || !$specifications || !$owner || !$quantity || !$source || !$status) {
    $error["error"] = "Invalid Request";
    echo json_encode($error);
    exit;
}


// Getting last inserted ID
connectDB();

$query = "SELECT ID FROM ASSETS ORDER BY ID DESC LIMIT 1";
$fetch = mysqli_query($con, $query);
$rows = mysqli_fetch_assoc($fetch);
$currentRowsCount = $rows['ID'];


// Getting last inserted transaction ID
$query = "SELECT TID FROM TRANSACTIONS ORDER BY TID DESC LIMIT 1";
$fetch = mysqli_query($con, $query);
$rows = mysqli_fetch_assoc($fetch);
$currentTransactionsCount = $rows['TID'];
$newTransactionID = $currentTransactionsCount + 1;

// Inserting New Items
for ($i = 1; $i <= $quantity; $i++) {
    # Inserting new entry to DB
    $query = "INSERT INTO ASSETS (AssetType, Category, Specifications, Owner, Location, AssetSource, Status, DepreciationValue) VALUES ('$assetType', '$category', '$specifications', '$owner', '$location', '$type','$status', '$depr')";
    mysqli_query($con, $query);

    # >>>>> Generating QR Codes with IDs ($currentRowsCount+1 -> $currentRowsCount+$quantity
    # Copying QR Code to Server's "QRCodes" Folder
    $qr = $currentRowsCount + $i;
    copy("https://api.qrserver.com/v1/create-qr-code/?data=$qr", "QRCodes/$qr.jpg");

    # Inserting into Transaction DB
    $query = "INSERT INTO TRANSACTIONS (TID, AssetID, AssetType, Category, FromLocation, ToLocation) VALUES ('$newTransactionID','$qr', '$assetType', '$category','$source', '$location')";
    mysqli_query($con, $query);
}

disconnectDB();

// Send an email here... 
$email_id = "p.rahulbhargav@gmail.com";

#<<<<<generate email body
$receiver = $email_id;
$subject = "QR Codes for New Assets";
$message =
    '
	<html>
	<head>
		<title> List of QR Codes </title>
		<style type="text/css">
		img {
			margin: 10px;
		}
		</style>
	</head>
	<body>';
for ($i = 1; $i <= $quantity; $i++) {
    $qr = $currentRowsCount + $i;
    $message .= '<img src="c4c.rootone.xyz/QRCodes/' . $qr . '.jpg" alt="' . $qr . '"/>';
}
$message .= '</body>
	</html>
	';

$headers = 'MIME-Version: 1.0' . "\r\n";
$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
#generate email body>>>>>

//<--******************************************************-->

#<<<<<send email
$send_email = mail($receiver, $subject, $message, $headers);
#send email>>>>>


$response["message"] = "New Assets Added to Inventory. QR Codes are generated.";
echo json_encode($response);

?>