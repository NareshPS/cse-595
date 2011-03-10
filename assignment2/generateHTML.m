%% This file generates HTML to display images.
function generateHTML(imageList, filePrefix, index)

htmlSuffix = '.html';
imageFileIdx = {'black', 'brown', 'red', 'silver', 'gold'};

fid = fopen([filePrefix imageFileIdx{index} htmlSuffix], 'w');
%% Write HTML Prologue
fprintf(fid, '%s\n', ['<HTML><HEAD><TITLE>' filePrefix ' ' imageFileIdx{index} '</TITLE></HEAD><BODY>']);

for i = 1:size(imageList,2);
    fprintf(fid, '%s\n',['<font size="20">' int2str(i) '</font><img src="' char(imageList(1,i)) '" />' ]);
end

%% Write HTML Epilogue
fprintf(fid, '%s\n', '</BODY></HTML>');
fclose(fid);