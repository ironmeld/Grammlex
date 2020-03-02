# Grammlex
An LR(1) Parsing Tool written in Java with an MIT license.

## Status
Grammlex is in development and is not ready for use. 

## Features
* Reads a grammar in a subset of the ANTLR4 format.
* Converts terms with modifiers, * + and ?, to lower level terms without them
* Prints grammar details
* Computes the FIRST set of all (left side) variables
* Computes the FOLLOW set of all (left side) variables
* Computes the item sets for each LR(1) state and transitions to other states
* "Modern" code, free of warnings (IntelliJ + SonarLint circa 2020)
* Code has comments
* Includes tests

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
2: document: BOM_opt HEADER value_repeat1 TRAILER;
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
S: [epsilon, BOM, HEADER]
S': [epsilon, BOM, HEADER]
document: [BOM, HEADER]
value: [ID]
map: [ID]
document_repeat: [epsilon, BOM, HEADER]

Follow Sets:
BOM_opt: [HEADER]
value_repeat1: [TRAILER]
S: [$]
S': [$]
document: [BOM, $, HEADER]
value: [ID, TRAILER]
map: [ID, TRAILER]
document_repeat: [$]


Creating initial state from item: S': • S , [$]
    state after closure:
        item: S': • S , [$]
        item: S: • document_repeat , [$]
        item: document_repeat: • document document_repeat , [$]
        item: document_repeat: epsilon • , [$]
        item: document: • BOM_opt HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
        item: BOM_opt: epsilon • , [HEADER]
        item: BOM_opt: • BOM , [HEADER]

Processing transitions for state 0
   Process transition from state 0 for term BOM
        New state before closure:
            item: BOM_opt: BOM • , [HEADER]
        New state after closure:
            item: BOM_opt: BOM • , [HEADER]
        Created transition from 0 with BOM to new state 1
   Process transition from state 0 for term BOM_opt
        New state before closure:
            item: document: BOM_opt • HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
        New state after closure:
            item: document: BOM_opt • HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
        Created transition from 0 with BOM_opt to new state 2
   Process transition from state 0 for term S
        New state before closure:
            item: S': S • , [$]
        New state after closure:
            item: S': S • , [$]
        Created transition from 0 with S to new state 3
   Process transition from state 0 for term document
        New state before closure:
            item: document_repeat: document • document_repeat , [$]
        New state after closure:
            item: document_repeat: document • document_repeat , [$]
            item: document_repeat: • document document_repeat , [$]
            item: document_repeat: epsilon • , [$]
            item: document: • BOM_opt HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
            item: BOM_opt: epsilon • , [HEADER]
            item: BOM_opt: • BOM , [HEADER]
        Created transition from 0 with document to new state 4
   Process transition from state 0 for term document_repeat
        New state before closure:
            item: S: document_repeat • , [$]
        New state after closure:
            item: S: document_repeat • , [$]
        Created transition from 0 with document_repeat to new state 5

Processing transitions for state 1
   No new states. All terms are reduces.

Processing transitions for state 2
   Process transition from state 2 for term HEADER
        New state before closure:
            item: document: BOM_opt HEADER • value_repeat1 TRAILER , [BOM, $, HEADER]
        New state after closure:
            item: document: BOM_opt HEADER • value_repeat1 TRAILER , [BOM, $, HEADER]
            item: value_repeat1: • value , [TRAILER]
            item: value_repeat1: • value value_repeat1 , [TRAILER]
            item: value: • map , [ID, TRAILER]
            item: map: • ID COLON STRING NL , [ID, TRAILER]
        Created transition from 2 with HEADER to new state 6

Processing transitions for state 3
   No new states. All terms are reduces.

Processing transitions for state 4
   Process transition from state 4 for term BOM
        New state before closure:
            item: BOM_opt: BOM • , [HEADER]
        New state after closure:
            item: BOM_opt: BOM • , [HEADER]
   Process transition from state 4 for term BOM_opt
        New state before closure:
            item: document: BOM_opt • HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
        New state after closure:
            item: document: BOM_opt • HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
   Process transition from state 4 for term document
        New state before closure:
            item: document_repeat: document • document_repeat , [$]
        New state after closure:
            item: document_repeat: document • document_repeat , [$]
            item: document_repeat: • document document_repeat , [$]
            item: document_repeat: epsilon • , [$]
            item: document: • BOM_opt HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
            item: BOM_opt: epsilon • , [HEADER]
            item: BOM_opt: • BOM , [HEADER]
   Process transition from state 4 for term document_repeat
        New state before closure:
            item: document_repeat: document document_repeat • , [$]
        New state after closure:
            item: document_repeat: document document_repeat • , [$]
        Created transition from 4 with document_repeat to new state 7

Processing transitions for state 5
   No new states. All terms are reduces.

Processing transitions for state 6
   Process transition from state 6 for term ID
        New state before closure:
            item: map: ID • COLON STRING NL , [ID, TRAILER]
        New state after closure:
            item: map: ID • COLON STRING NL , [ID, TRAILER]
        Created transition from 6 with ID to new state 8
   Process transition from state 6 for term map
        New state before closure:
            item: value: map • , [ID, TRAILER]
        New state after closure:
            item: value: map • , [ID, TRAILER]
        Created transition from 6 with map to new state 9
   Process transition from state 6 for term value
        New state before closure:
            item: value_repeat1: value • value_repeat1 , [TRAILER]
            item: value_repeat1: value • , [TRAILER]
        New state after closure:
            item: value_repeat1: value • value_repeat1 , [TRAILER]
            item: value_repeat1: value • , [TRAILER]
            item: value_repeat1: • value , [TRAILER]
            item: value_repeat1: • value value_repeat1 , [TRAILER]
            item: value: • map , [ID, TRAILER]
            item: map: • ID COLON STRING NL , [ID, TRAILER]
        Created transition from 6 with value to new state 10
   Process transition from state 6 for term value_repeat1
        New state before closure:
            item: document: BOM_opt HEADER value_repeat1 • TRAILER , [BOM, $, HEADER]
        New state after closure:
            item: document: BOM_opt HEADER value_repeat1 • TRAILER , [BOM, $, HEADER]
        Created transition from 6 with value_repeat1 to new state 11

