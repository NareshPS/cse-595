HADOOP_HOME=/home/hadoop4/hadoop_install/hadoop
mkdir -p classes

javac -Xlint:deprecation -classpath "lib/hadoop-0.20.2-core.jar;lib/commons-cli-1.2.jar" -d classes *.java
jar -cvf GistCalculator.jar -C classes .
