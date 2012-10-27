#!/bin/bash

. ./params.sh

RESULT_DIR=$TOP_DIR/current

for i in $( ls $RESULT_DIR/*.res ); do
	DAT_FILE=`echo $i | sed -e 's/\.res/.dat/'`
    PDF_FILE=`echo $i | sed -e 's/\.res/.pdf/'`
	cat $i | grep RESULT | sed -e 's/.*t=//;s/best_rule.*//; s/, [a-z_]*=/ /g;s/\([0-9]\),\([0-9]\)/\1.\2/g; s/,//' > $DAT_FILE

	echo -e "set term 'pdf' \n \
set output '$PDF_FILE' \n \
set title 'Fitness evolutions' \n \
set xlabel 'iteration no.' \n \
set key outside \n \
set yrange [0:1.1] \n \
plot '$DAT_FILE' using 1:2 with lines title 'max fitness', '$DAT_FILE' using 1:3 with lines title 'avg fitness'" | gnuplot
	
done



