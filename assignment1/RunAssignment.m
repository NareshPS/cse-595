%% Create output directories for Part 1
if exist('gist_shoes_out', 'dir') == 0
    mkdir gist_shoes_out
end

if exist('gist_bag_out', 'dir') == 0
    mkdir gist_bag_out
end

if exist('gist_all_out', 'dir') == 0
    mkdir gist_all_out
end

%% Create output directories for Part 2
if exist('text_shoes_out', 'dir') == 0
    mkdir text_shoes_out
end

if exist('text_bag_out', 'dir') == 0
    mkdir text_bag_out
end

if exist('text_all_out', 'dir') == 0
    mkdir text_all_out
end

%% Create output directories for Part 3
if exist('merged_shoes_out', 'dir') == 0
    mkdir merged_shoes_out
end

if exist('merged_bag_out', 'dir') == 0
    mkdir merged_bag_out
end

if exist('merged_all_out', 'dir') == 0
    mkdir merged_all_out
end
%% Run Part 1
part1

%% Run Part 2
part2

%% Run Part 3
part3