% Load the gist vectors.
load('gist_bags.mat')
load('gist_shoes.mat')

% Create a consolidate gist and filename vector.
gist = [];
filenames = {};

% For shoes.
for r = 1:size(shoe_gist_vector, 2)
  gist(r,:) = shoe_gist_vector(r).gist;
  filenames{r} = shoe_gist_vector(r).name; 
end

% For bags.
start = size(gist,1);
for r = 1:size(bag_gist_vector, 2)
  gist(r+start,:) = bag_gist_vector(r).gist;
  filenames{r+start} = bag_gist_vector(r).name; 
end

% Create Clusters and visualize using montage.
for numClusters = 2:2:8
  H = [];
  opts = statset('Display','final');
  [cidx, ctrs] = kmeans(gist, numClusters, 'Distance','cosine', 'Replicates', 5, 'Options', opts);
  handle = figure;
  for idx = 1:numClusters
    distances = [];
    k = 1;
    for fidx = 1:size(filenames,2)
      if cidx(fidx) ==  idx
        distances(k) = pdist2(ctrs(idx,:), gist(fidx,:));
        k = k + 1;
      end
    end
    [values, indices] = sort(distances);
    files = {filenames{cidx==idx}};
    files = files(indices(1:k-1));
    montage(files(1:10:size(files,2)));
    fname = sprintf('output/%d_%d.jpg', numClusters, idx); 
    print(handle, '-djpeg', fname);
  end
end
