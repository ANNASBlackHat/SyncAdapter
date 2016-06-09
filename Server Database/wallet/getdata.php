<?php
require 'dbconfig.php';

$sql = "SELECT * FROM data";
$result = $conn->query($sql);

$data = array();
if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        $data[] = $row;
    }
} 
print json_encode($data);
$conn->close();
?>