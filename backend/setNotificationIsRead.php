<?php
    require_once 'req.php';
    $notificationId = $_POST['id'];
    $isRead = 1 ;
    $stmt = $conn->prepare("UPDATE notifications set isRead = ? WHERE notificationId = ?;");
    $stmt->bind_param("is",$isRead,$notificationId);
    $stmt->execute();
?>