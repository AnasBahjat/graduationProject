<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['parentEmail']) && isset($_POST['teacherEmail'])  && isset($_POST['postId'])){
        $parentEmail = $_POST['parentEmail'];
        $teacherEmail = $_POST['teacherEmail'];
        $postID = $_POST['postId'];
        $checkStmt = $conn->prepare("SELECT requestId  FROM parentSentRequest p WHERE p.teacherEmail = ? AND p.parentEmail = ? AND p.postId= ?;");
        $checkStmt->bind_param("ssi",$teacherEmail,$parentEmail,$postID);
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