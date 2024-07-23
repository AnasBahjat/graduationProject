<?php

require_once 'req.php';
if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['email'])){
    $email = $_POST['email'];
    $isParentHaveRequests = checkIfParentHaveRequests($email,$conn);
    if($isParentHaveRequests == null){
        echo "ERROR";
    }
    else if($isParentHaveRequests == '[]'){
        echo "No Requests";
    }
    else {
       $parentDataStmt = $conn->prepare("SELECT pr.firstname,
        pr.lastname,
        pr.gender AS parentGender,
        pr.birthDate,
        p.id AS parentId,
        p.idNumber,
        ch.childId,
        ch.childName,
        ch.childAge,
        ch.childGender,
        ch.childGrade,
        tm.matchingId,
        tm.choseDays,
        tm.courses,
        tm.location,
        tm.teachingMethod,
        tm.startTime,
        tm.endTime,
        tm.priceMin,
        tm.priceMax,
        tm.startDate,
        tm.endDate,
        tm.posted,
        GROUP_CONCAT(DISTINCT pn.phoneNumber) AS phoneNumbers,
        GROUP_CONCAT(DISTINCT CONCAT(ad.city, ', ', ad.country) SEPARATOR ' | ') AS addresses
        FROM
            teacherMatching tm
        JOIN
            children ch ON tm.childId = ch.childId
        JOIN 
            parent p ON tm.parentEmail = p.email
        JOIN 
            profile pr ON pr.email = p.email
        LEFT JOIN 
            phoneNumber pn ON pn.email = pr.email
        LEFT JOIN 
            address ad ON ad.email = pr.email
        WHERE 
            tm.parentEmail = ?
        GROUP BY 
            tm.matchingId
       ");
       $parentDataStmt->bind_param("s",$email);
       if($parentDataStmt->execute()){
            $postedParentData = $parentDataStmt->get_result()->fetch_all(MYSQLI_ASSOC);
            $parentDataStmt->close();
            echo json_encode($postedParentData);
       }
       else {
        echo "Error";
       }
    }
}
else {
    echo "Connection Error";
}

function checkIfParentHaveRequests($email,$conn){
    $stmt = $conn->prepare("SELECT * FROM teachermatching tm,children c WHERE tm.parentEmail = ? AND tm.parentEmail = c.parentEmail AND tm.childId=c.childId;");

    $stmt->bind_param('s',$email);
    if($stmt->execute()){
        $matchingData = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
        $stmt->close();
        return json_encode($matchingData);
    }
    else {
        return null ;
    }
}
$conn->close();
?>