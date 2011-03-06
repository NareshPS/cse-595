%% Create training and test set

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

%% Build Models.
if exist('model.mat', 'file') == 0
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
        modelArray{i} = trainModel(splitSetLabels, trainHistograms, i);
    end
    save('model.mat', 'modelArray');
else
    load('model.mat')
end
%% Classify testing images.

if exist('allProbabilities.mat') == 0
    probEstimates = {};
    for i = 1:size(modelArray,2);
        randomLabels = randsrc(1, size(testHistograms, 1), [1 2]);
        [predictedLabels, accuracy, probEstimates{i}] = svmpredict(randomLabels', testHistograms, modelArray{i}, '-b 1');
    end

    allProbabilities =[probEstimates{1}(:,1) probEstimates{2}(:,1) probEstimates{2}(:,1) probEstimates{4}(:,1) probEstimates{5}(:,1)];
    save('allProbabilities.mat', 'allProbabilities');
else
    load('allProbabilities.mat');
end