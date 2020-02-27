#!/bin/bash
#
#=====================================================
#
#  预处理日志文件
#
#=====================================================


#set java env
export JAVA_HOME=/home/kobe/apps/jdk1.8.0_171
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH

#set hadoop env
export HADOOP_HOME=/home/kobe/apps/hadoop-2.6.4
export PATH=${HADOOP_HOME}/bin:${HADOOP_HOME}/sbin:$PATH


#获取时间信息
day_before=`date -d'-1 day' +%Y-%m-%d`
year=`date -d$day_before +%Y`
month=`date -d$day_before +%m`
day=`date -d$day_before +%d`


#待处理日志存放的目录
workdir=/flume/weblog/workdir/

#预处理输出结果_基本格式
log_pre=/data/weblog/basestyle/
#预处理输出结果_PageView
log_pageview=/data/weblog/pageview/
#预处理输出结果_VisitView
log_visitview=/data/weblog/visitview/
#预处理程序类名
preprocess_class="washLog.BaseStyle"
#PageView处理类
pageview_class="washLog.ViewOne"
#VisitView处理类
visitview_class='washLog.ViewTwo'
#文件名
filename='part-r-00000'
#读取日志文件的目录，判断是否有当日待处理的目录(如：2016-03-18)
files=`hadoop fs -ls $workdir | grep $day_before | wc -l`
echo 存在$files个目录
if [ $files -gt 0 ]; then
    #提交mr任务job运行
    echo "running..    hadoop jar /home/kobe/weblog.jar $preprocess_class $workdir$day_before $log_pre$day_before"
    hadoop jar /home/kobe/weblog.jar $preprocess_class $workdir$day_before $log_pre$day_before
    echo "预处理运行结果： $?"
else
    exit
fi
if [ $? -eq 0 ];then
    #提交mr任务job运行
    echo "running..    hadoop jar /home/kobe/weblog.jar $pageview_class $log_pre$day_before/$filename $log_pageview$day_before"
    hadoop jar /home/kobe/weblog.jar $pageview_class $log_pre$day_before/$filename $log_pageview$day_before
    echo "pageview运行结果： $?"
else
    exit
fi
if [ $? -eq 0 ];then
    #提交mr任务job运行
    echo "running..    hadoop jar /home/kobe/weblog.jar $visitview_class $log_pre$day_before/$filename $log_visitview$day_before"
    hadoop jar /home/kobe/weblog.jar $visitview_class $log_pre$day_before/$filename $log_visitview$day_before
    echo "visitview运行结果： $?"
else
    exit
fi
