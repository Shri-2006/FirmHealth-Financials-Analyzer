CC = gcc
all:ratios.c cJSON.c
	$(CC) -shared -fPIC ratios.c cJSON.c -o ratios.so -lm
executable: ratios.c cJSON.c
	$(CC) ratios.c cJSON.c -o ratios -lm

clean:
	rm -f *.so *.o
