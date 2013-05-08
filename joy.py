#
# Pendaphonic Joystick UDP sender
#

import pygame
import time as t
from pygame import locals
from socket import *

# Set the socket parameters
#host = raw_input("Host: ")
host = '127.0.0.1'
#host = '192.168.1.103' #'192.168.1.105'
#port = raw_input("Port: ")
port = 21567 #21567 was the original
buf = 1024
addr = (host,port)
# Create socket
UDPSock = socket(AF_INET,SOCK_DGRAM)

# Joystick configure
pygame.init()

pygame.joystick.init() # main joystick device system

try:
    j = pygame.joystick.Joystick(0) # create a joystick instance
    j.init() # init instance
    print "Enabled Joystick."
except pygame.error:
    print "No Joystick Found!"

while 1:
    for e in pygame.event.get(): # iterate over event stack
        if e.type == pygame.locals.JOYAXISMOTION: # 7		      
            x , y , z , rx , ry , rz = j.get_axis(0), j.get_axis(1), j.get_axis(2), j.get_axis(3), j.get_axis(4), j.get_axis(5)
            msg = "%f,%f,%f,%f,%f,%f" % (x,y,z,rx,ry,rz)
            # Send message
            UDPSock.sendto(msg,addr)
            print msg + '\r',
    t.sleep(0.1)
