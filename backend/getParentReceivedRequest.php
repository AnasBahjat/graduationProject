<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["parentEmail"])){
        $parentEmail = $_POST['parentEmail'];

        $notAccepted = -1;
        $isThereRequests = isThereRequests($parentEmail, $conn);
        if($isThereRequests == true){
            $getRequestsStmt = $conn->prepare("
            SELECT 
                tsr.requestId,
                tsr.parentEmail,
                tsr.teacherEmail,
                tsr.requestDate,
                tsr.isAccepted,
                tm.matchingId,
                tm.childId,
                tm.choseDays,
                tm.courses,
                tm.location,
                tm.teachingMethod,
                tm.startTime,
                tm.endTime,
                tm.startDate,
                tm.endDate,
                tm.priceMin,
                tm.priceMax,
                c.childName,
                c.childAge,
                c.childGender,
                c.childGrade,
                p.firstName,
                p.lastName,
                p.birthDate ,
                GROUP_CONCAT(DISTINCT pn.phoneNumber SEPARATOR ',') as teacherPhoneNumbers
            FROM 
                teacherSentRequest tsr
            JOIN 
                teacherMatching tm ON tsr.matchingId = tm.matchingId
            JOIN 
                children c ON tm.childId = c.childId
            JOIN 
                phonenumber pn ON tsr.parentEmail = pn.email
            JOIN
                profile p ON tsr.teacherEmail = p.email
            WHERE 
                tsr.parentEmail = ? AND tsr.isAccepted = ?
            GROUP BY 
                tsr.requestId, 
                tm.matchingId, 
                tm.childId,
                c.childName, 
                c.childAge, 
                c.childGender, 
                c.childGrade, 
                p.firstName, 
                p.lastName, 
                p.birthDate
        ");
        $isAccepted = -1 ;
        $getRequestsStmt->bind_param("si",$parentEmail,$isAccepted);
        if($getRequestsStmt->execute()){
            $result = $getRequestsStmt->get_result();
            $requestDate = [];
            while($row = $result->fetch_assoc()){
                $requestDate[] = $row;
            }
            if(empty($requestDate)){
                echo "No Requests";
            }
            else {
                echo json_encode($requestDate);
            }
        }
        else {
            echo "Error";
        }
        }
        else if($isThereRequests=="[]"){
            echo "No Requests";
        }
        else {
            echo "No Requests";
        }
    }
    else {
        echo "Connection Error";
    }

    function isThereRequests($email, $conn){
        $stmt = $conn->prepare("SELECT * FROM teacherSentRequest WHERE parentEmail = ?;");
        $stmt->bind_param("s", $email);

        if($stmt->execute()){
            $requestsData = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
            $jsonData = json_encode($requestsData);
            if($jsonData == "[]"){
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    $conn->close();
?>