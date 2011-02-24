%% Function to cluster the gist vectors and create montage.
function [] = clusterObjects(numClusters, gistVector, fileNames, outputDir)

opts = statset('Display','final');
[cidx, ctrs] = kmeans(gistVector, numClusters, 'Distance', 'cosine', 'Replicates', 5, 'Options', opts);
handle = figure;
for idx = 1:numClusters
  distances = [];
  k = 1;
  for fidx = 1:size(fileNames,2)
    if cidx(fidx) ==  idx
      distances(k) = pdist2(ctrs(idx,:), gistVector(fidx,:));
      k = k + 1;
    end
  end
  [values, indices] = sort(distances);
  files = {fileNames{cidx==idx}};
  files = files(indices(1:k-1));
  montageGrid(files(1:10:size(files,2)));
  %montage(files(1:10:size(files,2)));
  fname = sprintf('%s/%d_%d.jpg', outputDir, numClusters, idx); 
  print(handle, '-djpeg', fname);
end
