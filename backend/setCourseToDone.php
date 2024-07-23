<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["courseId"]) &&
        isset($_POST["parentSentRequestId"])&&
        isset($_POST["teacherSentRequestId"])){
            $courseId = $_POST["courseId"];
            $parentSentRequestId = $_POST["parentSentRequestId"];
            $teacherSentRequestId = $_POST["teacherSentRequestId"];

            $isCourseExists = checkIfCourseExists($courseId,$parentSentRequestId,$teacherSentRequestId,$conn);
            if($isCourseExists == "teacherCourse"){
                $updateTeacherCourse1 = $conn->prepare("UPDATE teacherCourse set isCompleted = 1 WHERE courseId = ? 
                AND teacherSentRequestId = ? AND parentSentRequestId = ? ;");
                $updateTeacherCourse1->bind_param("iii",$courseId,$teacherSentRequestId,$parentSentRequestId);

                $updateParentCourse1  = $conn->prepare("UPDATE parentChildrenCourse set isCompleted = 1 WHERE parentSentRequestId = ? AND teacherSentRequestId = 0;");
                $updateParentCourse1->bind_param("i",$parentSentRequestId);

                if($updateTeacherCourse1->execute() && $updateParentCourse1->execute()){
                    echo "Done";
                }
                else {
                    echo "Error";
                }
            }
            else if($isCourseExists == "parentChildrenCourse"){
                $updateParentCourse2 = $conn->prepare("UPDATE parentChildrenCourse set isCompleted = 1 WHERE courseId = ? 
                AND teacherSentRequestId = ? AND parentSentRequestId = ? ;");
                $updateTeacherCourse2->bind_param("iii",$courseId,$teacherSentRequestId,$parentSentRequestId);

                $updateTeacherCourse2 = $conn->prepare("UPDATE teacherCourse set isCompleted = 1 WHERE parentSentRequestId = 0 AND teacherSentRequestId = ?;");
                $updateTeacherCourse2 ->bind_param("i",$teacherSentRequestId);

                if($updateParentCourse2->execute() && $updateTeacherCourse2->execute()){
                    echo "Done";
                }
                else {
                    echo "Error";
                }
            }
            else if($isCourseExists == "None"){
                echo "Not Exists";
            }

            else {
                echo "Error";
            }
        }
        else {
            echo "Connection Error";
        }

        function checkIfCourseExists($courseId,$parentRequestId,$teacherRequestId,$conn){
            $stmt1 = $conn->prepare("SELECT courseId from teacherCourse WHERE courseId = ? AND parentSentRequestId = ? AND teacherSentRequestId =?");
            $stmt1->bind_param("iii",$courseId,$parentRequestId,$teacherRequestId);
            
            $stmt2 = $conn->prepare("SELECT courseId from parentchildrencourse WHERE courseId = ? AND parentSentRequestId = ? AND teacherSentRequestId =?");
            $stmt2->bind_param("iii",$courseId,$parentRequestId,$teacherRequestId);

            if($stmt1->execute()){
                $stmt1->store_result();
    
                if ($stmt1->num_rows > 0) {
                    $stmt1->close();
                    return "teacherCourse";
                }
                if($stmt2->execute()){
                    $stmt2->store_result();
                    if($stmt2->num_rows > 0){
                        $stmt2->close();
                        return "parentChildrenCourse";
                    }
                    else {
                        return "None";
                    }
                }
                return "Error";
            }
            return "Error";
        }
?>