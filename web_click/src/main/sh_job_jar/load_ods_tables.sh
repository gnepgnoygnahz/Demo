#!/bin/bash
#
#=====================================================
#
#  
#
#=====================================================
#


#set java env
export JAVA_HOME=/home/kobe/apps/jdk1.8.0_171
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH

#set hadoop env
export HADOOP_HOME=/home/kobe/apps/hadoop-2.6.4
export PATH=${HADOOP_HOME}/bin:${HADOOP_HOME}/sbin:$PATH

#获取时间信息
date_detail=`date '+%Y-%m-%d_%H:%M:%S'`
date_before=`date -d '-1 day' +%Y-%m-%d`
year=`date -d $date_before +%Y`
month=`date -d $date_before +%m`
day=`date -d $date_before +%d`
hour=`date +%H`
minute=`date +%M`
second=`date +%S`

#预处理输出结果_基本格式
log_pre=/data/weblog/basestyle/
#预处理输出结果_PageView
log_pageview=/data/weblog/pageview/
#预处理输出结果_VisitView
log_visitview=/data/weblog/visitview/

#待处理日志存放的目录
workdir=/flume/weblog/workdir/

#hive中weblog数据库中的ods表
ods_click_pageviews=weblog.ods_click_pageviews
ods_click_visit=weblog.ods_click_visit
ods_weblog_base=weblog.ods_weblog_base
ods_weblog_origin=weblog.ods_weblog_origin

#加载原始表
HQL_origin="load data inpath '$workdir$date_before' into table $ods_weblog_origin partition(datestr='$date_before')"
echo $HQL_origin
/home/kobe/apps/hive/bin/hive -e "$HQL_origin"

#加载基础表
HQL_base="load data inpath '$log_pre$date_before' into table $ods_weblog_base partition(datestr='$date_before')"
echo $HQL_base
/home/kobe/apps/hive/bin/hive -e "$HQL_base"

#加载pageviews表
HQL_pageviews="load data inpath '$log_pageview$date_before' into table $ods_click_pageviews partition(datestr='$date_before')"
echo $HQL_pageviews
/home/kobe/apps/hive/bin/hive -e "$HQL_pageviews"

#加载visit表
HQL_visit="load data inpath '$log_visitview$date_before' into table $ods_click_visit partition(datestr='$date_before')"
echo $HQL_visit
/home/kobe/apps/hive/bin/hive -e "$HQL_visit"


