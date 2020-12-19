#!/bin/sh

path_src="src/main/java/com"
path_bin="bin"
path_export_jar="CliSecsSimulator.jar"
version="8"

# remove bin files
rm -Rf ${path_bin}

# mkdir bin
mkdir ${path_bin}

# compile-src
javac -d ${path_bin} \
--release ${version} \
$(find ${path_src} -name "*.java")


# jar
jar -c \
-f ${path_export_jar} \
-e com.shimizukenta.secssimulator.cli.CliSecsSimulator \
-C ${path_bin} .

