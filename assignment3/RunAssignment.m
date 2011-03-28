%% Create training and test set
categories = {'clutch', 'hobo', 'shoulder', 'totes'};
numCats = numel(categories);

training = {};
if exist('training.mat', 'file') == 0
    for catIdx = 1 : numCats
        training{catIdx} = createCorpus(['./bags/bags_' categories{catIdx}],  ...
            ['img_bags_' categories{catIdx}], '.jpg', 1, 500);
    end
    save('training.mat', 'training');
else
    load('training.mat');
end

testing = {};
if exist('testing.mat', 'file') == 0
    for catIdx = 1 : numCats
        testing{catIdx} = createCorpus(['./bags/bags_' categories{catIdx}],  ...
            ['img_bags_' categories{catIdx}], '.jpg', 501, 999);
    end
    save('testing.mat', 'testing');
else
    load('testing.mat');
end

%% Calculate sift vectors

if exist('siftVector.mat', 'file') == 0
    allSifts = [];
    allVectorMaps = [];
    imageNameIdx = 1;

    for catIdx = 1 : numCats
        catTrainingSet = [training{catIdx}'; testing{catIdx}'];
        [siftVector, vectorImageMap, imageNameIdx] = calculateSift(catTrainingSet, imageNameIdx);
        allSifts = [allSifts; siftVector];
        allVectorMaps = [allVectorMaps; vectorImageMap];
    end
    save('siftVector.mat', 'allSifts', 'allVectorMaps', 'imageNameIdx');
else
    load('siftVector.mat');
end


%% Create visual words by kmeans clustering and create the feature vector
if exist('siftFeatureVector.mat', 'file') == 0
    k = 1000;
    %opts = statset('MaxIter', 500, 'Display', 'iter');
    [centroids, clusterIds] = vl_kmeans(double(allSifts)', k);
    imageFeatureVector = zeros(imageNameIdx-1, size(centroids, 2));

    for siftIdx = 1 : size(clusterIds, 2)
        imageIdx = allVectorMaps(siftIdx);
        imageFeatureVector(imageIdx, clusterIds(:,siftIdx)) = ...
            imageFeatureVector(imageIdx, clusterIds(:,siftIdx)) + 1;
    end
    save('siftFeatureVector.mat', 'imageFeatureVector');
else
    load('siftFeatureVector.mat');
end

%% Create lexicon

if exist('textLexicon.mat', 'file') == 0
    lexicon = {};
    for idx = 1:numel(categories)
        category = categories{idx};
        
        % extract filenames
        listing = dir(['./bags/bags_' category '/descr_bags*.txt']);
        listing = {listing.name};
        
        % convert to file path
        listing = cellfun(@(fileName) fullfile(['./bags/bags_' category], fileName), listing, 'UniformOutput', false);

        categoryLexicon = getFeatureWords(listing);
        disp(sprintf('Found %d unique words for %s.', numel(categoryLexicon), category));
        lexicon = union(lexicon, categoryLexicon);
    end
    
    % not sure why lexicon isn't a cell-array here. forcing.
    lexicon = {lexicon{:}};
    
    disp(sprintf('Found %d unique words in all. Saving.', numel(lexicon)));
    
    save('textLexicon.mat', 'lexicon');
else
    load('textLexicon.mat');
    disp(sprintf('Loaded %d unique words in all.', numel(lexicon)));
end

%% get feature vector for all files
if exist('textLexiconVectors.mat', 'file') == 0
    textLexiconVectors = zeros(0, numel(lexicon));
    
    for idx = 1:numel(training)
        descr_files = cellfun(@(filepath) strrep(strrep(filepath, 'jpg', 'txt'), 'img', 'descr'), training{idx}, 'UniformOutput', false);
        textLexiconVectors = [textLexiconVectors; createTextVector(descr_files, lexicon)];
    end

    for idx = 1:numel(testing)
        descr_files = cellfun(@(filepath) strrep(strrep(filepath, 'jpg', 'txt'), 'img', 'descr'), testing{idx}, 'UniformOutput', false);
        textLexiconVectors = [textLexiconVectors; createTextVector(descr_files, lexicon)];
    end

    save('textLexiconVectors.mat', 'textLexiconVectors');
else
    load('textLexiconVectors.mat');
end

%% get the k most frequently using words

if exist('textLexiconVectorsK.mat', 'file') == 0
    K = 1000;
    
    % get the sum across colummns
    csums = sum(textLexiconVectors, 1);
    
    % sort for most frequently using words
    [val, idx] = sort(csums, 'descend');
    
    % take the top K most frequently occuring words.
    textLexiconVectorsK = zeros(size(textLexiconVectors, 1), K);
    
    for kidx = 1:K
        textLexiconVectorsK(:,kidx) = textLexiconVectors(:,idx(kidx));
    end
    
    save('textLexiconVectorsK.mat', 'textLexiconVectorsK');
else
    load('textLexiconVectorsK.mat');
end

%textLexiconVectors = textLexiconVectors(:,[find(ismember(lexicon, 'clutch')), find(ismember(lexicon, 'hobo')), find(ismember(lexicon, 'shoulder')), find(ismember(lexicon, 'tote'))]);

%% Train the Naive Bayesian.
for idx = 1 : size(imageFeatureVector, 1)
    imageFeatureVector(idx,:) = 0.1*imageFeatureVector(idx,:) ./ sum(imageFeatureVector(idx,:),2);
end
%for idx = 1 : size(textLexiconVectorsK, 1)
%    textLexiconVectorsK(idx,:) = 10000 * textLexiconVectorsK(idx,:) ./ sum(textLexiconVectorsK(idx,:),2);
%end


imageIdx = 1;
fullTrainVector = [];
classIdx = [];
currentIdx = 1;
idxCount  = 0;

for idx = 1:2000
    fullTrainVector(idx,:) = [imageFeatureVector(imageIdx+idxCount,:) textLexiconVectorsK(idx,:)]; 
    classIdx(idx,1) = currentIdx;
    idxCount = idxCount + 1;
    if mod(idxCount, 500) == 0
        imageIdx = imageIdx + 500;
        currentIdx = currentIdx + 1;
        idxCount = 0;
    end
end

[condProb, uniqueClasses] = naiveBayesTrain(fullTrainVector, classIdx);

origClassIdx = [];
fullTestVector = [];
imageIdx = 501;
idxCount = 0;
currentIdx = 1;

for idx = 1:1996
    fullTestVector(idx,:) = [imageFeatureVector(imageIdx+idxCount,:) textLexiconVectorsK(idx+2000,:)];
    origClassIdx(idx,1) = currentIdx;
    idxCount = idxCount + 1;
    if mod(idxCount, 499) == 0
        imageIdx = imageIdx + 501;
        currentIdx = currentIdx + 1;
        idxCount = 0;
    end
end

[predClasses, logProbs] = naiveBayesPredict(fullTestVector, condProb, uniqueClasses);

confusionmat(origClassIdx, predClasses')