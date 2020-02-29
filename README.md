# Grammlex
An LR(1) Parsing Tool written in Java with an MIT license.

## Status
Grammlex is in development and is not ready for use. 

## Features
* Reads a grammar in a subset of the ANTLR4 format.
* Converts terms with modifiers, * + and ?, to lower level terms without them
* Computes the FIRST set of all (left side) variables
* Prints grammar details

## Install and Build
First, make sure you have the java compiler (javac) installed.
```
$ git clone https://github.com/ironmeld/Grammlex
$ cd Grammlex
$ make
```
## Usage
Create a grammar file, or use a provided example:
```
$ cat examples/mfield.g4
S: document*;
document: BOM? HEADER value+ TRAILER;
value: map;
map: ID COLON STRING NL;
```
Run the program:
```
$ java -jar BUILD/grammlex.jar examples/mfield.g4
Extended Rules:
S: document*;
document: BOM? HEADER value+ TRAILER;
value: map;
map: ID COLON STRING NL;

Rules:
0: S': S;
1: S: document_repeat;
2: document: value_repeat1 HEADER value+ TRAILER;
3: value: map;
4: map: ID COLON STRING NL;
5: document_repeat: epsilon;
6: document_repeat: document document_repeat;
7: value_repeat1: value;
8: value_repeat1: value value_repeat1;
9: BOM_opt: epsilon;
10: BOM_opt: BOM;

Start Variable: S

Variables:
BOM_opt
value_repeat1
S
document
value
map
document_repeat

Terminals:
BOM
HEADER
COLON
STRING
ID
TRAILER
NL

First Sets:
BOM_opt: [epsilon, BOM]
value_repeat1: [ID]
S: [epsilon, ID]
S': [epsilon, ID]
document: [ID]
value: [ID]
map: [ID]
document_repeat: [epsilon, ID]
```
### Acknowledgments
A significant portion of the LR(1) code is derived from
https://github.com/amirhossein-hkh/LR-Parser,
which is MIT licensed. See COPYRIGHT file.
