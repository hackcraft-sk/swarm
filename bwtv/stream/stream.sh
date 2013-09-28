#! /bin/bash
./capture_screen.sh $1 $2 | \
tee >(./sendto_file.sh) | \
#tee >(./sendto_twitch.sh) | \
cat > /dev/null
