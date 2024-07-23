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

        $stmt1 = $conn->prepare("DELETE FROM teacherCourse WHERE teacherSentRequestId = ? AND parentSentRequestId = ? AND teacherEmail = ? AND parentEmail = ?");
        $stmt1->bind_param("iiss",$teacherSentRequestId,$parentSentRequestId,$teacherEmail,$parentEmail);
        
        $stmt2 = $conn->prepare("DELETE FROM parentChildrenCourse WHERE parentSentRequestId = ? AND teacherSentRequestId = ? AND teacherEmail = ? and parentEmail = ?;");
        $stmt2->bind_param("iiss",$parentSentRequestId,$teacherSentRequestId,$teacherEmail,$parentEmail);

        if($stmt1->execute() && $stmt2->execute()){
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

            $notificationType = 21 ;
            $notificationTitle = "Course Deletion Accepted ";
            $notificationBody = "$teacherName accepted deleting the course ..";

            $notStmt = $conn->prepare("INSERT INTO notifications (ownerEmail,notificationType,notificationTitle,notificationBody) VALUES(?,?,?,?);");
            $notStmt->bind_param("siss",$parentEmail,$notificationType,$notificationTitle,$notificationBody);
            $notStmt->execute();

            $notType = 20 ;
            $tempParentId = 0;

            $deleteNotStmt = $conn->prepare("DELETE FROM notifications WHERE ownerEmail = ? AND notificationType = ? AND teacherRequestId = ? AND parentRequestId = ?");
            $deleteNotStmt->bind_param("siii",$parentEmail,$notType,$courseId,$tempParentId);
            $deleteNotStmt->execute();


            if($teacherSentRequestId == 0 && $parentSentRequestId != 0){
                $getPostIdFromParentSentRequest = $conn->prepare("SELECT postId FROM parentSentRequest WHERE requestId = ?;");
                $getPostIdFromParentSentRequest->bind_param("i",$parentSentRequestId);
                $getPostIdFromParentSentRequest->execute();
                $parentSentRequestPostId = 0 ;
                $getPostIdFromParentSentRequest->bind_result($parentSentRequestPostId);
                $getPostIdFromParentSentRequest->fetch();
                $getPostIdFromParentSentRequest->close();

                $updateIsAccepted = $conn->prepare("UPDATE teacherpostrequest SET isAccepted = -1 WHERE postId = ?");
                $updateIsAccepted->bind_param("i",$parentSentRequestPostId);
                $updateIsAccepted->execute();

                $deleteParentSentRequest = $conn->prepare("DELETE FROM parentSentRequest WHERE requestId = ?");
                $deleteParentSentRequest->bind_param("i",$parentSentRequestId);
                $deleteParentSentRequest->execute();
                $deleteParentSentRequest->close();
            }


            else if($teacherSentRequestId != 0 && $parentSentRequestId ==0){
                $getMatchinIdFromTeacherSentRequest = $conn ->prepare("SELECT matchingId FROM teacherSentRequest WHERE requestId = ?");
                $getMatchinIdFromTeacherSentRequest->bind_param("i",$teacherSentRequestId);
                $getMatchinIdFromTeacherSentRequest->execute();
                $teacherSentRequestMatchingId = 0 ;
                $getMatchinIdFromTeacherSentRequest->bind_result($teacherSentRequestMatchingId);
                $getMatchinIdFromTeacherSentRequest->fetch();
                $getMatchinIdFromTeacherSentRequest->close();

                $updateIsAccepted = $conn->prepare("UPDATE teacherMatching SET isAccepted = -1 WHERE matchingId =? ");
                $updateIsAccepted->bind_param("i",$teacherSentRequestMatchingId);
                $updateIsAccepted->execute();

                $deleteTeacherSentRequest = $conn->prepare("DELETE FROM teacherSentRequest WHERE requestId = ?");
                $deleteTeacherSentRequest->bind_param("i",$teacherSentRequestId);
                $deleteTeacherSentRequest->execute();
                $deleteTeacherSentRequest->close();
            }

        }
    }
    else {
        echo "Connection Error";
    }
    $conn->close();

?>