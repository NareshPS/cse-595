%% Call buildLexicon to get the list of Lexicons.
bagLex = buildLexicon('./bags/');
shoesLex = buildLexicon('/shoes/');

bagDir = './bags/';
shoesDir = './shoes/';

%% List all .txt files in bags and shoes directory.

bagFiles = dir(fullfile(bagDir, '*.txt'));
shoesFiles = dir(fullfile(shoesDir, '*.txt'));

%% Compute the frequency of each lexicon for bags.

bag_text_vector = [];
for idx = 1 : length(bagFiles)
    fd = fopen(fullfile(bagDir, bagFiles(idx).name));
    toks = split(fd);
    fclose(fid);
end

%% Compute the frequency of each lexicon for shoes.

shoes_text_vector = [];
for idx = 1 : length(shoesFiles)
    fd = fopen(fullfile(shoesDir, shoesFiles(idx).name));
    toks = split(fd);
    fclose(fid);
end