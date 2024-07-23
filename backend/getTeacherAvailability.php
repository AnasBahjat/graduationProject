<?php
    require_once 'req.php';
    if($_SERVER['REQUEST_METHOD'] && isset($_POST['email'])){
        $email = $_POST['email'];
        $stmt = $conn->prepare("SELECT availability AS avail from teacher where email = ?");
        $stmt->bind_param("s",$email);
        if($stmt->execute()){
            $result = $stmt->get_result();
            $row = $result->fetch_assoc();
            if($row && !empty($row['avail'])){
                echo $row['avail'];
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