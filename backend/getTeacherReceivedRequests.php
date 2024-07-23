<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST['email'])){
        $email = $_POST['email'];
        $notAccepted = -1;
        $isThereRequests = isThereRequests($email, $conn);
        if($isThereRequests == true){
            $getRequestsStmt = $conn->prepare("
            SELECT 
                psr.requestId,
                psr.postId,
                psr.parentEmail,
                psr.teacherEmail,
                psr.requestDate,
                psr.isAccepted,
                children_data.children,
                tpr.courses,
                tpr.availabilityForJob,
                tpr.duration,
                tpr.educationLevel,
                tpr.startTime,
                tpr.endTime,
                tpr.startDate,
                tpr.endDate,
                tpr.price,
                tpr.location,
                tpr.teachingMethod,
                p.firstName,
                p.lastName,
                GROUP_CONCAT(DISTINCT pn.phoneNumber SEPARATOR ',') as parentPhoneNumbers
            FROM 
                parentSentRequest psr
            JOIN 
                (
                    SELECT 
                        cr.requestId,
                        GROUP_CONCAT(
                            DISTINCT CONCAT(
                                '{\"childId\":', c.childId, 
                                ',\"childName\":\"', c.childName, 
                                '\",\"childGrade\":\"', c.childGrade, 
                                '\",\"childAge\":', c.childAge, 
                                ',\"childGender\":', c.childGender, 
                                ',\"childRequestId\":', cr.childRequestId, 
                                '}'
                            ) SEPARATOR '|'
                        ) AS children
                    FROM 
                        childrenrequests cr
                    JOIN 
                        children c ON cr.childId = c.childId
                    GROUP BY 
                        cr.requestId
                ) AS children_data ON psr.requestId = children_data.requestId
            JOIN 
                teacherpostrequest tpr ON psr.postId = tpr.postId
            JOIN 
                phonenumber pn ON psr.parentEmail = pn.email
            JOIN
                profile p ON psr.parentEmail = p.email
            WHERE 
                psr.teacherEmail = ? 
            AND psr.isAccepted = ?
            GROUP BY 
                psr.requestId,
                psr.postId,
                psr.parentEmail,
                psr.teacherEmail,
                psr.requestDate,
                psr.isAccepted,
                children_data.children,
                tpr.courses,
                tpr.availabilityForJob,
                tpr.duration,
                tpr.educationLevel,
                tpr.startTime,
                tpr.endTime,
                tpr.startDate,
                tpr.endDate,
                tpr.price,
                tpr.location,
                tpr.teachingMethod,
                p.firstName,
                p.lastName;");
            $isAccepted = -1 ;
            $getRequestsStmt->bind_param("si", $email,$isAccepted);
            if($getRequestsStmt->execute()){
                $result = $getRequestsStmt->get_result();
                $requestsData = [];
                 while ($row = $result->fetch_assoc()) {
                    $childrenArray = explode('|', $row['children']);
                    foreach ($childrenArray as &$child) {
                        $child = json_decode($child, true);
                    }
                $row['children'] = $childrenArray;
                $requestsData[] = $row;
            }
            echo json_encode($requestsData);
            } else {
                echo "Error";
            }
        } else {
            echo "No Requests";
        }
    } else {
        echo "Connection Error";
    }

    function isThereRequests($email, $conn){
        $stmt = $conn->prepare("SELECT * FROM parentSentRequest WHERE teacherEmail = ?;");
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