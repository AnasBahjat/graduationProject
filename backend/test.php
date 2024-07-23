<?php
        require_once 'req.php';
        if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['teacherEmail'])){
                    $teacherEmail = $_POST['teacherEmail'];
            
                    $stmt1 = $conn->prepare("SELECT 
                        startDate,
                        endDate,
                        startTime,
                        endTime,
                        availabilityForJob
                    FROM 
                        teacherCourse 
                    WHERE 
                        teacherEmail = ?");
                    $stmt1->bind_param("s", $teacherEmail);

                    $stmt2 = $conn->prepare("SELECT 
                            pcc.startDate,
                            pcc.endDate,
                            pcc.startTime,
                            pcc.endTime,
                            pcc.choseDays 
                    FROM 
                            parentChildrenCourse pcc ,
                            teachersentrequest tsr 
                    WHERE 
                            pcc.requestId = tsr.requestId 
                    AND 
                            tsr.teacherEmail = ?;");
                    $stmt2->bind_param("s", $teacherEmail);

                    if($stmt1->execute()){
                        $result1 = $stmt1->get_result();
                        $teacherCourses = [];
                        $stmt1->close();
                        while ($row = $result1->fetch_assoc()) {
                            $teacherCourses[] = $row;
                        }
                        if($stmt2->execute()){
                            $result2 = $stmt2->get_result();
                            $parentChildrenCourses = [];
                            $stmt2->close();
                            while ($row = $result2->fetch_assoc()) {
                                $parentChildrenCourses[] = $row;
                            }
                            //header('Content-Type: application/json');
                            echo json_encode([
                                'teacherCourses' => $teacherCourses,
                                'parentChildrenCourses' => $parentChildrenCourses
                            ]);
                        }
                        else {
                            echo "Error";
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
