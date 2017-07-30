#!/bin/bash

if [ $# -eq 0 ]; then
    ./gradlew --console plain run -q
else
    ./gradlew --console plain run -q -PappArgs="['${1}']"
fi
