#!/bin/bash

function usage(){
    cat<<EOF
    usage: 
    $0 map.osm
    
    Arguments:
        map.osm : The openstreetmap exported .osm file
EOF
}

if [ ! -e "$1" ]; then
    usage
    exit -1
fi

MAP="$1"
FNAME=$(echo "${MAP}" | cut -d'.' --complement -f2-)

echo "==== Netconvert ===="
netconvert --osm "${MAP}" --output-file "${FNAME}.net.xml" --no-internal-links
echo "==== Activitygen ===="
activitygen --net-file "${FNAME}.net.xml" --stat-file "${FNAME}.stat.xml" --output-file "${FNAME}.rou.xml" --random --duration-d 1
