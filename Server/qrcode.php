<?php 

for ($i=0; $i<10; $i++) {
	echo $i;
	copy("https://api.qrserver.com/v1/create-qr-code/?data=$i", "$i.jpg");
	echo "https://api.qrserver.com/v1/create-qr-code/?data='$i'";
}

?>