# Spectrometer protocol definition
### Protocol version: 3.12 (14–VI–03-VII-2019))
### API rev: 2.0  (14-VI-2019)
### Authors: Carles Ricart, Pau Garcia, Jorge E. Higuera, F. J. Campoy, Daniel Díaz, Josep Carrereas

## Version LOG

 - **2.1 (24–IV–2019)** 
	 - Initial
-  **3.0 (29-V-2019)** 
	- REPEAT_SYNC_SPECTRA suppressed
	- NEXT_SYNC_SPECTRA with block number
-  **3.1 (14-VI-2019)** 
	- Video sample rate is a float, not an int
 -  **3.2 (03-VII-2019)** 
	- deleted DeviceState.flashMemory 
	- added DeviceInfo.sharedAccessKey1 +  DeviceInfo.sharedAccessKey2

## Protocol frame structure

|SYNC|FLAGS|TOKEN|FUNCTION|SEQ| LENG
|--|--|--|--|--|--|
|2 byte|1 byte|1 byte|1 byte|2 byte|1 byte

### 0x01)	SET_INTEGRATION_TIME
**Description:** Time the sensor is open obtaining data. The longest the time the more light impacts the diode sensor (similar to *exposure time* in photography).

*Payload length:* 2 byte
*Arguments:*


 1. **time** ( 2  bytes=1 unsigned short) value ranging from 5 ms to 7000 ms, without decimal precision.
 
*Response:*
 - RSP_COMPLETED
 - RSP_ERROR 
- RSP_BUSY

### 0x02)	GET_INTEGRATION_TIME
**Description:** Time the sensor is open obtaining data. The longest the time the more light impacts the diode sensor (similar to *exposure time* in photography).


*Payload length:* 0 bytes


*Arguments:* None


*Response:*

 - RSP_RETURN: **time** (2 bytes) value ranging from 5 ms to 14000 ms, without decimal precision
 - RSP_BUSY
 - RSP_ERROR
 

### 0x03)	SET_GAIN
**Description:** Activates or deactivates the amplification of the received light signal modifying the wideness of the signal entry (similar concept to how much the shutter is open in photography).


*Payload length:* 1 byte


*Arguments:*


 1. **activate** (1 unsigned byte): 0 to desactivate, 1 to activate.

*Response:*

 - RSP_COMPLETED
 - RSP_ERROR 
- RSP_BUSY



### 0x04)	GET_GAIN
**Description:** Activates or deactivates the amplification of the received light signal modifying the wideness of the signal entry (similar concept to how much the shutter is open in photography).

*Payload length:* 0 byte

*Arguments:* None

*Response:*

 - RSP_RETURN: **activate** (1  byte): 0 if deactivated, 1 if activated.
 - RSP_BUSY
 - RSP_ERROR


### 0x05)	GET_RAW_SPECTRUM
**Description:** Gets the spectrum of the light received by the spectrometer. The data is sent raw from the sensor without prior treatment (only the tension between 0.0 V and 3.3 V is transformed to a 0 to 4095 count per pixel value). This is the counts passed to the micro by the 256 pixels of the sensor.

*Payload length:* 0 bytes

*Arguments:* None

*Response:*		

**seq 0:**
	
*DATA = start_capture (1 byte) + RTC (6 bytes) + integraton time (2 bytes) + temperature (1 float: 4 bytes) + spectrum (first 231 bytes)

**seq 1:**

*DATA = 244 bytes

**seq 2:**

DATA = 37 bytes


(flag_start_capture --> when a photo or video start, we set to 1 this byte.  In video cases, the first spectrum goes with 1, and the following spectro with 0. )


 - RSP_RETURN: **spectrum** (256 x 2 unsigned bytes = 512 bytes): Each element's value ranges from 0 to 4095 counts per pixel (related to a voltage).
 - RSP_BUSY
 - RSP_ERROR

### 0x06)	START_STREAMING
**Description:** Start video without save in flash
**Arguments:** 1bytes
**Trama:**
SYNC|FLAGS|TOKEN|FUNCTION|DATA*|
|--|--|--|--|--|
|2 bytes|1 byte|1 byte|1 byte|3 byte|

*Data =0x000000 --> without specifing the end time

*Data =0x000001 to 0x15180  (24hours)  -->stop streaming after this time in seconds.

*Response:*
 - RSP_RETURN
 - RSP_BUSY
 - RSP_ERROR

### 0x07)	STOP_STREAMING
**Description:** Stop the current video

