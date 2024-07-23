<?php
        require_once 'req.php';
        if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['parentEmail'])){
                    $parentEmail = $_POST['parentEmail'];
            
                    $stmt1 = $conn->prepare("SELECT 
                        startDate,
                        endDate,
                        startTime,
                        endTime,
                        choseDays,
                        childId
                    FROM 
                        parentChildrenCourse 
                    WHERE 
                        parentEmail = ?");
                    $stmt1->bind_param("s", $parentEmail);
                    if($stmt1->execute()){
                        $result1 = $stmt1->get_result();
                        $parentCourses = [];
                        $stmt1->close();
                        while ($row = $result1->fetch_assoc()) {
                            $parentCourses[] = $row;
                        }
                        echo json_encode(['parentCourses' => $parentCourses]);  
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
