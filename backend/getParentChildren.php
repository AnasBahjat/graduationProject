<?php

require_once 'req.php';
if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['email'])){
    $email = $_POST['email'];
    $checkEmailStmt=$conn->prepare("SELECT * FROM children WHERE parentEmail=?");
    $checkEmailStmt->bind_param("s",$email);
    if($checkEmailStmt->execute()){
        $result = $checkEmailStmt->get_result()->fetch_all(MYSQLI_ASSOC);
        $jsonResult = json_encode($result);
        if(!empty($jsonResult)){
            echo $jsonResult;
        }
        else {
            echo "No Data";
        }
    }
    else {
        echo "ERROR";
    }
}
else {
    echo "Connection Error";
}


$conn->close();
?>