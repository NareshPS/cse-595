%% filters files having just one color in their description

% constant - save elsewhere?
colors = { 'black', 'brown', 'red', 'silver', 'gold' };

files = dir('bags/*.jpg');

lines = cellfun(@(n)fileread (['bags/' strrep(strrep(n, 'jpg', 'txt'), 'img', 'descr')])', {files.name}, 'UniformOutput', false);

numColors = zeros(1, numel(lines));

for i = 1 : numel(colors)
    % If not for Matlab 2k8's lack of support for temporary arrays, this
    % for loop would have been eliminated, and perhaps this whole file
    % could have been a single line. Sigh.
    tempFuckingArr = cellfun(@(l) sum(any(strfind(lower(l'), colors{i}))), lines, 'UniformOutput', false);
    thisNumColors = [tempFuckingArr{:}];
    
    numColors = numColors + thisNumColors;
end

files = files(numColors==1);

