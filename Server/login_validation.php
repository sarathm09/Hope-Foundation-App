<?php

require_once('configi.php');
header('Content-Type: application/json');

# <<< To get JSON BOdy from POST Reuqest 
$POST_JSON = file_get_contents('php://input');
$POST_Body= json_decode( $POST_JSON, TRUE ); 
# To get JSON BOdy from POST Reuqest >>>

$username = $POST_Body["username"]; 
$password = $POST_Body["password"];

if(!$username || !$password) {
	$error["error"] = "Invalid Credentials";
	echo json_encode($error); exit;
}

connectDB();

if(!$con) {
	$error["error"] = "Connection Failed";
	echo json_encode($error); exit;
}

// // prepare a statement
// $stmt = $con->prepare('SELECT UserName, FROM USERS where UserName = ? and Password = ?');
// $stmt->bind_param('ss',$username, $password);

// $stmt->execute();

// while ($row = $stmt->fetch()) {
//     $results[] = $row;
// }

// var_dump($results);

$query = "SELECT UserName, Location FROM USERS where UserName = '$username' and Password = '$password'";
$fetch = mysqli_query($con, $query);
while($results = mysqli_fetch_assoc($fetch)) 
	$response[] =  $results;

if(!$response || count($response) != 1) {
	$error["error"] = "Invalid Credentials";
	echo json_encode($error); exit;
}
else 
	echo json_encode($response);

?>