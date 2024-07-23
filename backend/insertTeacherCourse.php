<?php

    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["teacherEmail"]) &&
    isset($_POST["parentEmail"])&&
    isset($_POST["requestId"])&&
    isset($_POST["childId"])&&
    isset($_POST["courses"])&&
    isset($_POST["educationLevel"])&&
    isset($_POST["duration"])&&
    isset($_POST["availabilityForJob"])&&
    isset($_POST["location"])&&
    isset($_POST["teachingMethod"])&&
    isset($_POST["startTime"])&&
    isset($_POST["endTime"])&&
    isset($_POST["startDate"])&&
    isset($_POST["endDate"])&&
    isset($_POST["price"])){
        $teacherEmail = $_POST['teacherEmail'];
        $parentEmail = $_POST['parentEmail'];
        $requestId = $_POST['requestId'];
        $childId = $_POST['childId'];
        $courses = $_POST["courses"];
        $educationLevel = $_POST["educationLevel"];
        $duration = $_POST["duration"];
        $availabilityForJob = $_POST["availabilityForJob"];
        $location = $_POST["location"];
        $teachingMethod = $_POST["teachingMethod"];
        $startTime = $_POST["startTime"];
        $endTime = $_POST["endTime"];
        $startDate = $_POST["startDate"];
        $endDate = $_POST["endDate"];
        $price = $_POST["price"];

        $insertStmt = $conn->prepare("INSERT INTO teacherCourse (teacherEmail,parentEmail,parentSentRequestId,childId,courses,educationLevel,duration,availabilityForJob,location,teachingMethod,startTime,endTime,startDate,endDate,price) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        $insertStmt->bind_param("ssiississsssssd",$teacherEmail,$parentEmail,$requestId,$childId,$courses,$educationLevel,$duration,$availabilityForJob,$location,$teachingMethod,$startTime,$endTime,$startDate,$endDate,$price);
        $isAccepted = 1 ;
        
        $insertIntoParentTableStmt = $conn->prepare("INSERT INTO 
        parentchildrencourse(parentEmail,teacherEmail,parentSentRequestId,childId,choseDays,courses,location,teachingMethod,startTime,endTime,startDate,endDate,price) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);");
        $insertIntoParentTableStmt->bind_param("ssiisssssssss",$parentEmail,$teacherEmail,$requestId,$childId,$availabilityForJob,$courses,$location,$teachingMethod,$startTime,$endTime,$startDate,$endDate,$price);

        if($insertStmt->execute() && $insertIntoParentTableStmt->execute()){
            $updateIsAccepted = $conn->prepare("update parentSentRequest set isAccepted = ? WHERE requestId = ?");
            $updateIsAccepted->bind_param("ii",$isAccepted,$requestId);
            if($updateIsAccepted->execute()){
                $updateIsAccepted->close();
                insertNewNotification($teacherEmail,$parentEmail,$requestId,$conn);
                setTeacherPostedCourseToAccepted($requestId,$conn);
                echo "Done";
            }
            else {
                echo "Error";
            }
        }
        else {
            echo "Error";
        }
    }
    else {
        echo "Connection Error";
    }

    function setTeacherPostedCourseToAccepted($requestId,$conn){
        $stmt = $conn->prepare("SELECT postId FROM parentSentRequest WHERE requestId = ?");
        $stmt->bind_param("i", $requestId);
    
    if ($stmt->execute()) {
        $result = $stmt->get_result();
        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $postId = $row['postId'];

            $updateStmt = $conn->prepare("UPDATE teacherPostRequest set isAccepted = 1 WHERE postId = ?");
            $updateStmt->bind_param("i", $postId);
            
            if ($updateStmt->execute()) {
                return ;
            } else {
                return ;
            }
        } else {
            return ;
        }
    } else {
        return ;
    }
    }

    function insertNewNotification($teacherEmail,$parentEmail,$requestId,$conn){
        $deleteNot = $conn->prepare("DELETE FROM notifications WHERE parentRequestId = ?");
        $deleteNot->bind_param("i",$requestId);
        $deleteNot->execute();
        $deleteNot->close();
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

        $notTitle = "Request Accepted";
        $notBody = "$teacherName Accepted Your New Course Request.. Check Your Courses";
        $notType = 8;

        $insertNot = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody,parentRequestId) VALUES(?,?,?,?,?)");
        $insertNot->bind_param("sissi",$parentEmail,$notType,$notTitle,$notBody,$requestId);
        $insertNot->execute();
        return ;
    

    }
    $conn->close();

?>