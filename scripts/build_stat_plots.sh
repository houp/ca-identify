#!/bin/bash

. ./params.sh

RESULT_DIR=$TOP_DIR/current

for i in $( ls $RESULT_DIR/*.res ); do
	DAT_FILE=`echo $i | sed -e 's/\.res/-cs.dat/'`
    PDF_FILE=`echo $i | sed -e 's/\.res/-cs.pdf/'`
	cat $i | grep RESULT | sed -e 's/.*t=//; s/, .*cs=/ /; s/, \}.*//; s/\, / /g' > $DAT_FILE

	echo -e "set term 'pdf' \n \
set output '$PDF_FILE' \n \
set title 'Crossover effect' \n \
set xlabel 'iteration no.' \n \
set key outside \n \
set yrange [0:1] \n \
plot '$DAT_FILE' using 1:2 with lines title 'fitness increase', '$DAT_FILE' using 1:3 with lines title 'fitness same', '$DAT_FILE' using 1:4 with lines title 'fitness decreased'" | gnuplot
	
done

for i in $( ls $RESULT_DIR/*.res ); do
	DAT_FILE=`echo $i | sed -e 's/\.res/-ms.dat/'`
    PDF_FILE=`echo $i | sed -e 's/\.res/-ms.pdf/'`
	cat $i | grep RESULT | sed -e 's/.*t=//; s/, .*ms=/ /; s/, \}.*//; s/\, / /g' > $DAT_FILE

	echo -e "set term 'pdf' \n \
set output '$PDF_FILE' \n \
set title 'Mutation effect' \n \
set xlabel 'iteration no.' \n \
set key outside \n \
set yrange [0:1] \n \
plot '$DAT_FILE' using 1:2 with lines title 'fitness increase', '$DAT_FILE' using 1:3 with lines title 'fitness same', '$DAT_FILE' using 1:4 with lines title 'fitness decreased'" | gnuplot
	
done


for i in $( ls $RESULT_DIR/*.res ); do
	DAT_FILE=`echo $i | sed -e 's/\.res/-cms.dat/'`
    PDF_FILE=`echo $i | sed -e 's/\.res/-cms.pdf/'`
	cat $i | grep RESULT | sed -e 's/.*t=//; s/, .*cms=/ /; s/, \}.*//; s/\, / /g' > $DAT_FILE

	echo -e "set term 'pdf' \n \
set output '$PDF_FILE' \n \
set title 'Crossover + Mutation effect' \n \
set xlabel 'iteration no.' \n \
set key outside \n \
set yrange [0:1] \n \
plot '$DAT_FILE' using 1:2 with lines title 'fitness increase', '$DAT_FILE' using 1:3 with lines title 'fitness same', '$DAT_FILE' using 1:4 with lines title 'fitness decreased'" | gnuplot
	
done



