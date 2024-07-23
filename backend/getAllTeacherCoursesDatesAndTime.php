<?php
        require_once 'req.php';
        if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['teacherEmail'])){
                    $teacherEmail = $_POST['teacherEmail'];
            
                    $stmt1 = $conn->prepare("SELECT 
                        startDate,
                        endDate,
                        startTime,
                        endTime,
                        availabilityForJob,
                        childId
                    FROM 
                        teacherCourse 
                    WHERE 
                        teacherEmail = ?");
                    $stmt1->bind_param("s", $teacherEmail);

                    if($stmt1->execute()){
                        $result1 = $stmt1->get_result();
                        $teacherCourses = [];
                        $stmt1->close();
                        while ($row = $result1->fetch_assoc()) {
                            $teacherCourses[] = $row;
                        }
                        echo json_encode(['teacherCourses' => $teacherCourses]);
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
