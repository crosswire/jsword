#!/bin/sh

# extssh makes cvs get the dir perms wrong, but we can fix it
chgrp -R -c jswordcvs /cvs/jsword/*
