<?php

require_once('configi.php');
header('Content-Type: application/json');

# <<< To get JSON BOdy from POST Reuqest 
$POST_JSON = file_get_contents('php://input');
$POST_Body= json_decode( $POST_JSON, TRUE ); 
# To get JSON BOdy from POST Reuqest >>>

/* Request : 
{
	"assetType" : "Projector",
	"category" : "Electronics",
	"location" : "Whitefield",
	"quantity" : 10,
	"source" : "SAP"
}
*/

# Params
$assetType = $POST_Body["assetType"];
$category = $POST_Body["category"];
$location = $POST_Body["location"];
$status = "IN_USE";
$quantity = $POST_Body["quantity"];
$source = $POST_Body["source"];

if(!$assetType || !$category || !$location || !$quantity || !$source) {
	$error["error"] = "Invalid Request";
	echo json_encode($error); exit;
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
for($i = 1; $i <= $quantity; $i++) {
	# Inserting new entry to DB
	$query  =  "INSERT INTO ASSETS (AssetType, Category, Location, Status) VALUES ('$assetType', '$category', '$location', '$status')";
	mysqli_query($con, $query);

	# >>>>> Generating QR Codes with IDs ($currentRowsCount+1 -> $currentRowsCount+$quantity
	# Copying QR Code to Server's "QRCodes" Folder
	$qr = $currentRowsCount + $i;
	copy("https://api.qrserver.com/v1/create-qr-code/?data=$qr", "QRCodes/$qr.jpg");

	# Inserting into Transaction DB 
	$query  =  "INSERT INTO TRANSACTIONS (TID, AssetID, AssetType, Category, FromLocation, ToLocation) VALUES ('$newTransactionID','$qr', '$assetType', '$category','$source', '$location')";
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
	for($i = 1; $i <= $quantity; $i++) {
		$qr = $currentRowsCount + $i;
		$message.='<img src="c4c.rootone.xyz/QRCodes/'.$qr.'.jpg" alt="'.$qr.'"/>';
	}
	$message.='</body>
	</html>
	';

$headers  = 'MIME-Version: 1.0' . "\r\n";
$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
#generate email body>>>>>

//<--******************************************************-->

#<<<<<send email
$send_email = mail($receiver,$subject,$message,$headers);
#send email>>>>>


$response["message"] = "New Assets Added to Inventory. QR Codes are generated.";
echo json_encode($response);

?>