**Arguments:** 0 bytes

*Trama:* 
SYNC|FLAGS|TOKEN|FUNCTION|DATA|
|--|--|--|--|--|
2 bytes|1 byte|1 byte|1 byte|0 byte|

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR



### 0x08)	SET_TRANSFER_FUNCTION
**Description:** Sets the spectrum calibration. This is an array of numbers that multiplying the spectrum converts pixel counts to milliwatts per nanometer (mW/nm) and adjusts the sensor according to the laboratory calibration. The calibration part means the transfer includes an adjustment that compensates the difference between the spectrum computed by the spectrometer and the same spectrum detected by the calibration sphere at the laboratory. That is, an adjustment from the sensor count to the lab measurement milliwatts.


*Payload length:* 324 bytes


*Arguments:*


 1. **TF** (81 floats x 4 bytes/float = 324 bytes): Array of multiplying factors that adjusts real count measurements to theoretical mW/nm of the spectrum.

Function transmitted using multitrama protocol

*Response:*

 - RSP_ACK
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR

### 0x09)	GET_TRANSFER_FUNCTION
**Description:** Gets the spectrum calibration. This is an array of numbers that allow to go from pixel counts to microwatts per nanometer (µW/nm) by multiplication at the same time the function is calibrated. The calibration part means the transfer includes an adjustment that compensates the difference between the spectrum computed by the spectrometer and the same spectrum detected by the calibration sphere at the laboratory. That is, an adjustment from the sensor count to the lab measurement microwatts.


*Payload length:* 0 bytes


*Arguments:* None

*Response:*
 - RSP_RETURN: **TF** (81 floats x 4 bytes/float = 324 bytes): Array of multiplying factors that adjusts real count measurements to theoretical µW/nm of the spectrum.
 Function transmitted using multitrama protocol
 - RSP_ERROR

### 0x0A)	SET_DEFAULT_TRANSFER_FUNCTION

Same as *SET_TRANSFER_FUNCTION* but sets the default value.

### 0x0B)	GET_DEFAULT_TRANSFER_FUNCTION

Same as *GET_TRANSFER_FUNCTION* but sets the default value.

### 0x0C)	RELOAD_DEFAULT_TRANSFER_FUNCTION

Sets the value of default_transfer_function in transfer_function### 0x0B)	GET_DEFAULT_TRANSFER_FUNCTION

**Description** 
*Payload length:* 

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 
### 0x0D)	SET_WAVELENGTH_CALIBRATION 
**Description:** Sets the conversion factors from pixel number (its order in the array) to wavelength in nanometers (nm). These are 6 coefficients that multiply the pixel number to obtain the nanometers represented by that pixel. The first element is the largest coefficient multiplied by one, the second is the second largest multiplied by the pixel number, the third is the third largest coefficient multiplied by the square of the pixel number and so on until the sixth coefficient, the smallest one. Formula: nm = y (px) = a0 + a1 * px + a2 * px^2 + a3 * px^3 + a4 * px^4 + a5 * px^5

*Payload length:* 24 bytes

*Arguments:*

 1. **Coefficients** (6 floats x 4 bytes/float = 24 bytes): Array of 6 values ranging from -380 to 380 nm (up to a 10-6 exponential and 5 significant digits).

*Response:*

 - RSP_ACK
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR

### 0x0E)	GET_WAVELENGTH_CALIBRATION
**Description:** Gets the conversion factors from pixel number (its order in the array) to wavelength in nanometers (nm). These are 6 coefficients that multiply the pixel number to obtain the nanometers represented by that pixel. The first element is the largest coefficient multiplied by one, the second is the second largest multiplied by the pixel number, the third is the third largest coefficient multiplied by the square of the pixel number and so on until the sixth coefficient, the smallest one. Formula: nm = y (px) = a0 + a1 * px + a2 * px^2 + a3 * px^3 + a4 * px^4 + a5 * px^5

*Payload length:*  bytes

*Response:*

 - RSP_RETURN: *Coefficients** (6 floats x 4 bytes/float = 24 bytes): Array of 6 values ranging from -380 to 380 nm (up to a 10-6 exponential and 5 significant digits).
 - RSP_ERROR
 - RSP_ERROR_CRC
 - RSP_ERROR_NO_RETRY

### 0x0F)	SET_DEFAULT_WAVELENGTH_CALIBRATION
Same as *SET_WAVELENGTH_CALIBRATION* but sets the default value.

