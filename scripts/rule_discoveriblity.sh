#!/bin/bash

. ./params.sh

RESULT_DIR=$TOP_DIR/current

FOUND=0
ITERS=0
COUNT=0

for i in $( ls $RESULT_DIR/*.res ); do
			COUNT=$((COUNT+1))
            found_best=`cat $RESULT_DIR/$i | grep "INFO: Got ideal solution!"`
            if [ -n "$found_best" ]; then
                FOUND=$((FOUND+1));
                iter=`cat $RESULT_DIR/$i | grep "INFO: Iterations :" | sed -e 's/.* ://'`;
                ITERS=$((ITERS+iter));
                fi
done

echo "-------------- SUMMARY: ----------------------------"
echo "Ideal solution found: $FOUND time out of $COUNT"
echo "Avg. iteration count: $((ITERS / $FOUND))"
