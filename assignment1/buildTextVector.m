%% This function build a text vector for input parameters.
function text_vector = buildTextVector(lexiconVector, filePath, filePrefix, fileSuffix)

if nargin == 2
    filePrefix = 'descr_';
    fileSuffix = '.txt';
end

allFiles = dir(fullfile(filePath, [filePrefix '*' fileSuffix]));

%% Compute the frequency of each lexicon for bags.

text_vector = zeros(length(allFiles));

for idx = 1 : length(allFiles)
    fd = fopen(fullfile(filePath, allFiles(idx).name));
    toks = split(fd);
    fclose(fd);
    
    text_vector(idx).vector = zeros(1, length(lexiconVector));
    
    for i=1:length(toks)
        indices = strmatch(toks(i), lexiconVector);
        
        for j = indices
            text_vector(idx).vector(j) = text_vector(idx).vector(j) + 1;
        end
    end
   
    text_vector(idx).name = fullfile(filePath, allFiles(idx).name);
end
