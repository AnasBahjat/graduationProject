<?php
    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD']=="POST" && isset($_POST['email'])){
        $email = $_POST['email'];
       $getDataStmt = $conn->prepare("SELECT 
       t.email,
       pr.firstname,
       pr.lastname,
       pr.gender,
       pr.profileType,
       pr.birthDate,
       t.idNumber,
       t.studentOrGraduate,
       t.expectedGraduationYear,
       t.college,
       t.field,
       t.availability,
       tpr.startTime,
       tpr.endTime,
       tpr.postId,
       tpr.teacherEmail,
       tpr.courses,
       tpr.duration,
       tpr.availabilityForJob,
       tpr.location,
       tpr.teachingMethod,
       tpr.educationLevel,
       tpr.price,
       tpr.startDate,
       tpr.endDate,
       tpr.posted,
       GROUP_CONCAT(DISTINCT p.phoneNumber) AS phoneNumbers,
       GROUP_CONCAT(DISTINCT CONCAT(ad.city, ' , ', ad.country) SEPARATOR ' | ') AS addresses
   FROM 
       teacher t
       JOIN teacherpostrequest tpr ON tpr.teacherEmail = t.email
       JOIN phoneNumber p ON p.email = t.email
       JOIN address ad ON ad.email = t.email
       JOIN profile pr ON pr.email = t.email 
   WHERE 
       t.email = ?
   GROUP BY 
       t.email, tpr.postId;"); 
       $getDataStmt->bind_param("s",$email);
        if($getDataStmt->execute()){
            $postedTeacherData = $getDataStmt->get_result()->fetch_all(MYSQLI_ASSOC);
            $getDataStmt->close();
            echo json_encode($postedTeacherData);
        }
        else {
            echo 'Error';
        }
    }
    else {
        echo 'Connection Error';
    }
    $conn->close();
?>