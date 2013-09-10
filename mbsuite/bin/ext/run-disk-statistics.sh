#!/bin/bash
deviceName=$4
token=$2
interval=$3
PID=$1
runSec=$5   
if [[ $1 = "" || $2 = "" || $3 = "" || $4 = "" ]]; then
echo "No parameter supplied valid sequence is <./filename>  <PID> <token> <interval> <deviceName>"
else
#echo "Gathering disk Statistics..."
if [ -z "$runSec" ];then
pidstat -h -d -p  $PID $interval  > ../../export/data-files/$deviceName.txt 
else
pidstat -h -d -p  $PID $interval $runSec   > ../../export/data-files/$deviceName.txt 
fi

#echo "Gathering disk Statistics done..."
./device-statistics-to-db.sh $deviceName $token 
fi