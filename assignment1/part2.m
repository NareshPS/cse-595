%% Build the lexicon
% Jesus saves, so do we.

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

if exist('bag_text_vector.mat', 'file') == 0
    bag_text_vector = buildTextVector(fullLex, bagDir);
    save('bag_text_vector.mat', 'bag_text_vector');
else
    load('bag_text_vector.mat');
end

if exist('shoes_text_vector.mat', 'file') == 0
    shoes_text_vector = buildTextVector(fullLex, shoesDir);
    save('shoes_text_vector.mat', 'shoes_text_vector');
else
    load('shoes_text_vector.mat');
end

%% Cluster the shoes.
shoesVector = [];
shoesFiles = {};

for r = 1:size(shoes_text_vector, 2)
  shoesVector(r,:) = shoes_text_vector(r).vector;
  name = strrep(shoes_text_vector(r).name, 'descr', 'img'); 
  name = strrep(name, 'txt', 'jpg'); 
  shoesFiles{r} = name;
end


for numClusters = 2:2:8
  clusterObjects(numClusters, shoesVector, shoesFiles, './text_shoes_out', 'Shoes Text');
end

%% Cluster the bags.
bagVector = [];
bagFiles = {};
for r = 1:size(bag_text_vector, 2)
  bagVector(r,:) = bag_text_vector(r).vector;
  name = strrep(bag_text_vector(r).name, 'descr', 'img'); 
  name = strrep(name, 'txt', 'jpg'); 
  bagFiles{r} = name;
end

for numClusters = 2:2:8
  clusterObjects(numClusters, bagVector, bagFiles, './text_bag_out', 'Bag Text);
end

%% Cluster everything.
full_text_vector = [shoesVector; bagVector];
allFiles = {shoesFiles; bagFiles};

for numClusters = 2:2:8
  clusterObjects(numClusters, full_text_vector, allFiles, './text_all_out', 'Text All');
end
