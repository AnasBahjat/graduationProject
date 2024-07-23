<?php

require_once 'req.php';
if ($_SERVER["REQUEST_METHOD"] == "POST" && 
    isset($_POST['matchingId']) && 
    isset($_POST['childId']) && 
    isset($_POST['email']) && 
    isset($_POST['selectedDays']) && 
    isset($_POST['startTime']) && 
    isset($_POST['endTime']) && 
    isset($_POST['courses']) && 
    isset($_POST['location']) && 
    isset($_POST['teachingMethod'])&& 
    isset($_POST['priceMin'])&& 
    isset($_POST['priceMax'])&& 
    isset($_POST['startDate'])&& 
    isset($_POST['endDate'])){

    $childId = $_POST['childId'];
    $matchingId = $_POST['matchingId'];
    $email = $_POST['email'];
    $selectedDays = $_POST['selectedDays'];
    $startTime = $_POST['startTime'];
    $endTime = $_POST['endTime'];
    $courses = $_POST['courses'];
    $location = $_POST['location'];
    $teachingMethod = $_POST['teachingMethod'];
    $priceMin = $_POST['priceMin'];
    $priceMax = $_POST['priceMax'];
    $startDate = $_POST['startDate'];
    $endDate = $_POST['endDate'];
    $isRequestExists = checkIfRequestExists($matchingId,$conn);
    if($isRequestExists == false){
        echo 'no data';
    }
    else {
        $updateStmt = $conn->prepare("UPDATE teacherMatching SET choseDays = ?,courses = ?,location=?,teachingMethod=?,startTime=?,endTime=?,startDate=?,endDate=?,priceMin = ?,priceMax=? WHERE matchingId  = ? AND parentEmail = ?;");
        $updateStmt->bind_param('ssssssssddis',$selectedDays,$courses,$location,$teachingMethod,$startTime,$endTime,$startDate,$endDate,$priceMin,$priceMax,$matchingId,$email);
        if($updateStmt->execute()){
            $updateStmt->close();
            echo 'Done';
        }
        else {
            echo 'Error';
        }
    }
}
else {
    echo 'Connection Error';
}


function checkIfRequestExists($matchingId,$conn){
    $stmt = $conn->prepare("SELECT * FROM teacherMatching where matchingId = ?");
    $stmt->bind_param('i',$matchingId);
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

$conn->close();
?>