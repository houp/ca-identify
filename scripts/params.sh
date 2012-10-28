#!/bin/bash
JAVA_ARGS="-Xmx2g -Dcom.amd.aparapi.executionMode=JTP -cp CaIdentify.jar:aparapi.jar"
JAVA_MAIN=ga.aparapi.AparapiRunner 
RUN_COUNT=2
POPULATION=200
MIN_RADIUS=1
MAX_RADIUS=4
RULE=2294967295
RADIUS=2
MAX_ITER=1000
KEEP_BEST="true"
ELITE=20

TIMESTAMP=`date +%H%M%S_%d%m%Y`
TOP_DIR=results	
RDIR=r_$TIMESTAMP
DIR=$TOP_DIR/$RDIR
