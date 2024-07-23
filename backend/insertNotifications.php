<?php
    require_once('req.php');
    main($conn);

    function main($conn){
        $ownerEmail = $_POST['ownerEmail'];
        $notificationType = $_POST['notificationType'];
        $notificationTitle = $_POST['notificationTitle'];
        $notificationBody = $_POST['notificationBody'];
        $isRead = 0 ;
        insertNewNotification($ownerEmail,$notificationType,$notificationTitle,$notificationBody,$isRead,$conn);
    }

    function insertNewNotification($ownerEmail,$notificationType,$notificationTitle,$notificationBody,$isRead,$conn){
        $insertNotificationStmt = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody,isRead)values (?,?,?,?,?);");
        $insertNotificationStmt->bind_param("sssss",$ownerEmail,$notificationType,$notificationTitle,$notificationBody,$isRead);
        if($insertNotificationStmt->execute()){
            echo "Insertion Done";
        }
        else {
            echo "Insertion Error";
        }
    }

?>