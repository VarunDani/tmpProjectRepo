

**B+ Tree Database Indexing**
-------------------------

This project is implemented as curriculum of database design subject.This will enable user for creating Index of n digit keys in B+ tree manner and allow to search and traverse through tree in memory management


Useful Commands for Indexing and searching in tree.


    java INDEX -create CS6360Asg5TestData.txt cs6360.idx 15
    
    java INDEX -find cs6360.idx 45526813100142A
    
    java INDEX -find cs6360.idx 111111111111
    
    java INDEX -find cs6360.idx 93288157045562A
    
    java INDEX -insert cs6360.idx "12222222222222C test data I added"
    
    java INDEX -find cs6360.idx 12222222222222C
    
    java INDEX -list cs6360.idx 38417813544394A 12