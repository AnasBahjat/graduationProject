<?php
require_once 'req.php';

$firstname = $_POST['firstname'];
$lastname=$_POST['lastname'];
$email=$_POST['email'];
$gender=$_POST['gender'];
// $profileType=[]''
$password = $_POST['pass'];

$hashedPassword = password_hash($androidPassword, PASSWORD_DEFAULT);


echo json_encode($response);
?>
