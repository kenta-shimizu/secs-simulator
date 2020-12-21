#!/bin/sh

path_src="src/main/java/com"
path_bin="bin"
path_export_jar="SwingSecsSimulator.jar"
main_class="com.shimizukenta.secssimulator.swing.SwingSecsSimulator"
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
-e ${main_class} \
-C ${path_bin} .
