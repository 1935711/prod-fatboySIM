#!/bin/bash

javac -d ./build App.java
cd build
jar cmf ../App.mf App.jar App.class ../App.java
