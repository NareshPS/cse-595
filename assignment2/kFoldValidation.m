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

FOLDS = 10;
classLabels = [1 2 3 4 5];

for labelIdx = 1:numel(classLabels)
    label = classLabels(labelIdx);
    disp(sprintf('Testing for label %d', label));
    currClassLabels = double(isMember([training.label], label)');
    indices = crossvalind('kfold', currClassLabels, FOLDS);
    
    labelAccuracy = zeros(FOLDS);
    
    for fold = 1:FOLDS
        disp(sprintf('FOLD %d', fold));
        foldTestIndices = (indices == fold);
        foldTrainIndices = ~foldTestIndices;
        
        foldTrainSet = trainHistograms(foldTrainIndices, :);
        foldTrainClassLabels = currClassLabels(foldTrainIndices, :);
        
        foldTestSet = trainHistograms(foldTestIndices, :);
        foldTestClassLabels = currClassLabels(foldTestIndices, :);
        
        model = svmtrain(foldTrainClassLabels, foldTrainSet, '-c 50 -t 2 -g 1');
        [predictedLabels, accuracy, probEstimates] = svmpredict(foldTestClassLabels, foldTestSet, model);
        labelAccuracy(fold) = accuracy(1);
    end
    
    disp(sprintf('Finished testing for label %d, accuracy %d.', label, mean(labelAccuracy)));
    
    % ideally, above, we should be tuning parameters.
    % Assuming that's done, build model over entire training set.
    model = svmtrain(currClassLabels, trainHistograms, '-c 50 -t 2 -g 1');

    % test the training set, and dump predicted labels for eac image into
    % file
end

