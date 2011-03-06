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

numSets = 5;
splitSetlabels = zeros(1, size(trainingLabel, 2));

for i = 1:numSets;
    for j = 1:size(trainingLabel, 2);
        if trainingLabel(j) == i;
            splitSetLabels(j) = 1;
        else
            splitSetLabels(j) = 2;
        end
    end
    modelArray{i} = trainAndTestSVM(splitSetLabels, trainHistograms, i);
end

save('model.mat', 'modelArray');