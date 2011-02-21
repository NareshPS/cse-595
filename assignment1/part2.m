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

bag_text_vector = buildTextVector(bagLex, bagDir);

%% Compute the frequency of each lexicon for shoes.

shoes_text_vector = buildTextVector(shoesLex, shoesDir);

%% Compute Full vector.

fullLex = [bagLex shoesLex]
bag_text_full_vector = buildTextVector(fullLex, bagDir);
shoes_text_full_vector = buildTextVector(fullLex, shoesDir);

%% Save all vectors here.
save('bag_text_vector.mat', 'bag_text_vector');
save('shoes_text_vector.mat', 'shoes_text_vector');
save('bag_text_full_vector.mat', 'bag_text_full_vector');
save('shoes_text_full_vector.mat', 'shoes_text_full_vector');