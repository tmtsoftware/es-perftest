
#!/bin/sh

#checkout and test dir passed from build.sh
#checkout_dir is the svn checkout destination dir
#test_dir is the where the final mbsuite dir  resides
checkout_dir=$1
test_dir=$2
mbsuite_packager_path=$3


cd $checkout_dir

#check if "export/" dir and it's subdirectories exist if no then create those dir otherwise proceed
if [[ ! -d  $checkout_dir/mbsuite/export/ksar   || ! -d  $checkout_dir/mbsuite/export/bcsuite || ! -d  $checkout_dir/mbsuite/export/data-files || ! -d  $checkout_dir/mbsuite/export/csv ]]; then
    mkdir -p  $checkout_dir/mbsuite/export/ksar
    mkdir -p  $checkout_dir/mbsuite/export/bcsuite
    mkdir -p  $checkout_dir/mbsuite/export/data-files
    mkdir -p  $checkout_dir/mbsuite/export/csv
fi

cd 
cd $checkout_dir

echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mBuilding mbsuite-publisher-base:\033[0m"
echo -e "\033[1m==============================================\033[0m"

#Builds mbsuite-publisher-base
cd mbsuite-publisher-base/
ant 
cd ..
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mExiting mbsuite-publisher-base.\033[0m"
echo -e "\033[1m==============================================\033[0m"

#Builds mbsuite-subscriber-base
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mBuilding mbsuite-subscriber-base:\033[0m"
echo -e "\033[1m==============================================\033[0m"

cd mbsuite-subscriber-base/
ant
cd ..
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mExiting mbsuite-subscriber-base.\033[0m"
echo -e "\033[1m==============================================\033[0m"


#copy the mbsuite-publisher-base.jar & mbsuite-subscriber-base.jar 
#into mbsuite-addons/lib/ for the purpose of building mbsuite-addons 
cp mbsuite-publisher-base/build/dist/mbsuite-publisher-base.jar  mbsuite-addons/lib/
cp mbsuite-subscriber-base/build/dist/mbsuite-subscriber-base.jar  mbsuite-addons/lib/


#Builds mbsuite-addons
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mBuilding mbsuite-addons:\033[0m"
echo -e "\033[1m==============================================\033[0m"
cd mbsuite-addons/
ant 
cd ..
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mExiting mbsuite-addons.\033[0m"
echo -e "\033[1m==============================================\033[0m"


#Builds mbsuite-utilities
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mBuilding mbsuite-utilities:\033[0m"
echo -e "\033[1m==============================================\033[0m"
cd mbsuite-utilities/
ant 
cd ..
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mExiting mbsuite-utilities.\033[0m"
echo -e "\033[1m==============================================\033[0m"

#copy mbsuite-utilities.jar into mbsuite/lib/
cp mbsuite-utilities/build/dist/mbsuite-utilities.jar mbsuite/lib/

#copy the mbsuite-publisher-base.jar , mbsuite-subscriber-base.jar &mbsuite-addons.jar
#into /mbsuite/lib/ 
echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mMoving jars to mbsuite/lib:\033[0m"
echo -e "\033[1m==============================================\033[0m"

cp mbsuite-addons/build/dist/mbsuite-addons.jar  mbsuite/lib
cp mbsuite-addons/lib/mbsuite-publisher-base.jar  mbsuite/lib
cp mbsuite-addons/lib/mbsuite-subscriber-base.jar  mbsuite/lib

#dos2unix mbsuite/bin
cd mbsuite/bin
sed -i -e 's/\r$//' *.*
cd ../..

cd mbsuite/bin/ext
sed -i -e 's/\r$//' *.*
cd ../../..

cd mbsuite/config
sed -i -e 's/\r$//' *.*
cd ../..

cd mbsuite/bin/ext/config
sed -i -e 's/\r$//' *.*
cd ../../../..

#dos2unix for build.sh
cd $mbsuite_packager_path/mbsuite-packager/bin/
sed -i -e 's/\r$//' *.*
cd ../../..
cd /

chmod -R 777 $test_dir/

#copy  $checkout_dir/mbsuite/lib/ into $test_dir/lib/ 
#this is else case in ./checkout-build.sh
#$checkout_dir & $test_dir is specified in begining of this file
cp -avr $checkout_dir/mbsuite/lib/.  $test_dir/lib/. 

#copy the mbsuite-addons/ext-lib into mbsuite/ext-lib/ & $test_dir/ext-lib/
cp -avr $checkout_dir/mbsuite-addons/ext-lib/.  $checkout_dir/mbsuite/ext-lib/. 
cp -avr $checkout_dir/mbsuite-addons/ext-lib/.  $test_dir/ext-lib/.  
 
chmod -R 777 $test_dir/


#dos2unix for $test_dir/bin/
cd  $test_dir/bin/
sed -i -e 's/\r$//' *.*
cd ../..

cd  $test_dir/config/
sed -i -e 's/\r$//' *.*
cd ../..

cd  $test_dir/bin/ext/
sed -i -e 's/\r$//' *.*


echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mMoving jars finish.\033[0m"
echo -e "\033[1m==============================================\033[0m"

echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mBuilding task is done.\033[0m"
echo -e "\033[1m==============================================\033[0m"

echo -e "\033[1m==============================================\033[0m"
echo -e "\033[1mNow you can use '$test_dir' dir for your test \033[0m"
echo -e "\033[1m==============================================\033[0m"




