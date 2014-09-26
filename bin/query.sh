#!/bin/bash

javac ../src/DriverClass.java ../src/Stemmer.java ../src/ParseXML.java ../src/EditData.java ../src/Search.java ../src/DocIdObject.java ../src/FileOperation.java ../src/WordInQueueNode.java ../src/WordObject.java

cp ../src/*.class .

java DriverClass query $1
