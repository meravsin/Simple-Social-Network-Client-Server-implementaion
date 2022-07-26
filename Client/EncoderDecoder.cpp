//
// Created by mersi on 06/01/2022.
//

#include <sstream>
#include <ctime>
#include <iomanip>
#include "../include/EncoderDecoder.h"
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

EncoderDecoder::EncoderDecoder(ConnectionHandler& handler): Handler(handler){}

bool EncoderDecoder::decode(std::string &frame) {
    string firstOpcode;
    if(!getShort(firstOpcode))return false;
    else{
        if(firstOpcode=="9") {
            frame.append("NOTIFICATION ");
            return decodeNotification(frame);
        }
        if(firstOpcode=="10") {
            frame.append("ACK ");
            return decodeACK(frame);
        }
        if(firstOpcode=="11") {
            frame.append("ERROR ");
            return decodeError(frame);
        }
    }
}

bool EncoderDecoder::decodeACK(std::string &frame) {
    std::string secondOpcode;
    // decode second opcode
    if (!getShort(secondOpcode))return false;
    else{
        frame.append(secondOpcode+ " ");
        if(secondOpcode=="7" || secondOpcode=="8"){ // stat and logStat cases
            for(int i=0; i<4; i++){
                if (!getShort(frame)) {
                    return false;
                }
                if(i<3)
                    frame.append(" ");
            }
        }
        if (secondOpcode=="4"){ // follow
            if (!Handler.getFrameAscii(frame, '\0')) {
                return false;
            }
        }
        return true;
    }
}
bool EncoderDecoder::decodeNotification(std::string &frame) {
    std::string NotificationType;
    //decode notification type
    if (!getByte(NotificationType)) return false;
    if (NotificationType == "0")
        frame.append("PM ");
    else
        frame.append("Public ");
    //decode PostingUser
    if (!Handler.getFrameAscii(frame, '\0')) return false;
    frame.append(" ");
    //decode Content
    if (!Handler.getFrameAscii(frame, '\0')) return false;
    return true;
}

bool EncoderDecoder::decodeError(std::string &frame) {
    // decode second opcode
    if(!getShort(frame)) return false;
    else return true;
}


