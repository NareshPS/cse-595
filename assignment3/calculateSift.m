function [siftVector, vectorImageMap, imageNameIdx] = calculateSift(imageFiles, imageNameIdx)
% Calcualtes the sift vector for the passed imageFiles.

siftIdx = 1;
for idx = 1: size(imageFiles, 1)
    img = imread(imageFiles{idx});
    imgSz = size(img);
    %% Convert to grayscale if required.
    if length(imgSz) > 2 && imgSz(3) == 3
      img = rgb2gray(img);
    end
    
    [sift, desc] = vl_sift(im2single(img));
    
    for descIdx = 1 : size(desc, 2)
        siftVector(siftIdx,:) = desc(:,descIdx);
        vectorImageMap(siftIdx,:) = imageNameIdx;
        siftIdx = siftIdx + 1;
    end
    imageNameIdx = imageNameIdx + 1;
end
