<?php
require_once 'req.php';

if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['email'])){
    $email = $_POST['email'];

function getTeacherData($email,$conn){
    $teacherDataStmt = $conn->prepare("SELECT t.*, a.city, a.country, p.phoneNumber FROM teacher t 
    INNER JOIN address a ON t.email = a.email 
    INNER JOIN phonenumber p ON t.email = p.email 
    WHERE t.email=?;
    ");
    $teacherDataStmt->bind_param("s",$email);
    if($teacherDataStmt->execute()){
        $teachersData = $teacherDataStmt->get_result()->fetch_all(MYSQLI_ASSOC);
        $jsonResult = json_encode($teachersData);
        if(empty($jsonResult)){
            echo "No data";
        }
        else {
            echo json_encode($teachersData);
        }
    }
    else{
        echo 'ERROR';
    }
    
}

getTeacherData($email,$conn);
}
else {
    echo "Connection Error";
}
$conn->close();


?>