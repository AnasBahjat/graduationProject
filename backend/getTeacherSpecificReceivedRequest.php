<?php

    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST['requestId'])){
        $requestId = $_POST["requestId"];
        $stmt = $conn->prepare("
            SELECT 
                psr.requestId,
                psr.postId,
                psr.parentEmail,
                psr.teacherEmail,
                psr.requestDate,
                psr.isAccepted,
                GROUP_CONCAT(
                    CONCAT(
                        '{\"childId\":', c.childId, 
                        ',\"childName\":\"', c.childName, 
                        '\",\"childGrade\":\"', c.childGrade, 
                        '\",\"childAge\":', c.childAge, 
                        ',\"childGender\":', c.childGender, 
                        ',\"childRequestId\":', cr.childRequestId, 
                        '}'
                    ) SEPARATOR '|'
                ) AS children,
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
                GROUP_CONCAT(DISTINCT pn.phoneNumber SEPARATOR ',') as parentPhoneNumbers
            FROM 
                parentSentRequest psr
            JOIN 
                childrenrequests cr ON psr.requestId = cr.requestId
            JOIN 
                children c ON cr.childId = c.childId
            JOIN 
                teacherpostrequest tpr ON psr.postId = tpr.postId
            JOIN 
                phonenumber pn ON psr.parentEmail = pn.email
            WHERE 
                psr.requestId = ?
            GROUP BY 
                psr.requestId,
                psr.postId,
                psr.parentEmail,
                psr.teacherEmail,
                psr.requestDate,
                psr.isAccepted,
                tpr.courses,
                tpr.availabilityForJob,
                tpr.duration,
                tpr.startTime,
                tpr.endTime,
                tpr.startDate,
                tpr.endDate,
                tpr.price,
                tpr.location,
                tpr.teachingMethod
        ;");
        $stmt->bind_param("i",$requestId);
        if($stmt->execute()){
            if($stmt->execute()){
                $result = $stmt->get_result();
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
            }
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