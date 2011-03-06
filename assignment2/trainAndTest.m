% Create training and test set
if exist('corpus.mat', 'file') == 0
    [training, test] = createCorpus('./bags');
    save('corpus.mat', 'training', 'test');
else
    load('corpus.mat');
end

% Build histograms
if exist('trainHist.mat', 'file') == 0
    trainHistograms = buildColorHistograms({training.filename});
    save('trainHist.mat', 'trainHistograms');
else
    load('trainHist.mat');
end

if exist('testHist.mat', 'file') == 0
    testHistograms = buildColorHistograms(test);
    save('testHist.mat', 'testHistograms');
else
    load('testHist.mat');
end

%% Split Training set into training and tuning set.

trainingLabel = [training(1:430).label]';
trainingHistogram = trainHistograms(1:430,:);

tuningLabel = [training(431:577).label]';
tuningHistogram = trainHistograms(431:577,:);

% Train SVM classifier.
model = svmtrain(trainingLabel, trainingHistogram, '-c 50 -t 2 -g 1');

% Test SVM classifier.
numTestHistograms = size(testHistograms, 1);
[predictedLabels, accuracy, probEstimates] = ...
    svmpredict(tuningLabel, ...
    tuningHistogram, model);