#!/bin/bash
FLUMEBIN=`dirname "$0"`
FLUMEBIN=`cd "$FLUMEBIN";pwd`

display_help (){
	echo "USAGE: $0 trace|exception|slowsql|business /path/to/conf-file [-daemon]."
}

if [ $# -lt 2 ];
then 
	display_help
	exit 1
fi
PID_FILE=""
log_type=$1
shift
if [[ $log_type = "trace" || $log_type = "exception" || $log_type = "slowsql" || $log_type = "business" ]];
then
	PID_FILE="/var/run/flume_${log_type}.pid"
	if [ ! -d "/var/run" ];
	then
		mkdir -p "/var/run"
	fi
	conf_file=$1
	shift
	if [ ! -f "$conf_file" ];
	then 
		echo "${conf_file} isn't exsit!"
		exit 1
	fi
	is_daemon=$1
	if [[ $is_daemon = "-daemon" ]];
	then
		if [ -f "$PID_FILE" ];
		then
			if kill -0 `cat "$PID_FILE"` > /dev/null 2>&1; then
				echo "flume ${log_type} already running as process "`cat "$PID_FILE"`
				exit 0
			fi
		fi
		nohup $FLUMEBIN/flume-ng agent --name agent --conf $FLUMEBIN/../conf --conf-file ${conf_file} > /dev/null 2>&1 &
		if [ $? -eq 0 ];
		then 
			if /bin/echo -n $! > "$PID_FILE"
			then
				sleep 1
				echo STARTED
			else
				echo FAILED TO WRITE PID
				exit 1
			fi
		else 
			echo SERVER DO NOT START
			exit 1
		fi
	else
		$FLUMEBIN/flume-ng agent --name agent --conf $FLUMEBIN/../conf --conf-file ${conf_file}
	fi
else 
	echo "Please choose a valid log type from [trace|exception|slowsql|business]!"
	exit 1
fi
