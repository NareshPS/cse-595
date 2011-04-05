USERNAME=`whoami`
HPATH=/user/$USERNAME/wap
FLICKR_PATH="${HPATH}/tagpander"

URLS_FILE="./urls.txt"

hadoop fs -copyFromLocal $URLS_FILE "${FLICKR_PATH}/urls.txt"
hadoop fs -rmr "${FLICKR_PATH}/images"

# download images
time hadoop jar GistCalculator.jar DownloadImages "${FLICKR_PATH}/urls.txt" "${FLICKR_PATH}/images"

