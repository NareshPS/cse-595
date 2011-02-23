%% Build the lexicon

if exist('bag_lexicon.mat', 'file') == 0
    bagLex = buildLexicon('./bags/');
    save('bag_lexicon.mat', 'bagLex');
else
    load('bag_lexicon.mat')
end

if exist('shoes_lexicon.mat', 'file') == 0
    shoesLex = buildLexicon('./shoes/');
    save('shoes_lexicon.mat', 'shoesLex');
else
    load('shoes_lexicon.mat');
end

if exist('full_lexicon.mat', 'file') == 0
    fullLex = [bagLex; shoesLex];
    save('full_lexicon.mat', 'fullLex');
else
    load('full_lexicon.mat');
end

bagDir = './bags/';
shoesDir = './shoes/';

%% Compute the frequency of each lexicon for bags and shoes.

% The full lexicon is used so that the size of the feature vector for both
% shoes and bags is the same.

bag_text_vector = buildTextVector(fullLex, bagDir);
shoes_text_vector = buildTextVector(fullLex, shoesDir);


%% Cluster the shoes.
shoeGist = [];
shoeFiles = {};

for r = 1:size(shoes_text_vector, 2)
  shoeGist(r,:) = shoes_text_vector(r).vector;
  name = strrep(shoes_text_vector(r).name, 'descr', 'img'); 
  name = strrep(name, 'txt', 'jpg'); 
  shoeFiles{r} = name;
end


for numClusters = 2:2:8
  clusterObjects(numClusters, shoeGist, shoeFiles, './shoe_out');
end

%% Cluster the bags.
bagGist = [];
bagFiles = {};
for r = 1:size(bag_text_vector, 2)
  bagGist(r,:) = bag_text_vector(r).vector;
  name = strrep(bag_text_vector(r).name, 'descr', 'img'); 
  name = strrep(name, 'txt', 'jpg'); 
  bagFiles{r} = name;
end

for numClusters = 2:2:8
  clusterObjects(numClusters, bagGist, bagFiles, './bag_out');
end

%% Cluster everything.
allGist = [shoeGist; bagGist];
allFiles = {shoeFiles; bagFiles};

for numClusters = 2:2:8
  clusterObjects(numClusters, allGist, allFiles, './all_out');
end


%% Jesus saves, so do we.
save('bag_text_vector.mat', 'bag_text_vector');
save('shoes_text_vector.mat', 'shoes_text_vector');
save('full_vector.mat', 'full_vector');

