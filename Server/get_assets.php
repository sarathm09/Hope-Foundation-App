<?php

require_once('configi.php');
header('Content-Type: application/json');

# Params -> location
$location = $_GET["location"];

if (!$location) {
    $error["error"] = "Invalid Request";
    echo json_encode($error);
    exit;
}

//$query  =   "SELECT  AssetType, Category, COUNT(*) Count, sum(case when Status = 'IN_USE' then 1 else 0 end) InUseCount, sum(case when Status = 'DAMAGED' then 1 else 0 end) DamagedCount, sum(case when Status = 'NOT_IN_USE' then 1 else 0 end) UnusedCount FROM ASSETS, AssetTypeDetails WHERE Location = '$location' GROUP BY AssetType, Category,Location";
$query = "SELECT  ASSETS.AssetType, ASSETS.Category, ImageUrl, COUNT(*) Count, sum(case when Status = 'IN_USE' then 1 else 0 end) InUseCount, sum(case when Status = 'DAMAGED' then 1 else 0 end) DamagedCount, sum(case when Status = 'NOT_IN_USE' then 1 else 0 end) UnusedCount FROM ASSETS, AssetTypeDetails WHERE Location = '" . $location . "' and ASSETS.AssetType=AssetTypeDetails.AssetType and ASSETS.AssetSource='INTERNAL' GROUP BY AssetType, Category, Location;";

connectDB();
$fetch = mysqli_query($con, $query);

$response = [];
while ($results = mysqli_fetch_assoc($fetch))
    $response[] = $results;

echo json_encode($response);

?>