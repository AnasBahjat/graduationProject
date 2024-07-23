<?php


require_once 'req.php';


if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $teacherEmail = $_POST["email"];
    $startDate1 = $_POST["startDate1"];
    $endDate1 = $_POST["endDate1"];
    $startTime1 = $_POST["startTime1"];
    $endTime1 = $_POST["endTime1"];
    $daysString1 = $_POST["days1"];

    $overlap = checkOverlap($teacherEmail,$startDate1, $startTime1, $endDate1, $endTime1, $daysString1, $conn);

    if ($overlap) {
        echo "There is a conflict between the new schedule and existing data.";
    } else {
        echo "There is no conflict. The new schedule can be added.";
    }
} 

else {
    echo "Connection Error";
}

function checkOverlap($teacherEmail,$startDate1, $startTime1, $endDate1, $endTime1, $daysString1, $conn) {
    $days1 = explode(" , ", $daysString1);

    $dayNames = [
        "Sun" => "Sunday",
        "Mon" => "Monday",
        "Tues" => "Tuesday",
        "Wed" => "Wednesday",
        "Thurs" => "Thursday",
        "Fri" => "Friday",
        "Sat" => "Saturday"
    ];

    $days1MySQL = array_map(function($day) use ($dayNames) {
        return $dayNames[$day];
    }, $days1);

    $sql = "SELECT COUNT(*) AS count_overlap
    FROM teacherCourse tc
    JOIN teacherPostTable tp ON tc.postId = tp.postId
    WHERE 
    tc.teacherEmail = ?
    (
        (tp.startDate <= ? AND tp.endDate >= ?)
        OR (tp.startDate <= ? AND tp.endDate >= ?)
        OR (tp.startDate >= ? AND tp.endDate <= ?)
    )
    AND (
        (tp.startTime < ? AND tp.endTime > ?)
        OR (tp.startTime < ? AND tp.endTime > ?)
        OR (tp.startTime >= ? AND tp.endTime <= ?)
    )
    AND (
        FIND_IN_SET(DAYNAME(tp.startDate), ?)
        OR FIND_IN_SET(DAYNAME(tp.endDate), ?)
        OR (tp.startDate < tp.endDate AND FIND_IN_SET(DAYNAME(tp.startDate + INTERVAL 1 DAY), ?))
    )
    ;";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ssssssssssssssss",$teacherEmail,$startDate1, $endDate1, $startDate1, $endDate1, $startDate1, $endDate1,
                      $startTime1, $endTime1, $startTime1, $endTime1, $startTime1, $endTime1,
                      implode(" , ", $days1MySQL), implode(" , ", $days1MySQL), implode(" , ", $days1MySQL));
    $stmt->execute();
    $countOverlap=0;
    $stmt->bind_result($countOverlap);
    $stmt->fetch();
    $stmt->close();

    return $countOverlap > 0;
}

$conn->close();
?>
