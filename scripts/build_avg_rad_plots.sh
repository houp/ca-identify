#!/bin/bash

# generate avg radius plots

. ./params.sh

RESULT_DIR=$TOP_DIR/current

for i in $( ls $RESULT_DIR/*.res ); do
	DAT_FILE=`echo $i | sed -e 's/\.res/-ar.dat/'`
    PDF_FILE=`echo $i | sed -e 's/\.res/-ar.pdf/'`
	cat $i | grep RESULT | sed -e 's/.*t=//; s/,.*avg_rad=/ /; s/, .*//; s/,/./' > $DAT_FILE

	echo -e "set term 'pdf' \n \
set output '$PDF_FILE' \n \
set title 'avg. radius' \n \
set xlabel 'iteration no.' \n \
unset key \n \
plot '$DAT_FILE' using 1:2 with lines, $RADIUS" | gnuplot
	
done




