#!/bin/bash


#export CLASSPATH=$CLASSPATH:../ext-lib/commons-math3-3.0.jar:../ext-lib/jcommon-1.0.17.jar:../ext-lib/jfreechart-1.0.14.jar:../ext-lib/log4j-1.2.16.jar:../lib/mbsuite-utilities.jar:../ext-lib/mysql-connector-java-5.1.25-bin.jar:../config/

java -cp "../../ext-lib/*":"../../export/data-files/*":"../../lib/*":"../../config/"  com.persistent.bcsuite.charts.reports.ReportGenerator $1


#java -cp $CLASSPATH com.bcsuite.graphs.GraphPlotter


