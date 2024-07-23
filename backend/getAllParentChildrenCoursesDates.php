<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"]=="POST" && isset($_POST["email"])){
        $email = $_POST['email'];
        $data = getParentCourses($email,$conn);
        if($data == "[]"){
            echo "No Courses";
        }
        else if($data){
            echo json_encode($data);
        }
        else{
            echo "Error";
        }
    }

    else {
        echo "Connection Error";
    }

    function getParentCourses($email,$conn){
        $isAccepted = 1;
        $sql = "SELECT DISTINCT pc.courseId , pc.startDate , pc.endDate , pc.startTime,pc.endTime , pc.choseDays
        ,tsr.requestId,pc.childId
        FROM
            parentchildrencourse pc
        JOIN
            teacherSentRequest tsr ON pc.teacherSentRequestId = tsr.requestId
        where
            pc.parentEmail = ?
        AND
            tsr.isAccepted = ?";
        
        if($stmt = $conn->prepare($sql)){
            $stmt->bind_param("si",$email,$isAccepted);
            $stmt->execute();
            $result = $stmt->get_result();
            $data = [];
            while($row = $result->fetch_assoc()){
                $data[] = $row;
            }
            $stmt->close();
            if(empty($data)){
                return "[]";
            }
            else {
                return $data;
            }
        }
        else {
            return false;
        }
    }

    $conn->close();
?>