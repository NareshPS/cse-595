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

if exist('bestParams.mat', 'file') == 0
    bestParams = getBestParams(training, trainHistograms);
    save('bestParams.mat', 'bestParams');
else
    load('bestParams.mat');
end

classLabels = [1 2 3 4 5];

%% Split Training set into training and tuning set.
for labelIdx = 1:numel(classLabels)    

    label = classLabels(labelIdx);
    disp(sprintf('Now, really testing for label %d', label));
    currClassLabels = double(ismember([training.label], label))';    % ideally, above, we should be tuning parameters.
    
    % Assuming that's done, build model over entire training set.
    svmOpt = sprintf('-c %d -t 2 -g %d -b 1', bestParams(labelIdx, 1), bestParams(labelIdx, 2)); 
    model = svmtrain(currClassLabels, trainHistograms, svmOpt);

    % test the test set, and dump predicted labels for each image into
    % file. this is for the current class label.
    
    testModel(model, label, zeros(size(testHistograms, 1), 1) - 1, test, testHistograms);    
    
    % Uncomment the line below to check evaluation on the training set
    % itself.
    
    % testModel(model, label, currClassLabels, {training.filename}, trainHistograms);    
end


