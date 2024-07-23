<?php
    $con = require_once 'req.php';
    if($con == 'Connection error'){
        echo "Network Error";
    }

    $email = $_POST['email'];
    $idNumber = $_POST['idNumber'];
    $studentOrGraduate=$_POST['studentOrGraduate'];
    $expectedGraduationYear=$_POST['expectedGraduationYear'];
    $college=$_POST['college'];
    $field=$_POST['field'];
    $availability=$_POST['availability'];
    $city=$_POST['city'];
    $country=$_POST['country'];
    $phoneNumber=$_POST['phoneNumber'];
    $educationLevel = $_POST['educationLevel'];

    function insertNewTeacher($email,$id,$studOrGrad,$gradYear,$coll,$fiel,$availability,$city,$country,$phoneNumber,$educationLevel,$conn){
        $statement = $conn->prepare("INSERT INTO teacher VALUES (?,?,?,?,?,?,?,?);");
        $statement->bind_param("ssssssss",$email,$id,$studOrGrad,$gradYear,$coll,$fiel,$availability,$educationLevel);
        if($statement->execute()){
            $statement->close();
            $updateProfileDone = $conn->prepare("UPDATE profile SET doneInformation = 1 where email = ?;");
            $updateProfileDone->bind_param("s",$email);
            if($updateProfileDone->execute()){
                $updateProfileDone->close();
                $insertAddress = $conn->prepare("INSERT INTO address(email,city,country) VALUES (?,?,?);");
                $insertAddress->bind_param("sss",$email,$city,$country);
                if($insertAddress->execute()){
                    $insertAddress->close();
                    $insertPhoneNumber = $conn->prepare("INSERT INTO phonenumber (email,phoneNumber) VALUES (?,?)");
                    $insertPhoneNumber->bind_param("ss",$email,$phoneNumber);
                    if($insertPhoneNumber->execute()){
                        echo 'Done insertion';
                    }
                    else {
                        echo 'Error insertion';
                    }
                }
                else {
                    echo 'Error insertion';
                }
            }
            else {
                echo 'Error insertion';
            }
        }
        else {
            echo 'Error insertion';
        }
    }
    function checkTeacherExists($email,$id,$studOrGrad,$gradYear,$coll,$fiel,$availability,$city,$country,$phoneNumber,$educationLevel,$conn){
        $statement = $conn->prepare("SELECT email FROM teacher WHERE email = ?;");
        $statement->bind_param("s",$email);
        if($statement->execute()){
            $statement->store_result();
            if($statement->num_rows() != 0){
                echo 'exists';
            }
            else {
                insertNewTeacher($email,$id,$studOrGrad,$gradYear,$coll,$fiel,$availability,$city,$country,$phoneNumber,$educationLevel,$conn);
            }
        }
    }

    checkTeacherExists($email,$idNumber,
    $studentOrGraduate,$expectedGraduationYear
    ,$college,$field,$availability,$city,$country,$phoneNumber,$educationLevel,$conn);
    $conn->close();

?>