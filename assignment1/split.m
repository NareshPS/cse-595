% text can be file descriptor or string
function ftoks = split(text, delim)

% default delimiter
if nargin == 1
    delim = ' .;,!:''';
end

% get delimited strings
toks = textscan(text, '%s', 'Delimiter', delim);
toks = toks{1};

% skip empties
ftoks = {};
skip = 0;

for i = 1: numel(toks)
    if numel(toks{i}) > 0
        ftoks{i-skip} = toks{i};
    else
        skip = skip + 1;
    end
end