### 0x10)	GET_DEFAULT_WAVELENGTH_CALIBRATION
Same as *GET_WAVELENGTH_CALIBRATION* but sets the default value.

### 0x11)	RELOAD_DEFAULT_WAVELENGTH_CALIBRATION
Sets the value of default_wavelength_calibration in wavelength_calibration

### 0x12)	CALIBRATE_BACKGROUND 
**Description:** Order the spectrometer to calibraa a ue sh esrt e bckgrdterac i l teareter. 

Spectrometer prepares to calibrate.

*Payloadretrn length:* 0 bytes

*Arguments:*

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 - RSP_ERROR_CRC
 - RSP_ERROR_NO_RETRY

### 0x13)	GET_BACKGROUND_CALIBRATIONS 
**Description:** Retornarla mitja  espectre mesurat del background, el temps d'integració i la temperatura amb que s'ha mesurat el backgorund

*return length:* 8 bytes

*Response:*

 - RSP_RETURN: *DATA= 2 bytes counts background + 2 bytes integraton 		times+1float(4bytes) 	temperatura 
 - RSP_ERROR

### 0x14)	CALIBRATE_DEFAULT_BACKGROUND
**Description:** Order the spectrometer to calibraa a ue sh esrt e bckgrdterac i l teareter. 

Spectrometer prepares to calibrate.

*Payloadretrn length:* 0 bytes

*Arguments:*

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR

### 0x15)	GET_DEFAULT_BACKGROUND_CALIBRATIONS
Same as GET_BACKGROUND_CALIBRATIONS but with the default values

### 0x16)	RELOAD_DEFAULT_BACKGROUND_CALIBRATIONS
**Description:** puts the default calibration background into calibration background.


### 0x17)	--NOP--
undefined

### 0x18)	SET_LUX_CALIBRATION

**Description:** Sets the lux calibration
*Arguments* 1 float (4 bytes)

*Response:*

 - RSP_COMPLETED 
 - RSP_ERROR

### 0x19)	GET_LUX_CALIBRATION
**Description:** Gets the lux calibration
*Arguments* None

*Response:*

 - RSP_RETURN 1 float (4 bytes) 
 - RSP_ERROR

### 0x1A)	SET_DEFAULT_LUX_CALIBRATION
Same as 	SET_LUX_CALIBRATION but with the default values

### 0x1B)	GET_DEFAULT_LUX_CALIBRATION
Same as 	GET_LUX_CALIBRATION but with the default values

### 0x1C)	RELOAD_DEFAULT_LUX_CALIBRATION
Loads the default lux value in the lux calibration

### 0x1D)	SET_VIDEO_SAMPLE_RATE
**Description:** Sets the video frequency between frames in seconds.

*Arguments:* 1 float

*Response:*
 - RSP_COMPLETED
 - RSP_ERROR

### 0x1E)	GET_VIDEO_SAMPLE_RATE
**Description:** Returns the frame frequency in time.

*return length:* 1 float

*Response:*

 - RSP_RETURN:  DATA= 1 float (value in seconds)
 

### 0x1F)	SET_TIME
**Description:** Sets the current time. IMPORTANT: time must be in UTC-0

*Arguments:* 7 bytes

*Trama:*  			
					                             
SYNC|FLAGS|TOKEN|FUNCTION|HOURS|MINUTES|SECONDS|DAY|MONTH|YEAR*
|--|--|--|--|--|--|--|--|--|--|
|2 bytes|1 byte|1 byte|1 byte|1 byte|1 byte|1 byte|1 byte|1 byte|1 byte

The *year* field must contain the year minus 2000. (i.e the year 2018 would be codified as 18)

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 
### 0x20)	GET_TIME

**Description:** Gets the current time. IMPORTANT: time is in UTC-0
**Arguments:** 0 bytes

*Response:*
 - RSP_RETURN:**time** ( 7 bytes): the photonphy time stamp
 - RSP_BUSY
 - RSP_ERROR

### 0x21)	GET_DEVICE_STATE
**Description:** Returns 5 bytes(1byte+1float)
**Arguments:** 0 bytes

RSP_RESPONSE **Battery state**  (1 byte)   0-100%
							**Temperature Sensor**(1float): the actual sensor temptature 
							
### 0x22)	SET_DEVICE_INFO
*Payload length:*  variable

*Arguments:* 
- **serial_number**  (max 24 bytes = 24 char) [String that consist of the fabrication time + product name (990004) + product version + a chronological number]. It has to end with endstring char [\0]

