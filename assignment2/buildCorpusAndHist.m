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

% Sort training Label
trainingLabel = [training.label];
[values, indices] = sort(trainingLabel);

numSets =5;
startIndex = 1;
fullIdx = 1:size(trainingLabel, 2);
model = {};

for i = 1:numSets
    elemCount = size(values(values == i),2);
    positiveHistogram = trainHistograms(indices(startIndex: startIndex + elemCount - 1),:);
    restCount = size(values(values ~= i),2);
    effIdx = setdiff(fullIdx, indices(startIndex: startIndex + elemCount - 1));
    negativeHistogram = trainHistograms(effIdx,:);
    fullHistogram = [positiveHistogram; negativeHistogram];
    positiveLabel = repmat(1, 1, size(positiveHistogram, 1));
    negativeLabel = repmat(2, 1, size(negativeHistogram, 1));
    fullLabel = [positiveLabel negativeLabel];
    trainAndTestSVM(fullLabel, fullHistogram);
    startIndex = startIndex + elemCount;
end