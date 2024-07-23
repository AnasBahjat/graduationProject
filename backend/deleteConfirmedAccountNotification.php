<?php
require_once 'req.php';
$notificationId = $_POST['notificationId'];
$deleteStmt = $conn->prepare("DELETE FROM notifications where notificationId = ? ;");
$deleteStmt->bind_param("i",$notificationId);
if(!$deleteStmt->execute()){
   echo 'Error'; 
}
$deleteStmt->close();
$conn->close();
?>