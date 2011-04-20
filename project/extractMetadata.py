class MetaReader(object):
    IN = 1
    OUT = 0

    group = None

    id, secret, server, owner, url, tags = None, None, None, None, None, None
    state = 0

    def __init__(self, group):
        self.group = group
        pass

    def read(self, line):
        if line.startswith('photo:') and self.state == self.OUT:
            self.state = self.IN
            c = line.split()
            self.id, self.secret, self.server = c[1], c[2], c[3]

        if line.startswith('owner:') and self.state == self.IN:
            c = line.split()
            self.owner = c[1]
            self.url = 'http://static.flickr.com/%s/%s_%s_o.jpg' % (self.server, self.id, self.secret)

        if line.startswith('tags: ') and self.state == self.IN:
            self.tags = line.replace('tags: ', '').split()

        if line.strip() == '' and self.state == self.IN:
            self.state = self.OUT
            print self.id, self.secret, self.server, self.owner, self.url, '|'.join(self.tags)

        pass

    pass


def main(args):
    for filePath in args[1:]:
        f = open(filePath)

        import os

        m = MetaReader(os.path.basename(filePath))

        for line in f.readlines():
            m.read(line)

if __name__ == "__main__":
    import sys

    main(sys.argv)
