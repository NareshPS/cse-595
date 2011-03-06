function [dummy] = testModel(model, label, filenames, histograms)

colors = {'black', 'brown', 'red', 'silver', 'gold'};

[predictedLabels, accuracy, probEstimates] = ...
    svmpredict(zeros(size(histograms, 1), 1) - 1, histograms, model, '-b 1');


%     goodLabels = 1:numel(predictedLabels);
%     goodLabels = goodLabels(predictedLabels==1);
%     
%     disp(goodLabels);
%     
%     for gLabel = goodLabels
%         disp(sprintf('predicted %s : %d with probability %d.', ...
%              test{gLabel}, predictedLabels(gLabel), ...
%              probEstimates(gLabel)));
%     end

[sortedProbEstimates, sortedIdx] = sort(probEstimates, 'descend');

numResults = min(numel(sortedProbEstimates), 200); 
disp(sprintf('showing %d sorted results', numResults));

for rIdx = 1:numResults;
    actualIdx = sortedIdx(rIdx);
    predictedLabel = 'UNKNOWN';

    if predictedLabels(actualIdx) == 1
        predictedLabel = colors{label};
    end

    disp(sprintf('predicted %s : %s with probability %d.', ...
        filenames{actualIdx}, predictedLabel, probEstimates(actualIdx)));
end

dummy = 1;