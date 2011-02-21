% build a lexicon of all words in the corpus
% returns a cell array of all words in all the files in the given path
function lexicon = buildLexicon(path, filePrefix, fileSuffix)

if nargin == 1
    filePrefix = 'descr_';
    fileSuffix = '.txt';
end

lexicon = {};

files = dir(fullfile(path, [filePrefix '*' fileSuffix]));

for i = 1 : numel(files)
    fid = fopen(strcat(strcat(path, '/'), files(i).name));
    toks = split(fid);
    fclose(fid);
    lexicon = union(lexicon, toks);
end

