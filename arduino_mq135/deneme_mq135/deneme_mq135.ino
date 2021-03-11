#include "MQ135.h"
char command;
String string;
String yazdir;

void setup (){
     Serial.begin (9600);
     Serial.println("bbb");

}
void loop() {

      MQ135 gasSensor = MQ135(A0); // Attach sensor to pin A0
    if (Serial.available() > 0) 
      {string = "";}

    while(Serial.available() > 0)
    {
      command = ((byte)Serial.read());
      string += command;
      float air_quality = gasSensor.getPPM();
      
      yazdir = "Air quality is:" + String(air_quality)+":";
      Serial.println(yazdir);
      delay(5000); 
    }
}
