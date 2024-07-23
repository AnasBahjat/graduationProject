<?php 
require 'req.php' ; 

$email=$_POST['email'];
$password=$_POST['password'];

function checkIfEmailExists($emailToTest,$conn){
    $testSql=$conn->prepare("select email from profile where email=?;");
    $testSql->bind_param('s',$emailToTest);
    $resultOfTestSQL='';
    if($testSql->execute()){
        $testSql->bind_result($resultOfTestSQL);
        $testSql->fetch();
        if(empty($resultOfTestSQL)){
            $testSql->close();
            return 'email does not exist';
        }
        else {
            $testSql->close();
            return 'exists';
        }
    }
}

function checkPasswordIfTrue($existsEmail,$conn,$passwordToCheck){
    $checkPasswordSQL=$conn->prepare('select password from profile where email=?;');
    $checkPasswordSQL->bind_param('s',$existsEmail);
    $resultOfPasswordSQL='';
    if($checkPasswordSQL->execute()){
        $checkPasswordSQL->bind_result($resultOfPasswordSQL);
        $checkPasswordSQL->fetch();
        if(password_verify($passwordToCheck,$resultOfPasswordSQL)){
            $checkPasswordSQL->close();
            return 'true password';
        }
        else {
            $checkPasswordSQL->close();
            return 'wrong password';
        }
    }
}

function getAllData($email,$conn){
    $sql=$conn->prepare('select * from profile where profile.email =?;');
    $sql->bind_param('s',$email);
    if($sql->execute()){
       $resultOfSQL = $sql->get_result()->fetch_all(MYSQLI_ASSOC);
        return json_encode($resultOfSQL);
    }
    else {
        echo "ERROR";
    }
}

$checkEmail=checkIfEmailExists($email,$conn);
if($checkEmail=='email does not exist'){
    echo 'email does not exist';
}
else {
    $checkPassowrd=checkPasswordIfTrue($email,$conn,$password);
    if($checkPassowrd == 'true password'){
        $jsonResult=getAllData($email,$conn);
        echo $jsonResult;
    }
    else {
        echo 'wrong password';
    }
}

$conn->close();
?>