#!/bin/bash
gcc -o libencrypt_hom.so -lc -shared -fPIC -I${JAVA_HOME}/include -I${JAVA_HOME}/include/linux myJniFunc.c test_gmp.c -lgmp
