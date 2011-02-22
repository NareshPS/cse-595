%% Funtion to create vector of gist for the given image files.
%% imageFiles is a cell array of filenames.
%% returns an array of structures of filenames and gist.
function gist = buildGist(inputDir, imageRegex, imageSize, numberBlocks, orientationsPerScale)

if nargin == 1
    orientationsPerScale = [8 8 4];
    numberBlocks = 4;
    imageSize = 280;
    imageRegex = '*.jpg';
end

G = createGabor(orientationsPerScale, imageSize);

%% Calculate gist vector for shoes.
gist = [];
imageFiles = dir(fullfile(inputDir, imageRegex));
for idx = 1:length(imageFiles)
    imageFile = fullfile(inputDir, imageFiles(idx).name);
    img = imread(imageFile);
    imgSz = size(img);
    %% Convert to grayscale if required.
    if length(imgSz) > 2 && imgSz(3) == 3
      img = rgb2gray(img);
    end
    output = prefilt(double(img), 4);
    g = gistGabor(output, numberBlocks, G);
    gist(idx).name = imageFile;
    gist(idx).gist = g';
end
