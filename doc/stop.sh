pid=`ps -ef | grep record | grep -v grep |awk '{print $2}'`
if [ $pid ]; then
    echo record is  running pid=$pid
    kill -9 $pid
    echo stoping...
    sleep 3
fi
echo stoped
