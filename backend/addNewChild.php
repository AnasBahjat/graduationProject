<?php
    require_once 'req.php';
    if($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['parentEmail']) && isset($_POST['childName'])
    && isset($_POST['childAge']) && isset($_POST['childGender']) && isset($_POST['childGrade'])){
        $parentEmail = $_POST['parentEmail'];
        $childName = $_POST['childName'];
        $childAge = $_POST['childAge'];
        $childGender = $_POST['childGender'];
        $childGrade = $_POST['childGrade'];

        $insertChildStmt = $conn->prepare("INSERT INTO children (parentEmail,childName,childAge,childGender,childGrade) VALUES(?,?,?,?,?)");
        $insertChildStmt->bind_param("sssss",$parentEmail,$childName,$childAge,$childGender,$childGrade);
        if($insertChildStmt->execute()){
            $insertChildStmt->close();
            echo "Done";
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