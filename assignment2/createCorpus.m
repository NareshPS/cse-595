function [training, testing] = createCorpus(path, filePrefix, fileSuffix)
% Builds a corpus of training and test files.
% Returns a tuple of training and test data.

if nargin == 1
    filePrefix = 'descr_';
    fileSuffix = '.txt';
end

files = dir(fullfile(path, [filePrefix '*' fileSuffix]));

trainIdx = 1;
testIdx = 1;

for fileIdx = 1 : numel(files)
    fileName = fullfile(path, files(fileIdx).name);
    description = fileread(fileName);
    colorLabels = getColorLabels(description);
    imageFileName = strrep(fileName, 'descr_', 'img_');
    imageFileName = strrep(imageFileName, '.txt', '.jpg');
    if numel(colorLabels) == 1
        training(trainIdx).filename = imageFileName;
        training(trainIdx).label = colorLabels(1);
        trainIdx = trainIdx + 1;
    else
        testing{testIdx} = imageFileName;
        testIdx = testIdx + 1;
    end
end

function [ colorLabels ] = getColorLabels(description)
% Function returns colorLabels found in text.

colors = {'black', 'brown', 'red', 'silver', 'gold'};
colorIndices = [1, 2, 3, 4, 5];

numColors = numel(colors);
found = zeros(1, numColors);

for colorIdx = 1 : numColors
    foundPos = strfind(lower(description), colors{colorIdx});
    found(1, colorIdx) = any(foundPos); % returns 1 for non-empty foundPos
end

colorLabels = colorIndices(found == 1);
