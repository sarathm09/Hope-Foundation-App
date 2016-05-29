<?php 

require_once('configi.php');

$location = $_GET["location"];

//Output headers to make file downloadable
header('Content-Type: text/csv; charset=utf-8');
header('Content-Disposition: attachment; filename=spreadsheet.csv');

//Write the output to  buffer
$data = fopen('php://output', 'w');

//Output Column Headings
fputcsv($data,array('TID','AssetID', 'AssetType', 'Category', 'FromLocation', 'ToLocation', 'TimeStamp'));

//Retrieve the data from database
connectDB();
$query = "SELECT * FROM TRANSACTIONS WHERE FromLocation = '$location' OR ToLocation = '$location' ORDER BY TimeStamp DESC LIMIT 20";
$rows = mysqli_query($con, $query);

//Loop through the data to store them inside CSV
while($row = mysqli_fetch_assoc($rows)){
	fputcsv($data, $row);
}

?>