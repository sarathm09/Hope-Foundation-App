<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params
$assetID = $_GET["id"];

if (!$assetID) {
    $error["error"] = "Invalid Request";
    echo json_encode($error);
    exit;
}

$query = "SELECT a.*, at.* FROM ASSETS a, AssetTypeDetails at WHERE a.ID = '$assetID' and a.AssetType = at.AssetType";

connectDB();
$fetch = mysqli_query($con, $query);
$response = [];
while ($results = mysqli_fetch_assoc($fetch))
    $response[] = $results;

if (count($response) == 0) {
    $error["error"] = "Invalid Asset ID";
    echo json_encode($error);
    exit;
}

echo json_encode($response);

?>