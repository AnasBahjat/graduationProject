<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST['parentEmail'])){
        $email = $_POST["parentEmail"];
        $isThereCourses = checkIfTeacherHaveCourses($email,$conn);
        if($isThereCourses == true){
            $coursesStmt = $conn->prepare("SELECT 
            pcc.courseId,
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
            p.gender,
            GROUP_CONCAT(DISTINCT CONCAT(a.city, ' , ', a.country) SEPARATOR ' | ') AS addresses,
            GROUP_CONCAT(DISTINCT ph.phoneNumber SEPARATOR ', ') as phoneNumbers
        FROM 
            parentChildrenCourse pcc
        JOIN 
            children ch ON pcc.childId = ch.childId
        JOIN
            profile p ON pcc.parentEmail = p.email
        LEFT JOIN
            phoneNumber ph ON p.email = ph.email 
        LEFT JOIN
            address a ON p.email = a.email
        WHERE 
            pcc.parentEmail = ?
        AND
            pcc.isCompleted = -1 
        GROUP BY 
            pcc.courseId, pcc.teacherEmail, pcc.parentEmail, pcc.teacherSentRequestId, pcc.parentSentRequestId, pcc.childId, 
            pcc.courses, pcc.choseDays, pcc.location, pcc.teachingMethod, 
            pcc.startTime, pcc.endTime, pcc.startDate, pcc.endDate, pcc.price, ch.childName, ch.childAge, ch.childGender, 
            ch.childGrade, p.firstName, p.lastName
                ");
                $coursesStmt->bind_param("s",$email);
                if($coursesStmt->execute()){
                    $result = $coursesStmt->get_result();
                    $coursesData = array();
            
                    while ($row = $result->fetch_assoc()) {
                        $coursesData[] = $row;
                    }
            
                    header('Content-Type: application/json');
                    echo json_encode($coursesData);
            }
        }
        else if($isThereCourses == false){
            echo "No Courses";
        }
        else {
            echo "Error";
        }
    }


    else {
        echo 'Connection Error';
    }


    function checkIfTeacherHaveCourses($email,$conn){
        $sql = $conn->prepare("SELECT * FROM parentChildrenCourse Where parentEmail =?");
        $sql->bind_param("s",$email);
        if($sql->execute()){
            $result = $sql->get_result();
            if ($result->num_rows > 0) {
                return true;  
            } else {
                return false;
            }
        }
        else {
            return "Error";
        }
    }
    $conn->close();
?>