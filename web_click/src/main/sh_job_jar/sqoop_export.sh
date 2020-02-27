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
date_detail=`date '+%Y-%m-%d %H:%M:%S'`
date_before=`date -d '-1 day' +%Y-%m-%d`
year=`date -d $date_before +%Y`
month=`date -d $date_before +%m`
day=`date -d $date_before +%d`
hour=`date +%H`
minute=`date +%M`
second=`date +%S`

mysql_db_pwd=root
mysql_db_name=root

#导入数据到origin表
echo 'sqoop origin start'
/home/kobe/apps/sqoop/bin/sqoop export \
--connect "jdbc:mysql://kobe-01:3306/weblog" \
--username $mysql_db_name \
--password $mysql_db_pwd \
--table ods_weblog_origin \
--fields-terminated-by '\001' \
--export-dir /user/hive/warehouse/weblog.db/ods_weblog_origin/datestr=$date_before
echo 'sqoop origin end'

#导入数据到detail表
echo 'sqoop detail start'
/home/kobe/apps/sqoop/bin/sqoop export \
--connect "jdbc:mysql://kobe-01:3306/weblog" \
--username $mysql_db_name \
--password $mysql_db_pwd \
--table ods_weblog_detail \
--fields-terminated-by '\t' \
--export-dir /user/hive/warehouse/weblog.db/ods_weblog_detail/datestr=$date_before
echo 'sqoop detail end'

#导入数据到base表
echo 'sqoop base start'
/home/kobe/apps/sqoop/bin/sqoop export \
--connect "jdbc:mysql://kobe-01:3306/weblog" \
--username $mysql_db_name \
--password $mysql_db_pwd \
--table ods_weblog_base \
--fields-terminated-by '\t' \
--export-dir /user/hive/warehouse/weblog.db/ods_weblog_base/datestr=$date_before
echo 'sqoop base end'

#导入数据到pageviews表
echo 'sqoop pageviews start'
/home/kobe/apps/sqoop/bin/sqoop export \
--connect "jdbc:mysql://kobe-01:3306/weblog" \
--username $mysql_db_name \
--password $mysql_db_pwd \
--table ods_click_pageviews \
--fields-terminated-by '\t' \
--export-dir /user/hive/warehouse/weblog.db/ods_click_pageviews/datestr=$date_before
echo 'sqoop pageviews end'

#导入数据到visit表
echo 'sqoop visit start'
/home/kobe/apps/sqoop/bin/sqoop export \
--connect "jdbc:mysql://kobe-01:3306/weblog" \
--username $mysql_db_name \
--password $mysql_db_pwd \
--table ods_click_visit \
--fields-terminated-by '\t' \
--export-dir /user/hive/warehouse/weblog.db/ods_click_visit/datestr=$date_before
echo 'sqoop visit end'

