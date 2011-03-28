function [condProb, uniqueClasses] = naiveBayesTrain(featureVectors, classes)
% The function trains a naive bayes classifier and returns
% the P(feature/class)
% Returns a num class X num feature matrix with the probabilities.

uniqueClasses = unique(classes);
numFeats = size(featureVectors, 2);

for classIdx = 1 : size(uniqueClasses, 1)
    uniqueClassId = uniqueClasses(classIdx);
    featsForClass = featureVectors(classes == uniqueClassId, :);
    featCountForClass = sum(featsForClass, 1);
    condProb(classIdx,:) = (1 + featCountForClass) ./ (sum(featCountForClass) + size(featsForClass, 2));
end

