#!/bin/bash

#load the parameters to run the specific test from  config/publisher-config.properties 
. config/subscriber-config.properties 

#parameter for broker
MIN=$1
increment=$2
MAX=$3
key=$4
groupName=$5
token=$6

#check if all parameters are provided for test
if [[ $1 = "" || $2 = "" || $3 = "" || $4 = "" || $5 = "" || $6 = "" ]]; then
echo "No parameter supplied :  valid sequence is <./run-broker-statistics.sh>  <MIN> <increment> <MAX> <key> <groupName> <token>"
else

#parameter for sar test 
interval=1
totalRun=$(( $MAX / $increment )) 
runForSec=$(( $totalRun * $shutdownDelay + 5 ))

#start the sar test for specified sec & o/p of sar is stored in  "../../export/data-files/" dir

#sar test is disabled because all statistics data is captured by other utilities
#to enable sar test uncomment following two lines 
 
 #echo "sar-test for  broker-statistics started [$runForSec sec]..."
 #./broker-sar-export.sh $token $key $interval $runForSec  > ../../export/data-files/broker-sar-temp.txt   &
 
#start the iteration upto MAX limit  
echo "Broker statistics test started..."
for (( j=$MIN; j <= $MAX; j=j+$increment )) 
  do

#Reinitialize token value 
token=$6-$j


	#get the PID of broker process
	brokerPid=`ps -ef | grep HornetQBootstrapServer| grep -v grep | awk '{print $2}'`

	#calls the CPU statistics script with required parameter & dump data to "log/" dir
	./run-cpu-statistics.sh $brokerPid $token $interval  broker-cpu $((shutdownDelay+5)) >> logs/broker-cpu.log &
	#get the PID of CPU statistics process 
	cpuPid=$!

	#calls the MEMORY statistics script with required parameter & dump data to "log/" dir
	./run-memory-statistics.sh $brokerPid  $token $interval broker-memory $((shutdownDelay+5)) >> logs/broker-memory.log &
	#get the PID of MEMORY statistics process
	memoryPid=$!

	#calls the DISK statistics script with required parameter & dump data to "log/" dir
	./run-disk-statistics.sh $brokerPid  $token $interval  broker-disk $((shutdownDelay+5)) >> logs/broker-disk.log &
	#get the PID of DISK statistics process
	diskPid=$!

	#calls the NETWORK statistics script with required parameter & dump data to "log/" dir
	./run-network-statistics.sh $((shutdownDelay+5)) $token $interval  broker-network >> logs/broker-network.log &
	#get the PID of NETWORK statistics process
	networkPid=$!

	#calls the HEAP statistics script with required parameter & dump data to "log/" dir
	./run-heap-statistics.sh $brokerPid  $token $interval $((shutdownDelay+5))  broker-heap >> logs/broker-heap.log 2>&1 &
	#get the PID of HEAP statistics process
	heapPid=$!
	
	echo "Gathering broker statistics .." 
	sleep $((shutdownDelay+5))
	echo "broker statistics test [$j] finish.."
	read -p "Press [Enter] key to continue.. or Ctr-C to quit"


	
  done
fi