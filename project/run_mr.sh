USERNAME=`whoami`
HPATH=/user/$USERNAME/wap

INPUT_LIST=$1
INPUT_FILENAME=`basename $INPUT_LIST`

hadoop fs -rm "${HPATH}/gistin/$INPUT_FILENAME" 
hadoop fs -copyFromLocal $INPUT_LIST "${HPATH}/gistin/$INPUT_FILENAME"

hadoop fs -rm "${HPATH}/libraries/libgist.so"
make
hadoop fs -copyFromLocal libgist.so "${HPATH}/libraries/libgist.so" 

./build.sh

hadoop fs -rmr "${HPATH}/gistout"
time hadoop jar GistCalculator.jar GistCalculatorMR "${HPATH}/gistin/$INPUT_FILENAME" wap/gistout "${HPATH}/libraries/libgist.so#libgist.so"
rm -rf gistout
hadoop fs -get /user/rohith/wap/gistout gistout
