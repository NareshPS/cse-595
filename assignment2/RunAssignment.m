%% Delete any prior HTML and .mat files
delete *.mat
delete *.html

%% Run the assignment.
% It will generate the model on bags, and classify them
% according to five color attributes. The HTML files
% are generated corresponding to each attribute and stored
% in the current directory.

trainAllModels

%% Extra credit 1.
% It will classify shoes images based on the model trained over
% bags images. This part will also generate HTMLs showing top 200 files
% for each color attribute.

classifyShoes