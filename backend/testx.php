<?php
require_once 'req.php';


$email=$_POST['email'];
$password=$_POST['password'];

$retPass='';

$passwordSql=$conn->prepare("select password from profile where email=?;");
$passwordSql->bind_param('s',$email);
$passwordSql->execute();
$passwordSql->bind_result($retPass);
$passwordSql->fetch();



if(empty($retPass)){
    echo "Wrong email ...";
}
if(password_verify($password,$retPass)){
    echo "Passowr Confirmed";
}
else {
    echo $passwordToCompare;
    echo "\n$retPass";
    echo "\r\n";
    echo "\nwrong password ...";
}


$conn->close();
$passwordSql->close();

?>

