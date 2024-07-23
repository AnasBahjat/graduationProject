<?php

require_once 'req.php';
if($_SERVER["REQUEST_METHOD"] == "POST"){
    $allMatchingDataStmt = $conn->prepare("SELECT *
     FROM 
     teacherMatching t,
     children c 
     WHERE  
     t.childId = c.childId
     AND t.isAccepted = -1");
    if($allMatchingDataStmt->execute()){
        $matchingData = $allMatchingDataStmt->get_result()->fetch_all(MYSQLI_ASSOC);
        echo json_encode($matchingData);
        $allMatchingDataStmt->close();
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