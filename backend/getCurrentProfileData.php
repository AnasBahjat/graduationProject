<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["email"])) {
        $email= $_POST['email'];
        $profileType = getProfileTypeForEmail($email,$conn);
        if($profileType === "0"){
            $getParentStmt = $conn->prepare("SELECT 
            p.email, 
            p.firstname, 
            p.lastname, 
            p.birthDate,
            par.idNumber,
            c.childId,
            c.parentEmail, 
            c.childName, 
            c.childAge, 
            c.childGender, 
            c.childGrade,
            GROUP_CONCAT(DISTINCT CONCAT(pn.phoneId , pn.phoneNumber) SEPARATOR ' | ') AS phoneNumbers,
            GROUP_CONCAT(DISTINCT CONCAT(a.addressId, ' , ' , a.city, ' , ', a.country) SEPARATOR ' | ') AS addresses
        FROM 
            profile p
        LEFT JOIN 
            parent par ON p.email = par.email
        LEFT JOIN 
            children c ON p.email = c.parentEmail
        LEFT JOIN 
            address a ON p.email = a.email
        LEFT JOIN 
            phoneNumber pn ON p.email = pn.email
        WHERE p.email = ?") ;
        $getParentStmt->bind_param("s",$email);
        $getParentStmt->execute();
        $result = $getParentStmt->get_result()->fetch_all(MYSQLI_ASSOC);
        echo json_encode($result);
        }
        else if($profileType === "1"){
            echo "mohammad"; 
        }
        else {
            echo "No Profile";
        }
    }
    else {
        echo "Connection Error";
    }

    function getProfileTypeForEmail($email,$conn){
        $stmt = $conn->prepare("SELECT profileType as profileType FROM profile WHERE email = ?");
        $stmt->bind_param("s",$email);
        if($stmt->execute()){
            $result = $stmt->get_result();
            $row = $result->fetch_assoc();
            if($row && $row['profileType'] !== null){
                return $row['profileType'];
            }
            else {
                return "No Profile";
            }
        }
        else {
            echo "Error";
        }
        
    }
    $conn->close();
?>