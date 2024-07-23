<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['teacherEmail']) && isset($_POST['courses']) && isset($_POST['educationLevel']) && isset($_POST['duration'])
    && isset($_POST['location']) && isset($_POST['teachingMethod']) && isset($_POST['startTime']) && isset($_POST['endTime']) && isset($_POST['availabilityForJob']) && isset($_POST['price'])&&
    isset($_POST['startDate'])&& isset($_POST['endDate'])){
        $email = $_POST['teacherEmail'];
        $courses = $_POST['courses'];
        $educationLevel = $_POST['educationLevel'];
        $availabilityForJob = $_POST['availabilityForJob'];
        $duration = $_POST['duration'];
        $location = $_POST['location'];
        $teachingMethod = $_POST['teachingMethod'];
        $startTime = $_POST['startTime'];
        $endTime = $_POST['endTime'];
        $price = $_POST['price'];
        $startDate = $_POST['startDate'];
        $endDate = $_POST['endDate'];
        $insertStmt = $conn->prepare("INSERT INTO teacherpostrequest (teacherEmail,courses,educationLevel,duration,availabilityForJob,location,teachingMethod,startTime,endTime,price,startDate,endDate,posted) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,DATE_FORMAT(NOW(), '%d-%m-%Y %H:%i')  )");
        $insertStmt->bind_param("sssssssssdss",$email,$courses,$educationLevel,$duration,$availabilityForJob,$location,$teachingMethod,$startTime,$endTime,$price,$startDate,$endDate);
        if($insertStmt->execute()){
            $insertStmt->close();
            echo 'Done';
        }
        else {
            echo 'Error';
        }
    }
    else {
        echo 'connection error';
    }
?>