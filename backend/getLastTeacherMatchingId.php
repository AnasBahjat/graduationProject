<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "GET"){
        $stmt = $conn->prepare("SELECT MAX(matchingId) AS max_id FROM teachermatching;");
        if($stmt->execute()){
            $result = $stmt->get_result();
            $row = $result->fetch_assoc();
            if($row && $row['max_id'] !== null){
                echo $row['max_id'];
            }
            else if($row['max_id'] == null){
                echo "Empty Table";
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
?>
