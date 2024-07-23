<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["courseId"])
    && isset($_POST["teacherSentRequestId"]) && 
    isset($_POST["parentSentRequestId"])&& 
    isset($_POST["parentEmail"])&& 
    isset($_POST["teacherEmail"]))
    
    {
        $courseId = $_POST["courseId"];
        $teacherSentRequestId = $_POST["teacherSentRequestId"];
        $parentSentRequestId = $_POST["parentSentRequestId"];
        $parentEmail = $_POST["parentEmail"];
        $teacherEmail = $_POST["teacherEmail"];

        $isRequestExists = checkIfCourseExists($courseId,$parentSentRequestId,$teacherSentRequestId,$conn);
        if($isRequestExists == 1){
            
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

            $notificationType = 20 ;
            $notificationTitle = "Request To Delete Course";
            $notificationBody = "$teacherName Wants to delete an existing course , click to show more ..";

            $isNotificationSent = isNotificationSentBefore($parentEmail,$notificationType,$notificationTitle,$notificationBody,$courseId,$conn);
            if($isNotificationSent == true){
                echo "Exists";
            }
            else {
                $setParentNotificationStmt = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody,teacherRequestId) VALUES(?,?,?,?,?)");
            $setParentNotificationStmt->bind_param("sissi",$parentEmail,$notificationType,$notificationTitle,$notificationBody,$courseId);
            if($setParentNotificationStmt->execute()){
                echo "Done";
            }
            else {
                echo "Error";
            }
            }
        }
        else {
            echo "Not Exists";
        }
    }
    else {
        echo "Connection Error";
    }

    function isNotificationSentBefore($parentEmail,$notificationType,$notificationTitle,$notificationBody,$courseId,$conn){
        $SQL = $conn->prepare("SELECT notificationId FROM notifications WHERE ownerEmail = ? AND notificationType = ?
         AND notificationTitle = ? AND notificationBody = ? and teacherRequestId = ? ;");
        $SQL->bind_param("sissi",$parentEmail,$notificationType,$notificationTitle,$notificationBody,$courseId);
        if($SQL->execute()){
            $SQL->store_result();
            if($SQL->num_rows > 0){
                return true ;
            }
            return false;
        }
        return false ;
    }

    function checkIfCourseExists($courseId,$parentRequestId,$teacherRequestId,$conn){
        $stmt1 = $conn->prepare("SELECT courseId from teacherCourse WHERE courseId = ? AND parentSentRequestId = ? AND teacherSentRequestId =?");
        $stmt1->bind_param("iii",$courseId,$parentRequestId,$teacherRequestId);
        
        $stmt2 = $conn->prepare("SELECT courseId from parentchildrencourse WHERE parentSentRequestId = ? AND teacherSentRequestId =?");
        $stmt2->bind_param("ii",$parentRequestId,$teacherRequestId);

        $stmt3 = $conn->prepare("SELECT courseId from parentchildrencourse WHERE courseId = ? AND parentSentRequestId = ? AND teacherSentRequestId =?");
        $stmt3->bind_param("iii",$courseId,$parentRequestId,$teacherRequestId);


        $stmt4 = $conn->prepare("SELECT courseId from teacherCourse WHERE parentSentRequestId = ? AND teacherSentRequestId =?");
        $stmt4->bind_param("ii",$parentRequestId,$teacherRequestId);


        $existsFlag = 0;

        if($stmt1->execute()){
            $stmt1->store_result();
            if ($stmt1->num_rows > 0) {
                $stmt1->close();
                $existsFlag = 1;
                if($stmt2->execute()){
                    $stmt2->store_result();
                    if($stmt2->num_rows > 0){
                        $stmt2->close();
                        $existsFlag = 1;
                    }
                    else {
                        $existsFlag = 0;
                    }
                }
                else {
                    $existsFlag = 0 ;
                }
            }
            else {
                if($stmt3->execute()){
                    $stmt3->store_result();
                    if($stmt3->num_rows > 0){
                        $stmt3->close();
                        $existsFlag = 1;
                        if($stmt4->execute()){
                            $stmt4->store_result();
                            if($stmt4->num_rows > 0){
                                $stmt4->close();
                                $existsFlag = 1;
                            }
                            else {
                                $existsFlag = 0;
                            }
                        }
                        else {
                            $existsFlag = 0 ;
                        }
                    }
                    else {
                        $existsFlag = 0 ;
                    }
                }
                else {
                    $existsFlag = 0;
                }
            }
        }
        else{
            $existsFlag = 0;
        }
        return $existsFlag;
    }

    $conn->close();

?>