function [dummy] = testModel(model, colorLabel, trueLabels, filenames, histograms)

% constant, move elsewhere?
colors = {'black', 'brown', 'red', 'silver', 'gold'};

[predictedLabels, accuracy, probEstimates] = ...
    svmpredict(trueLabels, histograms, model, '-b 1');

[sortedProbEstimates, sortedIdx] = sort(probEstimates, 'descend');

numResults = numel(sortedProbEstimates); 

% how many of our class did we find
disp(sprintf('got %d favorable labels', sum(predictedLabels)));

fid = fopen(sprintf('%s_prediction.html', colors{colorLabel}), 'w');

for rIdx = 1:numResults;
    actualIdx = sortedIdx(rIdx);

    % let us not bother with that which is not in our class
    if predictedLabels(actualIdx) ~= 1
        continue;
    end

    predictedLabel = colors{colorLabel};
    
    disp(sprintf('predicted %s : %s with probability %d.', ...
        filenames{actualIdx}, predictedLabel, probEstimates(actualIdx)));

     fprintf(fid, '<p><div><h1>%f</h1></div><div><img src="%s" /></div></p>', probEstimates(actualIdx), filenames{actualIdx});
end

fclose(fid);

dummy = 1;

