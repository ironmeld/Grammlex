# Grammlex
An LR(1) Parsing Tool with an MIT license.

## Status
Grammlex is in development and is not ready for use.

## Install and Build
First, make sure you have the java compiler (javac) installed.
```
$ git clone https://github.com/ironmeld/Grammlex
$ cd Grammlex
$ make
(cd src;javac -d ../BUILD org/grammlex/v1/Tool.java)
cp src/manifest.mf BUILD/
cd BUILD && jar cvfm grammlex.jar manifest.mf org/grammlex/v1/Tool.class
added manifest
adding: org/grammlex/v1/Tool.class(in = 430) (out= 299)(deflated 30%)
```
## Usage
```
$ java -jar BUILD/grammlex.jar 
Hello World!
```
### Acknowledgments
A significant portion of the LR(1) code is derived from
https://github.com/amirhossein-hkh/LR-Parser,
which is MIT licensed. See COPYRIGHT file.
