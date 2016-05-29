<?php

require_once('configi.php');
header('Content-Type: application/json');

// Send an email here... 
$email_id = "p.rahulbhargav@gmail.com";

#<<<<<generate email body
$receiver = $email_id;
$subject = "QR Codes for New Assets";
$message =
	'
	<html>
	<head>
		<title> List of QR Codes </title>
		<style type="text/css">
		img {
			margin: 10px;
		}
		</style>
	</head>
	<body>';
	$quantity = 3;
	$currentRowsCount = 100;
	for($i = 1; $i <= $quantity; $i++) {
		$qr = $currentRowsCount + $i;
		$message.='<img src="c4c.rootone.xyz/QRCodes/'.$qr.'.jpg" alt="'.$qr.'"/>';
	}
	$message.='</body>
	</html>
	';

echo $message;
$headers  = 'MIME-Version: 1.0' . "\r\n";
$headers .= 'Content-type: text/html; charset=iso-8859-1' . "\r\n";
#generate email body>>>>>

//<--******************************************************-->

#<<<<<send email
$send_email = mail($receiver,$subject,$message,$headers);
#send email>>>>>

?>