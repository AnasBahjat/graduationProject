<?php
    require_once 'req.php' ;
    $email = $_POST['email'];
    $idNumber = $_POST['idNumber'];
    $phoneNumber = $_POST['phoneNumber'];
    $city = $_POST['city'];
    $country = $_POST['country'];
    $jsonChildren = $_POST['children'];

    $jsonDate = json_decode($jsonChildren,true);
    if(json_last_error() === JSON_ERROR_NONE){
        $insertParent = $conn -> prepare("INSERT INTO parent(email,idNumber,city,country) VALUES(?,?,?,?);");
        $insertParent->bind_param('ssss',$email,$idNumber,$city,$country);
        if($insertParent->execute()){
            $insertParent->close();
            $insertPhoneResult = insertPhoneNumber($email,$phoneNumber,$conn);
            if($insertPhoneResult == "true"){
                $insertCildrenResult = insertChildren($email,$jsonDate,$conn);
                if($insertCildrenResult=="true"){
                    $setProfileDone = $conn->prepare("UPDATE profile SET doneInformation = 1 WHERE email = ?");
                    $setProfileDone->bind_param("s",$email);
                    $setProfileDone->execute();
                    echo "Done Insertion";
                }
                else {
                    deleteParent($email,$conn);
                    echo "Error";
                }
            }
            else {
                deleteParent($email,$conn);
                echo 'Error';
            }
        }
        else {
            echo 'Error';
        }
    }

    function deleteParent($email,$conn){
                $removeParentAdded = $conn->prepare("DELETE FROM parent WHERE email = ?");
                $removeParentAdded->bind_param("s",$email);
                $removeParentAdded->execute();
                $removeParentAdded->close();
    }


    function insertPhoneNumber($email,$phoneNumber,$conn){
       $insertPhone = $conn->prepare("INSERT INTO phoneNumber(email,phoneNumber) VALUES (?,?)");
       $insertPhone->bind_param("ss",$email,$phoneNumber);
       if($insertPhone->execute()){
            $insertPhone->close();
            return "true";
       }
       else {
        return "false";
       }
    }

    function insertChildren($email,$jsonDate,$conn){
        $errorFlag="false";
        foreach($jsonDate as $child){
            $name = $conn->real_escape_string($child['childName']);
            $age = $conn->real_escape_string($child['childAge']);
            $gender = $conn->real_escape_string($child['childGender']);
            $grade = $conn->real_escape_string($child['grade']);
            $insertChildStmt = $conn->prepare("INSERT INTO children (parentEmail,childName,childAge,childGender,childGrade) VALUES (?,?,?,?,?)");
            $insertChildStmt->bind_param("sssss",$email,$name,$age,$gender,$grade);
            if($insertChildStmt->execute()){
                $errorFlag = "true";
            }
            else {
                $errorFlag = "false";
            }
        }
        return $errorFlag;
    }
    $conn->close();
?>