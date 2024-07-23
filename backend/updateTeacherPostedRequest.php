<?php
    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD'] == "POST" && isset($_POST['postId']) && isset($_POST['teacherEmail'])&& isset($_POST['courses']) 
    && isset($_POST['educationLevel'])&& isset($_POST['availabilityForJob'])
    && isset($_POST['duration'])&& isset($_POST['location'])&& isset($_POST['teachingMethod'])
    && isset($_POST['startTime'])&& isset($_POST['endTime']) && isset($_POST['price'])
    && isset($_POST['startDate']) && isset($_POST['endDate'])){
        $postId = $_POST['postId'];
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
        $isRequestExists = checkIfRequestExists($postId,$conn);
        if($isRequestExists===false){
            echo "No Request";
        }
        else {
            $updateStmt = $conn->prepare("UPDATE teacherpostrequest SET courses = ? , educationLevel = ?, duration = ? ,
            availabilityForJob=?,location=?,teachingMethod=?,
            startTime=?,endTime=?,price = ?,startDate = ?,endDate = ? WHERE postId = ?;");
            $updateStmt->bind_param("ssssssssdssi",$courses,$educationLevel,$duration,$availabilityForJob,$location,$teachingMethod,$startTime,$endTime,$price,$startDate,$endDate,$postId);
            if($updateStmt->execute()){
                $updateStmt->close();
                echo "updated";
            }
            else {
                echo "Error Updating";
            }

        }
    }
    else {
        echo "Connection Error";
    }


    function checkIfRequestExists($postId,$conn){
        $stmt = $conn->prepare("SELECT * FROM teacherpostrequest where postId = ?");
        $stmt->bind_param('i',$postId);
        if($stmt->execute()){
            $matchingData = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
            $jsonMatchingData = json_encode($matchingData);
            $stmt->close();
            if(empty($jsonMatchingData)){
                return false;
            }
            return true;
        }
        else {
            return false;
        }
    }
?>