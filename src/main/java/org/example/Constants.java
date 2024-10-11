package org.example;

import java.util.Dictionary;
import java.util.Hashtable;

public class Constants {
    public static Dictionary<String, Integer> Commands = new Hashtable<>();


    public void initConst(){
        Commands.put("GET_INTEGRATION_TIME",0x02);
        Commands.put("GET_GAIN",0x04);
        Commands.put("GET_RAW_SPECTRUM",0x05);
        Commands.put("STOP_STREAMING",0x07);
        Commands.put("GET_TRANSFER_FUNCTION",0x09);
        Commands.put("GET_DEFAULT_TRANSFER_FUNCTION",0x011);
        Commands.put("RELOAD_DEFAULT_TRANSFER_FUNCTION",0x012);
        Commands.put("GET_WAVELENGTH_CALIBRATION",0x14);
        Commands.put("GET_DEFAULT_WAVELENGTH_CALIBRATION",0x16);
        Commands.put("SET_DEFAULT_TRANSFER_FUNCTION",0xA);
        Commands.put("RELOAD_DEFAULT_WAVELENGTH_CALIBRATION",0x11);
        Commands.put("GET_DEFAULT_WAVELENGTH_CALIBRATION",0x10);
        Commands.put("CALIBRATE_BACKGROUND",0x12);
        Commands.put("GET_BACKGROUND_CALIBRATIONS",0x13);
        Commands.put("CALIBRATE_DEFAULT_BACKGROUND",0x14);
        Commands.put("GET_DEFAULT_BACKGROUND_CALIBRATIONS",0x15);
        Commands.put("RELOAD_DEFAULT_BACKGROUND_CALIBRATIONS",0x16);
        Commands.put("GET_LUX_CALIBRATION",0x19);

        Commands.put("GET_DEFAULT_LUX_CALIBRATION",0x1B);
        Commands.put("RELOAD_DEFAULT_LUX_CALIBRATION",0x1C);
        Commands.put("GET_VIDEO_SAMPLE_RATE",0x1E);
        Commands.put("GET_TIME",0x20);
        Commands.put("GET_DEVICE_STATE",0x21);
        Commands.put("GET_DEVICE_INFO",0x23);
        Commands.put("SET_SOFT_FACTORY_RESET",0x27);
        Commands.put("SET_HARD_FACTORY_RESET",0x28);
        Commands.put("GET_BACKGROUND_COEFFICIENTS",0x2C);
        Commands.put("GET_DEFAULT_BACKGROUND_COEFFICIENTS",0x2E);
        Commands.put("RELOAD_DEFAULT_BACKGROUND_COEFFICIENTS",0x2F);
        Commands.put("GET_FLICKERING",0x32);
        Commands.put("SET_TIME",0x1F);


        Commands.put("SET_INTEGRATION_TIME",0x1);
        Commands.put("SET_GAIN",0x3);
        Commands.put("SET_WAVELENGTH_CALIBRATION",0xD);
        Commands.put("SET_LUX_CALIBRATION",0x18);
        Commands.put("SET_BLUETOOTH_NAME",0x33);
        Commands.put("SET_DEFAULT_LUX_CALIBRATION",0x1A);
        Commands.put("SET_VIDEO_SAMPLE_RATE",0x1D);

        Commands.put("SET_BACKGROUND",0x29);
        Commands.put("SET_DEFAULT_BACKGROUND",0x2A);


    }
}