Processing transitions for state 7
   No new states. All terms are reduces.

Processing transitions for state 8
   Process transition from state 8 for term COLON
        New state before closure:
            item: map: ID COLON • STRING NL , [ID, TRAILER]
        New state after closure:
            item: map: ID COLON • STRING NL , [ID, TRAILER]
        Created transition from 8 with COLON to new state 12

Processing transitions for state 9
   No new states. All terms are reduces.

Processing transitions for state 10
   Process transition from state 10 for term ID
        New state before closure:
            item: map: ID • COLON STRING NL , [ID, TRAILER]
        New state after closure:
            item: map: ID • COLON STRING NL , [ID, TRAILER]
   Process transition from state 10 for term map
        New state before closure:
            item: value: map • , [ID, TRAILER]
        New state after closure:
            item: value: map • , [ID, TRAILER]
   Process transition from state 10 for term value
        New state before closure:
            item: value_repeat1: value • value_repeat1 , [TRAILER]
            item: value_repeat1: value • , [TRAILER]
        New state after closure:
            item: value_repeat1: value • value_repeat1 , [TRAILER]
            item: value_repeat1: value • , [TRAILER]
            item: value_repeat1: • value , [TRAILER]
            item: value_repeat1: • value value_repeat1 , [TRAILER]
            item: value: • map , [ID, TRAILER]
            item: map: • ID COLON STRING NL , [ID, TRAILER]
   Process transition from state 10 for term value_repeat1
        New state before closure:
            item: value_repeat1: value value_repeat1 • , [TRAILER]
        New state after closure:
            item: value_repeat1: value value_repeat1 • , [TRAILER]
        Created transition from 10 with value_repeat1 to new state 13

Processing transitions for state 11
   Process transition from state 11 for term TRAILER
        New state before closure:
            item: document: BOM_opt HEADER value_repeat1 TRAILER • , [BOM, $, HEADER]
        New state after closure:
            item: document: BOM_opt HEADER value_repeat1 TRAILER • , [BOM, $, HEADER]
        Created transition from 11 with TRAILER to new state 14

Processing transitions for state 12
   Process transition from state 12 for term STRING
        New state before closure:
            item: map: ID COLON STRING • NL , [ID, TRAILER]
        New state after closure:
            item: map: ID COLON STRING • NL , [ID, TRAILER]
        Created transition from 12 with STRING to new state 15

Processing transitions for state 13
   No new states. All terms are reduces.

Processing transitions for state 14
   No new states. All terms are reduces.

Processing transitions for state 15
   Process transition from state 15 for term NL
        New state before closure:
            item: map: ID COLON STRING NL • , [ID, TRAILER]
        New state after closure:
            item: map: ID COLON STRING NL • , [ID, TRAILER]
        Created transition from 15 with NL to new state 16

Processing transitions for state 16
   No new states. All terms are reduces.

State #0:
S': • S , [$]
S: • document_repeat , [$]
document_repeat: • document document_repeat , [$]
document_repeat: epsilon • , [$]
document: • BOM_opt HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
BOM_opt: epsilon • , [HEADER]
BOM_opt: • BOM , [HEADER]

State #1:
BOM_opt: BOM • , [HEADER]

State #2:
document: BOM_opt • HEADER value_repeat1 TRAILER , [BOM, $, HEADER]

State #3:
S': S • , [$]

State #4:
document_repeat: document • document_repeat , [$]
document_repeat: • document document_repeat , [$]
document_repeat: epsilon • , [$]
document: • BOM_opt HEADER value_repeat1 TRAILER , [BOM, $, HEADER]
BOM_opt: epsilon • , [HEADER]
BOM_opt: • BOM , [HEADER]

State #5:
S: document_repeat • , [$]

State #6:
document: BOM_opt HEADER • value_repeat1 TRAILER , [BOM, $, HEADER]
value_repeat1: • value , [TRAILER]
value_repeat1: • value value_repeat1 , [TRAILER]
value: • map , [ID, TRAILER]
map: • ID COLON STRING NL , [ID, TRAILER]

State #7:
document_repeat: document document_repeat • , [$]

State #8:
map: ID • COLON STRING NL , [ID, TRAILER]

State #9:
value: map • , [ID, TRAILER]

State #10:
value_repeat1: value • value_repeat1 , [TRAILER]
value_repeat1: value • , [TRAILER]
value_repeat1: • value , [TRAILER]
value_repeat1: • value value_repeat1 , [TRAILER]
value: • map , [ID, TRAILER]
map: • ID COLON STRING NL , [ID, TRAILER]

State #11:
document: BOM_opt HEADER value_repeat1 • TRAILER , [BOM, $, HEADER]

State #12:
map: ID COLON • STRING NL , [ID, TRAILER]

State #13:
value_repeat1: value value_repeat1 • , [TRAILER]

State #14:
document: BOM_opt HEADER value_repeat1 TRAILER • , [BOM, $, HEADER]

State #15:
map: ID COLON STRING • NL , [ID, TRAILER]

State #16:
map: ID COLON STRING NL • , [ID, TRAILER]

```
### Acknowledgments
A significant portion of the LR(1) code is derived from
https://github.com/amirhossein-hkh/LR-Parser,
which is MIT licensed. See COPYRIGHT file.
