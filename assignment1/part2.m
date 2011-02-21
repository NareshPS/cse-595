%% Call buildLexicon to get the list of Lexicons.
bagLex = buildLexicon('./bags/');
shoesLex = buildLexicon('./shoes/');

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
    fclose(fd);
    
    file_vector(length(bagLex)) = 0;
    for i=1:length(toks)
        indices = strmatch(toks(i), bagLex);
        
        for j = indices
            file_vector(j) = file_vector(j) + 1;
        end
    end
   
    bag_text_vector(idx).name = fullfile(bagDir, bagFiles(idx).name);
    bag_text_vector(idx).vector = file_vector;
end

%% Compute the frequency of each lexicon for shoes.

shoes_text_vector = [];

for idx = 1 : length(shoesFiles)
    fd = fopen(fullfile(shoesDir, shoesFiles(idx).name));
    toks = split(fd);
    fclose(fd);
    
    file_vector(length(shoesLex)) = 0;
    for i=1:length(toks)
        indices = strmatch(toks(i), shoesLex);
        
        for j = indices
            file_vector(j) = file_vector(j) + 1;
        end
    end
   
    shoes_text_vector(idx).name = fullfile(shoesDir, shoesFiles(idx).name);
    shoes_text_vector(idx).vector = file_vector;
end

save('bag_text_vector.mat', 'bag_text_vector');
save('shoes_text_vector.mat', 'shoes_text_vector');