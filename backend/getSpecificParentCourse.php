<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST["courseId"])){
        $courseId = $_POST["courseId"];
        $getCourseStmt = $conn->prepare("SELECT pcc.courseId,
                pcc.teacherEmail,
                pcc.parentEmail,
                pcc.teacherSentRequestId,
                pcc.parentSentRequestId,
                pcc.courses,
                pcc.choseDays,
                pcc.location,
                pcc.teachingMethod,
                pcc.startTime,
                pcc.endTime,
                pcc.startDate,
                pcc.endDate,
                pcc.price,
                pcc.childId,
                ch.childName,
                ch.childAge,
                ch.childGender,
                ch.childGrade,
                p.firstName,
                p.lastName,
                GROUP_CONCAT(DISTINCT ph.phoneNumber SEPARATOR ', ') as phoneNumbers
            FROM 
                parentChildrenCourse pcc
            JOIN 
                children ch ON pcc.childId = ch.childId
            JOIN 
                profile p ON pcc.parentEmail = p.email
            LEFT JOIN
                phoneNumber ph ON p.email = ph.email 
            WHERE 
                pcc.courseId = ?;");
        $getCourseStmt->bind_param("i",$courseId);
        if($getCourseStmt->execute()){
            $result=$getCourseStmt->get_result()->fetch_all(MYSQLI_ASSOC);
            if (empty($result) || (count($result) == 1 && allValuesAreNull($result[0]))) {
                echo "No Course";
            }
            else {
                echo json_encode($result);
            }
        }
        else{
            echo "Error";
        }
    }
    else{
        echo "Connection Error";
    }

    function allValuesAreNull($array) {
        foreach ($array as $value) {
            if (!is_null($value)) {
                return false;
            }
        }
        return true;
    }

    $conn->close();
?>