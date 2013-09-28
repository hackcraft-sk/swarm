#! /bin/bash

STREAM_KEY="live_36584852_I1P3sz3qDuI04hCW9G8hmo0Y5xG0uc"
URL="rtmp://live.justin.tv/app/$STREAM_KEY"

ffmpeg \
-f mpegts \
-i - \
-acodec copy \
-vcodec copy \
-f flv "$URL"
