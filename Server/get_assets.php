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
connectDB();

if($_REQUEST["filter"] == "transferred"){
    $query = "SELECT  ASSETS.AssetType, ASSETS.Category, ImageUrl, COUNT(*) Count, sum(case when Status = 'IN_USE' then 1 else 0 end) InUseCount, sum(case when Status = 'DAMAGED' then 1 else 0 end) DamagedCount, sum(case when Status = 'NOT_IN_USE' then 1 else 0 end) UnusedCount FROM ASSETS, AssetTypeDetails WHERE Location = '" . $location . "' and ASSETS.AssetType=AssetTypeDetails.AssetType and ASSETS.Location!=ASSETS.Owner GROUP BY AssetType, Category, Location;";

    $fetch = mysqli_query($con, $query);

    $response1 = [];
    while ($results = mysqli_fetch_assoc($fetch))
        $response1[] = $results;

    echo json_encode($response1);
}else{
    $query = "SELECT  ASSETS.AssetType, ASSETS.Category, ImageUrl, COUNT(*) Count, sum(case when Status = 'IN_USE' then 1 else 0 end) InUseCount, sum(case when Status = 'DAMAGED' then 1 else 0 end) DamagedCount, sum(case when Status = 'NOT_IN_USE' then 1 else 0 end) UnusedCount FROM ASSETS, AssetTypeDetails WHERE Location = '" . $location . "' and ASSETS.AssetType=AssetTypeDetails.AssetType and ASSETS.AssetSource='EXTERNAL' GROUP BY AssetType, Category, Location;";

    $fetch = mysqli_query($con, $query);

    $response2 = [];
    while ($results = mysqli_fetch_assoc($fetch))
        $response2[] = $results;
    echo json_encode($response2);
}

?>