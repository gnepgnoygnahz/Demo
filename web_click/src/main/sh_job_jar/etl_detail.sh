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

HQL_detail="
insert into table weblog.ods_weblog_detail partition(datestr='$date_before')
select c.valid,c.remote_addr,c.remote_user,c.time_local,
substring(c.time_local,0,10) as daystr,
substring(c.time_local,12) as tmstr,
substring(c.time_local,6,2) as month,
substring(c.time_local,9,2) as day,
substring(c.time_local,11,3) as hour,
c.request,
c.status,
c.body_bytes_sent,
c.http_referer,
c.ref_host,
c.ref_path,
c.ref_query,
c.ref_query_id,
c.http_user_agent
from
(SELECT 
a.valid,
a.remote_addr,
a.remote_user,a.time_local,
a.request,a.status,a.body_bytes_sent,a.http_referer,a.http_user_agent,b.ref_host,b.ref_path,b.ref_query,b.ref_query_id 
FROM weblog.ods_weblog_origin a where a.datestr='$date_before'
LATERAL VIEW 
parse_url_tuple(regexp_replace(http_referer, \"\\\"\", \"\"), 'HOST', 'PATH','QUERY', 'QUERY:id') b 
as ref_host, ref_path, ref_query, ref_query_id) c
"
echo $HQL_detail
/home/kobe/apps/hive/bin/hive -e "$HQL_detail"
echo 执行结果 $?
