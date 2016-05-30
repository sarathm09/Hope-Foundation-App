<?php

require_once('configi.php');
header('Content-Type: application/json');


connectDB();

$query = "SELECT Location \"loc\", COUNT(Location) \"count\" from ASSETS WHERE Owner='". $_GET['location'] ."' GROUP BY Location;";
$fetch = mysqli_query($con, $query);
$response1 = [];
while ($results = mysqli_fetch_assoc($fetch))
    $response1[] = $results;

if (count($response1) == 0) {
    $error["error"] = "Invalid location";
    echo json_encode($error);
    exit;
}

$query = "SELECT TimeStamp, COUNT(TID) \"count\", AssetType \"assetType\" from TRANSACTIONS WHERE FromLocation='". $_GET['location'] ."' or ToLocation='". $_GET['location'] ."' GROUP BY TID ORDER BY TimeStamp DESC LIMIT 10;";
$fetch = mysqli_query($con, $query);
$response2 = [];
while ($results = mysqli_fetch_assoc($fetch))
    $response2[] = $results;

if (count($response2) == 0) {
    $error["error"] = "Invalid location";
    echo json_encode($error);
    exit;
}

$query = "SELECT u.BlockedAssets \"req\", COUNT(t.TID) \"total\" FROM USERS u, TRANSACTIONS t WHERE u.Location='". $_GET['location'] ."' and (u.Location=t.ToLocation OR u.Location=t.FromLocation);";
$fetch = mysqli_query($con, $query);
$response3 = [];
while ($results = mysqli_fetch_assoc($fetch))
    $response3[] = $results;

if (count($response3) == 0) {
    $error["error"] = "Invalid location";
    echo json_encode($error);
    exit;
}

$result["count"] = $response1;
$result["trans"] = $response2;
$result["meta"] = $response3;

echo json_encode($result);