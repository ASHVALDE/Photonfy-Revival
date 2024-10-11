### 0x06)	(No esta bien documentado) START_STREAMING
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



### 0x08)	(Muchos datos) SET_TRANSFER_FUNCTION
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



### 0x0A)	(Muchos datos)  SET_DEFAULT_TRANSFER_FUNCTION

Same as *SET_TRANSFER_FUNCTION* but sets the default value.

### 0x23)	(Implementado pero el callback no esta documentado) GET_DEVICE_INFO
// No Coincide con la informacion que manda el espectrometro
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


### 0x22)	(Funciones Peligrosas) SET_DEVICE_INFO
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



### 0x24)	(Funciones Peligrosas) SET_FIRMWARE_UPDATE
**Description:** With the multitrama protocol transfers all the file containing the firmware update

### 0x25)	(Funciones Peligrosas) SET_DEFAULT_FIRMWARE_UPDATE
**Description:** With the multitrama protocol transfers all the file containing the firmware update

### 0x26)	(Funciones Peligrosas) SET_FIRMWARE_UPDATE_METADATA

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


### 0x31)	(Muchos datos) NEXT_SYNC_SPECTRA
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



### 0x14)	(Callback no esta documentado #173 -> 0xAD) CALIBRATE_DEFAULT_BACKGROUND
**Description:** Order the spectrometer to calibraa a ue sh esrt e bckgrdterac i l teareter. 

Spectrometer prepares to calibrate.

*Payloadretrn length:* 0 bytes

*Arguments:*

*Response:*
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR




## Wavelenght Calibration

### 0x0D)	(Implementado pero sin probar) SET_WAVELENGTH_CALIBRATION 
**Description:** Sets the conversion factors from pixel number (its order in the array) to wavelength in nanometers (nm). These are 6 coefficients that multiply the pixel number to obtain the nanometers represented by that pixel. The first element is the largest coefficient multiplied by one, the second is the second largest multiplied by the pixel number, the third is the third largest coefficient multiplied by the square of the pixel number and so on until the sixth coefficient, the smallest one. Formula: nm = y (px) = a0 + a1 * px + a2 * px^2 + a3 * px^3 + a4 * px^4 + a5 * px^5

*Payload length:* 24 bytes

*Arguments:*

 1. **Coefficients** (6 floats x 4 bytes/float = 24 bytes): Array of 6 values ranging from -380 to 380 nm (up to a 10-6 exponential and 5 significant digits).

*Response:*

 - RSP_ACK
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR

### 0x0E)	(Callback no esta documentado #173 -> 0xAD) GET_WAVELENGTH_CALIBRATION

### 0x0F)	(Callback no esta documentado #173 -> 0xAD) SET_DEFAULT_WAVELENGTH_CALIBRATION
Same as *SET_WAVELENGTH_CALIBRATION* but sets the default value.

### 0x10)	(Callback no esta documentado #173 -> 0xAD) GET_DEFAULT_WAVELENGTH_CALIBRATION
Same as *GET_WAVELENGTH_CALIBRATION* but sets the default value.

### 0x11)	(Callback no esta documentado #173 -> 0xAD) RELOAD_DEFAULT_WAVELENGTH_CALIBRATION
Sets the value of default_wavelength_calibration in wavelength_calibration

## (Muchos datos) BACKGROUND_COEFFICIENTS

### 0x2B) (Muchos datos)  SET_BACKGROUND_COEFFICIENTS

**Description:** Set the backgrounds coefficients previously mesured. We have been mesured  14 diferents integration time in a range of temperature in order to know the background behaviour. formula:  y=a*x^2+b*x+c , where a,b,c is the matrix.
*Arguments:*

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

*Response:*
 - RSP_ACK
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 - 

### 0x2C) (Muchos datos)  GET_BACKGROUND_COEFFICIENTS

**Description:** Get the backgrounds coefficients previously.

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

### 0x2D) (Muchos datos)  SET_DEFAULT_BACKGROUND_COEFFICIENTS

**Description:** Set the backgrounds coefficients previously mesured. We have been mesured  14 diferents integration time in a range of temperature in order to know the background behaviour. formula:  y=a*x^2+b*x+c , where a,b,c is the matrix.
*Arguments:*

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

*Response:*
 - RSP_ACK
 - RSP_COMPLETED
 - RSP_BUSY
 - RSP_ERROR
 - 

### 0x2E)  (Muchos datos) GET_DEFAULT_BACKGROUND_COEFFICIENTS

**Description:** Get the backgrounds coefficients previously.

 1. **Payload** (14 floats x 4 bytes/float *3(a,b,c)= 168 bytes): for each integration time(14 diferent values) we have 3 values (a,b,c). 

