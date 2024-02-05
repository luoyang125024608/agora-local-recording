pid=`ps -ef | grep record.jar | grep -v grep |awk '{print $2}'`
if [ $pid ]; then
    echo oa-admin is  running pid=$pid
    kill -9 $pid
    echo killing...
    sleep 3
fi
nohup java -jar record.jar >/dev/null 2>&1 &
