#!/bin/sh

# dos2unix
sed -i 's/\r//' build.sh
sed -i 's/\r//' ../config/parameter.properties
sed -i 's/\r//'  package-builder.sh

#load properties file 
#This file contain svn-url and checkout_dir test_dir
#checkout_dir is the svn checkout destination dir
#test_dir is the where the final mbsuite dir  resides

. ../config/parameter.properties

#path for  test_dir where the final mbsuite resides
test_dir=$test_dir/mbsuite
mbsuite_packager_path=`pwd`
cd ../..
mbsuite_packager_dir=`pwd` 
cd $mbsuite_packager_path

#path for ant 
export ANT_HOME=$mbsuite_packager_dir/mbsuite-packager/ant/dist
export PATH=${PATH}:${ANT_HOME}/bin

chmod 777 $mbsuite_packager_dir/mbsuite-packager/ant/dist/bin/ant


if [ -d "$checkout_dir/trunk" ]
then
mv -f $checkout_dir/trunk/*  $checkout_dir
rm -rf   $checkout_dir/trunk
rm -rf   $checkout_dir/branches
else
if [ -d "$test_dir" ]
then
	echo "$test_dir/mbsuite directory  exists!"
	#call the package-builder.sh which builds publisher,subscriber,addons,utilities
	./package-builder.sh $checkout_dir $test_dir $mbsuite_packager_dir	
else
	echo "$test_dir/mbsuite directory not found! creating mbsuite dir"
	cp -avr $checkout_dir/mbsuite $test_dir 
	#call the package-builder.sh which builds publisher,subscriber,addons,utilities
	./package-builder.sh $checkout_dir $test_dir $mbsuite_packager_dir
fi
fi










