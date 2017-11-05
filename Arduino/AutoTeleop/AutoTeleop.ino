#include <Wire.h>
#include <Adafruit_NeoPixel.h>

#define PIN 6
Adafruit_NeoPixel strip = Adafruit_NeoPixel(24, PIN, NEO_GRB + NEO_KHZ800);

String state;

uint32_t red = strip.Color(60,0,0);
uint32_t green = strip.Color(0,100,15);
uint32_t oyellow = strip.Color(140,40,0);
uint32_t blue = strip.Color(0,0,100);
uint32_t orange = strip.Color(140,10,0);
uint32_t yellow = strip.Color(90,20,0);
uint32_t purple = strip.Color(55,0,75);
uint32_t pink = strip.Color(80,0,65);
uint32_t lgreen = strip.Color(10,80,10);
uint32_t lblue = strip.Color(15,35,135);
uint32_t cyan = strip.Color(0,80,60);
uint32_t white = strip.Color(40,40,40);

const int rgbChangeSpeed = 18;

//highest value a color would reach.
const int colorMax = 108;
int rred = colorMax;
int rgreen = 0;
int rblue = 0;

//information about the white pixel bit that flows around
int currentPos = 0;
const int whiteLength = 7;
const int whiteSpeed = 2;

int tempIndex;

//last position position that the white pixel bit was at.
int lastPos = strip.numPixels();
//previous color that gets overwritten by the white pixel bit.
uint32_t lastColor;

uint32_t ra1 = purple;
uint32_t ra2 = pink;
uint32_t ba1 = lgreen;
uint32_t ba2 = cyan;
uint32_t rt1 = red;
uint32_t rt2 = orange;
uint32_t bt1 = blue;
uint32_t bt2 = purple;

int loopCounter;
boolean rippleInvert = false;
int ripplePos = 0;

void setup() {
  // put your setup code here, to run once:
  Wire.begin(4);
  Serial.begin(9600);
  Wire.onReceive(receiveEventWire);
  strip.begin();
  strip.show();
  state = "init";
}

void loop() {
  if (loopCounter % 2 == 0) {
    ripplePos++;
    loopCounter = 1;
  } else {
    loopCounter++;
  }

  if (ripplePos > strip.numPixels() / 2) {
    ripplePos = ripplePos % (strip.numPixels()/2);
    rippleInvert = !rippleInvert;
  }
  
  if (state.equals("init")) {
    //if you've completed a cycle (ie current position is now 0, last position is 150)
    if (currentPos < lastPos || currentPos > 149) {
      //store lastColor, then find your next incremented color
      lastColor = strip.Color(rred,rgreen,rblue);
      nextRGB();
    }
    lastPos = currentPos;
    tempIndex = currentPos;
    //sets the little white pixel bit to white.
    while (tempIndex != (currentPos + whiteLength) % 150) {
      strip.setPixelColor(tempIndex,100,100,100);
      tempIndex = (tempIndex + 1) % 150;
    }
    //if you're behind the white thing, get set to the new color
    for (int i = 0; i < currentPos; i++) {
      strip.setPixelColor(i,rred,rgreen,rblue);
    }
    //if you're beyond the white thing, get set to the old color.
    for (int i = currentPos + whiteLength + 1; i < 150; i++) {
      strip.setPixelColor(i,lastColor);
    }
    //update strip, move white pixel bit forward by speed defined above
    strip.show();
    currentPos += whiteSpeed;
    currentPos %= 150;
    delay(17);
  } else if (state.equals("red_auto")) {
    for (int i = 0; i < strip.numPixels(); i++) {
      setColor(i, ra1, ra2);
    }
  } else if (state.equals("blue_auto")) {
    for (int i = 0; i < strip.numPixels(); i++) {
      setColor(i, ba1, ba2);
    }
  } else if (state.equals("red_teleop")) {
    for (int i = 0; i < strip.numPixels(); i++) {
      setColor(i, rt1, rt2);
    }
  } else if (state.equals("blue_teleop")) {
    for (int i = 0; i < strip.numPixels(); i++) {
      setColor(i, bt1, bt2);
    }
  }
  strip.show();
  delay(1);
}

void receiveEventWire(int numBytes) {
  //Serial.println("just entered event");
  String temp  = "";
  while(Wire.available() > 0){
    char n = (char) Wire.read();
    temp+=n;
  }

  state = temp;
}

void setColor(int i, uint32_t c1, uint32_t c2) {
  if (!rippleInvert) {
    if (i < strip.numPixels()/2 - ripplePos || i > strip.numPixels()/2+ripplePos) {
      strip.setPixelColor(i,c2);
    } else if (i == strip.numPixels()/2 - ripplePos || i == strip.numPixels()/2+ripplePos){
      strip.setPixelColor(i,90,90,90);
    } else {
      strip.setPixelColor(i,c1);
    }
  } else {
    if (i < strip.numPixels()/2 - ripplePos || i > strip.numPixels()/2+ripplePos) {
      strip.setPixelColor(i,c1);
    } else if (i == strip.numPixels()/2 - ripplePos || i == strip.numPixels()/2+ripplePos) {
      strip.setPixelColor(i,90,90,90);
    }
    else {
      strip.setPixelColor(i,c2);
    }
  }
}

void nextRGB() {
  if (rred == colorMax && rgreen < colorMax && rblue == 0) {
    rgreen += rgbChangeSpeed;
  } else if (rgreen == colorMax && rred > 0 && rblue == 0) {
    rred -= rgbChangeSpeed * 1.5;
  } else if (rgreen == colorMax && rblue < colorMax && rred == 0) {
    rblue += rgbChangeSpeed;
  } else if (rblue == colorMax && rgreen > 0 && rred == 0) {
    rgreen -= rgbChangeSpeed;
  } else if (rblue == colorMax && rred < colorMax && rgreen == 0) {
    rred += rgbChangeSpeed;
  } else if (rred == colorMax && rblue > 0 && rgreen == 0) {
    rblue -= rgbChangeSpeed * .5;
  }
}
