

Language used : Java

The program can be run using Java SDK-7 or above
Files : ID3.java, Instance.java and Node.java should be kept in the same folder 

Compiling the program :
javac ID3.java

Executing the program :
java ID3 arg1 arg2 arg3 arg4 [optional arg5] 

where 
arg1 = The complete path of training data file 
arg2 = The complete path of validation data file
arg3 = The complete path of testing data file
arg4 = Pruning factor between 0 to 1
arg5 = Purity needed at each leaf node. By default the value is 85% if left blank.


The code will thus produce the following outputs:
1) The decision tree

2) Pre-pruning

   Number of training instances
   Number of training attributes
   Total number of nodes in the tree
   Number of leaf nodes in the tree
   The accuracy achieved on the training data

   Number of validation instances
   Number of validation attributes
   The accuracy achieved on the validation data

   Number of training instances
   Number of training attributes
   The accuracy achieved on the testing data

3) The pruned decision tree

4) Post-pruning
   
   Number of training instances
   Number of training attributes
   Total number of nodes in the tree
   Number of leaf nodes in the tree
   The accuracy achieved on the training data

   Number of validation instances
   Number of validation attributes
   The accuracy achieved on the validation data

   Number of training instances
   Number of training attributes
   The accuracy achieved on the testing data
