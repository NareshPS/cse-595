%%

numSets = 3;
trainingLabel = [1     3     2     1     3     2     2     1     1     3]
trainingHistogram = [4,5,6,7; 2,3,4,1; 1,2,6,4; 4,6,2,4; 6,2,2,9; 8,2,3,4; 7,2,2,4; 6,1,9,9; 8,4,5,4; 7,9,2,4]

% Sort training Label
[values, indices] = sort(trainingLabel)

startIndex = 1
fullIdx = 1:size(trainingLabel, 2)
model = {}

for i = 1:numSets
    elemCount = size(values(values == i),2)
    positiveHistogram = trainingHistogram(indices(startIndex: startIndex + elemCount - 1),:)
    restCount = size(values(values ~= i),2)
    effIdx = setdiff(fullIdx, indices(startIndex: startIndex + elemCount - 1))
    negativeHistogram = trainingHistogram(effIdx,:)
    fullHistogram = [positiveHistogram; negativeHistogram]
    positiveLabel = repmat(1, 1, size(positiveHistogram, 1))
    negativeLabel = repmat(2, 1, size(negativeHistogram, 1))
    fullLabel = [positiveLabel negativeLabel]
    startIndex = startIndex + elemCount
end