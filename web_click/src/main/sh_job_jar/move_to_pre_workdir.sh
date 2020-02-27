#!/bin/bash
#
#===========================================
#  程序功能：  将日志文件移动到处理目录
#  日志目录：  /flume/weblog/logdir
#  处理目录：  /flume/weblog/workdir
#===========================================


#set java env
export JAVA_HOME=/home/kobe/apps/jdk1.8.0_171
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH

#set hadoop env
export HADOOP_HOME=/home/kobe/apps/hadoop-2.6.4
export PATH=${HADOOP_HOME}/bin:${HADOOP_HOME}/sbin:$PATH

#flume采集日志目录
logdir=/flume/weblog/logdir/

#预处理程序工作目录
workdir=/flume/weblog/workdir/

#获取时间信息
date_before=`date -d'-1 day' +%Y-%m-%d`
year=`date -d$date_before +%Y`
month=`date -d$date_before +%m`
day=`date -d$date_before +%d`

#将日志目录的文件移动到工作目录
dirnum=`hadoop fs -ls $logdir | grep $date_before | wc -l`
filenum=`hadoop fs -ls $logdir$date_before | grep *.tmp | wc -l`
echo 存在$dirnum个目录需采集
if [ $dirnum -gt 0 ];then
    if [ $filenum -gt 0 ];then    
	sleep 3610
	if [ $filenum -gt 0 ];then 
	    echo `hadoop fs -ls $logdir$date_before | grep *.tmp` >> /data/weblog/unfinish.log
	fi
    fi
    hadoop fs -mv $logdir$date_before $workdir
    echo "=====success moved $logdir$date_before to $workdir====="
fi
