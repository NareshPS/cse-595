function [ histograms ] = buildColorHistograms( imageFiles )
% Function calculates the color histograms for a set of imageFiles
% Returns a vector of color histograms.

numImages = numel(imageFiles);
histograms = zeros(numImages, 1000);
for idx = 1:numImages
    imageFile = char(imageFiles(idx));
    img = imread(imageFile); 
    imgSz = size(img); 
    if length(imgSz) < 3
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
for i = 1 : sizeX
    for j = 1 : sizeY
        % We divide by 11 because we have 11 discrete values
        % from 0.0 to 1.0 with intervals of 0.1
        hVal = round(hsv(i, j, 1)/11*100);
        sVal = round(hsv(i, j, 2)/11*100);
        vVal = round(hsv(i, j, 3)/11*100);
        histIdx = hVal * 100 + sVal * 10 + vVal + 1;
        histogram(1, histIdx) = histogram(1, histIdx) + 1;
    end
end
