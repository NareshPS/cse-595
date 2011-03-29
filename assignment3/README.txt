This is the README file for Assignment 3 for Words 'N' Pictures course.

Naresh P. Singh
Rohith Menon
Sandesh Singh

Script to run the assignment is:

>> RunAssignment

Files
============================================================================

calculateSift.m
calculates SIFT vectors for the image filenames passed

cleanStopWords.m
eliminates stopwords from a cell array of words

confmat.m
prints a confusion matrix, given predicted classes and actual classes

createCorpus.m
creates an ordered list of filenames, so that we can relate feature vectors, classes and files

createTextVector.m
creates text vectors, given an array of filenames and a lexicon

getFeatureWords.m
creates a global lexicon vector by reading in all files passed.

naiveBayesPredict.m
predicts the class of input instances given the class/feature probabilities

naiveBayesTrain.m
calculates class/feature probabilities given a set of training instances

porterStemmer.m
stems a given word to a base representation

split.m
splits a given pitece of text into words

RunAssignment.m
Performs all the steps required as part of the assignment.
 - creates training and test sets
 - calculates sift vectors
 - creates visual words uisng k=1000 means clustering
 - creates the lexicon
 - creates the text feature vectors of top K=1000 words
 - trains and test the respective sets, and prints the confusion matrix
