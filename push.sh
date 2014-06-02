#!/bin/bash

echo 'post-receive' > do-post-receive
git add .
git commit -a -m 'post-receive'
git push open
