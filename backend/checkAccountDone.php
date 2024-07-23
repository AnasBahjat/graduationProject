<?php 
    require_once('req.php');
    $email=$_POST['email'];
    $doneInformationResult = $conn->prepare("SELECT doneInformation FROM profile WHERE email = ?");
    $doneInformationResult->bind_param("s",$email);
    if($doneInformationResult->execute()){
        $checkDone='';
        $doneInformationResult->bind_result($checkDone);
        $doneInformationResult->fetch();
        if($checkDone == '0'){
            echo "Not done";
        }
        else {
            $doneInformationResult->close();
            $getNotificationId = $conn->prepare("SELECT notificationId from notifications WHERE ownerEmail = ? AND notificationType = 1;");
            $getNotificationId->bind_param("s",$email);
            if($getNotificationId->execute()){
                $getNotificationId->bind_result($id);
                $getNotificationId->fetch();
                echo $id;
            }
            else {
                echo "Error";
            }
            $getNotificationId->close();
        }
    }
    else {
        echo "Error";
    }
    $conn->close();
?>