<?php
    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST['postId'])){
        $postId = $_POST['postId'];
        $checkRequestExist = checkIfRequestExists($postId,$conn);
        if($checkRequestExist === true){
            $stmt = $conn->prepare("DELETE FROM teacherpostrequest WHERE postId=?");
            $stmt->bind_param("i",$postId);
            if($stmt->execute()){
                $stmt->close();
                echo "Deleted";
            }
            else {
                echo "Error";
            }
            }
            else {
                echo "No Request";
            }
    }
    else {
        echo "Connection Error";
    }

    function checkIfRequestExists($postId,$conn){
        $requestStmt = $conn->prepare("SELECT postId as post_id from teacherpostrequest where postId=?");
        $requestStmt->bind_param("i",$postId);
        if($requestStmt->execute()){
            $result = $requestStmt->get_result();
            $row = $result->fetch_assoc();
            if($row && $row['post_id'] !== null){
                return true;
            }
            return false;
        }
        else {
            return false ;
        }
    }

    $conn->close();
?>