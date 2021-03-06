es-perftest
===========

Event Services Performance Tests and Prototyping


Instructions to Build from source on GitHub-- 
	
	* For Git-checkout ensure that you have proper internet access on your centOS machine.

	* Extract "mbsuite-packager.zip" it creates "mbsuite-packager" dir.
	
	* Type "vi mbsuite-packager/config/parameter.properties" and enter the GitHub-url with username,password,checkout_dir,test_dir.
	  checkout_dir - is where you want the source code to be checked out
	  test_dir is  - where you will run the tests from. We recommend having a separate folder for running tests.
	 	
	* Go to "cd mbsuite-packager/bin/" & perform following steps-
			
			--> If you have "mbsuite-packager.zip" & you want to checkout code from GitHub & then build the code then  run " ./checkout-build.sh "

			--> If you facing problem with git-chechout using mbsuite-packager  then just go to "https://github.com/tmtsoftware/es-perftest" and choose option "Download ZIP". 
				 --> It will download the entire git directory into zip file.
				 --> So you downloaded the GitHub repository as a zip (es-perftest-master.zip) & you want to only build the source code without checking out  
					 then go to mbsuite-packager/config/parameter.properties & value of " checkout_dir " will be path of unzipped folder "es-perftest-master"
					 i.e if you unzipped "es-perftest-master.zip" into /root/git-checkout/source  then value to "checkout_dir" will be 
					 checkout_dir=/root/git-checkout/source/es-perftest-master   &   "test_dir" is dir path where you want the mbsuite test dir 
					 i.e test_dir=/root/git-checkout
					 then go to mbsuite-packager/bin & 
					 
					 run  " ./build.sh " 	
			
	* Ensure that during the build there no errors. After the build complete a "mbsuite" directory gets created under the path specified for parameter "test_dir" . From hereon this mbsuite dir is 
	  the base dir for benchmarking framework and scripts.
	
	* Prerequisite for running the test are available in "setup-info/prerequisite.txt" on GitHub repository & these utilities are mandatory.
		
	* Before you start executing the tests , create db-tables using the create table scripts that are available in "setup-info/create-table-queries.txt".
		
	* After creation of db-tables go to "<"test_dir" path>/mbsuite/config/common-settings.properties" and enter db-url and other parameters. 

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
	
	* The "mbsuite/ext-lib" dir contains external jars which are required to run the messaging API specific test.
	
--> Hornetq -	
	
	* Download "hornetq-2.3.0.Final-bin.tar.gz" from download link provided in "setup-info/prerequisite.txt".
	
	* After download create the hornetq broker setup directory & copy the "hornetq-2.3.0.Final-bin.tar.gz" into it.
		e.g
			cd /root
			mkdir hornetq-setup
			cd /hornetq-setup
		- Then copy "hornetq-2.3.0.Final-bin.tar.gz" into /root/hornetq-setup	
			cp -avr hornetq-2.3.0.Final-bin.tar.gz /root/hornetq-setup/
		- Extarct the hornetq-2.3.0.Final-bin.tar.gz	
			tar -zxvf  hornetq-2.3.0.Final-bin.tar.gz
			
	* We alos provide "hornetq-configuration.xml" file in "es-perftest/setup-info" , copy the "hornetq-configuration.xml" into 
	  "/root/hornet-setup/hornetq-2.3.0.Final/config/stand-alone/non-clustered/ directory"
		
	* For more information on hornetq refer the " User manual for hornetq.pdf " in "es-perftest/setup-info"
	
--> Redis -

	* Download "redis-2.6.16.tar.gz" from download link provided in "setup-info/prerequisite.txt".
	
	* After download create the redis broker setup directory & copy the "redis-2.6.16.tar.gz" into it.
		e.g
			cd /root
			mkdir redis-setup
			cd /redis-setup
		- Then copy "redis-2.6.16.tar.gz" into /root/redis-setup	
			cp -avr redis-2.6.16.tar.gz /root/redis-setup/
		- Extarct the redis-2.6.16.tar.gz
			tar -zxvf  redis-2.6.16.tar.gz
			
	* We alos provide "redis.conf" file in "es-perftest/setup-info" , copy the "redis.conf" into 
	  "/root/redis-setup/redis-2.6.16/ directory"
		
	* For more information on redis refer the " User manual for redis.pdf " in "es-perftest/setup-info"
	
--> OpenSpliceDDS -
	
	* Download OpenspliceDDS from "http://www.prismtech.com/download-documents/1374"
	  it then downloads the "OpenSpliceDDSV6.3.130716OSS-HDE-x86_64.linux-gcc4.1.2-glibc2.5.tar.gz" file.
	  
	* After download create the ospl setup directory & copy the "OpenSpliceDDSV6.3.130716OSS-HDE-x86_64.linux-gcc4.1.2-glibc2.5.tar.gz" into it.
		e.g
			cd /opt
		- Then copy "OpenSpliceDDSV6.3.130716OSS-HDE-x86_64.linux-gcc4.1.2-glibc2.5.tar.gz" into /opt	
			cp -avr OpenSpliceDDSV6.3.130716OSS-HDE-x86_64.linux-gcc4.1.2-glibc2.5.tar.gz  /opt/
		- Extarct the file
			tar -zxvf  OpenSpliceDDSV6.3.130716OSS-HDE-x86_64.linux-gcc4.1.2-glibc2.5.tar.gz


