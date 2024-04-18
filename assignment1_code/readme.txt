# Introduction of the Program
The assignment involves using modern OpenGL(JOGL) to render a snowy scene, focusing on Modelling the scene, Texturing, Lighting,
Interface controls(Swing), and Animations.

# Running the Program
## Running from Terminal by following steps(Tested in mac terminal)
1. export PATH={ABSOLUTE_PATH_TO_(jogl25/lib)}:$PATH
eg: export PATH=/Users/chichungcheung/Desktop/SheffieldMaster/COM6503\ 3D\ Computer\ Graphics/jogl25/lib:$PATH

2.export CLASSPATH=.:{ABSOLUTE_PATH_TO_(jogl25/jar/jogl-all.jar)}:{ABSOLUTE_PATH_TO_(jogl25/jar//gluegen-rt.jar)}:$CLASSPATH
eg: export CLASSPATH=.:/Users/chichungcheung/Desktop/SheffieldMaster/COM6503\ 3D\ Computer\ Graphics/
    jogl25/jar/jogl-all.jar:/Users/chichungcheung/Desktop/SheffieldMaster/COM6503\ 3D\ Computer\ Graphics/
    jogl25/jar/gluegen-rt.jar:$CLASSPATH

3. javac *.java
4. java Aliens

## Running in IntelliJ
1. Go to Aliens.java in the root path;
2. Run Aliens.main();

# Features
## Backdrop
- Vertical planes textured and animated with pictures of snowing
- The scene surrounded by vertical planes and skybox

## Security spotlight
- Continuously rotating with User Interface to Control
- Implemented spotlight effect inspired by Joey's tutorial

## Aliens
- Aliens were made hierarchically and associated transformations
- Used different textures on different aliens
- Used diffuse and specular maps

## General illumination
- Two general world lights with controller

## User Interface
- user-controlled camera as in the lab code
- Lights Controller
- Aliens Animate Controller

## Animation
- Rock: Rock its whole body from side to side.
- Roll: Roll its head around its body a little.
- Cheering: Circling with rock and roll.
- Stand still: Reset aliens

# Notice
- Once you run the program, it may show a blank canva in the center of the window, that's because it needs some time to
initial the scene. Please give it sometimes(around 5-10 sec), and hopefully you can see the proper scene.

