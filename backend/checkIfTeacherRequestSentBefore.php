<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['teacherEmail'])&& isset($_POST['parentEmail']) && isset($_POST['matchingId'])){
        $teacherEmail = $_POST['teacherEmail'];
        $parentEmail = $_POST['parentEmail'];
        $matchingId = $_POST['matchingId'];

        $checkStmt = $conn->prepare("SELECT requestId  FROM teacherSentRequest t WHERE t.teacherEmail = ? AND t.parentEmail = ? AND matchingId = ?;");
        $checkStmt->bind_param("ssi",$teacherEmail,$parentEmail,$matchingId);
        if($checkStmt->execute()){
            $checkStmt->store_result();
            if($checkStmt->num_rows() > 0){
                echo "Exists";
            }
            else {
                echo "Not Exist";
            }
        }
        else {
            echo "Error";
        }
    }
    else {
        echo "Connection Error";
    }
    $conn->close();
?>