std::vector<char> EncoderDecoder::encode(std::string &frame){
    std::stringstream strt(frame);
    string message;
    std::vector<std::string> split;
    std::vector<char> toReturn;
    while (getline(strt, message, ' ')) {
        split.push_back(message);
    }
    char opcodeBytesArr[2];
    std::string opcodeString = split.at(0);
    if(opcodeString == "REGISTER"){
        shortToBytes(1, opcodeBytesArr);
        toReturn.push_back(opcodeBytesArr[0]);
        toReturn.push_back(opcodeBytesArr[1]);
        std::string username = split.at(1);
        std::string password = split.at(2);
        std::string birthday = split.at(3);
        const char *usernameChar = username.c_str();
        const char *passwordChar = password.c_str();
        const char *birthdayChar = birthday.c_str();
        for (int i = 0; (unsigned)i < username.length(); i++) {
            toReturn.push_back(usernameChar[i]);
        }
        toReturn.push_back('\0');
        for (int i = 0;(unsigned) i < password.length(); i++) {
            toReturn.push_back(passwordChar[i]);
        }
        toReturn.push_back('\0');
        for (int i = 0;(unsigned) i < birthday.length(); i++) {
            toReturn.push_back(birthdayChar[i]);
        }
        toReturn.push_back('\0');
        toReturn.push_back(';');
        return toReturn;
    }

    else if(opcodeString == "LOGIN"){
        shortToBytes(2, opcodeBytesArr);
        toReturn.push_back(opcodeBytesArr[0]);
        toReturn.push_back(opcodeBytesArr[1]);
        std::string username = split.at(1);
        std::string password = split.at(2);
        std::string Captcha = split.at(3);
        const char *usernameChar = username.c_str();
        const char *passwordChar = password.c_str();
        const char *CaptchaChar = Captcha.c_str();
        for (int i = 0; (unsigned)i < username.length(); i++) {
            toReturn.push_back(usernameChar[i]);
        }
        toReturn.push_back('\0');
        for (int i = 0;(unsigned) i < password.length(); i++) {
            toReturn.push_back(passwordChar[i]);
        }
        toReturn.push_back('\0');
        for (int i = 0;(unsigned) i < Captcha.length(); i++) {
            toReturn.push_back(CaptchaChar[i]);
        }
        toReturn.push_back('\0');
        toReturn.push_back(';');
        return toReturn;
    }

    else if(opcodeString == "LOGOUT" ){
        shortToBytes(3, opcodeBytesArr);
        toReturn.push_back(opcodeBytesArr[0]);
        toReturn.push_back(opcodeBytesArr[1]);
        toReturn.push_back(';');
        return toReturn;
    }

    else if(opcodeString== "LOGSTAT"){
        shortToBytes(7, opcodeBytesArr);
        toReturn.push_back(opcodeBytesArr[0]);
        toReturn.push_back(opcodeBytesArr[1]);
        toReturn.push_back(';');
        return toReturn;
    }

        else if(opcodeString == "FOLLOW"){
        shortToBytes(4, opcodeBytesArr);
        toReturn.push_back(opcodeBytesArr[0]);
        toReturn.push_back(opcodeBytesArr[1]);
        std::string followUnfollow = split.at(1);
        std::string username = split.at(2);
        const char *usernameChar = username.c_str();
        if(followUnfollow=="1") {
            toReturn.push_back('\1');
        }
        else{
            toReturn.push_back('\0');
        }
        for (int i = 0; (unsigned)i < username.length(); i++) {
            toReturn.push_back(usernameChar[i]);
        }
        toReturn.push_back(';');
        return toReturn;
    }

    else if(opcodeString == "POST" || opcodeString == "STAT" || opcodeString == "BLOCK"){
        if(opcodeString == "POST") {
            shortToBytes(5, opcodeBytesArr);
            toReturn.push_back(opcodeBytesArr[0]);
            toReturn.push_back(opcodeBytesArr[1]);
        }
        if(opcodeString == "STAT") {
            shortToBytes(8, opcodeBytesArr);
            toReturn.push_back(opcodeBytesArr[0]);
            toReturn.push_back(opcodeBytesArr[1]);
        }
        if(opcodeString == "BLOCK") {
            shortToBytes(12, opcodeBytesArr);
            toReturn.push_back(opcodeBytesArr[0]);
            toReturn.push_back(opcodeBytesArr[1]);
        }
        std::string Content = split.at(1);
        const char *ContentChar = Content.c_str();
        for (int i = 0; (unsigned)i < Content.length(); i++) {
            toReturn.push_back(ContentChar[i]);
        }
        toReturn.push_back('\0');
        toReturn.push_back(';');
        return toReturn;
    }
    else if(opcodeString == "PM"){
        shortToBytes(6, opcodeBytesArr);
        toReturn.push_back(opcodeBytesArr[0]);
        toReturn.push_back(opcodeBytesArr[1]);
        std::string username = split.at(1);
        const char *usernameChar = username.c_str();
        for (int i = 0; (unsigned)i < username.length(); i++) {
            toReturn.push_back(usernameChar[i]);
        }
        toReturn.push_back('\0');
        std::string Content = split.at(2);
        const char *ContentChar = Content.c_str();
        for (int i = 0; (unsigned)i < Content.length(); i++) {
            toReturn.push_back(ContentChar[i]);
        }
        toReturn.push_back('\0');
        auto t = std::time(nullptr);
        auto tm = *std::localtime(&t);
        std::ostringstream oss;
        oss << std::put_time(&tm, "%d-%m-%Y %H-%M-%S");
        auto str = oss.str();
        const char *strChar = str.c_str();
        for (int i = 0; (unsigned)i < str.length(); i++) {
            toReturn.push_back(strChar[i]);
        }
        toReturn.push_back('\0');
        toReturn.push_back(';');
        return toReturn;
    }
}

short EncoderDecoder::bytesToShort(char *bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void EncoderDecoder::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}


bool EncoderDecoder::getShort(std::string &line){
    char ch1;
    char ch2;
    try {
        Handler.getBytes(&ch1, 1);
    }catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    try {
        Handler.getBytes(&ch2, 1);
    }catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    char bytesArr[2] = {ch1, ch2};
    short value= bytesToShort(bytesArr);
    line.append(std::to_string(value));
    return true;
}

bool EncoderDecoder::getByte(std::string &line){
    char bytesArr;
    try {
        Handler.getBytes(&bytesArr, 1);
    }catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    line.append(1, bytesArr);
    return true;
}

bool EncoderDecoder::sendShort(short s){
    char c[2];
    shortToBytes(s,c);
    return Handler.sendBytes(c, 2);
}
