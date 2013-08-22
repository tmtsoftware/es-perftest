es-perftest
===========

Event Services Performance Tests and Prototyping

Instructions-- 

	* Download ant extract "mbsuite-packager.zip" 
	
	* Go to "constants.properties" by typing "cd mbsuite-packager/config/" and enter the Git-url with username & password.The "checkouot_dir" is the path where your "mbsuite-packager" dir is present & test_dir is the path where you want to store  "mbsuite" dir.
	
	* Go to "mbsuite-packager/bin/" & perform following steps-
	
		--> Type "chmod 777 *.sh" for Read-Write-Execute permission to scripts.
		
		--> Type "dos2unix *.sh " or type "sed -i -e 's/\r$//' *.*" for converting files to bash compatible mode.
		
		--> For github checkout type "./build-git.sh" it will checkout the code from Git & build it.
		
		--> For svn checkout type "./build.sh" it will checkout the code from svn & build it.
		
		
	* The "mbsuite" test dir  is available in "test_dir" path in "constants.properties" file 
	
	* Prerequisite for running the the test are available in "setup-info/prerequisite.txt" & these utilities are mandatory.
		
	* Before running the test first create db-tables using the create table scripts that are available in "setup-info/create-table-queries.txt".
		
	* After creation of db-tables go to "mbsuite/config/common-settings.properties" and enter db-url and other parameters. 

	* Go to <"test_dir" path>/mbsuite 
	
		--> For Sample publisher & subscriber run the "mbsuite/bin/test-publisher.sh" & "mbsuite/bin/test-subscriber.sh"
		
		--> For additional tests go to "mbsuite/bin/ext/" and run the tests.
		
		--> For running these test you need two terminal window one for publisher script & other for subscriber script.
		
		--> Additional details for running the test is available in "setup-info/User Manual for MBSuite.pdf" 
		
	* The "mbsuite/bin/ext" dir contains following bash scripts for publisher program

		publisher sccripts ---
 
		1) run-publisher-vary-size.sh // this is varying message-size test with constant publishers & topics for publisher
		2) run-publisher-vary-pub.sh	// this is varying publishers test with constant message-size & topics  for publisher
		3) run-publisher-vary-sub.sh // this is varying subscribers test with constant message-size,publishers & topics  for publisher
		4) run-publisher-vary-topics.sh // this is varying topics test with varying publisher too,and constant message-size  for publisher
		5) run-publisher-latency-test.sh // this is latency test with constant message-size,publishers & topics  for publisher
		6) run-publisher-reliability-tets.sh // this is reliability test for publisher.

	* The "mbsuite/bin/ext" dir also contains following bash scripts for subscriber program.

		subscriber scripts--

		1)run-subscriber-vary-size.sh // this is varying message-size test with constant subscribers & topics for subscriber
		2)run-subscriber-vary-pub.sh	// this is varying publishers test with constant message-size & topics  for subscriber
		3)run-subscriber-vary-sub.sh // this is varying subscribers test with constant message-size,subscribers & topics  for subscriber
		4)run-subscriber-vary-topics.sh // this is varying topics test with varying subscribers too,and constant message-size  for subscriber
		5)run-subscriber-latency-test.sh // this is latency test with constant message-size,subscribers & topics  for subscriber
		6)run-subscriber-reliability-tets.sh // this is reliability test for subscriber.

		
	* All above scripts loads property file from path  "mbsuite/bin/ext/config/publisher-config.properties " for publisher scripts & "mbsuite/bin/ext/config/subscriber-config.properties " for subscriber scripts.

	* The property file "mbsuite/bin/ext/config/publisher-config.properties " contains attributes which are required to run publisher program.

	* The property file "mbsuite/bin/ext/config/subscriber-config.properties " contains attributes which are required to run subscriber program.
	
	* For running the messging API specific tests All above script requires the configuration attributes to be set for both publisher & subscriber which are available 
	  in the file "mbsuite/config/publisher-config.tmpl" & "mbsuite/config/subscriber-config.tmpl". 
	  
	* All above scripts contain statistics reports such as CPU,DISK,MEMORY,NETWORK,HEAP  for running the NETWORK statistics script we need nitstat utility to be installed
	   in your machine , more about nitstat and it's downloaded URL is available in "prerequisite.txt" file.
	   
	* All test details are dumped to respective files in "mbsuite/bin/ext/logs" dir.
	
	* All test data is dumped to respective files in "mbsuite/export/data-files" dir.
	
	* The exported .jpeg format output of sar & ksar test is available in "mbsuite/data-files/ksar/publisher/images"  & "mbsuite/data-files/ksar/subscriber/images"
	
	* The "mbsuite/ext-lib" dir contains external jars which are required to run the messaging API specific test.
	
	

