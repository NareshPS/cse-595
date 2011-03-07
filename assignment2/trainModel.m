%% Train and test SVM.
function model = trainModel(trainLabel, trainHistograms)
%% Split Training set into training and tuning set.

tunePct = 30;
dataSize = size(trainLabel, 2);
trainingSize = cast((dataSize - (tunePct/100)*dataSize), 'uint32');

trainingLabel = [trainLabel(1:trainingSize)];
trainingHistogram = trainHistograms(1:trainingSize,:);

tuningLabel = [trainLabel((trainingSize + 1):size(trainLabel, 2))]';
tuningHistogram = trainHistograms((trainingSize + 1):size(trainLabel, 2),:);

% Train SVM classifier.
model = svmtrain(trainingLabel', trainingHistogram, '-c 10 -t 2 -g 2 -b 1');

% Test SVM classifier.
svmpredict(tuningLabel, tuningHistogram, model, '-b 1');