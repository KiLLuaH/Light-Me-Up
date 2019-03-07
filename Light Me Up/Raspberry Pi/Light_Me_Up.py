#!/usr/bin/env python
import paho.mqtt.client as mqtt
from PIL import ImageFont
from PIL import Image
from PIL import ImageDraw
from samplebase import SampleBase
from rgbmatrix import RGBMatrix, RGBMatrixOptions, graphics
from thread import start_new_thread
import time

winningTeam = ""
msg = ""
col = ""

redMsg = "Red-Team Wins"
blueMsg = "Blue-Team Wins"
greenMsg = "Green-Team Wins"
yellowMsg = "Yellow-Team Wins"

redCol = "255 0 0"
blueCol = "0 0 255"
greenCol = "0 187 0"
yellowCol = "253 221 0"

redScore = 0
blueScore = 0
greenScore = 0
yellowScore = 0
timeValue = 20
tmr = timeValue

status = ""
admin = "Admin"
ready = "Ready"
start = "Start"
stop = "Stop"
pause = "Pause"
red = "Red"
blue = "Blue"
green = "Green"
yellow = "Yellow"
blackfile = "Black.ppm"
redfile = "Red.ppm"
bluefile = "Blue.ppm"
greenfile = "Green.ppm"
yellowfile = "Yellow.ppm"
r = "R"
b = "B"
g = "G"
y = "Y"

options = RGBMatrixOptions()
options.rows = 16
options.chain_length = 2
options.parallel = 1
options.hardware_mapping = 'regular'
matrix = RGBMatrix(options = options)



url = "broker.mqttdashboard.com"
topic = "Light Me Up"

def on_connect(client, userdata, flags, rc):
    print("connected with result code"+str(rc))

    client.subscribe(topic)


def on_message(client, userdata, msg):
    task, content = msg.payload.split(" ")

    global redScore
    global blueScore
    global greenScore
    global yellowScore

    if task == admin:
        if content == ready:
            getReady()
            print("Game is Ready")
        elif content == start:
            startGame()
            print("Game started")
        elif content == stop:
            stopGame()
            print("Game stopped")
        elif content == pause:
            pauseGame()
            print("Game paused")
            
    elif task == red:
        redScore += 1
        publish(r, redScore)
    elif task == blue:
        blueScore += 1
        publish(b, blueScore)
    elif task == green:
        greenScore += 1
        publish(g, greenScore)
    elif task == yellow:
        yellowScore += 1
        publish(y, yellowScore)

    winner()
   

def winner():    
    global redScore
    global blueScore
    global greenScore
    global yellowScore
    global winningTeam
    redVal = redScore
    blueVal = blueScore
    greenVal = greenScore
    yellowVal = yellowScore

    winner=[redScore, blueScore, greenScore, yellowScore]

    if max(winner) == redVal and max(winner) > 0:
        drawImage(redfile)
        winningTeam = red
    elif max(winner) == blueVal and max(winner) > 0:
        drawImage(bluefile)
        winningTeam = blue
    elif max(winner) == greenVal and max(winner) > 0:
        drawImage(greenfile)
        winningTeam = green
    elif max(winner) == yellowVal and max(winner) > 0:
        drawImage(yellowfile)
        winningTeam = yellow

def drawImage(team):
    global matrix
    path = team
    image = Image.open(path)
    image.thumbnail((matrix.width, matrix.height), Image.ANTIALIAS)
    matrix.SetImage(image.convert('RGB'))   
    
def getReady():
    global redScore
    global blueScore
    global greenScore
    global yellowScore
    global tmr
    global gameStopped
    global winningTeam
    global msg
    global col
    global status
    redScore = 0
    blueScore = 0
    greenScore = 0
    yellowScore = 0
    tmr = timeValue
    gameStopped = False
    winningTeam = ""
    msg = ""
    col = ""
    status = ready
    drawImage(blackfile)

def startGame():
    global status
    status = start
    start_new_thread(startTimer, ("a",))

def stopGame():
    global status
    status = stop
    start_new_thread(runText, (winningTeam,))

def pauseGame():
    global status
    status = pause
    start_new_thread(pauseScreen, ("a",))
    

def publish(team, score):
    scr = str(score)
    message = team + " " + scr
    client.publish(topic, message)

def pauseScreen(txt):
    count = 0
    while True:
        
        if status == ready:
            break
        elif status == start:
            break
        if status == stop:
            break
        elif count == 0:
            count += 1
            drawImage(redfile)
            time.sleep(1)
        elif count == 1:
            count += 1
            drawImage(bluefile)
            time.sleep(1)
        elif count == 2:
            count += 1
            drawImage(greenfile)
            time.sleep(1)
        elif count == 3:
            count += 1
            drawImage(yellowfile)
            time.sleep(1)
        elif count == 4:
            count = 0
    

def runText(txt):
    global msg
    global col
    
    if(txt == red):
        msg = redMsg
        col = redCol
    elif(txt == blue):
        msg = blueMsg
        col = blueCol
    elif(txt == green):
        msg = greenMsg
        col = greenCol
    elif(txt == yellow):
        msg = yellowMsg
        col = yellowCol
    
    offscreen_canvas = matrix.CreateFrameCanvas()
    font = graphics.Font()
    font.LoadFont("../../fonts/9x15B.bdf")
    r, g, b = col.split(" ")
    textColor = graphics.Color(int(r), int(g), int(b))
    pos = offscreen_canvas.width
    my_text = msg

    while True:
        if status == ready:
            drawImage(blackfile)
            break
        elif status == pause:
            drawImage(blackfile)
            break
        elif status == start:
            drawImage(blackfile)
            break
        
        offscreen_canvas.Clear()
        len = graphics.DrawText(offscreen_canvas, font, pos, 13, textColor, my_text)
        pos -= 1
        if (pos + len < 0):
            pos = offscreen_canvas.width

        time.sleep(0.05)
        offscreen_canvas = matrix.SwapOnVSync(offscreen_canvas)

def startTimer(txt):
    while True:
        global tmr

        if status == ready:
            break
        elif status == stop:
            break
        elif status == pause:
            break 
        elif tmr == 0:
            publish(admin, stop)
            break
            
        print("Timer: " + str(tmr))
        tmr -= 1
        time.sleep(1)

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect(url, 1883, 60)


client.loop_forever()
