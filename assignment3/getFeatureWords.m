function words = getFeatureWords(textFiles)

words = {};

for file = textFiles
    % read, split, and eliminate stopwords
    fileWords = cleanStopWords(split(fileread(file{1})));

    % apply porter stemmer to each
    fileWords = cellfun(@(word)porterStemmer(word), fileWords,'UniformOutput',false)';
    fileWords = cellfun(@(word)lower(word), fileWords,'UniformOutput',false)';
    
    % eliminate duplicates
    words = union(words, fileWords);
end


