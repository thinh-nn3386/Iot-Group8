
#include "RadioSense.h"


configuration RadioSenseAppC {}
implementation {
  components MainC, RadioSenseC as App, LedsC;
  components new SensirionSht11C() as TempSensor;
  components new SensirionSht11C() as HumiSensor;
  components new HamamatsuS1087ParC() as LightSensor;

  components ActiveMessageC;
  components new AMSenderC(7);
  components new TimerMilliC();
  
  App.Boot -> MainC.Boot;
  App.Leds -> LedsC;
  App.MilliTimer -> TimerMilliC;
  
  App.AMSend -> AMSenderC;
  App.RadioControl -> ActiveMessageC;
  App.Packet -> AMSenderC;

  App.TempSensor -> TempSensor.Temperature;
  App.HumiSensor -> HumiSensor.Humidity;
  App.LightSensor -> LightSensor;
}
