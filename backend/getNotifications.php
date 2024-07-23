<?php
    require_once 'req.php';
    $email = $_POST['email'];
    $isRead = 0 ;
    $getNotificationsStmt = $conn->prepare("SELECT * FROM notifications where ownerEmail = ? AND notificationType != ? AND notificationType != ?;");
    $notificationType1 = 1 ;
    $notificationType0 = 0 ;
    $getNotificationsStmt->bind_param("sss",$email,$notificationType1,$notificationType0);
    if($getNotificationsStmt->execute()){
        $resultOfSQL = $getNotificationsStmt->get_result()->fetch_all(MYSQLI_ASSOC);
        echo json_encode($resultOfSQL);
    }
    else{
        echo "Error";
    }
?>