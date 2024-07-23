<?php

    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["requestId"])){
        $requestId = $_POST["requestId"];
        $sql = $conn->prepare("DELETE FROM teacherSentRequest WHERE requestId = ?");
        $sql->bind_param("i",$requestId);
        $sql->execute();
        $sql->close();
        $notSQL = $conn->prepare("DELETE FROM notifications WHERE teacherRequestId = ?");
        $notSQL->bind_param("i",$requestId);
        $notSQL->execute();
    }
    $conn->close();

?>