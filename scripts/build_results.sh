#!/bin/bash
. ./params.sh

mkdir -p $DIR
rm -f $TOP_DIR/current
ln -s $RDIR $TOP_DIR/current

for i in `seq 1 $RUN_COUNT`;
do
echo "INFO: Run number: $i"
CMD="java $JAVA_ARGS -Dca.elite=$ELITE -Dca.keepBest=$KEEP_BEST -Dca.populationCount=$POPULATION -Dca.radius=$RADIUS -Dca.maxRunCount=$MAX_ITER -Dca.rule=$RULE -Dca.minRadius=$MIN_RADIUS -Dca.maxRadius=$MAX_RADIUS $JAVA_MAIN > $DIR/$i.res"
echo $CMD
eval $CMD
done

echo "Building plots"

./build_plots.sh
./build_avg_rad_plots.sh
./build_dist_plots.sh
./build_stat_plots.sh