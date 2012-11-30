#!/bin/bash

. ./params.sh

RESULT_DIR=$TOP_DIR/current

for i in $( ls $RESULT_DIR/*.res ); do
	DAT_FILE=`echo $i | sed -e 's/\.res/-dist.dat/'`
    PDF_FILE=`echo $i | sed -e 's/\.res/-dist.pdf/'`
	cat $i | grep RESULT | sed -e 's/.*t=//; s/\,.*dist\:/ /; s/[0-9]* = //g; s/\([0-9]\),\([0-9]\)/\1.\2/g; s/,//g' > $DAT_FILE

	RADS=`head -1 $DAT_FILE | wc -w`

	PLOT="plot "
	for R in `seq 2 $RADS`; do
	PLOT="$PLOT '$DAT_FILE' using 1:$R with lines title 'r=$((R-1))'"
	
	if [ $R -lt $RADS ] ; then
	PLOT="$PLOT,"
	fi
	done

	echo -e "set term 'pdf' \n \
set output '$PDF_FILE' \n \
set title 'Radius distribution' \n \
set xlabel 'iteration no.' \n \
$PLOT
" | gnuplot
	
done



