<?php
    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST["parentEmail"]) && isset ($_POST["postId"])){
        $parentEmail = $_POST["parentEmail"] ;
        $postId = $_POST["postId"];

        $isAcceptedBefore = checkIfRequestAccepted($postId,$parentEmail,$conn);
        
        
        if($isAcceptedBefore == 1){
            echo "Request Accepted Before";
        }
        
        else if($isAcceptedBefore == -1) {
            $isAccepted = -1;
            $stmt=$conn->prepare("DELETE FROM parentSentRequest  WHERE parentEmail = ? AND postId = ? AND isAccepted = ?");
            $stmt->bind_param("sii",$parentEmail,$postId,$isAccepted);
            if($stmt->execute()){
                $stmt->close();
                echo "Done";
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

    function checkIfRequestAccepted($postId,$parentEmail,$conn){
        $stmt = $conn->prepare("SELECT isAccepted FROM parentSentRequest WHERE postId = ? AND parentEmail = ?");
        $stmt->bind_param("is", $postId, $parentEmail);
        $stmt->execute();
        $stmt->store_result();

        $isAccepted = -2 ;
        if ($stmt->num_rows > 0) {
            $stmt->bind_result($isAccepted);
            $stmt->fetch();
            if($isAccepted == 1){
                return 1 ;
            }
            else if($isAccepted == -1) {
                return -1;
            }
            else {
                return -2 ;
            }
        } 
        else {
            return -2 ;
        }
    
    $stmt->close();
    }
    $conn->close();
?>