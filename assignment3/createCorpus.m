function files = createCorpus(path, filePrefix, fileSuffix, startIdx, endIdx)
% Returns a list of files based on the criteria specified.

if nargin == 1
    filePrefix = 'img_';
    fileSuffix = '.jpg';
    startIdx = 1;
    endIdx = 500;
end

fileIdx = 1;
files = {};

for idx = startIdx : endIdx
    files{fileIdx} = sprintf('%s/%s_%d%s', path, filePrefix, idx, fileSuffix);
    fileIdx = fileIdx + 1;
end