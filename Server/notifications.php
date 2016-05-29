<?php
require_once('configi.php');
header('Content-Type: application/json');

$query = "";
connectDB();

if (isset($_GET)) {

    if (isset($_GET["count"])) {

        $query = "SELECT COUNT(*) \"count\" FROM Notifications n WHERE n.From='" . $_GET['location'] . "' OR n.To='" . $_GET['location'] . "'";
        $fetch = mysqli_query($con, $query);

        $results = mysqli_fetch_assoc($fetch);

        echo json_encode($results);
        exit(0);

    } else if (isset($_GET["location"])) {
        $query = "SELECT * FROM Notifications n WHERE n.From='". $_GET['location'] ."' OR n.To='". $_GET['location'] ."' ORDER BY n.TimeStamp DESC";

        $fetch = mysqli_query($con, $query);

        $response = [];
        while ($results = mysqli_fetch_assoc($fetch))
            $response[] = $results;

        echo json_encode($response);
        exit(0);
    }
}