<?php
    require_once 'req.php';
    $email=$_POST['email'];
    $updateStatment = $conn->prepare('update profile set signedIn = 0 where email=?;');
    $updateStatment->bind_param('s',$email);
    if($updateStatment->execute()){
        echo 'done';
    }

    $conn->close();
    $updateStatment->close();
?>