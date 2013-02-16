#!/bin/sh

export PATH=/home/jsword/bin:$PATH
cd /home/jsword/build

/bin/rm -rf *

initialCheckout.sh

cp /home/dmsmith/cert/crosswire.keystore /home/jsword/build/jsword
