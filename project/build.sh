HADOOP_HOME=/home/hadoop4/hadoop_install/hadoop
mkdir -p classes

javac -Xlint:deprecation -classpath $HADOOP_HOME/hadoop-0.20.0-lydia-core.jar:$HADOOP_HOME/hadoop-0.20.0-lydia-tools.jar:$HADOOP_HOME/lib/commons-logging-1.0.4.jar:$HADOOP_HOME/lib/commons-logging-api-1.0.4.jar:$HADOOP_HOME/lib/commons-cli-2.0-SNAPSHOT.jar -d classes *.java
jar -cvf GistCalculator.jar -C classes .
