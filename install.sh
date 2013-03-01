#!/bin/bash


echo "Installing BIANA Package"

#echo "Compiling unification program"
g++ -Wno-deprecated ./src/biana/BianaDB/unify.cpp -o ./src/biana/BianaDB/unify
chmod 711 ./src/biana/BianaDB/unify

#echo "Installing BIANA Package"

if [ ! -z $1 ]; then
	#echo "The first option"
	#cp -r src/biana $1
	python setup.py install --install-purelib=$1 --optimize 2
	chmod +x $1/biana/BianaDB/unify
	echo
	echo "BIANA Package installed."
	echo
	echo "!!! Important Notice !!!"
	echo "Remember to add $1 to your PYTHONPATH. See you operating system manual for checking how to setup environment variables"
	echo
else
	#echo "The second option"
	python setup.py install --optimize 2
fi
