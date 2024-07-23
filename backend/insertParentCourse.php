<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && 
    isset($_POST["parentEmail"]) &&
    isset($_POST["teacherEmail"]) && 
    isset($_POST["requestId"]) && 
    isset($_POST["childId"]) && 
    isset($_POST["choseDays"]) && 
    isset($_POST["courses"]) && 
    isset($_POST["location"]) && 
    isset($_POST["teachingMethod"]) && 
    isset($_POST["startTime"])&& 
    isset($_POST["endTime"])&& 
    isset($_POST["startDate"])&& 
    isset($_POST["endDate"])&& 
    isset($_POST["duration"])&& 
    isset($_POST["educationLevel"])&& 
    isset($_POST["price"])){
        $parentEmail = $_POST["parentEmail"];
        $teacherEmail = $_POST["teacherEmail"];
        $requestId = $_POST["requestId"];
        $childId = $_POST["childId"];
        $choseDays = $_POST["choseDays"];
        $courses = $_POST["courses"];
        $location = $_POST["location"];
        $teachingMethod = $_POST["teachingMethod"];
        $startTime = $_POST["startTime"];
        $endTime = $_POST["endTime"];
        $startDate = $_POST["startDate"];
        $endDate = $_POST["endDate"];
        $price = $_POST["price"];
        $duration = $_POST["duration"];
        $educationLevel = $_POST["educationLevel"];


        $insertStmt = $conn->prepare("INSERT INTO parentchildrencourse(teacherSentRequestId,parentEmail,teacherEmail,childId,choseDays,courses,location,teachingMethod,
        startTime,endTime,
        startDate,endDate,price) VALUES
        (?,?,?,?,?,?,?,?,?,?,?,?,?);");
        $insertStmt->bind_param("ississssssssd",$requestId,$parentEmail,$teacherEmail,$childId,$choseDays,$courses,$location,$teachingMethod,$startTime,$endTime,$startDate,$endDate,$price);
        
        $insertIntoTeacherTableStmt = $conn->prepare("INSERT INTO teacherCourse(teacherEmail,parentEmail,teacherSentRequestId,
        childId,courses,educationLevel,duration,availabilityForJob,location,
        teachingMethod,startTime,endTime,startDate,endDate,price) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
        $insertIntoTeacherTableStmt->bind_param("ssiississsssssd",$teacherEmail,$parentEmail,$requestId,$childId,$courses,$educationLevel,$duration,$choseDays,$location,$teachingMethod,$startTime,$endTime,$startDate,$endDate,$price);
        if($insertStmt->execute() && $insertIntoTeacherTableStmt->execute()){
            $insertStmt->close();
            $isAccepted = 1;
            $updateIsAccepted = $conn->prepare("UPDATE teacherSentRequest SET isAccepted = ? WHERE requestId = ?");
            $updateIsAccepted->bind_param("ii",$isAccepted,$requestId);
            if($updateIsAccepted->execute()){
                $updateIsAccepted->close();
                insertNewNotification($parentEmail,$teacherEmail,$requestId,$conn);
                deleteParentPostedCourse($requestId,$conn);
                echo "Done";
            }
        }
        else {
            echo "Error";
        }
        }
    else {
        echo "Connection Error";
    }


    function deleteParentPostedCourse($requestId,$conn){
        $stmt = $conn->prepare("SELECT matchingId FROM teacherSentRequest WHERE requestId = ?");
        $stmt->bind_param("i", $requestId);
    
    if ($stmt->execute()) {
        $result = $stmt->get_result();
        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $matchingId = $row['matchingId'];

            $updateStmt = $conn->prepare("UPDATE teacherMatching set isAccepted = 1 WHERE matchingId = ?");
            $updateStmt->bind_param("i", $matchingId);
            
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

    function insertNewNotification($parentEmail,$teacherEmail,$requestId,$conn){
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

        $notTitle = "Request Accepted";
        $notBody = "$parentName Accepted Your new course Request.. Check Your Courses";
        $notType = 7;


        $insertNot = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody,teacherRequestId) VALUES(?,?,?,?,?)");
        $insertNot->bind_param("sissi",$teacherEmail,$notType,$notTitle,$notBody,$requestId);
        $insertNot->execute();
        $insertNot->close();
        return ;
    }
    $conn->close();
?>