- **model** (max 20 bytes = 20 char) [String that contains the name of the model of this spectrometer]. It has to end with endstring char [\0]

- **Default firmware version** (max 20 bytes = 20 char). Sets the default firmware version. It has to end with endstring char [\0](Example: 1.4.1a)	
- **sensor_type** (1 byte) 
- **Shared acces key1** (max 60 bytes = 60 char) [String that contains the shared aaces key1]. It has to end with endstring char [\0]
- **Shared acces key2**(max 60 bytes = 60 char)  [String that contains the shared aaces key2]. It has to end with endstring char [\0]	 
- **IotHubName**(max 20 bytes = 20 char)[String that contains the IotHubname].It has to end with endstring char [\0]
- **Sensorname**(max 20 bytes = 20 char)[String that contains the opticalsensorname].It has to end with endstring char [\0]

*Response:* 
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR



### 0x23)	GET_DEVICE_INFO
**Description** Returns a tuple of strings defining the serial code and the model of the the portable spectrometer. The model is concatenated after the serial code.

*Payload length:* 0 bytes

*Arguments:* None

*Response:* 
 - RSP_RETURN: 
  	 - **serial_number**  (24 bytes = 24 char) [String that consist of the fabrication time + product name (990004) + product version + a chronological number]. It has to end with endstring char [\0]
  	 - **model** (20 bytes = 20 char) [String that contains the name of the model of this spectrometer]. It has to end with endstring char [\0]
  	  - **sensor_type** (1 byte) 
  	 - **default_fw_version** ( 4  bytes  = 3 byte + 1 char) 
  	 - **current_fw_version** ( 4  bytes  =  3 byte+ 1 char) 
  	 - **shared acces key 1** (6 char max)
	 - **shared acces key 2** (6 char max)
	 - **max_integration_time** (2  bytes = 1 unsigned short)
	 - **min_integration_time** ( 2 bytes = 1 unsigned short)
	 - **total_flash_memory** (3 bytes )   Flash capacity0
	 - **IotHubName**(max 20 bytes = 20 char)[String that contains the IotHubname].It has to end with endstring char [\0]
	 - **Sensorname**(max 20 bytes = 20 char)[String that contains the opticalsensorname].It has to end with endstring char [\0]
	 

 - RSP_ERROR
 - RSP_BUSY

### 0x24)	SET_FIRMWARE_UPDATE
**Description:** With the multitrama protocol transfers all the file containing the firmware update

### 0x25)	SET_DEFAULT_FIRMWARE_UPDATE
**Description:** With the multitrama protocol transfers all the file containing the firmware update

### 0x26)	SET_FIRMWARE_UPDATE_METADATA

**Description:** Set the firmware update information
**Arguments:** 12 bytes

SYNC|FLAGS|TOKEN|FUNCTION|SEQ|LENGTH|Size new firmware|Crc valu|Fw version|isdefaultfirmware
|--|--|--|--|--|--|--|--|--|--|--|--|
|2 bytes|1 byte|1 byte|1 byte|2 byte|1 byte|4 byte|4 byte|4 byte|1 byte

isfedaultfirmware = 1, (is the default firmware metadata)
isfedaultfirmware = 0 (is the firmware metadata)

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR

### 0x27)	SET_SOFT_FACTORY_RESET 
Reload the calibrations

### 28)	SET_HARD_FACTORY_RESET 
Reload firmware + all the calibrations

### 0x29)	SET_BACKGROUND

**Description:** Set the background information
**Arguments:** 8 bytes

 1. **Payload** (2 bytes(counts) + 2 bytes(integration time)+4bytes(1 float) temperature=8 bytes): 

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR

### 0x2A)	SET_DEFAULT_BACKGROUND

Same as SET_BACKGROUND but for the default values

### 0x2B)   SET_BACKGROUND_COEFFICIENTS

**Description:** Set the backgrounds coefficients previously mesured. We have been mesured  14 diferents integration time in a range of temperature in order to know the background behaviour. formula:  y=a*x^2+b*x+c , where a,b,c is the matrix.
*Arguments:*

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

*Response:*
 - RSP_ACK
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 - 
### 0x2C)   GET_BACKGROUND_COEFFICIENTS

**Description:** Get the backgrounds coefficients previously.

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

### 0x2D)   SET_DEFAULT_BACKGROUND_COEFFICIENTS

