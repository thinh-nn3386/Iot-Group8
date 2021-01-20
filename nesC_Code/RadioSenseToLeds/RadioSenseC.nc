
 
#include "Timer.h"
#include "RadioSense.h"

module RadioSenseC @safe(){
  uses {
    interface Leds;
    interface Boot;
    interface AMSend;
    interface Timer<TMilli> as MilliTimer;
    interface Packet;
    interface Read<uint16_t> as HumiSensor;
    interface Read<uint16_t> as TempSensor;
    interface Read<uint16_t> as LightSensor;
    interface SplitControl as RadioControl;
  }
}
implementation {

  message_t packet;
  bool locked = FALSE;
  uint16_t ID = 0x0003;
  uint16_t Temp;
  uint16_t Humi;
  uint16_t Light;

  task void send();
  event void Boot.booted() {
    call RadioControl.start();
  }

  event void RadioControl.startDone(error_t err) {
    if (err == SUCCESS) {
      call MilliTimer.startPeriodic(10240);
    }
  }
  event void RadioControl.stopDone(error_t err) {}
  
  event void MilliTimer.fired() {

    if ( call TempSensor.read() == SUCCESS 
		&& call LightSensor.read() == SUCCESS
			&& call HumiSensor.read() == SUCCESS)
    {
      call Leds.led1Toggle();
    }
    else 
      call Leds.led0Toggle();
  }

  event void TempSensor.readDone(error_t result, uint16_t val)
  {
    if (result == SUCCESS)
    {
      Temp = val;                  		
    }
    else 
      call Leds.led0On();
  }
	
  event void LightSensor.readDone(error_t result, uint16_t val)
  {
    if (result == SUCCESS)
    {
      Light = val;                  		
    }
    else 
      call Leds.led0On();
  }

  event void HumiSensor.readDone(error_t result, uint16_t val)
  {
    if (result == SUCCESS)
    {
      Humi = val;  
      post send();
    }
    else 
      call Leds.led0On();
  }


  event void AMSend.sendDone(message_t* bufPtr, error_t error) {
    if (&packet == bufPtr) {
      locked = FALSE;
    }
  }

  task void send(){
    if (locked) {
      return;
    }
    else {
      radio_sense_msg_t* rsm;

      rsm = (radio_sense_msg_t*)call Packet.getPayload(&packet, sizeof(radio_sense_msg_t));
      if (rsm == NULL) {
	return;
      }
      rsm->id = ID;
      rsm->temp = Temp;
      rsm->humi = Humi;
      rsm->light = Light;
      if (call AMSend.send(0x0004, &packet, sizeof(radio_sense_msg_t)) == SUCCESS) {
	locked = TRUE;
      }
    }
  }

}
