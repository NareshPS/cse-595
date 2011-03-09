function [bestParams] = getBestParams(training, trainHistograms)

FOLDS = 3;
classLabels = [1 2 3 4 5];

% we are building a classifier per-label

bestParams = zeros(numel(classLabels), 3);

for labelIdx = 1:numel(classLabels)
    label = classLabels(labelIdx);
    disp(sprintf('Testing for label %d', label));
    currClassLabels = double(ismember([training.label], label))';
    
    % we could also manually split here, based on the 30/70 suggested in
    % the assignment.
    indices = crossvalind('kfold', currClassLabels, FOLDS);
    
    labelAccuracy = zeros(FOLDS, 1);

    % loop here, to tune parameters for accuracy?
    bestAccuracy = 0;
    bestC = 0;
    bestG = 0;
    
    for c = 10:10:100
        for g = 1:1:10
            for fold = 1:FOLDS
                disp(sprintf('trying c=%d, g=%d', c, g));
                svmOpt = sprintf('-c %d -t 2 -g %d -b 1', c, g); 
                
                disp(sprintf('FOLD %d', fold));
                foldTestIndices = (indices == fold);
                foldTrainIndices = ~foldTestIndices;

                foldTrainSet = trainHistograms(foldTrainIndices, :);
                foldTrainClassLabels = currClassLabels(foldTrainIndices, :);

                foldTestSet = trainHistograms(foldTestIndices, :);
                foldTestClassLabels = currClassLabels(foldTestIndices, :);

                model = svmtrain(foldTrainClassLabels, foldTrainSet, svmOpt);
                [predictedLabels, accuracy, probEstimates] = svmpredict(foldTestClassLabels, foldTestSet, model);
                labelAccuracy(fold) = accuracy(1);
            end
            
            if mean(labelAccuracy) > bestAccuracy
                bestAccuracy = mean(labelAccuracy);
                bestC = c;
                bestG = g;
            end
        end
    end
    
    disp(sprintf('Finished testing for label %d. bestAccuracy %f.', label, bestAccuracy));
    
    bestParams(labelIdx, 1) = bestC;
    bestParams(labelIdx, 2) = bestG;
    bestParams(labelIdx, 3) = bestAccuracy;
end