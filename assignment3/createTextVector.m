function counts = createTextVector(textFiles, lexicon)

counts = zeros(numel(textFiles), numel(lexicon));
% count the number of these words in the individual files

for fidx = 1:numel(textFiles)
    disp(sprintf('counting words in %s', textFiles{fidx}));

    text = fileread(textFiles{fidx});
    
    fileWords = cleanStopWords(split(text));
    fileWords = cellfun(@(word)porterStemmer(word), fileWords,'UniformOutput',false)';
    fileWords = cellfun(@(word)lower(word), fileWords,'UniformOutput',false)';
    fileWords = unique(fileWords);
    
    ccounts = cellfun(@(word)sum(ismember(word, fileWords)), lexicon, 'UniformOutput', false);
    
    disp(sum(cell2mat(ccounts)));
    
    counts(fidx,:) = cell2mat(ccounts);
end

