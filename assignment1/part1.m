% Create gist vectors if not already created.
if exist('gist_shoes.mat', 'file') == 0
    shoeGistVector = buildGist('./shoes/');
    save('gist_shoes.mat', 'shoeGistVector');
else
    load('gist_shoes.mat');
end

if exist('gist_bags.mat', 'file') == 0
    bagGistVector = buildGist('./bags/');
    save('gist_bags.mat', 'bagGistVector');
else
    load('gist_bags.mat');
end

% For shoes.
shoeGist = [];
shoeFiles = {};

for r = 1:size(shoeGistVector, 2)
  shoeGist(r,:) = shoeGistVector(r).gist;
  shoeFiles{r} = shoeGistVector(r).name; 
end

for numClusters = 2:2:8
  clusterObjects(numClusters, shoeGist, shoeFiles, './gist_shoes_out', 'Shoes Gist');
end

% For bags.
bagGist = [];
bagFiles = {};
for r = 1:size(bagGistVector, 2)
  bagGist(r,:) = bagGistVector(r).gist;
  bagFiles{r} = bagGistVector(r).name; 
end

for numClusters = 2:2:8
  clusterObjects(numClusters, bagGist, bagFiles, './gist_bag_out', 'Bag Gist');
end

% Create a consolidate gist and filename vector.
allGist = [shoeGist; bagGist];
allFiles = [shoeFiles; bagFiles];

for numClusters = 2:2:8
  clusterObjects(numClusters, allGist, allFiles, './gist_all_out', 'Gist All');
end