#!/bin/bash

NAME=$1
FONT=$2
NUM=$3

if  [ ! -n "$NAME" ] ;then
    echo "******输入语言名******"
    exit
fi

if  [ ! -n "$FONT" ] ;then
    FONT='font'
fi

if  [ ! -n "$NAME" ] ;then
    NUM=0
fi

echo "input $NAME $FONT $NUM"

tesseract ${NAME}.${FONT}.exp${NUM}.tif ${NAME}.${FONT}.exp${NUM} box.train
unicharset_extractor ${NAME}.${FONT}.exp${NUM}.box
mftraining -F font_properties -U unicharset -O ${NAME}.unicharset ${NAME}.${FONT}.exp${NUM}.tr
cntraining ${NAME}.${FONT}.exp${NUM}.tr

mv normproto ${NAME}.normproto
mv inttemp ${NAME}.inttemp
mv pffmtable ${NAME}.pffmtable
mv shapetable ${NAME}.shapetable

combine_tessdata ${NAME}.
