%% This Script run clustering algorithm on text vectors.

%% Load bag and shoes text vectors.
load('bag_text_full_vector.mat')
load('shoes_text_full_vector.mat')

%% Create a consolidate text and filename vector.
text_vector = [];
filenames = {};

%% For bags.
for r = 1:size(bag_text_full_vector, 2)
  text_vector(r,:) = bag_text_full_vector(r).vector;
  filenames{r} = bag_text_full_vector(r).name; 
end

%% For Shoes.
start = size(text_vector,1);
for r = 1:size(shoes_text_full_vector, 2)
  text_vector(r+start,:) = shoes_text_full_vector(r).vector;
  filenames{r+start} = shoes_text_full_vector(r).name; 
end

%% Create Clusters and visualize using montage.
for numClusters = 2:2:8
  H = [];
  opts = statset('Display','final');
  [cidx, ctrs] = kmeans(text_vector, numClusters, 'Distance','cosine', 'Replicates', 5, 'Options', opts);
  handle = figure;
  for idx = 1:numClusters
    distances = [];
    k = 1;
    for fidx = 1:size(filenames,2)
      if cidx(fidx) ==  idx
        distances(k) = pdist2(ctrs(idx,:), text_vector(fidx,:));
        k = k + 1;
      end
    end
    [values, indices] = sort(distances);
    files = {filenames{cidx==idx}};
    files = files(indices(1:k-1))
    montage(files(1:10:size(files,2)));
    fname = sprintf('output/%d_%d.jpg', numClusters, idx); 
    print(handle, '-djpeg', fname);
  end
end