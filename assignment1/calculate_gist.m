% Initializing parameters for gist vector calculation.
Nblocks = 4;
imageSize = 280; 
orientationsPerScale = [8 8 4];
numberBlocks = 4;
G = createGabor(orientationsPerScale, imageSize);

shoes = dir(fullfile('./shoes/', '*.jpg'));
bags = dir(fullfile('./bags/', '*.jpg'));

% Calculate gist vector for shoes.
shoe_gist_vector = []
for idx = 1:length(shoes)
    fname = fullfile('./shoes/', shoes(idx).name);
    img = imread(fname);
    imgSz = size(img);
    if length(imgSz) > 2 && imgSz(3) == 3
      img = rgb2gray(img);
    end
    output = prefilt(double(img), 4);
    g = gistGabor(output, numberBlocks, G);
    shoe_gist_vector(idx).name = fname;
    shoe_gist_vector(idx).gist = g';
end

% Save gist vector.
save('gist_shoes.mat', 'shoe_gist_vector');

% Calculate gist vector for bags.
bag_gist_vector = []
for idx = 1:length(bags)
    fname = fullfile('./bags/', bags(idx).name);
    img = imread(fname);
    imgSz = size(img);
    if length(imgSz) > 2 && imgSz(3) == 3
      img = rgb2gray(img);
    end
    output = prefilt(double(img), 4);
    g = gistGabor(output, numberBlocks, G);
    bag_gist_vector(idx).name = fname;
    bag_gist_vector(idx).gist = g';
end

% Save gist vector for bags.
save('gist_bags.mat', 'bag_gist_vector');
