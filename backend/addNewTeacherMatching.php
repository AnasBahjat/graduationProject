<?php
require_once 'req.php';
if($_SERVER["REQUEST_METHOD"] == "POST" 
    && isset($_POST['parentEmail']) 
    && isset($_POST['childId'])
    && isset($_POST['choseDays'])
    && isset($_POST['courses'])
    && isset($_POST['location'])
    && isset($_POST['teachingMethod'])
    && isset($_POST['startTime'])
    && isset($_POST['endTime'])
    &&isset($_POST['priceMin'])
    && isset($_POST['priceMax'])
    && isset($_POST['startDate'])
    && isset($_POST['endDate'])){
        $parentEmail=$_POST['parentEmail'];
        $childId=$_POST['childId'];
        $choseDays = $_POST['choseDays'];
        $courses =$_POST['courses'];
        $location = $_POST['location'];
        $teachingMethod = $_POST['teachingMethod'];
        $startTime = $_POST['startTime'];
        $endTime = $_POST['endTime'];
        $priceMin = $_POST['priceMin'];
        $priceMax = $_POST['priceMax'];
        $startDate = $_POST['startDate'];
        $endDate = $_POST['endDate'];
        $insertMatchingTeacherStmt = $conn->prepare("INSERT INTO teacherMatching (parentEmail,childId,choseDays,courses,location,teachingMethod,startTime,endTime,startDate,endDate,priceMin,priceMax,posted) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,DATE_FORMAT(NOW(), '%d-%m-%Y %H:%i'));");
        $insertMatchingTeacherStmt->bind_param("sissssssssdd",$parentEmail,$childId,$choseDays,$courses,$location,$teachingMethod,$startTime,$endTime,$startDate,$endDate,$priceMin,$priceMax);
        if($insertMatchingTeacherStmt->execute()){
            $insertMatchingTeacherStmt->close();
            echo "Done";
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