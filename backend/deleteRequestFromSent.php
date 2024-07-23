<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["requestId"])){
        $requestId = $_POST["requestId"];
        $stmt = $conn->prepare("DELETE FROM parentSentRequest WHERE requestId = ?;");
        $stmt->bind_param("i",$requestId);
        if($stmt->execute()){
            $stmt->close();
            $notStmt = $conn->prepare("DELETE FROM notifications WHERE parentRequestId = ?");
            $notStmt->bind_param("i",$requestId);
            $notStmt->execute();
            echo "Done";
        }
        else {
            echo "Error";
        }
    }
    else{
        echo "Connection Error";
    }
?>