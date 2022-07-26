//
// Created by mersi on 05/01/2022.
//

#include <EncoderDecoder.h>
#include "../include/writingTread.h"
#include "../include/connectionHandler.h"

writingTread::writingTread(ConnectionHandler& handler, EncoderDecoder& encoderDecoder) :
Handler(handler), encDec(encoderDecoder){}

void writingTread::run(){
    while(1) {
        std::string answer;
        if (!encDec.decode(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        std::cout<< answer << std::endl;
        if (answer == "ACK 3") {
            break;
        }
        answer.clear();
    }
}