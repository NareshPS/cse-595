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

% Train SVM classifier.
model = svmtrain([training.label]', trainHistograms, '-c 10.01 -t 2 -g 1');


% Test SVM classifier.
numTestHistograms = size(testHistograms, 1);
[predictedLabels, accuracy, probEstimates] = ...
    svmpredict(zeros(numTestHistograms, 1), ...
    testHistograms, model);