<?php
    require_once "req.php";
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['matchingId'])&&
    isset($_POST['parentEmail']) &&
    isset($_POST['teacherEmail'])&&
    isset($_POST['childId'])){

        $matchingId = $_POST['matchingId'];
        $teacherEmail = $_POST['teacherEmail'];
        $parentEmail = $_POST['parentEmail'];
        $childId = $_POST['childId'];

        $isRequestSentBefore = checkIfRequestSentBefore($matchingId,$teacherEmail,$parentEmail,$conn);

        if($isRequestSentBefore == true){
            echo "Request Sent Before";
        }
        else if($isRequestSentBefore == "ERROR") {
            echo "Error";
        }
        else if($isRequestSentBefore == false) {
            $insertTeacherSentRequest = $conn->prepare("INSERT INTO teacherSentRequest (matchingId,parentEmail,teacherEmail,requestDate) VALUES (?,?,?,DATE_FORMAT(NOW(), '%d-%m-%Y %H:%i'))");
            $insertTeacherSentRequest->bind_param("iss",$matchingId,$parentEmail,$teacherEmail);    
            if($insertTeacherSentRequest->execute()){
                $requestId = $insertTeacherSentRequest->insert_id ;
                $insertTeacherSentRequest->close();
                insertNewNotification($requestId,$parentEmail,$teacherEmail,$conn);
                echo "Done";
            }
            else {
                echo "Error";
            }
        }
    }
    else {
        echo "Connection Error";
    }

    function checkIfRequestSentBefore($matchingId,$teacherEmail,$parentEmail,$conn){
        $checkStmt = $conn->prepare("SELECT requestId  FROM teacherSentRequest t WHERE t.teacherEmail = ? AND t.parentEmail = ? AND matchingId = ?;");
        $checkStmt->bind_param("ssi",$teacherEmail,$parentEmail,$matchingId);
        if($checkStmt->execute()){
            $checkStmt->store_result();  
        if ($checkStmt->num_rows > 0) {
            return true;
        } 
        else {
            return false;
        }
        }
        else {
            return "ERROR";
        }
    }

    function insertNewNotification($requestId,$parentEmail,$teacherEmail,$conn){
        $sql = "SELECT firstname, lastname FROM profile WHERE email = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $teacherEmail);
        $stmt->execute();
        $firstname='';
        $lastname = '';
        $stmt->bind_result($firstname, $lastname);
        $stmt->fetch();
        $stmt->close();
    
        $firstname = ucfirst($firstname);
        $lastname = ucfirst($lastname);
    
        $teacherName = $firstname . " " . $lastname;

        $notTitle = "New Request";
        $notBody = "$teacherName sent you a request..Click to show received requests ";
        $notType = 3;

        $insertNot = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody,teacherRequestId) VALUES(?,?,?,?,?)");
        $insertNot->bind_param("sissi",$parentEmail,$notType,$notTitle,$notBody,$requestId);
        $insertNot->execute();
        return ;
    }

    $conn->close();
?>