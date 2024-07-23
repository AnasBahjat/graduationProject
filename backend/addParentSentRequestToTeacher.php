<?php


    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD'] == "POST" &&
     isset($_POST['teacherPostRequestId']) &&
     isset($_POST['parentEmail']) &&
     isset($_POST['teacherEmail']) &&
     isset($_POST['childrenIds'])){
        $teacherPostId = $_POST['teacherPostRequestId'];
        $parentEmail = $_POST['parentEmail'];
        $teacherEmail = $_POST['teacherEmail'];
        $idsArray = json_decode($_POST['childrenIds']);
    
        $insertToParentSentRequestStmt = $conn->prepare("INSERT INTO parentsentrequest (postId,parentEmail,teacherEmail,requestDate) VALUES (?,?,?,DATE_FORMAT(NOW(), '%d-%m-%Y %H:%i'));");
        $insertToParentSentRequestStmt->bind_param("iss",$teacherPostId,$parentEmail,$teacherEmail);
        $existingIds = [];

        if($insertToParentSentRequestStmt->execute()){
            $requestId = $insertToParentSentRequestStmt->insert_id ;
            $insertToParentSentRequestStmt->close();
            $insertChild = $conn->prepare("INSERT INTO childrenrequests(requestId,childId) VALUES(?,?);");
            foreach($idsArray as $id){
                $isChildAdded = checkIfChildAddedToRequest($requestId,$id,$conn);
                if($isChildAdded === false){
                    $insertChild->bind_param("ii",$requestId,$id);
                    $insertChild->execute();
                }
                else {
                    array_push($existingIds,$isChildAdded);
                }
            }
            $insertChild->close();
            insertNewNotification($teacherPostId,$requestId,$parentEmail,$teacherEmail,$conn);
            if(empty($existingIds)){
                echo "Done";
            }
            else {
                echo json_encode($existingIds);
            }
        }
        else {
         echo "Error";
        }
     }


     else {
         $conn->close();
        echo "Connection Error";
     }

     function checkIfChildAddedToRequest($requestId,$id,$conn){
        $checkIfChildExists = $conn->prepare("SELECT * FROM childrenRequests WHERE requestId = ? AND childId = ?;");
        $checkIfChildExists->bind_param("ii",$requestId,$id);
        if($checkIfChildExists->execute()){
            $childData = $checkIfChildExists->get_result()->fetch_all(MYSQLI_ASSOC);
            $jsonRes = json_encode($childData);
            if($jsonRes == "[]"){
                return false;
            }
            $data = json_decode($jsonRes, true);
            foreach($data as $item){
                return $item['childId'];
            }
        }
     }

     function insertNewNotification($teacherPostId,$requestId,$parentEmail,$teacherEmail,$conn){
        $sql = "SELECT firstname, lastname FROM profile WHERE email = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("s", $parentEmail);
        $stmt->execute();
        $firstname='';
        $lastname = '';
        $stmt->bind_result($firstname, $lastname);
        $stmt->fetch();
        $stmt->close();
    
        $firstname = ucfirst($firstname);
        $lastname = ucfirst($lastname);
    
        $parentName = $firstname . " " . $lastname;

        $notTitle = "New Request";
        $notBody = "$parentName sent you a request..Click to show received requests ";
        $notType = 2;

        $insertNot = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody,parentRequestId) VALUES(?,?,?,?,?)");
        $insertNot->bind_param("sissi",$teacherEmail,$notType,$notTitle,$notBody,$requestId);
        $insertNot->execute();
        return ;
    }
     $conn->close();

?>