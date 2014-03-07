#please update HORNETQ_HOME variable as per HornetQ installation directory path on your machine, below mentioned is an example
HORNETQ_HOME=/root/TMT-phase-3/hornetq-setup/hornetq-2.3.0.Final

#please update JAVA_HOME variable as per JAVA installation directory path on your machine, below mentioned is an example
JAVA_HOME=/usr/lib/jvm/java-1.6.0

#please update ANT_HOME variable as per ANT installation directory path on your machine, below mentioned is an example
ANT_HOME=/root/ant19/apache-ant-1.9.2

export PATH=${PATH}:${JAVA_HOME}/bin

export HORNETQ_HOME

export PATH=${PATH}:${ANT_HOME}/bin

echo JAVA_HOME=$JAVA_HOME 
echo ANT_HOME=$ANT_HOME
echo HORNETQ_HOME=$HORNETQ_HOME