# Lear's GIST implementation, version 1.1, (c) INRIA 2009, Licence: GPL


all: compute_gist

FFTW3=../fftw-3.2.2/

# gist.c: ../../descriptors/gist.c 
#	cp $< $@


gist.o: gist.c gist.h standalone_image.h
	gcc -fPIC -c -Wall -g $< $(WFFTINC) -DUSE_GIST -DSTANDALONE_GIST -I$(FFTW3)/api

standalone_image.o: standalone_image.c standalone_image.h
	gcc -fPIC -c -Wall -g $< 

compute_gist: compute_gist.c gist.o standalone_image.o
	gcc -fPIC -shared -I/scratch2/hadoop2/hi/java/jdk1.6.0_07/include/ -I/scratch2/hadoop2/hi/java/jdk1.6.0_07/include/linux/ $^ -o libgist.so -lfftw3 -lm -O3

clean: 
	rm -rf *.o *.so *.class classes GistCalculator.jar
