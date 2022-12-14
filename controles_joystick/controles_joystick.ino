#include <ezButton.h>

#define MAINX_PIN A0
#define MAINY_PIN A2
#define SECONDX_PIN A1
#define SECONDY_PIN A3
#define MAIN_SW_PIN 2
#define SECOND_SW_PIN 3

#define BUTTON1_PIN 4
#define BUTTON2_PIN 5
#define BUTTON3_PIN 6
#define BUTTON4_PIN 7

ezButton button1(MAIN_SW_PIN);
ezButton button2(SECOND_SW_PIN);

int x1Value = 0;
int y1Value = 0;
int b1Value = 0;

int x2Value = 0;
int y2Value = 0;
int b2Value = 0;

bool buttonState1 = 0;
bool buttonState2 = 0;
bool buttonState3 = 0;
bool buttonState4 = 0;

void setup() {
    Serial.begin(9600);
    button1.setDebounceTime(50);
    button2.setDebounceTime(50);
    
    pinMode(BUTTON1_PIN, INPUT);
    pinMode(BUTTON2_PIN, INPUT);
    pinMode(BUTTON3_PIN, INPUT);
    pinMode(BUTTON4_PIN, INPUT);
}

void loop() {
    button1.loop();
    button2.loop();
    
    // Read analog X and Y analog values
    x1Value = analogRead(MAINX_PIN);
    y1Value = analogRead(MAINY_PIN);
    x2Value = analogRead(SECONDX_PIN);
    y2Value = analogRead(SECONDY_PIN);
    
    // Read the button1 value
    b1Value = !button1.getState();
    b2Value = !button2.getState();
    
    buttonState1 = digitalRead(BUTTON1_PIN);
    buttonState2 = digitalRead(BUTTON2_PIN);
    buttonState3 = digitalRead(BUTTON3_PIN);
    buttonState4 = digitalRead(BUTTON4_PIN);
    
    // Print data to Serial Monitor on Arduino IDE
    Serial.print("Main:");
    Serial.print(x1Value);
    Serial.print(", ");
    Serial.print(y1Value);

    Serial.print("|Secondary:");
    Serial.print(x2Value);
    Serial.print(", ");
    Serial.print(y2Value);

    Serial.print("|Buttons:");
    Serial.print(b1Value);
    Serial.print(", ");
    Serial.print(b2Value);
    Serial.print(", ");
    Serial.print(buttonState1);
    Serial.print(", ");
    Serial.print(buttonState2);
    Serial.print(", ");
    Serial.print(buttonState3);
    Serial.print(", ");
    Serial.println(buttonState4);
}
