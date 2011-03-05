
files = dir('bags/*.jpg');

descs = cellfun(@(m) ...
            m(:,:,1) * 100 + m(:,:,2) * 10 + m(:,:,3), ...      % sum up h + s + v values
            cellfun(@(n)  ...                                   % this rounds the hsv values converted from rgb values returned from imread
                round(rgb2hsv(imread(['bags/' n])) * 10), ...   % multiply by 10 and round the value to integers
                {files.name}, ... 
                'UniformOutput', false), ...
            'UniformOutput', false);

save descs;