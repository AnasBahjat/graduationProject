<?php
    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST["teacherEmail"]) && isset ($_POST["matchingId"])){
        $teacherEmail = $_POST["teacherEmail"] ;
        $matchingId = $_POST["matchingId"];

        $isAcceptedBefore = checkIfRequestAccepted($matchingId,$teacherEmail,$conn);
        
        
        if($isAcceptedBefore == 1){
            echo "Request Accepted Before";
        }
        
        else if($isAcceptedBefore == -1) {
            $isAccepted = -1;
            $stmt=$conn->prepare("DELETE FROM teacherSentRequest  WHERE teacherEmail = ? AND matchingId = ? AND isAccepted = ?");
            $stmt->bind_param("sii",$teacherEmail,$matchingId,$isAccepted);
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

    function checkIfRequestAccepted($matchingId,$teacherEmail,$conn){
        $stmt = $conn->prepare("SELECT isAccepted FROM teachersentrequest WHERE matchingId = ? AND teacherEmail = ?");
        $stmt->bind_param("is", $matchingId, $teacherEmail);
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