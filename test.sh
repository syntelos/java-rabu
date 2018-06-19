#!/bin/bash

if jarf=$(2>/dev/null ls java-rabu-*.jar)&& [ -n "${jarf}" ]&&[ -d test ]
then
    prefix="java -jar ${jarf} "
    num=1

    for src in $(2>/dev/null ls test/*.txt | egrep -v 'lorum-ipsum' )
    do
	printf -v fnum "%3d" ${num}

	if suffix=$(egrep -v '^#' ${src} | head -n 1 )&&[ -n "${suffix}" ]&&
	    result=$(egrep -v '^#' ${src} | tail -n 1 )&&[ -n "${result}" ]
	then

	    cmd="${prefix}${suffix}"
	    echo "${fnum}: ${cmd}"
	    echo

	    ${cmd}; rc=$?

	    echo
	    echo "${fnum}: ${cmd}"
	    echo "${fnum}: result: ${rc}, expected: ${result}"

	    if [ ${result} != ${rc} ]
	    then
		exit 1
	    else
		num=$(( ${num} + 1 ))
	    fi
	else
	    cat<<EOF>&2
$0 error loading command and result from test '${src}'.
EOF
	    exit 1
	fi
    done
    exit 0
else
    cat<<EOF>&2
$0 error, jar file 'java-rabu-*.jar' not found.  Try 'javab build'.
EOF
    exit 1
fi
