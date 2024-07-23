<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST['teacherEmail'])){
        $email = $_POST["teacherEmail"];
        $isThereCourses = checkIfTeacherHaveCourses($email,$conn);
        if($isThereCourses == true){
            $coursesStmt = $conn->prepare("SELECT 
            tc.courseId,
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
            GROUP_CONCAT(DISTINCT CONCAT(a.city, ' , ', a.country) SEPARATOR ' | ') AS addresses,
            GROUP_CONCAT(DISTINCT ph.phoneNumber SEPARATOR ', ') as phoneNumbers
        FROM 
            teacherCourse tc
        JOIN 
            children ch ON tc.childId = ch.childId
        JOIN 
            profile p ON tc.parentEmail = p.email
        LEFT JOIN
            phoneNumber ph ON p.email = ph.email 
        LEFT JOIN 
            address a ON p.email = a.email
        WHERE 
            tc.teacherEmail = ?
        AND
            tc.isCompleted = -1 
        GROUP BY 
            tc.courseId, tc.teacherEmail, tc.parentEmail, tc.teacherSentRequestId, tc.parentSentRequestId, tc.childId, 
            tc.courses, tc.educationLevel, tc.duration, tc.availabilityForJob, tc.location, tc.teachingMethod, 
            tc.startTime, tc.endTime, tc.startDate, tc.endDate, tc.price, ch.childName, ch.childAge, ch.childGender, 
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
        $sql = $conn->prepare("SELECT * FROM teacherCourse Where teacherEmail =?");
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