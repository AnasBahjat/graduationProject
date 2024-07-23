<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST["courseId"])){
        $courseId = $_POST["courseId"];
        $getCourseStmt = $conn->prepare("SELECT tc.courseId,
                tc.teacherEmail,
                tc.parentEmail,
                tc.teacherSentRequestId,
                tc.parentSentRequestId,
                tc.courses,
                tc.educationLevel,
                tc.duration,
                tc.availabilityForJob,
                tc.location,
                tc.teachingMethod,
                tc.startTime,
                tc.endTime,
                tc.startDate,
                tc.endDate,
                tc.price,
                tc.childId,
                ch.childName,
                ch.childAge,
                ch.childGender,
                ch.childGrade,
                p.firstName,
                p.lastName,
                GROUP_CONCAT(DISTINCT ph.phoneNumber SEPARATOR ', ') as phoneNumbers
            FROM 
                teacherCourse tc
            JOIN 
                children ch ON tc.childId = ch.childId
            JOIN 
                profile p ON tc.teacherEmail = p.email
            LEFT JOIN
                phoneNumber ph ON p.email = ph.email 
            WHERE 
                tc.courseId = ?;");
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