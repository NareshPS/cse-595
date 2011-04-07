USERNAME=`whoami`
HPATH=/user/$USERNAME/wap
FLICKR_PATH="${HPATH}/tagpander"

URLS_FILE="./urls.txt"

hadoop fs -copyFromLocal $URLS_FILE "${FLICKR_PATH}/metas.txt"
hadoop fs -rmr "${FLICKR_PATH}/tags"

# download images
time hadoop jar GistCalculator.jar TagCoOccurenceCounter "${FLICKR_PATH}/metas.txt" "${FLICKR_PATH}/tags"

