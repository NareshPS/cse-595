HADOOP_HOME=/home/hadoop4/hadoop_install/hadoop

mkdir -p classes
mkdir -p bld

javac -Xlint:deprecation -classpath "lib/hadoop-0.20.2-core.jar;lib/commons-cli-1.2.jar" -d classes src/*.java

# get the deps
cp lib classes -R

# we dont need hadoop libs
rm -f classes/lib/*hadoop*.jar

jar -cvf bld/DistanceCalculator.jar -C classes .

rm -rf classes

