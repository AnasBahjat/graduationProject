<?php

use Lcobucci\JWT\Signer\Ecdsa;

require_once 'req.php';
if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['parentEmail'])){
    $email = $_POST['parentEmail'];
    $emailExistsFlag = checkIfEmailExists($email,$conn);  
    if($emailExistsFlag=="exists"){
        $getParentInformationStmt = $conn->prepare("SELECT 
        pr.email,
        pr.firstname,
        pr.lastname,
        pr.birthDate,
        pr.gender,
        pa.idNumber,
        GROUP_CONCAT(DISTINCT CONCAT(ad.city, ', ', ad.country) SEPARATOR ' | ') as addresses,
        GROUP_CONCAT(DISTINCT ph.phoneNumber SEPARATOR ', ') as phoneNumbers
    FROM profile pr
    JOIN parent pa ON pr.email = pa.email
    LEFT JOIN address ad ON pr.email = ad.email
    LEFT JOIN phonenumber ph ON pr.email = ph.email
    WHERE pr.email = ?
    GROUP BY pr.email;");
        $getParentInformationStmt->bind_param("s",$email);
        if($getParentInformationStmt->execute()){
            $parentInformationResult = $getParentInformationStmt->get_result()->fetch_all(MYSQLI_ASSOC);
            $jsonParentInformation = json_encode($parentInformationResult);
            if(empty($jsonParentInformation)){
                echo "No Data";
            }
            else {
                echo $jsonParentInformation;
            }
        }
        else {
            echo "ERROR";
        }
    }
    else {
        echo "ERROR";
    }
}
else {
    echo "Connection Error";
}

function checkIfEmailExists($email,$conn){
    $checkIfExistsStmt = $conn->prepare("SELECT * FROM parent WHERE email = ? ;");
    $checkIfExistsStmt->bind_param("s",$email);
    if($checkIfExistsStmt->execute()){
        $parentData = $checkIfExistsStmt->get_result()->fetch_all(MYSQLI_ASSOC);
        $jsonParentData = json_encode($parentData);
        if(empty($jsonParentData)){
            return "empty";
        }
        else {
            return "exists";
        }
    }
}
$conn->close();
?>