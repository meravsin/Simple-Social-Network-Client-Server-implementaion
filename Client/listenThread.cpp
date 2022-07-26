//
// Created by mersi on 05/01/2022.
//

#include <EncoderDecoder.h>
#include "../include/listenThread.h"
#include "../include/connectionHandler.h"

listenThread::listenThread(ConnectionHandler& handler, EncoderDecoder& encoderDecoder):
connectionHandler(handler), encDec(encoderDecoder){}

void listenThread::run(){
    while(1) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<char> encoded = encDec.encode(line);
        char outputChar[encoded.size()];
        for(int i=0;(unsigned)i<encoded.size();i++) {
            outputChar[i] = encoded.at(i);
            std::cout << outputChar[i] << std::endl;
        }
        if (!connectionHandler.sendBytes(outputChar,encoded.size())) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if (line == "LOGOUT")
            break;
    }
}
