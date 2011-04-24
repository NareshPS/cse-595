USERNAME=`whoami`
HPATH=/user/$USERNAME/wap

INPUT_LIST=$1
INPUT_FILENAME=`basename $INPUT_LIST`

hadoop fs -rm "${HPATH}/gistin/$INPUT_FILENAME" 
hadoop fs -mkdir "${HPATH}/gistin"
hadoop fs -copyFromLocal $INPUT_LIST "${HPATH}/gistin/$INPUT_FILENAME"

hadoop fs -rm "${HPATH}/libraries/libgist.so"
hadoop fs -mkdir "${HPATH}/libraries"
make
hadoop fs -copyFromLocal libgist.so "${HPATH}/libraries/libgist.so" 

./build.sh

hadoop fs -rmr "${HPATH}/gistout"
time hadoop jar GistCalculator.jar GistCalculatorMR "${HPATH}/gistin/$INPUT_FILENAME" wap/gistout "${HPATH}/libraries/libgist.so#libgist.so"
rm -rf gistout
hadoop fs -get $HPATH/gistout gistout
