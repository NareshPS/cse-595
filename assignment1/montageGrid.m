%% Creates grid from image list.
function grid = montageGrid(filenames)

numImages = size(filenames, 2);
rowSize = uint32(sqrt(numImages));
colSize = uint32(numImages/rowSize);

remImages = numImages - (rowSize*colSize);

if remImages ~= 0
    colSize = colSize + 1;
end

map = [];
imIndex = 1;
if size(filenames, 2) > 0
  imgSize = size(imread(char(filenames(1))));
  
  for i=1:rowSize
      tempMap = [];
      for j=1:colSize
          if (imIndex <= numImages)
              image = imread(char(filenames(imIndex)));
              if size(image, 3) == 3
                  tempMap = [tempMap rgb2gray(image)];
              else
                  tempMap = [tempMap image];
              end
              imIndex  = imIndex + 1;
          else
              tempMap = [tempMap zeros(imgSize(1), imgSize(2))];
          end
      end
      map = [map; tempMap];
  end
  
  montage(map);
end
