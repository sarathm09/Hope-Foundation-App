<?php


require_once('configi.php');
header('Content-Type: application/json');


connectDB();

$query = "select lat, lng, Location FROM USERS";
$fetch = mysqli_query($con, $query);
$response1 = [];
while ($results = mysqli_fetch_assoc($fetch))
    $response1[] = $results;

echo json_encode($response1);