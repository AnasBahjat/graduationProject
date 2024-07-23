<?php
    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST["postId"])){
        $matchingId = $_POST['postId'];
        $checkIfExists = checkIfParentPostExists($matchingId,$conn);
        if($checkIfExists){
            $deleteStmt = $conn->prepare("DELETE FROM teacherMatching WHERE matchingId = ?");
            $deleteStmt->bind_param("i",$matchingId);
            if($deleteStmt->execute()){
                $deleteStmt->close();
                echo  "Done";
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

    function checkIfParentPostExists($postId,$conn){
        $stmt=$conn->prepare("SELECT matchingId as mid FROM teachermatching where matchingId=?");
        $stmt->bind_param("i",$postId);
        if($stmt->execute()){
            $result = $stmt->get_result();
            $row = $result->fetch_assoc();
            $stmt->close();
            if($row && $row['mid'] !== null){
                return true;
            }
            return false;
        }
            return false ;
    }
    $conn->close();
?> 