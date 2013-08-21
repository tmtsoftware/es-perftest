es-perftest
===========

Event Services Performance Tests and Prototyping

Instructions-- 

1)   Click "Download ZIP" option.

2)   It downloads "es-perftest-master.zip" file.

3)   After you download just unzip the "es-perftest-master" directory.

4)   It creates "es-perftest-master/mbsuite" & "es-perftest-master/setup-info" dir. 

5)   Go to "cd es-perftest-master/" and type  "chmod 777 mbsuite/ -R " it will give Read-Write-Execute permission to "mbsuite" dir.

6)   Prerequisite for running the the test are available in "es-perftest-master/setup-info/prerequisite.txt" & these utilities are mandatory.

7)   Before running the test first create db-tables using the create table scripts that are available in "es-perftest-master/setup-info/create-table-queries.txt".

8)   After creation of db-tables go to "es-perftest-master/mbsuite/config/common-settings.properties" and enter db-url and other parameters. 

9)   For Sample publisher & subscriber run the "es-perftest-master/mbsuite/bin/test-publisher.sh" & "es-perftest-master/mbsuite/bin/test-subscriber.sh"

10)  For additional tests go to "es-perftest-master/mbsuite/bin/ext/" and run the tests.