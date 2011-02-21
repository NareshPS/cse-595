%% Call buildLexicon to get the list of Lexicons.

if exist('bag_lexicon.mat', 'file') == 0
    bagLex = buildLexicon('./bags/');
    save('bag_lexicon.mat', 'bagLex');
else
    load('bag_lexicon.mat')
end

if exist('shoes_lexicon.mat', 'file') == 0
    shoesLex = buildLexicon('./shoes/');
    save('shoes_lexicon.mat', 'shoesLex');
else
    load('shoes_lexicon.mat');
end

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
    
    bag_text_vector(idx).vector(length(bagLex)) = 0;
    for i=1:length(toks)
        indices = strmatch(toks(i), bagLex);
        
        for j = indices
            bag_text_vector(idx).vector(j) = bag_text_vector(idx).vector(j) + 1;
        end
    end
   
    bag_text_vector(idx).name = fullfile(bagDir, bagFiles(idx).name);
end

%% Compute the frequency of each lexicon for shoes.

shoes_text_vector = [];

for idx = 1 : length(shoesFiles)
    fd = fopen(fullfile(shoesDir, shoesFiles(idx).name));
    toks = split(fd);
    fclose(fd);
    
    shoes_text_vector(idx).vector(length(shoesLex)) = 0;
    for i=1:length(toks)
        indices = strmatch(toks(i), shoesLex);
        
        for j = indices
            shoes_text_vector(idx).vector(j) = shoes_text_vector(idx).vector(j) + 1;
        end
    end
   
    shoes_text_vector(idx).name = fullfile(shoesDir, shoesFiles(idx).name);
end

save('bag_text_vector.mat', 'bag_text_vector');
save('shoes_text_vector.mat', 'shoes_text_vector');