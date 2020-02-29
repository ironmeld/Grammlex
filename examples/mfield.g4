S: document*;
document: BOM? HEADER value+ TRAILER;
value: map;
map: ID COLON STRING NL;
