%% Load Image Gist Vectors.
load('gist_shoes.mat');
load('gist_bags.mat');

%% Load Image Text Vectors.

load('bag_text_vector.mat');
load('shoes_text_vector.mat');

%% Create consolidated vector.

consolidatedShoesVector = [];
consolidatedShoesFiles = {};

for r = 1:size(shoeGistVector, 2)
  consolidatedShoesVector(r,:) = [shoeGistVector(r).gist shoes_text_vector(r).vector];
  consolidatedShoesFiles{r} = shoeGistVector(r).name; 
end

consolidatedBagVector = [];
consolidatedBagFiles = {};

for r = 1:size(bagGistVector, 2)
  consolidatedBagVector(r,:) = [bagGistVector(r).gist bag_text_vector(r).vector];
  consolidatedBagFiles{r} = bagGistVector(r).name; 
end

%% Cluster shoes and bags on consolidated vector.
%{
for numClusters = 2:2:8
  clusterObjects(numClusters, consolidatedShoesVector, consolidatedShoesFiles, './merged_shoes_out', 'Merged Shoes');
end

for numClusters = 2:2:8
  clusterObjects(numClusters, consolidatedBagVector, consolidatedBagFiles, './merged_bag_out', 'Merged Bags');
end
%}

%% Cluster both shoes and bags.
for numClusters = 2:2:8
  clusterObjects(numClusters, [consolidatedBagVector; consolidatedShoesVector], [consolidatedBagFiles consolidatedShoesFiles], './merged_all_out', 'Merged Bags and Shoes');
end
