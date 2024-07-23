<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST["courseId"]) &&
    isset($_POST['parentSentRequestId']) &&
    isset($_POST['teacherSentRequestId'])){
        $courseId = $_POST["courseId"];
        $parentSentRequestId = $_POST['parentSentRequestId'];
        $teacherSentRequestId = $_POST['teacherSentRequestId'];

        $getTeacherCourseStmt = $conn->prepare("SELECT tc.courseId,
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
                     profile p ON tc.parentEmail = p.email
            LEFT JOIN
                    phoneNumber ph ON p.email = ph.email 
            WHERE 
                    tc.courseId = ? AND tc.parentSentRequestId= ? AND tc.teacherSentRequestId = ?;");            

        $getTeacherCourseStmt->bind_param("iii",$courseId,$parentSentRequestId,$teacherSentRequestId);

        $getParentCourseStmt = $conn->prepare("SELECT pcc.courseId,
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
                pcc.courseId = ? AND pcc.parentSentRequestId= ? AND pcc.teacherSentRequestId = ?;");

        $getParentCourseStmt->bind_param("iii",$courseId,$parentSentRequestId,$teacherSentRequestId);
 
        $getTeacherCourseStmt->execute();
        $result_getTeacherCoursesStmt = $getTeacherCourseStmt->get_result();

        $getParentCourseStmt->execute();
        $result_getParentCoursesStmt = $getParentCourseStmt->get_result();

        $response = [];

        while ($row = $result_getTeacherCoursesStmt->fetch_assoc()) {
            if (!allValuesAreNull($row)) {
                $row['source'] = 'teacherCourse'; // Add source table name
                $response[] = $row;
            }
        }

        while ($row = $result_getParentCoursesStmt->fetch_assoc()) {
            if (!allValuesAreNull($row)) {
                $row['source'] = 'parentChildrenCourse'; // Add source table name
                $response[] = $row;
            }
        }

        if (empty($response)) {
            echo "No Course";
        } else {
            echo json_encode($response);
        }

        $getTeacherCourseStmt->close();
        $getParentCourseStmt->close();
    } else {
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
