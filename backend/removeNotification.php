<?php
    require_once 'req.php';
    $id = $_POST['notId'];
    $sql = $conn->prepare("DELETE FROM notifications WHERE notificationId = ?");
    $sql->bind_param("i",$id);
    $sql->execute();
    $conn->close();
?>