<?php

require_once('configi.php');

header('Content-Type: application/json');
# <<< To get JSON BOdy from POST Reuqest 
$POST_JSON = file_get_contents('php://input');
$POST_Body= json_decode( $POST_JSON, TRUE ); 
# To get JSON BOdy from POST Reuqest >>>


$assetType = $POST_Body["assetType"];
$category = $POST_Body["category"];
$from = $POST_Body["from"];
$to = $POST_Body["to"];
$quantity = $POST_Body["quantity"];


$query  =   "UPDATE USERS SET BlockedAssets = (BlockedAssets + '$quantity') WHERE Location='$to'";

connectDB();
$result = mysqli_query($con, $query);
if(!$result) {
	$error["error"] = "Error requesting items";
	echo json_encode($error); exit;
}

$query = "INSERT INTO Notifications (Notifications.From, Notifications.To, Notifications.Message, Notifications.Title) VALUES('$from' , '$to', $from.' has requested '. $quantity.' '.$assetType.'(s). Please ship the required items.', 'Asset Requested')";
$result = mysqli_query($con, $query);
if(!$result) {
	$error["error"] = "Error requesting items";
	echo json_encode($error); exit;
}

?> 