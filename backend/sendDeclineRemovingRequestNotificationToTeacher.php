<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["courseId"])
    && isset($_POST["teacherSentRequestId"])
    && isset($_POST["parentSentRequestId"])
    && isset($_POST["teacherEmail"])
    && isset($_POST["parentEmail"])){

        $courseId = $_POST['courseId'];
        $teacherSentRequestId = $_POST['teacherSentRequestId'];
        $parentSentRequestId = $_POST['parentSentRequestId'];
        $teacherEmail = $_POST['teacherEmail'];
        $parentEmail = $_POST['parentEmail'];

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
            $parentEmail = $firstname . " " . $lastname;

            $notificationType = 32 ;
            $notificationTitle = "Course Deletion Declined ";
            $notificationBody = "$parentEmail declined deleting the course, click to show more ..";

        $stmt = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody,parentRequestId,teacherRequestId,tempCourseId) VALUES (?,?,?,?,?,?,?);");
        $stmt->bind_param("sissiii",$teacherEmail,$notificationType,$notificationTitle,$notificationBody,$parentSentRequestId,$teacherSentRequestId,$courseId);
        $stmt->execute();
    }
    else {

    }
    $conn->close();

?>