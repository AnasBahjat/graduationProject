<?php 
    require_once 'req.php';



    $firstname = $_POST['firstname'];  
    $lastname = $_POST['lastname'];
    $email=$_POST['email'];
    $birthDate=$_POST['birthDate'];
    $gender=$_POST['gender'];
    $password=$_POST['password'];
    $profileType=$_POST['profileType'];
    $hashedPassword=password_hash($password,PASSWORD_DEFAULT);
    $doneInformation='0';


    

    function checkIfEmailExists($email,$conn){
        $testQuery=$conn->prepare('select * from profile where email=?;');
        $testQuery->bind_param('s',$email);
        if($testQuery->execute()){
            $testQuery->store_result();
            $numRows=$testQuery->num_rows;
            if($numRows > 0){
                return 'email exists';
            }
            else {
                return 'does not exist';
            }
        }
    }

    function insertNewRecord($email,$firstname,$lastname,$gender,$profileType,$birthDate,$hashedPassword,$doneInformation,$conn){
        $insertProfile=$conn->prepare("insert into profile values (?,?,?,?,?,?,?,?);");
        $insertProfile->bind_param("ssssssss",$email,$firstname,$lastname,$gender,$profileType,$birthDate,$hashedPassword,$doneInformation);


        $notType="";
        $notBody = "Fill in extra information to confirm your account ..";
        $notTitle="Confirm Account";
        $isRead = 0 ;
        if($profileType == '0'){
            $notType = '0';
        }
        else{
            $notType = '1';
        }
        

        

        if($insertProfile->execute()){
            return ;
        }
        else {
            echo "ERROR";
        }
        $insertProfile->close();
    }


    $testEmail=checkIfEmailExists($email,$conn);
    if($testEmail=='email exists'){
        echo 'exist';
    }

    else {
        insertNewRecord($email,$firstname,$lastname,$gender,$profileType,$birthDate,$hashedPassword,$doneInformation,$conn);
        echo 'True';
    }
    
    $conn->close();
?>