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
  clusterObjects(numClusters, shoeGist, shoeFiles, './shoe_out');
end

% For bags.
bagGist = [];
bagFiles = {};
for r = 1:size(bagGistVector, 2)
  bagGist(r,:) = bagGistVector(r).gist;
  bagFiles{r} = bagGistVector(r).name; 
end

for numClusters = 2:2:8
  clusterObjects(numClusters, bagGist, bagFiles, './bag_out');
end

% Create a consolidate gist and filename vector.
allGist = [shoeGist; bagGist];
allFiles = [shoeFiles; bagFiles];

for numClusters = 2:2:8
  clusterObjects(numClusters, allGist, allFiles, './all_out');
end
