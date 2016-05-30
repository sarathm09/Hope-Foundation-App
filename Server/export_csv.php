<?php 

require_once('configi.php');

$location = $_GET["location"];

//Output headers to make file downloadable
header('Content-Type: text/csv; charset=utf-8');
header('Content-Disposition: attachment; filename=spreadsheet.csv');

//Write the output to  buffer
$data = fopen('php://output', 'w');

//Output Column Headings
fputcsv($data,array('TID','AssetID', 'AssetType', 'Category', 'FromLocation', 'ToLocation', 'TimeStamp', 'Current Location', 'Status', 'Depreciation Value', 'Owner'));

//Retrieve the data from database
connectDB();
$query = "SELECT t.*, a.Location, a.Status, a.DepreciationValue, a.Owner FROM TRANSACTIONS t, ASSETS a WHERE t.FromLocation = '$location' OR t.ToLocation = '$location' and a.ID=t.AssetID ORDER BY TimeStamp DESC";
$rows = mysqli_query($con, $query);

//Loop through the data to store them inside CSV
while($row = mysqli_fetch_assoc($rows)){
	fputcsv($data, $row);
}

?>