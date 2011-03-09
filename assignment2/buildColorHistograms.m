function [ histograms ] = buildColorHistograms( imageFiles )
% Function calculates the color histograms for a set of imageFiles
% Returns a vector of color histograms.

numImages = numel(imageFiles);
histograms = zeros(numImages, 1000);
for idx = 1:numImages
    imageFile = char(imageFiles(idx));
    img = imread(imageFile); 
    imgSz = size(img); 
    if length(imgSz) < 3 || size(img, 3) < 3
        continue
    else
        histograms(idx,:) = buildColorHistogram(img);
    end
end

function [ histogram ] = buildColorHistogram( image )
% Function calculates the histogram of the given image.
% Returns a 1000 binned histogram of h,s,v.

histogram = zeros(1, 1000);
hsv = rgb2hsv(image);
sizeX = size(hsv, 1);
sizeY = size(hsv, 2);
eps = 0.000001;
for i = 1 : sizeX
    for j = 1 : sizeY
        % We divide by 11 because we have 11 discrete values
        % from 0.0 to 1.0 with intervals of 0.1
        hVal = floor((hsv(i, j, 1) - eps)*10);
        sVal = floor((hsv(i, j, 2) - eps)*10);
        vVal = floor((hsv(i, j, 3) - eps)*10);
        if (hVal < 0)
            hVal = 0;
        end
        if (sVal < 0)
            sVal = 0;
        end
        if (vVal < 0)
            vVal = 0;
        end
        
        histIdx = hVal * 100 + sVal * 10 + vVal + 1;
        histogram(1, histIdx) = histogram(1, histIdx) + 1;
    end
end

histogram(1,:) = histogram(1,:)/sum(histogram(1,:));