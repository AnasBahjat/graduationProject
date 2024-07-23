<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "GET"){
        $checkIfTeacherRequestsExists = checkTeacherRequests($conn);
        if($checkIfTeacherRequestsExists==="Error"){
            echo "Error";
        }
        else if($checkIfTeacherRequestsExists === "[]"){
            echo "No Requests";
        }
        else{
            echo $checkIfTeacherRequestsExists;
        }
    }
    else {
        echo "Connection Error";
    }

    function checkTeacherRequests($conn){
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
       tpr.educationLevel,
       tpr.startTime,
       tpr.endTime,
       tpr.postId,
       tpr.teacherEmail,
       tpr.courses,
       tpr.duration,
       tpr.availabilityForJob,
       tpr.location,
       tpr.teachingMethod,
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
        tpr.isAccepted = -1 
   GROUP BY 
       t.email, tpr.postId;"); 
       if($getDataStmt->execute()){
        $result = $getDataStmt->get_result()->fetch_all(MYSQLI_ASSOC);
       $jsonResult = json_encode($result);
       if(empty($jsonResult)){
        return "[]";
       }
       else {
        return $jsonResult;
       }
       }
       else {
        return "Error";
       }
    }
$conn->close();
?>