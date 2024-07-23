<?php

require_once 'req.php';

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['teacherEmail'])) {
    $teacherEmail = $_POST["teacherEmail"];
    $getCoursesDatesAndTimes = $conn->prepare("SELECT 
            courseId, 
            startDate,
            endDate,
            startTime,
            endTime,
            availabilityForJob,
            parentSentRequestId
    FROM 
            teacherCourse
    WHERE 
            teacherEmail = ? ;");
    $getCoursesDatesAndTimes->bind_param("s",$teacherEmail);
    if($getCoursesDatesAndTimes->execute()){
        $result = $getCoursesDatesAndTimes->get_result();
        $courses = [];
        while($row = $result->fetch_assoc()){
            $courses[] = $row;
        }
        $getCoursesDatesAndTimes->close();

        if (empty($courses)) {
            echo "No Courses";
        } 
        
        else {
            echo json_encode($courses);
        }
    }

    else {
        echo "Error";
    }
} 
else {
    echo "Connection Error";
}
$conn->close();
?>
