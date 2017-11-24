#!/bin/bash

for i in {0..10}
do
  echo "识别card$i"
  tesseract card${i}.tif test/card${i}_eng -l eng
  tesseract card${i}.tif test/card${i}_card -l card
  tesseract card${i}.tif test/card${i}_num -l num
   #ffmpeg -i card${i}.png tif/card${i}.tif
done
