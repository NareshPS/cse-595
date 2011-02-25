%% Function to cluster the gist vectors and create montage.
function [] = clusterObjects(numClusters, gistVector, fileNames, outputDir, figTitle)

opts = statset('Display','final');
[cidx, ctrs] = kmeans(gistVector, numClusters, 'Distance', 'sqEuclidean', 'Replicates', 5, 'Options', opts);

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
  handle = figure('name', [figTitle ' | Cluster Size: ' num2str(numClusters) ' Cluster ' num2str(idx) ' of ' num2str(numClusters)]);
  montageGrid(files(1:min(16,size(files,2))));
  %montage(files(1:10:size(files,2)));
  fname = sprintf('%s/%d_%d.jpg', outputDir, numClusters, idx); 
  
  fid = fopen(sprintf('%s/%d_%d.txt', outputDir, numClusters, idx), 'w');
  for fidx = 1 : min(5, numel(files))
    descfile = strrep(strrep(files(fidx), 'jpg', 'txt'), 'img', 'descr');
    
    df = fopen(descfile{1});
    toks = textscan(df, '%s');
    toks = toks';
    fclose(df);
    
    for ti = 1 : numel(toks{1}')
        fprintf(fid, '%s ', toks{1}{ti});
    end
    
    fprintf(fid, '\n');
  end
  fclose(fid);
  
  print(handle, '-djpeg', fname);
  close(handle);
end