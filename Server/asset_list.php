<?php

require_once('configi.php');
header('Content-Type: application/json');


connectDB();

$query = "SELECT ID, AssetType from ASSETS WHERE Owner='".$_GET['location']."'";

$fetch = mysqli_query($con, $query);
$response = [];
while($results = mysqli_fetch_assoc($fetch))
    $response[] =  $results;

echo json_encode($response);