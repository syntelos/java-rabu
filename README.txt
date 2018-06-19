

Design

    Byte buffer with I/O and get/set interfaces and windowing to avoid
    buffer copies.

    Intended for binary data file parsing.  Many binary data file
    formats are consumed as a tree of nested blocks of bytes.  A tree
    of nested byte buffers organize this parsing over relative offset
    programming.


Status

    Work in progress

