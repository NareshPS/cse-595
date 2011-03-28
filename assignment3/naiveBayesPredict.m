function [classes, logProbs] = naiveBayesPredict(featureVectors, condProbs, uniqueClasses)
% The function trains a naive bayes classifier and returns
% the P(feature/class)
% Returns a num class X num feature matrix with the probabilities.

for vectorIdx = 1 : size(featureVectors, 1)
    [class, logProb] = getMaxClass(featureVectors(vectorIdx,:), condProbs, uniqueClasses);
    classes(vectorIdx) = class;
    logProbs(vectorIdx) = logProb;
end

function [class, logProb] = getMaxClass(featureVector, condProbs, classes)
% Function returns the class with max probability for the given feature
% vector.

classProbs = [];

for classIdx = 1 : size(classes, 1)
    condProbForClass = condProbs(classIdx,:);
    logCondProb = log(condProbForClass);
    classProbs(classIdx, :) = logCondProb .* (featureVector + 1);
end

totalProbs = sum(classProbs, 2);
[maxProb, probIdx] = max(totalProbs);
maxClass = classes(probIdx);
class = maxClass(1, 1);
logProb = maxProb(1, 1);