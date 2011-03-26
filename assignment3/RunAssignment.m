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
        catTrainingSet = training{catIdx}';
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
    k = 9;
    opts = statset('MaxIter', 500, 'Display', 'iter');
    [clusterIds, centroids] = kmeans(double(allSifts), k, 'Options', opts);
    imageFeatureVector = zeros(imageNameIdx-1, size(centroids, 1));

    for siftIdx = 1 : size(clusterIds, 1)
        imageIdx = allVectorMaps(siftIdx);
        imageFeatureVector(imageIdx, clusterIds(siftIdx)) = ...
            imageFeatureVector(imageIdx, clusterIds(siftIdx)) + 1;
    end
    save('siftFeatureVector.mat', 'imageFeatureVector');
else
    load('siftFeatureVector.mat');
end