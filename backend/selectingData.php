<?php
    require_once 'req.php';

    $stmt=$conn->prepare("select * from profile;");
    $stmt2=$conn->prepare("select * from phonenumber;");
    $stmt3=$conn->prepare("select * from address;");

    $stmt->execute();
    $result = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);

    $stmt2->execute();
    $result2 = $stmt2->get_result()->fetch_all(MYSQLI_ASSOC);

    $stmt3->execute();
    $result3 = $stmt3->get_result()->fetch_all(MYSQLI_ASSOC);


    echo json_encode($result);
    echo "\r\n\n";
    echo json_encode($result2);
    echo "\r\n\n";
    echo json_encode($result3);
?>