#! /bin/bash

DATE=`date +%Y-%m-%d-%H-%M-%S`
FILE="stream-$DATE.flv"

ffmpeg \
-f mpegts \
-i - \
-acodec copy \
-vcodec copy \
-f flv "files/$FILE"
