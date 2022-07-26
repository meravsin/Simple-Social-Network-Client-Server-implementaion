#include <stdlib.h>
#include <thread>
#include <EncoderDecoder.h>
#include "../include/connectionHandler.h"
#include "../include/listenThread.h"
#include "../include/writingTread.h"




/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    EncoderDecoder encoderDecoder1(connectionHandler);
    EncoderDecoder encoderDecoder2(connectionHandler);
    listenThread listen(connectionHandler,encoderDecoder1);
    writingTread writing(connectionHandler,encoderDecoder2);
    std::thread listenThread(&listenThread::run,&listen);
    std::thread writingTread(&writingTread::run,&writing);
    listenThread.join();
    writingTread.join();
    
    return 0;
}
