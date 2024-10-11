import sys
from ast import literal_eval
from frame import frame
import spectrometer as Spectrometer
import parsepayload
import struct
import matplotlib.pyplot as plt
import numpy as np

data = literal_eval(sys.argv[1])[:236]
# 237 datos entre 380 780
# 
xpoints = np.arange(380, 780, 1.7, dtype=int)
ypoints = np.array(data)

plt.plot(xpoints, ypoints)

plt.show()



#print(len(dataFrame.payload))