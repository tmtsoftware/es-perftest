1. Set environment variables i.e HORNETQ_HOME, JAVA_HOME, ANT_HOME inside setenv.sh file 

2. In each seprate console first run setenv.sh file to set environment variables before excuting any functionality 
   related to EventServiceAPI.  	

3. To run setenv.sh  type    . setenv.sh  or  source setenv.sh

4. To permanently set environment variables add contents of setenv.sh into .bash_profile file
	where path of this file is  /root/.bash_profile
	
5. After running setenv.sh you can run ./run.sh & ./stop.sh from anywhere regardless of dependency to run from 
   hornetq installation directory only
   
6. If you are facing issues to run ./run.sh & incase you get
	java.net.MalformedURLException: Local host name unknown: java.net.UnknownHostException: 
	exception while running run.sh , you need to first add machine name in /etc/host   file
-     Actual file contents  are 
                127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4  
				::1                localhost localhost.localdomain localhost6 localhost6.localdomain6  

-    Then you need to add machine name to this file (In this case our machine name is "TMT-Centos6.4" put your machine name there)
				127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4  TMT-Centos6.4
				::1         localhost localhost.localdomain localhost6 localhost6.localdomain6  TMT-Centos6.4

-	After this you will able to run ./run.sh   

7. Replace hornetq-configuration.xml  inside "config/stand-alone/non-clustered" dir of your hornetq installation directory with the hornetq-configuration.xml we provided you. 
	e.g HOME/hornetq-2.3.0.Final/config/stand-alone/non-clustered/
	    where lets suppose  HOME=/root
		
8. Replace 
			  <param key="host"  value="127.0.0.1"/>
	with 		  
			  <param key="host"  value=<Your-Hornetq-Broker-Machine-IP-Address>/>
			  
	In "hornetq-configuration.xml" file		  
	
9. Replace hornetq-beans.xml  inside "config/stand-alone/non-clustered" dir of your hornetq installation directory with the hornetq-beans.xml we provided you. 
		e.g HOME/hornetq-2.3.0.Final/config/stand-alone/non-clustered/
	    where lets suppose  HOME=/root
		
10. To monitor hornetq server remotely using jconsole 
	
	Replace- 
	 
	  <property name="port">${jnp.port:1099}</property>
      <property name="bindAddress">${jnp.host:127.0.0.1}</property>
      <property name="rmiPort">${jnp.rmiPort:1098}</property>
      <property name="rmiBindAddress">${jnp.host:127.0.0.1}</property>
   
   with - 
   
      <property name="port">${jnp.port:1099}</property>
      <property name="bindAddress">${jnp.host:<Your-Hornetq-Broker-Machine-IP-Address>}</property>
      <property name="rmiPort">${jnp.rmiPort:1098}</property>
      <property name="rmiBindAddress">${jnp.host:<Your-Hornetq-Broker-Machine-IP-Address>}</property>
	  
	 In  "hornetq-beans.xml" file
   