**Description:** Set the backgrounds coefficients previously mesured. We have been mesured  14 diferents integration time in a range of temperature in order to know the background behaviour. formula:  y=a*x^2+b*x+c , where a,b,c is the matrix.
*Arguments:*

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

*Response:*
 - RSP_ACK
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 - 
### 0x2E)   GET_DEFAULT_BACKGROUND_COEFFICIENTS

**Description:** Get the backgrounds coefficients previously.

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

### 0x2F) RELOAD_DEFAULT_BACKGROUND_COEFFICIENTS
Reloads the default values

### 0x30) END_INITIALIZATION

**Description:** This function must be called application have all the calibrations and information about the spectrometer. This function idicates to the spectrometer that is able to take fotos and videos because initialization phase has finalized.

 **Payload** None
 
*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 - 
### 0x31) NEXT_SYNC_SPECTRA
**Description:** Returns a chunk of N spectra codified on the same way as on GET_RAW_SPECTRUM. 

Videos and photos are distinguished with the byte start_capture on the payload of the spectrum. 

Example of byte start capture:

1 -> Photo
1 -> Photo
1 -> Video (because it is followed by 0s)
1 -> Photo

The end of the chunk is indicated with the flag of multitrama at 0.

The block_number indicates which block of the flash memory has to be transmitted. This number starts on 0 and has to be incremental. If it is not consecutive or the last transmitted, the spectrometer will respond with RSP_ERROR.

If the spectrometer has no more data to transmit, it sends a RSP_NO_MORE_DATA

 **Payload** 1 byte: block number
 
*Response:*
 - RSP_RETURN
 - RSP_NO_MORE_DATA
 - RSP_BUSY
 - RSP_ERROR

### 0x32) GET_FLICKERING
**Description:** Get the flickering of the light

**Payload** 200 Unsigned integers of 32 bits. The total length is expected to be of 800 bytes (200*4).

*Response:*
 - RSP_RETURN
 - RSP_BUSY
 - RSP_ERROR
 
**Payload response** 

### 0x33) SET_BLUETOOTH_NAME

**Description:** Sets the bluetooth name

 1. **Payload**  (max 24 bytes = 24 char) [String consist in the name of the bluetooth device. It has to end with endstring char [\0]

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR


## Header definition

SYNC|FLAGS|TOKEN|FUNCTION|SEQ|LENGTH|DATA|  
|--|--|--|--|--|--|--|
|2 bytes|1 byte|1 byte|1 byte|2 byte|1 byte|244 byte|


####  SYNC HEADER
It is the header of all the frames

0x55|0x02
|--|--|
|1 byte|1 byte|

## FLAGS

|0|0|0|0|0|0|STREAMING_VIDEO_SYNC|MULTITRAMA|  	
|--|--|--|--|--|--|--|--|
|1 Bit|1 Bit|1 Bit|1 Bit|1 Bit|1 Bit|1 Bit|1 Bit

Multitrama:
 - 1 (is not last frame)
 - 0 (last frame)

STREAMING_VIDEO_SYNC --> 1 , in video cases, the first frame in a spectrum goes with 1 in this bit.  Is the way to know when spectrum starts. Is useful in case of a desinchronization.					


## RESPONSE codes

*PROTOCOL*

no data in comand response. 

ENUMS
 - 0xA0 RSP_ACK
 - 0xA1 RSP_COMPLETED
 - 0xA2 RSP_RETURN
 - 0xA3 RSP_NO_MORE_DATA
 - 0xA6 RSP_BUSY
 - 0xA7 RSP_ERROR

## EVENT codes

 - 0xE1 EV_FOTO
 - 0xE2 EV_STREAMING
 - 0xE4 EV_POWER_OFF 
 - 0xE5 EV_ENTERING_SLEEP_MODE 
 - 0xE6 EV_SPECTROMETER_STATE

## Processes

### Background calibration

In order to calibrate the background, user have to call this functions:

1) **CALIBRATE_BACKGROUND** Spectrometer prepares the calibration, checking the dark position sensor and returning a completed if it is ready. If it is not ready, it will return an error. In order to calibrate the default calibration, function *CALIBRATE_DEFAULT_BACKGROUND* must be used in this place.

2) **GET_RAW_SPECTRUM** Returns the spectrum in the dark.

4) **SET_BACKGROUND** If user is satisfied with the calibration result, he has to use this function in order to set the calibration. In case of default calibration, function *SET_DEFAULT_BACKGROUND* must be used.

