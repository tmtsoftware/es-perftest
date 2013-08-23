#!/bin/sh

# dos2unix
sed -i 's/\r//' build.sh
sed -i 's/\r//' ../config/constants.properties
sed -i 's/\r//' dir-exist.sh

#load properties file 
#This file contain svn-url and checkout_dir test_dir
#checkout_dir is the svn checkout destination dir
#test_dir is the where the final mbsuite dir  resides

. ../config/constants.properties

#path for  test_dir where the final mbsuite resides
test_dir=$test_dir/mbsuite

#path for ant 
export ANT_HOME=$checkout_dir/mbsuite-packager/ant/dist
export PATH=${PATH}:${ANT_HOME}/bin

#checkout the code from svn
chmod 777 $checkout_dir/mbsuite-packager/ant/dist/bin/ant

echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mSVN Checkout:\033[0m"
echo -e "\033[1m==============================================\033[0m"
ant 
cd ..
echo -e "\033[1m==============================================\033[0m"
echo  -e "\033[1mFinish SVN Checkout.\033[0m"
echo -e "\033[1m==============================================\033[0m"
cd ..


#check if mbsuite dir is present 
#if no then copy entire mbsuite  
#if yes then copy only jars
if [ -d "$test_dir" ]
then
	echo "$test_dir/mbsuite directory  exists!"
	#call the dir-exist.sh which builds publisher,subscriber,addons,utilities
	exec $checkout_dir/mbsuite-packager/bin/./dir-exist.sh $checkout_dir $test_dir	
else
	echo "$test_dir/mbsuite directory not found!"
	cp -avr $checkout_dir/mbsuite $test_dir 
	#call the dir-exist.sh which builds publisher,subscriber,addons,utilities
	exec $checkout_dir/mbsuite-packager/bin/./dir-exist.sh $checkout_dir $test_dir
fi
