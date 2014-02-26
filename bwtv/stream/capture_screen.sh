#! /bin/bash

# input resolution
INRES="640x480"

# output resolution
OUTRES="640x480"

# offsets
OFFSET_X=$1
OFFSET_Y=$2

# target fps
FPS="12"

# quality
QUAL="medium"

echo $OFFSET_X
echo $OFFSET_Y

ffmpeg \
-f x11grab \
-s "$INRES" \
-r "$FPS" \
-i :0.0+"$OFFSET_X","$OFFSET_Y" \
-ab 96k \
-f alsa \
-ac 2 \
-i pulse \
-vcodec libx264 \
-crf 28 \
-preset "$QUAL" \
-s "$OUTRES" \
-acodec libmp3lame \
-ab 72k \
-ar 44100 \
-threads 0 \
-f mpegts -
